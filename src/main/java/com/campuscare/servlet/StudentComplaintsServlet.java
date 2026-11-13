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

@WebServlet(name = "StudentComplaintsServlet", urlPatterns = {"/student/dashboard", "/student/my-complaints"})
public class StudentComplaintsServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String studentId = (session != null) ? (String) session.getAttribute("userId") : null;
        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/student-login");
            return;
        }

        String path = req.getServletPath();

        try {
            if ("/student/dashboard".equals(path)) {
                Map<String, Integer> counts = complaintDAO.getStudentCounts(studentId);
                List<Complaint> recentComplaints = complaintDAO.findByStudentId(studentId, "ALL", null);
                if (recentComplaints.size() > 5) {
                    recentComplaints = recentComplaints.subList(0, 5);
                }
                req.setAttribute("counts", counts);
                req.setAttribute("recentComplaints", recentComplaints);
                req.getRequestDispatcher("/WEB-INF/views/student/dashboard.jsp").forward(req, resp);
            } else {
                // /student/my-complaints
                String status = req.getParameter("status");
                String search = req.getParameter("search");
                List<Complaint> complaints = complaintDAO.findByStudentId(studentId, status, search);
                req.setAttribute("complaints", complaints);
                req.setAttribute("selectedStatus", status != null ? status : "ALL");
                req.setAttribute("searchQuery", search != null ? search : "");
                req.getRequestDispatcher("/WEB-INF/views/student/my-complaints.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Error fetching complaint records: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/common/messages.jsp").forward(req, resp);
        }
    }
}
