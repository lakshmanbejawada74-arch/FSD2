package com.campuscare.servlet;

import com.campuscare.dao.StudentDAO;
import com.campuscare.model.Student;
import com.campuscare.util.CSRFUtil;
import com.campuscare.util.PasswordUtil;
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

@WebServlet(name = "StudentRegisterServlet", urlPatterns = {"/student-register"})
public class StudentRegisterServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        CSRFUtil.getToken(session);
        req.getRequestDispatcher("/WEB-INF/views/auth/student-register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(true);

        // Validate CSRF token
        if (!CSRFUtil.isValidToken(req)) {
            req.setAttribute("errorMessage", "Security validation failed. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/auth/student-register.jsp").forward(req, resp);
            return;
        }

        String studentId = req.getParameter("studentId");
        String fullName = req.getParameter("fullName");
        String department = req.getParameter("department");
        String studyYearStr = req.getParameter("studyYear");
        String studentType = req.getParameter("studentType");
        String hostelName = req.getParameter("hostelName");
        String roomNumber = req.getParameter("roomNumber");
        String email = req.getParameter("email");
        String mobile = req.getParameter("mobile");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        List<String> errors = new ArrayList<>();

        if (!ValidationUtil.isValidId(studentId)) {
            errors.add("Student ID must be 3-30 alphanumeric characters (letters, numbers, hyphens, underscores).");
        } else {
            try {
                if (studentDAO.isIdExists(studentId.trim())) {
                    errors.add("Student ID is already registered.");
                }
            } catch (SQLException e) {
                errors.add("Database error checking Student ID uniqueness.");
            }
        }

        if (!ValidationUtil.isValidName(fullName)) {
            errors.add("Full Name is mandatory (2 to 100 characters).");
        }

        if (!ValidationUtil.isValidDepartment(department)) {
            errors.add("Department is mandatory (up to 80 characters).");
        }

        int studyYear = 0;
        try {
            studyYear = Integer.parseInt(studyYearStr);
            if (!ValidationUtil.isValidStudyYear(studyYear)) {
                errors.add("Study year must be between 1 and 6.");
            }
        } catch (NumberFormatException e) {
            errors.add("Study year must be a valid number between 1 and 6.");
        }

        if (!ValidationUtil.isValidStudentType(studentType)) {
            errors.add("Student type must be either 'HOSTEL' or 'DAY_SCHOLAR'.");
        } else if ("HOSTEL".equalsIgnoreCase(studentType)) {
            if (hostelName == null || hostelName.trim().isEmpty() || hostelName.trim().length() > 100) {
                errors.add("Hostel Name is required for hostel students (up to 100 characters).");
            }
            if (roomNumber == null || roomNumber.trim().isEmpty() || roomNumber.trim().length() > 20) {
                errors.add("Room Number is required for hostel students (up to 20 characters).");
            }
        } else {
            // DAY_SCHOLAR must have null hostel fields
            hostelName = null;
            roomNumber = null;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            errors.add("Please provide a valid email address (up to 150 characters).");
        } else {
            try {
                if (studentDAO.isEmailExists(email.trim())) {
                    errors.add("An account with this email address already exists.");
                }
            } catch (SQLException e) {
                errors.add("Database error checking email uniqueness.");
            }
        }

        if (!ValidationUtil.isValidMobile(mobile)) {
            errors.add("Mobile number must be 10 to 15 digits (optional leading +).");
        }

        if (!PasswordUtil.isPasswordComplex(password)) {
            errors.add("Password must contain at least 8 characters, including at least one uppercase letter, one lowercase letter, one number, and one special character.");
        } else if (confirmPassword == null || !confirmPassword.equals(password)) {
            errors.add("Password confirmation does not match.");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            // Retain form inputs (do not echo password)
            req.setAttribute("studentId", studentId);
            req.setAttribute("fullName", fullName);
            req.setAttribute("department", department);
            req.setAttribute("studyYear", studyYearStr);
            req.setAttribute("studentType", studentType);
            req.setAttribute("hostelName", hostelName);
            req.setAttribute("roomNumber", roomNumber);
            req.setAttribute("email", email);
            req.setAttribute("mobile", mobile);
            req.getRequestDispatcher("/WEB-INF/views/auth/student-register.jsp").forward(req, resp);
            return;
        }

        // Hash password and save student
        try {
            String hash = PasswordUtil.hashPassword(password);
            Student s = new Student();
            s.setStudentId(studentId.trim());
            s.setFullName(fullName.trim());
            s.setDepartment(department.trim());
            s.setStudyYear(studyYear);
            s.setStudentType(studentType.trim().toUpperCase());
            s.setHostelName(hostelName != null ? hostelName.trim() : null);
            s.setRoomNumber(roomNumber != null ? roomNumber.trim() : null);
            s.setEmail(email.trim().toLowerCase());
            s.setMobile(mobile.trim());
            s.setPasswordHash(hash);
            s.setAccountStatus("ACTIVE");

            studentDAO.insert(s);
            resp.sendRedirect(req.getContextPath() + "/student-login?success=Account created successfully! Please sign in.");
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Registration failed due to a database error. Please verify your details.");
            req.getRequestDispatcher("/WEB-INF/views/auth/student-register.jsp").forward(req, resp);
        }
    }
}
