package com.campuscare.servlet;

import com.campuscare.dao.ComplaintCategoryDAO;
import com.campuscare.dao.ComplaintDAO;
import com.campuscare.model.Complaint;
import com.campuscare.model.ComplaintCategory;
import com.campuscare.model.Student;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ComplaintCreateServlet", urlPatterns = {"/student/new-complaint", "/student/complaint-create"})
public class ComplaintCreateServlet extends HttpServlet {

    private final ComplaintCategoryDAO categoryDAO = new ComplaintCategoryDAO();
    private final ComplaintDAO complaintDAO = new ComplaintDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<ComplaintCategory> categories = categoryDAO.findAllActive();
            req.setAttribute("categories", categories);
            req.getRequestDispatcher("/WEB-INF/views/student/new-complaint.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Error loading categories. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/student/dashboard.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);

        // Derive student identity strictly from the authenticated session
        String studentId = (session != null) ? (String) session.getAttribute("userId") : null;
        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/student-login");
            return;
        }

        String categoryIdStr = req.getParameter("categoryId");
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String priority = req.getParameter("priority");

        List<String> errors = new ArrayList<>();

        int categoryId = 0;
        try {
            categoryId = Integer.parseInt(categoryIdStr);
            ComplaintCategory cat = categoryDAO.findById(categoryId);
            if (cat == null || !cat.isActive()) {
                errors.add("Please select a valid, active complaint category.");
            }
        } catch (Exception e) {
            errors.add("Please select a valid complaint category.");
        }

        if (!ValidationUtil.isValidComplaintTitle(title)) {
            errors.add("Title is mandatory and must be between 5 and 100 characters.");
        }

        if (!ValidationUtil.isValidComplaintDescription(description)) {
            errors.add("Description is mandatory and must be between 10 and 1000 characters.");
        }

        if (!ValidationUtil.isValidPriority(priority)) {
            errors.add("Priority must be LOW, MEDIUM, or HIGH.");
        }

        if (!errors.isEmpty()) {
            try {
                req.setAttribute("categories", categoryDAO.findAllActive());
            } catch (SQLException ignored) {}
            req.setAttribute("errors", errors);
            req.setAttribute("categoryId", categoryIdStr);
            req.setAttribute("title", title);
            req.setAttribute("description", description);
            req.setAttribute("priority", priority);
            req.getRequestDispatcher("/WEB-INF/views/student/new-complaint.jsp").forward(req, resp);
            return;
        }

        try {
            Complaint c = new Complaint();
            c.setStudentId(studentId);
            c.setCategoryId(categoryId);
            c.setTitle(title.trim());
            c.setDescription(description.trim());
            c.setPriority(priority.trim().toUpperCase());

            long grievanceId = complaintDAO.createComplaint(c);
            resp.sendRedirect(req.getContextPath() + "/student/complaint-details?id=" + grievanceId + "&success=Complaint GC" + grievanceId + " submitted successfully.");
        } catch (SQLException e) {
            try {
                req.setAttribute("categories", categoryDAO.findAllActive());
            } catch (SQLException ignored) {}
            req.setAttribute("errorMessage", "Failed to register complaint due to database error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/student/new-complaint.jsp").forward(req, resp);
        }
    }
}
