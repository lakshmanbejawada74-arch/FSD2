package com.campuscare.servlet;

import com.campuscare.dao.ComplaintCategoryDAO;
import com.campuscare.dao.ComplaintDAO;
import com.campuscare.dao.StaffDAO;
import com.campuscare.model.Complaint;
import com.campuscare.model.ComplaintUpdate;
import com.campuscare.model.Staff;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ComplaintDetailsServlet", urlPatterns = {
        "/student/complaint-details",
        "/staff/complaint-details",
        "/admin/complaint-details"
})
public class ComplaintDetailsServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final StaffDAO staffDAO = new StaffDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        String userId = (session != null) ? (String) session.getAttribute("userId") : null;

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            idParam = req.getParameter("grievanceId");
        }

        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing complaint ID parameter.");
            return;
        }

        long grievanceId;
        try {
            String clean = idParam.trim();
            if (clean.toUpperCase().startsWith("GC")) {
                clean = clean.substring(2);
            }
            grievanceId = Long.parseLong(clean);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid complaint ID format.");
            return;
        }

        try {
            Complaint complaint = complaintDAO.findById(grievanceId);
            if (complaint == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Complaint GC" + grievanceId + " not found.");
                return;
            }

            // Role-based access control and isolation
            if ("STUDENT".equalsIgnoreCase(role)) {
                if (!complaint.getStudentId().equals(userId)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You cannot view complaints filed by other students.");
                    return;
                }
            } else if ("STAFF".equalsIgnoreCase(role)) {
                if (complaint.getAssignedStaffId() == null || !complaint.getAssignedStaffId().equals(userId)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You may only view complaints assigned to you.");
                    return;
                }
            }

            List<ComplaintUpdate> updates = complaintDAO.findUpdatesByGrievanceId(grievanceId);
            req.setAttribute("complaint", complaint);
            req.setAttribute("updates", updates);

            if ("STUDENT".equalsIgnoreCase(role)) {
                req.getRequestDispatcher("/WEB-INF/views/student/complaint-details.jsp").forward(req, resp);
            } else if ("STAFF".equalsIgnoreCase(role)) {
                req.getRequestDispatcher("/WEB-INF/views/staff/resolve-complaint.jsp").forward(req, resp);
            } else if ("ADMIN".equalsIgnoreCase(role)) {
                List<Staff> activeStaff = staffDAO.findAllActive();
                req.setAttribute("activeStaff", activeStaff);
                req.getRequestDispatcher("/WEB-INF/views/admin/assign-complaint.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error retrieving complaint: " + e.getMessage());
        }
    }
}
