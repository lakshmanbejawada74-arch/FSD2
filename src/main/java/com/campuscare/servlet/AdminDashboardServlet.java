package com.campuscare.servlet;

import com.campuscare.dao.ComplaintCategoryDAO;
import com.campuscare.dao.ComplaintDAO;
import com.campuscare.dao.StaffDAO;
import com.campuscare.dao.StudentDAO;
import com.campuscare.model.Complaint;
import com.campuscare.model.ComplaintCategory;
import com.campuscare.model.Staff;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard", "/admin/all-complaints"})
public class AdminDashboardServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final StaffDAO staffDAO = new StaffDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final ComplaintCategoryDAO categoryDAO = new ComplaintCategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        try {
            if ("/admin/dashboard".equals(path)) {
                Map<String, Integer> counts = complaintDAO.getOverallCounts();
                int totalStudents = studentDAO.countTotal();
                int totalStaff = staffDAO.countTotal();
                List<Map<String, Object>> staffWorkload = staffDAO.getStaffWorkload();
                List<Complaint> recentComplaints = complaintDAO.findAllFiltered(null, null, null, null, null, null, null, null, null);
                if (recentComplaints.size() > 8) {
                    recentComplaints = recentComplaints.subList(0, 8);
                }

                req.setAttribute("counts", counts);
                req.setAttribute("totalStudents", totalStudents);
                req.setAttribute("totalStaff", totalStaff);
                req.setAttribute("staffWorkload", staffWorkload);
                req.setAttribute("recentComplaints", recentComplaints);
                req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
            } else {
                // /admin/all-complaints
                String complaintNum = req.getParameter("complaintNum");
                String student = req.getParameter("student");
                String department = req.getParameter("department");
                String catParam = req.getParameter("categoryId");
                Integer categoryId = null;
                if (catParam != null && !catParam.trim().isEmpty() && !"ALL".equalsIgnoreCase(catParam)) {
                    try { categoryId = Integer.parseInt(catParam.trim()); } catch (NumberFormatException ignored) {}
                }
                String priority = req.getParameter("priority");
                String staffId = req.getParameter("staffId");
                String status = req.getParameter("status");
                String dateFrom = req.getParameter("dateFrom");
                String dateTo = req.getParameter("dateTo");

                List<Complaint> complaints = complaintDAO.findAllFiltered(
                        complaintNum, student, department, categoryId, priority, staffId, status, dateFrom, dateTo
                );

                List<ComplaintCategory> categories = categoryDAO.findAll();
                List<Staff> staffList = staffDAO.findAll();

                req.setAttribute("complaints", complaints);
                req.setAttribute("categories", categories);
                req.setAttribute("staffList", staffList);

                // Preserve filter choices
                req.setAttribute("filterComplaintNum", complaintNum);
                req.setAttribute("filterStudent", student);
                req.setAttribute("filterDepartment", department);
                req.setAttribute("filterCategoryId", catParam);
                req.setAttribute("filterPriority", priority);
                req.setAttribute("filterStaffId", staffId);
                req.setAttribute("filterStatus", status);
                req.setAttribute("filterDateFrom", dateFrom);
                req.setAttribute("filterDateTo", dateTo);

                req.getRequestDispatcher("/WEB-INF/views/admin/all-complaints.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Error loading administrative data: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/common/messages.jsp").forward(req, resp);
        }
    }
}
