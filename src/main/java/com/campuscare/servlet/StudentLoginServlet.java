package com.campuscare.servlet;

import com.campuscare.dao.StudentDAO;
import com.campuscare.model.Student;
import com.campuscare.util.CSRFUtil;
import com.campuscare.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "StudentLoginServlet", urlPatterns = {"/student-login"})
public class StudentLoginServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && "STUDENT".equals(session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/student/dashboard");
            return;
        }
        CSRFUtil.getToken(req.getSession(true));
        req.getRequestDispatcher("/WEB-INF/views/auth/student-login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        if (!CSRFUtil.isValidToken(req)) {
            req.setAttribute("errorMessage", "Security validation failed. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/auth/student-login.jsp").forward(req, resp);
            return;
        }

        String identifier = req.getParameter("identifier"); // Email or Student ID
        String password = req.getParameter("password");

        if (identifier == null || identifier.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Please provide both Student ID / Email and password.");
            req.setAttribute("identifier", identifier);
            req.getRequestDispatcher("/WEB-INF/views/auth/student-login.jsp").forward(req, resp);
            return;
        }

        try {
            Student student = null;
            if (identifier.contains("@")) {
                student = studentDAO.findByEmail(identifier.trim());
            } else {
                student = studentDAO.findById(identifier.trim());
            }

            if (student == null || !PasswordUtil.checkPassword(password, student.getPasswordHash())) {
                req.setAttribute("errorMessage", "Invalid Student ID/Email or password.");
                req.setAttribute("identifier", identifier);
                req.getRequestDispatcher("/WEB-INF/views/auth/student-login.jsp").forward(req, resp);
                return;
            }

            if (!student.isActive()) {
                req.setAttribute("errorMessage", "Your account is currently inactive. Please contact administration.");
                req.getRequestDispatcher("/WEB-INF/views/auth/student-login.jsp").forward(req, resp);
                return;
            }

            // Authentication successful: reset session to prevent session fixation
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("user", student);
            newSession.setAttribute("userId", student.getStudentId());
            newSession.setAttribute("userName", student.getFullName());
            newSession.setAttribute("userEmail", student.getEmail());
            newSession.setAttribute("role", "STUDENT");
            CSRFUtil.getToken(newSession);

            resp.sendRedirect(req.getContextPath() + "/student/dashboard");
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "System error processing login. Please try again later.");
            req.getRequestDispatcher("/WEB-INF/views/auth/student-login.jsp").forward(req, resp);
        }
    }
}
