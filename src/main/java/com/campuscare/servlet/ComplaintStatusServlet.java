package com.campuscare.servlet;

import com.campuscare.dao.ComplaintDAO;
import com.campuscare.model.Complaint;
import com.campuscare.util.CSRFUtil;
import com.campuscare.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "ComplaintStatusServlet", urlPatterns = {
        "/staff/complaint-status",
        "/admin/complaint-status",
        "/student/complaint-withdraw"
})
public class ComplaintStatusServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);

        String role = (session != null) ? (String) session.getAttribute("role") : null;
        String userId = (session != null) ? (String) session.getAttribute("userId") : null;

        if (role == null || userId == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        String idStr = req.getParameter("grievanceId");
        if (idStr == null || idStr.trim().isEmpty()) {
            idStr = req.getParameter("id");
        }

        long grievanceId;
        try {
            String clean = idStr.trim();
            if (clean.toUpperCase().startsWith("GC")) {
                clean = clean.substring(2);
            }
            grievanceId = Long.parseLong(clean);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid complaint identifier.");
            return;
        }

        String newStatus = req.getParameter("status");
        String remarks = req.getParameter("remarks");

        // Specific handling for student withdrawal endpoint
        if ("/student/complaint-withdraw".equals(req.getServletPath())) {
            newStatus = "WITHDRAWN";
            if (remarks == null || remarks.trim().isEmpty()) {
                remarks = "Complaint withdrawn by student.";
            }
        }

        if (newStatus == null || !ValidationUtil.isValidStatus(newStatus)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid target complaint status: " + newStatus);
            return;
        }

        // Additional business validations:
        // Resolution requires non-empty remarks up to 500 characters
        if ("RESOLVED".equalsIgnoreCase(newStatus)) {
            if (!ValidationUtil.isValidRemarks(remarks)) {
                String redirectUrl = getRedirectUrl(req, role, grievanceId);
                resp.sendRedirect(redirectUrl + (redirectUrl.contains("?") ? "&" : "?") + "error=Resolution remarks are required (up to 500 characters).");
                return;
            }
        }

        // Rejection requires remarks
        if ("REJECTED".equalsIgnoreCase(newStatus)) {
            if (!ValidationUtil.isValidRemarks(remarks)) {
                String redirectUrl = getRedirectUrl(req, role, grievanceId);
                resp.sendRedirect(redirectUrl + (redirectUrl.contains("?") ? "&" : "?") + "error=Rejection remarks are mandatory explaining the reason.");
                return;
            }
        }

        try {
            Complaint complaint = complaintDAO.findById(grievanceId);
            if (complaint == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Complaint not found.");
                return;
            }

            // Authorization verification
            if ("STUDENT".equalsIgnoreCase(role)) {
                if (!complaint.getStudentId().equals(userId)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You cannot update another student's complaint.");
                    return;
                }
            } else if ("STAFF".equalsIgnoreCase(role)) {
                if (complaint.getAssignedStaffId() == null || !complaint.getAssignedStaffId().equals(userId)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You can only update complaints assigned to you.");
                    return;
                }
            }

            boolean success = complaintDAO.updateStatus(grievanceId, newStatus.toUpperCase(), role, userId, remarks);
            if (success) {
                String redirectUrl = getRedirectUrl(req, role, grievanceId);
                resp.sendRedirect(redirectUrl + (redirectUrl.contains("?") ? "&" : "?") + "success=Status updated to " + newStatus + " successfully.");
            } else {
                String redirectUrl = getRedirectUrl(req, role, grievanceId);
                resp.sendRedirect(redirectUrl + (redirectUrl.contains("?") ? "&" : "?") + "error=Could not update status.");
            }
        } catch (IllegalArgumentException e) {
            String redirectUrl = getRedirectUrl(req, role, grievanceId);
            resp.sendRedirect(redirectUrl + (redirectUrl.contains("?") ? "&" : "?") + "error=" + e.getMessage());
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error during status transition: " + e.getMessage());
        }
    }

    private String getRedirectUrl(HttpServletRequest req, String role, long grievanceId) {
        if ("STUDENT".equalsIgnoreCase(role)) {
            return req.getContextPath() + "/student/complaint-details?id=" + grievanceId;
        } else if ("STAFF".equalsIgnoreCase(role)) {
            return req.getContextPath() + "/staff/complaint-details?id=" + grievanceId;
        } else {
            return req.getContextPath() + "/admin/complaint-details?id=" + grievanceId;
        }
    }
}
