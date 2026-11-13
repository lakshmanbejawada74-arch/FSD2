package com.campuscare;

import com.campuscare.dao.AdminDAO;
import com.campuscare.dao.ComplaintCategoryDAO;
import com.campuscare.dao.ComplaintDAO;
import com.campuscare.dao.StaffDAO;
import com.campuscare.dao.StudentDAO;
import com.campuscare.model.*;
import com.campuscare.util.PasswordUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CampusCareWorkflowIntegrationTest {

    private static StudentDAO studentDAO;
    private static StaffDAO staffDAO;
    private static AdminDAO adminDAO;
    private static ComplaintCategoryDAO categoryDAO;
    private static ComplaintDAO complaintDAO;

    @BeforeClass
    public static void setUp() {
        studentDAO = new StudentDAO();
        staffDAO = new StaffDAO();
        adminDAO = new AdminDAO();
        categoryDAO = new ComplaintCategoryDAO();
        complaintDAO = new ComplaintDAO();
    }

    @Test
    public void testStudentRegistrationAndDuplicatePrevention() throws Exception {
        String testStudentId = "TEST_STU_" + System.currentTimeMillis();
        String testEmail = testStudentId.toLowerCase() + "@campuscare.test";

        Student student = new Student();
        student.setStudentId(testStudentId);
        student.setFullName("Test Integration Student");
        student.setDepartment("Computer Science and Engineering");
        student.setStudyYear(2);
        student.setStudentType("DAY_SCHOLAR");
        student.setHostelName(null);
        student.setRoomNumber(null);
        student.setEmail(testEmail);
        student.setMobile("9988776655");
        student.setPasswordHash(PasswordUtil.hashPassword("Student@123"));
        student.setAccountStatus("ACTIVE");

        boolean inserted = studentDAO.insert(student);
        assertTrue("Student registration should succeed", inserted);

        // Verify lookup
        Student found = studentDAO.findById(testStudentId);
        assertNotNull(found);
        assertEquals("Test Integration Student", found.getFullName());
        assertTrue(found.isActive());

        // Verify duplicate prevention
        assertTrue(studentDAO.isIdExists(testStudentId));
        assertTrue(studentDAO.isEmailExists(testEmail));
    }

    @Test
    public void testHostelStudentConstraints() throws Exception {
        String testHostelId = "TEST_HST_" + System.currentTimeMillis();
        String testEmail = testHostelId.toLowerCase() + "@campuscare.test";

        Student hostelStudent = new Student();
        hostelStudent.setStudentId(testHostelId);
        hostelStudent.setFullName("Test Hostel Student");
        hostelStudent.setDepartment("Information Technology");
        hostelStudent.setStudyYear(3);
        hostelStudent.setStudentType("HOSTEL");
        hostelStudent.setHostelName("Ganga Hall");
        hostelStudent.setRoomNumber("C-101");
        hostelStudent.setEmail(testEmail);
        hostelStudent.setMobile("9988776656");
        hostelStudent.setPasswordHash(PasswordUtil.hashPassword("Student@123"));
        hostelStudent.setAccountStatus("ACTIVE");

        boolean inserted = studentDAO.insert(hostelStudent);
        assertTrue(inserted);

        Student fetched = studentDAO.findById(testHostelId);
        assertNotNull(fetched);
        assertEquals("HOSTEL", fetched.getStudentType());
        assertEquals("Ganga Hall", fetched.getHostelName());
        assertEquals("C-101", fetched.getRoomNumber());
    }

    @Test
    public void testCompleteComplaintLifecycle() throws Exception {
        // Step 1: Student creates complaint
        Complaint c = new Complaint();
        c.setStudentId("STU1001");
        c.setCategoryId(1); // HOSTEL
        c.setTitle("AC and fan power socket damaged");
        c.setDescription("The primary electrical socket in room B-204 sparks intermittently and poses a risk.");
        c.setPriority("HIGH");

        long grievanceId = complaintDAO.createComplaint(c);
        assertTrue("Grievance ID should be >= 1001", grievanceId >= 1001);

        // Verify initial state
        Complaint created = complaintDAO.findById(grievanceId);
        assertNotNull(created);
        assertEquals("GC" + grievanceId, created.getComplaintNumber());
        assertEquals("PENDING", created.getStatus());
        assertNull(created.getAssignedStaffId());

        // Verify initial update record
        List<ComplaintUpdate> updates = complaintDAO.findUpdatesByGrievanceId(grievanceId);
        assertFalse(updates.isEmpty());
        assertEquals("PENDING", updates.get(0).getNewStatus());
        assertEquals("STUDENT", updates.get(0).getUpdatedByRole());

        // Step 2: Admin assigns complaint to staff STF002 (Hostel staff)
        boolean assigned = complaintDAO.assignStaff(grievanceId, "STF002", "ADM001", "Priority electrical repair required.");
        assertTrue("Assignment should succeed", assigned);

        Complaint assignedComplaint = complaintDAO.findById(grievanceId);
        assertEquals("ASSIGNED", assignedComplaint.getStatus());
        assertEquals("STF002", assignedComplaint.getAssignedStaffId());

        // Step 3: Staff moves complaint to IN_PROGRESS
        boolean inProgress = complaintDAO.updateStatus(grievanceId, "IN_PROGRESS", "STAFF", "STF002", "Electrician dispatched with spare socket board.");
        assertTrue("Status change to IN_PROGRESS should succeed", inProgress);

        Complaint progressComplaint = complaintDAO.findById(grievanceId);
        assertEquals("IN_PROGRESS", progressComplaint.getStatus());

        // Step 4: Staff resolves complaint with remarks
        String resolutionRemarks = "Replaced 16A modular power outlet and verified earthing voltage. Operational test passed.";
        boolean resolved = complaintDAO.updateStatus(grievanceId, "RESOLVED", "STAFF", "STF002", resolutionRemarks);
        assertTrue("Status change to RESOLVED should succeed", resolved);

        Complaint resolvedComplaint = complaintDAO.findById(grievanceId);
        assertEquals("RESOLVED", resolvedComplaint.getStatus());

        // Step 5: Verify status history audit trail
        List<ComplaintUpdate> finalUpdates = complaintDAO.findUpdatesByGrievanceId(grievanceId);
        assertEquals(4, finalUpdates.size());
        assertEquals("PENDING", finalUpdates.get(0).getNewStatus());
        assertEquals("ASSIGNED", finalUpdates.get(1).getNewStatus());
        assertEquals("IN_PROGRESS", finalUpdates.get(2).getNewStatus());
        assertEquals("RESOLVED", finalUpdates.get(3).getNewStatus());
        assertEquals(resolutionRemarks, finalUpdates.get(3).getRemarks());
    }

    @Test
    public void testUnauthorizedTransitionRejection() throws Exception {
        // Create complaint
        Complaint c = new Complaint();
        c.setStudentId("STU1001");
        c.setCategoryId(5); // INTERNET
        c.setTitle("Wi-Fi dropouts during online lecture");
        c.setDescription("Wi-Fi keeps dropping every 5 minutes in hostel wing B.");
        c.setPriority("MEDIUM");

        long grievanceId = complaintDAO.createComplaint(c);

        // Student tries to directly RESOLVE their own complaint -> must throw IllegalArgumentException
        try {
            complaintDAO.updateStatus(grievanceId, "RESOLVED", "STUDENT", "STU1001", "Fixed it myself");
            fail("Students must not be able to mark complaints as RESOLVED.");
        } catch (IllegalArgumentException expected) {
            // Success
        }

        // Staff tries to RESOLVE without setting IN_PROGRESS first
        try {
            complaintDAO.updateStatus(grievanceId, "RESOLVED", "STAFF", "STF001", "Direct resolve attempt");
            fail("Staff cannot transition directly from PENDING to RESOLVED.");
        } catch (IllegalArgumentException expected) {
            // Success
        }
    }

    @Test
    public void testStudentWithdrawWorkflow() throws Exception {
        Complaint c = new Complaint();
        c.setStudentId("STU1001");
        c.setCategoryId(2); // SANITATION
        c.setTitle("Corridor dusting request");
        c.setDescription("Dust accumulating on 2nd floor window sills.");
        c.setPriority("LOW");

        long grievanceId = complaintDAO.createComplaint(c);

        // Student withdraws their complaint
        boolean withdrawn = complaintDAO.updateStatus(grievanceId, "WITHDRAWN", "STUDENT", "STU1001", "Cleaned by regular housekeeping.");
        assertTrue(withdrawn);

        Complaint updated = complaintDAO.findById(grievanceId);
        assertEquals("WITHDRAWN", updated.getStatus());
    }

    @Test
    public void testStaffWorkloadStatistics() throws Exception {
        List<Map<String, Object>> workload = staffDAO.getStaffWorkload();
        assertNotNull(workload);
        assertFalse(workload.isEmpty());

        for (Map<String, Object> staffEntry : workload) {
            assertNotNull(staffEntry.get("staffId"));
            assertNotNull(staffEntry.get("fullName"));
            assertNotNull(staffEntry.get("department"));
            assertTrue(((Number) staffEntry.get("totalAssigned")).intValue() >= 0);
        }
    }

    @Test
    public void testFilteredComplaintSearch() throws Exception {
        List<Complaint> list = complaintDAO.findAllFiltered(
                null, null, null, null, null, null, "RESOLVED", null, null
        );
        assertNotNull(list);
        for (Complaint c : list) {
            assertEquals("RESOLVED", c.getStatus());
        }
    }
}
