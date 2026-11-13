package com.campuscare.servlet;

import com.campuscare.dao.StaffDAO;
import com.campuscare.model.Staff;
import com.campuscare.util.PasswordUtil;
import com.campuscare.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "StaffManagementServlet", urlPatterns = {"/admin/manage-staff"})
public class StaffManagementServlet extends HttpServlet {

    private final StaffDAO staffDAO = new StaffDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Staff> staffList = staffDAO.findAll();
            req.setAttribute("staffList", staffList);
            req.getRequestDispatcher("/WEB-INF/views/admin/manage-staff.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Error loading staff directory: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/common/messages.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        if ("toggleStatus".equalsIgnoreCase(action)) {
            String staffId = req.getParameter("staffId");
            String status = req.getParameter("status");
            if (staffId != null && status != null) {
                try {
                    if ("ACTIVE".equalsIgnoreCase(status) || "INACTIVE".equalsIgnoreCase(status)) {
                        staffDAO.updateStatus(staffId.trim(), status.trim().toUpperCase());
                        resp.sendRedirect(req.getContextPath() + "/admin/manage-staff?success=Staff " + staffId + " status updated to " + status + ".");
                        return;
                    }
                } catch (SQLException e) {
                    resp.sendRedirect(req.getContextPath() + "/admin/manage-staff?error=Database error updating staff status: " + e.getMessage());
                    return;
                }
            }
        } else if ("create".equalsIgnoreCase(action)) {
            String staffId = req.getParameter("staffId");
            String fullName = req.getParameter("fullName");
            String department = req.getParameter("department");
            String email = req.getParameter("email");
            String mobile = req.getParameter("mobile");
            String password = req.getParameter("password");

            List<String> errors = new ArrayList<>();

            if (!ValidationUtil.isValidId(staffId)) {
                errors.add("Staff ID must be 3-30 alphanumeric characters.");
            } else {
                try {
                    if (staffDAO.isIdExists(staffId.trim())) {
                        errors.add("Staff ID already exists.");
                    }
                } catch (SQLException e) {
                    errors.add("Database error validating Staff ID.");
                }
            }

            if (!ValidationUtil.isValidName(fullName)) {
                errors.add("Full name is mandatory (2 to 100 characters).");
            }

            if (!ValidationUtil.isValidDepartment(department)) {
                errors.add("Department is mandatory (up to 80 characters).");
            }

            if (!ValidationUtil.isValidEmail(email)) {
                errors.add("Valid email is mandatory.");
            } else {
                try {
                    if (staffDAO.isEmailExists(email.trim())) {
                        errors.add("Email address is already in use by another staff member.");
                    }
                } catch (SQLException e) {
                    errors.add("Database error validating email.");
                }
            }

            if (!ValidationUtil.isValidMobile(mobile)) {
                errors.add("Valid mobile number (10 to 15 digits) is required.");
            }

            if (!PasswordUtil.isPasswordComplex(password)) {
                errors.add("Password must contain at least 8 characters, with uppercase, lowercase, number, and special character.");
            }

            if (!errors.isEmpty()) {
                try {
                    req.setAttribute("staffList", staffDAO.findAll());
                } catch (SQLException ignored) {}
                req.setAttribute("errors", errors);
                req.setAttribute("formStaffId", staffId);
                req.setAttribute("formFullName", fullName);
                req.setAttribute("formDepartment", department);
                req.setAttribute("formEmail", email);
                req.setAttribute("formMobile", mobile);
                req.getRequestDispatcher("/WEB-INF/views/admin/manage-staff.jsp").forward(req, resp);
                return;
            }

            try {
                String hash = PasswordUtil.hashPassword(password);
                Staff s = new Staff();
                s.setStaffId(staffId.trim());
                s.setFullName(fullName.trim());
                s.setDepartment(department.trim());
                s.setEmail(email.trim().toLowerCase());
                s.setMobile(mobile.trim());
                s.setPasswordHash(hash);
                s.setAccountStatus("ACTIVE");

                staffDAO.insert(s);
                resp.sendRedirect(req.getContextPath() + "/admin/manage-staff?success=Staff member " + s.getFullName() + " created successfully.");
                return;
            } catch (SQLException e) {
                resp.sendRedirect(req.getContextPath() + "/admin/manage-staff?error=Database error creating staff: " + e.getMessage());
                return;
            }
        }

        resp.sendRedirect(req.getContextPath() + "/admin/manage-staff");
    }
}
