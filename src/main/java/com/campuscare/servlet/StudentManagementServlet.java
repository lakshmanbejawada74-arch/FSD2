package com.campuscare.servlet;

import com.campuscare.dao.StudentDAO;
import com.campuscare.model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "StudentManagementServlet", urlPatterns = {"/admin/manage-students"})
public class StudentManagementServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Student> students = studentDAO.findAll();
            req.setAttribute("students", students);
            req.getRequestDispatcher("/WEB-INF/views/admin/manage-students.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Error loading students list: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/common/messages.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String studentId = req.getParameter("studentId");
        String status = req.getParameter("status");

        if ("toggleStatus".equalsIgnoreCase(action) && studentId != null && status != null) {
            try {
                if ("ACTIVE".equalsIgnoreCase(status) || "INACTIVE".equalsIgnoreCase(status)) {
                    studentDAO.updateStatus(studentId.trim(), status.trim().toUpperCase());
                    resp.sendRedirect(req.getContextPath() + "/admin/manage-students?success=Student " + studentId + " account status changed to " + status + ".");
                    return;
                }
            } catch (SQLException e) {
                resp.sendRedirect(req.getContextPath() + "/admin/manage-students?error=Failed to update status: " + e.getMessage());
                return;
            }
        }
        resp.sendRedirect(req.getContextPath() + "/admin/manage-students");
    }
}
