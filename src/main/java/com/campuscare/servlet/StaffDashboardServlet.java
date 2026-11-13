package com.campuscare.servlet;

import com.campuscare.dao.ComplaintDAO;
import com.campuscare.model.Complaint;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "StaffDashboardServlet", urlPatterns = {"/staff/dashboard", "/staff/assigned-complaints"})
public class StaffDashboardServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String staffId = (session != null) ? (String) session.getAttribute("userId") : null;
        if (staffId == null) {
            resp.sendRedirect(req.getContextPath() + "/staff-login");
            return;
        }

        String path = req.getServletPath();

        try {
            if ("/staff/dashboard".equals(path)) {
                Map<String, Integer> counts = complaintDAO.getStaffCounts(staffId);
                List<Complaint> recentComplaints = complaintDAO.findByStaffId(staffId, "ALL", null);
                if (recentComplaints.size() > 5) {
                    recentComplaints = recentComplaints.subList(0, 5);
                }
                req.setAttribute("counts", counts);
                req.setAttribute("recentComplaints", recentComplaints);
                req.getRequestDispatcher("/WEB-INF/views/staff/dashboard.jsp").forward(req, resp);
            } else {
                // /staff/assigned-complaints
                String status = req.getParameter("status");
                String search = req.getParameter("search");
                List<Complaint> complaints = complaintDAO.findByStaffId(staffId, status, search);
                req.setAttribute("complaints", complaints);
                req.setAttribute("selectedStatus", status != null ? status : "ALL");
                req.setAttribute("searchQuery", search != null ? search : "");
                req.getRequestDispatcher("/WEB-INF/views/staff/assigned-complaints.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Error fetching staff dashboard data: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/common/messages.jsp").forward(req, resp);
        }
    }
}
