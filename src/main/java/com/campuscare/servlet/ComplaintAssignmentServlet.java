package com.campuscare.servlet;

import com.campuscare.dao.ComplaintDAO;
import com.campuscare.dao.StaffDAO;
import com.campuscare.model.Complaint;
import com.campuscare.model.Staff;
import com.campuscare.util.CSRFUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ComplaintAssignmentServlet", urlPatterns = {"/admin/assign-complaint"})
public class ComplaintAssignmentServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final StaffDAO staffDAO = new StaffDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/all-complaints");
            return;
        }

        try {
            long grievanceId;
            String clean = idParam.trim();
            if (clean.toUpperCase().startsWith("GC")) {
                clean = clean.substring(2);
            }
            grievanceId = Long.parseLong(clean);

            Complaint complaint = complaintDAO.findById(grievanceId);
            if (complaint == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Complaint not found.");
                return;
            }

            List<Staff> activeStaff = staffDAO.findAllActive();
            req.setAttribute("complaint", complaint);
            req.setAttribute("activeStaff", activeStaff);
            req.setAttribute("updates", complaintDAO.findUpdatesByGrievanceId(grievanceId));
            req.getRequestDispatcher("/WEB-INF/views/admin/assign-complaint.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading assignment form: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        String adminId = (session != null) ? (String) session.getAttribute("userId") : null;

        if (adminId == null) {
            resp.sendRedirect(req.getContextPath() + "/admin-login");
            return;
        }

        String idStr = req.getParameter("grievanceId");
        String staffId = req.getParameter("staffId");
        String remarks = req.getParameter("remarks");

        if (idStr == null || staffId == null || staffId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/all-complaints?error=Invalid complaint or staff identifier.");
            return;
        }

        try {
            long grievanceId;
            String clean = idStr.trim();
            if (clean.toUpperCase().startsWith("GC")) {
                clean = clean.substring(2);
            }
            grievanceId = Long.parseLong(clean);

            boolean assigned = complaintDAO.assignStaff(grievanceId, staffId.trim(), adminId, remarks);
            if (assigned) {
                resp.sendRedirect(req.getContextPath() + "/admin/complaint-details?id=" + grievanceId + "&success=Complaint GC" + grievanceId + " successfully assigned to staff " + staffId + ".");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/complaint-details?id=" + grievanceId + "&error=Failed to assign complaint.");
            }
        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/assign-complaint?id=" + idStr + "&error=" + e.getMessage());
        } catch (SQLException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/assign-complaint?id=" + idStr + "&error=Database error during assignment: " + e.getMessage());
        }
    }
}
