package com.campuscare.servlet;

import com.campuscare.dao.AdminDAO;
import com.campuscare.model.Admin;
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

@WebServlet(name = "AdminLoginServlet", urlPatterns = {"/admin-login"})
public class AdminLoginServlet extends HttpServlet {

    private final AdminDAO adminDAO = new AdminDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && "ADMIN".equals(session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }
        CSRFUtil.getToken(req.getSession(true));
        req.getRequestDispatcher("/WEB-INF/views/auth/admin-login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        if (!CSRFUtil.isValidToken(req)) {
            req.setAttribute("errorMessage", "Security validation failed. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/auth/admin-login.jsp").forward(req, resp);
            return;
        }

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Please provide administrator username and password.");
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/views/auth/admin-login.jsp").forward(req, resp);
            return;
        }

        try {
            Admin admin = adminDAO.findByUsername(username.trim());

            if (admin == null || !PasswordUtil.checkPassword(password, admin.getPasswordHash())) {
                req.setAttribute("errorMessage", "Invalid administrator credentials.");
                req.setAttribute("username", username);
                req.getRequestDispatcher("/WEB-INF/views/auth/admin-login.jsp").forward(req, resp);
                return;
            }

            if (!admin.isActive()) {
                req.setAttribute("errorMessage", "This administrator account is disabled.");
                req.getRequestDispatcher("/WEB-INF/views/auth/admin-login.jsp").forward(req, resp);
                return;
            }

            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("user", admin);
            newSession.setAttribute("userId", admin.getAdminId());
            newSession.setAttribute("userName", admin.getUsername());
            newSession.setAttribute("role", "ADMIN");
            CSRFUtil.getToken(newSession);

            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "System error processing admin login. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/auth/admin-login.jsp").forward(req, resp);
        }
    }
}
