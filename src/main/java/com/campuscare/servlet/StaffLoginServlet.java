package com.campuscare.servlet;

import com.campuscare.dao.StaffDAO;
import com.campuscare.model.Staff;
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

@WebServlet(name = "StaffLoginServlet", urlPatterns = {"/staff-login"})
public class StaffLoginServlet extends HttpServlet {

    private final StaffDAO staffDAO = new StaffDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && "STAFF".equals(session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/staff/dashboard");
            return;
        }
        CSRFUtil.getToken(req.getSession(true));
        req.getRequestDispatcher("/WEB-INF/views/auth/staff-login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        if (!CSRFUtil.isValidToken(req)) {
            req.setAttribute("errorMessage", "Security validation failed. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/auth/staff-login.jsp").forward(req, resp);
            return;
        }

        String identifier = req.getParameter("identifier"); // Staff ID or Email
        String password = req.getParameter("password");

        if (identifier == null || identifier.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Please provide Staff ID / Email and password.");
            req.setAttribute("identifier", identifier);
            req.getRequestDispatcher("/WEB-INF/views/auth/staff-login.jsp").forward(req, resp);
            return;
        }

        try {
            Staff staff = null;
            if (identifier.contains("@")) {
                staff = staffDAO.findByEmail(identifier.trim());
            } else {
                staff = staffDAO.findById(identifier.trim());
            }

            if (staff == null || !PasswordUtil.checkPassword(password, staff.getPasswordHash())) {
                req.setAttribute("errorMessage", "Invalid Staff ID/Email or password.");
                req.setAttribute("identifier", identifier);
                req.getRequestDispatcher("/WEB-INF/views/auth/staff-login.jsp").forward(req, resp);
                return;
            }

            if (!staff.isActive()) {
                req.setAttribute("errorMessage", "Your staff account has been deactivated. Please contact administrator.");
                req.getRequestDispatcher("/WEB-INF/views/auth/staff-login.jsp").forward(req, resp);
                return;
            }

            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("user", staff);
            newSession.setAttribute("userId", staff.getStaffId());
            newSession.setAttribute("userName", staff.getFullName());
            newSession.setAttribute("userEmail", staff.getEmail());
            newSession.setAttribute("department", staff.getDepartment());
            newSession.setAttribute("role", "STAFF");
            CSRFUtil.getToken(newSession);

            resp.sendRedirect(req.getContextPath() + "/staff/dashboard");
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "System error processing staff login. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/auth/staff-login.jsp").forward(req, resp);
        }
    }
}
