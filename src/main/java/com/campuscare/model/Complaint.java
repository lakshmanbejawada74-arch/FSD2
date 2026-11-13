package com.campuscare.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;

    private long grievanceId;
    private String studentId;
    private int categoryId;
    private String title;
    private String description;
    private String priority; // LOW, MEDIUM, HIGH
    private String status;   // PENDING, ASSIGNED, IN_PROGRESS, RESOLVED, REJECTED, WITHDRAWN
    private String assignedStaffId;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Associated / View-helper fields
    private String categoryName;
    private String categoryDepartment;
    private String studentName;
    private String studentEmail;
    private String studentMobile;
    private String studentDepartment;
    private String studentType;
    private String hostelName;
    private String roomNumber;
    private String assignedStaffName;
    private String assignedStaffEmail;
    private String latestRemarks;

    public Complaint() {}

    public Complaint(long grievanceId, String studentId, int categoryId, String title,
                     String description, String priority, String status,
                     String assignedStaffId, Timestamp createdAt, Timestamp updatedAt) {
        this.grievanceId = grievanceId;
        this.studentId = studentId;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.assignedStaffId = assignedStaffId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Formatted complaint number for display (e.g. GC1001)
     */
    public String getComplaintNumber() {
        return "GC" + grievanceId;
    }

    public long getGrievanceId() { return grievanceId; }
    public void setGrievanceId(long grievanceId) { this.grievanceId = grievanceId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(String assignedStaffId) { this.assignedStaffId = assignedStaffId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getCategoryDepartment() { return categoryDepartment; }
    public void setCategoryDepartment(String categoryDepartment) { this.categoryDepartment = categoryDepartment; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getStudentMobile() { return studentMobile; }
    public void setStudentMobile(String studentMobile) { this.studentMobile = studentMobile; }

    public String getStudentDepartment() { return studentDepartment; }
    public void setStudentDepartment(String studentDepartment) { this.studentDepartment = studentDepartment; }

    public String getStudentType() { return studentType; }
    public void setStudentType(String studentType) { this.studentType = studentType; }

    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getAssignedStaffName() { return assignedStaffName; }
    public void setAssignedStaffName(String assignedStaffName) { this.assignedStaffName = assignedStaffName; }

    public String getAssignedStaffEmail() { return assignedStaffEmail; }
    public void setAssignedStaffEmail(String assignedStaffEmail) { this.assignedStaffEmail = assignedStaffEmail; }

    public String getLatestRemarks() { return latestRemarks; }
    public void setLatestRemarks(String latestRemarks) { this.latestRemarks = latestRemarks; }
}
