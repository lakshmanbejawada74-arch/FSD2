package com.campuscare.dao;

import com.campuscare.model.Complaint;
import com.campuscare.model.ComplaintUpdate;
import com.campuscare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplaintDAO {

    /**
     * Creates a new complaint and logs the initial PENDING update record in a transaction.
     */
    public long createComplaint(Complaint c) throws SQLException {
        String insertSql = "INSERT INTO grievances (STUDENT_ID, CATEGORY_ID, TITLE, DESCRIPTION, PRIORITY, STATUS) " +
                           "VALUES (?, ?, ?, ?, ?, 'PENDING')";
        String updateSql = "INSERT INTO grievance_updates (GRIEVANCE_ID, UPDATED_BY_ROLE, UPDATED_BY_ID, OLD_STATUS, NEW_STATUS, REMARKS) " +
                           "VALUES (?, 'STUDENT', ?, NULL, 'PENDING', 'Complaint created.')";

        Connection conn = null;
        PreparedStatement psInsert = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            psInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            psInsert.setString(1, c.getStudentId());
            psInsert.setInt(2, c.getCategoryId());
            psInsert.setString(3, c.getTitle().trim());
            psInsert.setString(4, c.getDescription().trim());
            psInsert.setString(5, c.getPriority().toUpperCase());
            psInsert.executeUpdate();

            rs = psInsert.getGeneratedKeys();
            long grievanceId;
            if (rs.next()) {
                grievanceId = rs.getLong(1);
            } else {
                conn.rollback();
                throw new SQLException("Failed to retrieve generated complaint ID.");
            }

            psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setLong(1, grievanceId);
            psUpdate.setString(2, c.getStudentId());
            psUpdate.executeUpdate();

            conn.commit();
            return grievanceId;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            DBConnection.closeQuietly(rs, psInsert, psUpdate, conn);
        }
    }

    /**
     * Updates complaint status and records the update in GRIEVANCE_UPDATES within a single transaction.
     */
    public boolean updateStatus(long grievanceId, String newStatus, String updatedByRole,
                                String updatedById, String remarks) throws SQLException {
        String selectSql = "SELECT STATUS, ASSIGNED_STAFF_ID FROM grievances WHERE GRIEVANCE_ID = ? FOR UPDATE";
        String updateGrievanceSql = "UPDATE grievances SET STATUS = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE GRIEVANCE_ID = ?";
        String insertHistorySql = "INSERT INTO grievance_updates (GRIEVANCE_ID, UPDATED_BY_ROLE, UPDATED_BY_ID, OLD_STATUS, NEW_STATUS, REMARKS) " +
                                  "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psSelect = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psHistory = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            psSelect = conn.prepareStatement(selectSql);
            psSelect.setLong(1, grievanceId);
            rs = psSelect.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return false;
            }

            String oldStatus = rs.getString("STATUS");

            // Server-side transition validation
            if (!isValidTransition(oldStatus, newStatus, updatedByRole)) {
                conn.rollback();
                throw new IllegalArgumentException("Illegal status transition from " + oldStatus + " to " + newStatus + " by role " + updatedByRole);
            }

            psUpdate = conn.prepareStatement(updateGrievanceSql);
            psUpdate.setString(1, newStatus);
            psUpdate.setLong(2, grievanceId);
            psUpdate.executeUpdate();

            psHistory = conn.prepareStatement(insertHistorySql);
            psHistory.setLong(1, grievanceId);
            psHistory.setString(2, updatedByRole);
            psHistory.setString(3, updatedById);
            psHistory.setString(4, oldStatus);
            psHistory.setString(5, newStatus);
            psHistory.setString(6, remarks != null ? remarks.trim() : null);
            psHistory.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException | IllegalArgumentException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            DBConnection.closeQuietly(rs, psSelect, psUpdate, psHistory, conn);
        }
    }

    /**
     * Assigns or reassigns staff to a complaint in a single transaction.
     */
    public boolean assignStaff(long grievanceId, String staffId, String adminId, String remarks) throws SQLException {
        String staffCheckSql = "SELECT ACCOUNT_STATUS FROM staff WHERE STAFF_ID = ?";
        String selectSql = "SELECT STATUS, ASSIGNED_STAFF_ID FROM grievances WHERE GRIEVANCE_ID = ? FOR UPDATE";
        String updateSql = "UPDATE grievances SET ASSIGNED_STAFF_ID = ?, STATUS = 'ASSIGNED', UPDATED_AT = CURRENT_TIMESTAMP WHERE GRIEVANCE_ID = ?";
        String historySql = "INSERT INTO grievance_updates (GRIEVANCE_ID, UPDATED_BY_ROLE, UPDATED_BY_ID, OLD_STATUS, NEW_STATUS, REMARKS) " +
                            "VALUES (?, 'ADMIN', ?, ?, 'ASSIGNED', ?)";

        Connection conn = null;
        PreparedStatement psStaff = null;
        PreparedStatement psSelect = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psHistory = null;
        ResultSet rsStaff = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Check staff active
            psStaff = conn.prepareStatement(staffCheckSql);
            psStaff.setString(1, staffId);
            rsStaff = psStaff.executeQuery();
            if (!rsStaff.next() || !"ACTIVE".equalsIgnoreCase(rsStaff.getString("ACCOUNT_STATUS"))) {
                conn.rollback();
                throw new IllegalArgumentException("Target staff member is not active or does not exist.");
            }

            // Get current grievance
            psSelect = conn.prepareStatement(selectSql);
            psSelect.setLong(1, grievanceId);
            rs = psSelect.executeQuery();
            if (!rs.next()) {
                conn.rollback();
                return false;
            }

            String oldStatus = rs.getString("STATUS");
            String oldStaffId = rs.getString("ASSIGNED_STAFF_ID");

            psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setString(1, staffId);
            psUpdate.setLong(2, grievanceId);
            psUpdate.executeUpdate();

            String fullRemarks = (oldStaffId == null) 
                    ? "Assigned to staff " + staffId + (remarks != null && !remarks.trim().isEmpty() ? ": " + remarks.trim() : "")
                    : "Reassigned from " + oldStaffId + " to " + staffId + (remarks != null && !remarks.trim().isEmpty() ? ": " + remarks.trim() : "");

            psHistory = conn.prepareStatement(historySql);
            psHistory.setLong(1, grievanceId);
            psHistory.setString(2, adminId);
            psHistory.setString(3, oldStatus);
            psHistory.setString(4, fullRemarks);
            psHistory.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException | IllegalArgumentException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            DBConnection.closeQuietly(rsStaff, rs, psStaff, psSelect, psUpdate, psHistory, conn);
        }
    }

    private boolean isValidTransition(String oldStatus, String newStatus, String role) {
        if (oldStatus == null || newStatus == null || role == null) return false;
        if (oldStatus.equals(newStatus)) return true; // Remarks update without status shift

        if ("STUDENT".equalsIgnoreCase(role)) {
            // Students can only withdraw their own complaint if not already resolved or rejected
            return ("PENDING".equals(oldStatus) || "ASSIGNED".equals(oldStatus)) && "WITHDRAWN".equals(newStatus);
        }

        if ("STAFF".equalsIgnoreCase(role)) {
            // Staff transitions:
            // ASSIGNED -> IN_PROGRESS
            // IN_PROGRESS -> RESOLVED
            if ("ASSIGNED".equals(oldStatus) && "IN_PROGRESS".equals(newStatus)) return true;
            if ("IN_PROGRESS".equals(oldStatus) && "RESOLVED".equals(newStatus)) return true;
            return false;
        }

        if ("ADMIN".equalsIgnoreCase(role)) {
            // Admin can assign, reject, or resolve in exceptional cases
            if ("PENDING".equals(oldStatus) && ("ASSIGNED".equals(newStatus) || "REJECTED".equals(newStatus))) return true;
            if ("ASSIGNED".equals(oldStatus) && ("IN_PROGRESS".equals(newStatus) || "REJECTED".equals(newStatus))) return true;
            if ("IN_PROGRESS".equals(oldStatus) && ("RESOLVED".equals(newStatus) || "REJECTED".equals(newStatus))) return true;
            if ("REJECTED".equals(oldStatus) && "PENDING".equals(newStatus)) return true; // Reopen
            return true;
        }

        return false;
    }

    public Complaint findById(long grievanceId) throws SQLException {
        String sql = "SELECT g.*, c.CATEGORY_NAME, c.DEPARTMENT AS CAT_DEPT, " +
                     "s.FULL_NAME AS STUDENT_NAME, s.EMAIL AS STUDENT_EMAIL, s.MOBILE AS STUDENT_MOBILE, " +
                     "s.DEPARTMENT AS STUDENT_DEPT, s.STUDENT_TYPE, s.HOSTEL_NAME, s.ROOM_NUMBER, " +
                     "st.FULL_NAME AS STAFF_NAME, st.EMAIL AS STAFF_EMAIL " +
                     "FROM grievances g " +
                     "JOIN complaint_categories c ON g.CATEGORY_ID = c.CATEGORY_ID " +
                     "JOIN students s ON g.STUDENT_ID = s.STUDENT_ID " +
                     "LEFT JOIN staff st ON g.ASSIGNED_STAFF_ID = st.STAFF_ID " +
                     "WHERE g.GRIEVANCE_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, grievanceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFullComplaint(rs);
                }
            }
        }
        return null;
    }

    public List<Complaint> findByStudentId(String studentId, String statusFilter, String search) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT g.*, c.CATEGORY_NAME, c.DEPARTMENT AS CAT_DEPT, " +
                "s.FULL_NAME AS STUDENT_NAME, s.EMAIL AS STUDENT_EMAIL, s.MOBILE AS STUDENT_MOBILE, " +
                "s.DEPARTMENT AS STUDENT_DEPT, s.STUDENT_TYPE, s.HOSTEL_NAME, s.ROOM_NUMBER, " +
                "st.FULL_NAME AS STAFF_NAME, st.EMAIL AS STAFF_EMAIL " +
                "FROM grievances g " +
                "JOIN complaint_categories c ON g.CATEGORY_ID = c.CATEGORY_ID " +
                "JOIN students s ON g.STUDENT_ID = s.STUDENT_ID " +
                "LEFT JOIN staff st ON g.ASSIGNED_STAFF_ID = st.STAFF_ID " +
                "WHERE g.STUDENT_ID = ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(studentId);

        if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
            sql.append("AND g.STATUS = ? ");
            params.add(statusFilter.trim().toUpperCase());
        }

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (g.TITLE LIKE ? OR g.DESCRIPTION LIKE ? OR c.CATEGORY_NAME LIKE ? OR g.GRIEVANCE_ID LIKE ?) ");
            String term = "%" + search.trim() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
            params.add(term);
        }

        sql.append("ORDER BY g.CREATED_AT DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFullComplaint(rs));
                }
            }
        }
        return list;
    }

    public List<Complaint> findByStaffId(String staffId, String statusFilter, String search) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT g.*, c.CATEGORY_NAME, c.DEPARTMENT AS CAT_DEPT, " +
                "s.FULL_NAME AS STUDENT_NAME, s.EMAIL AS STUDENT_EMAIL, s.MOBILE AS STUDENT_MOBILE, " +
                "s.DEPARTMENT AS STUDENT_DEPT, s.STUDENT_TYPE, s.HOSTEL_NAME, s.ROOM_NUMBER, " +
                "st.FULL_NAME AS STAFF_NAME, st.EMAIL AS STAFF_EMAIL " +
                "FROM grievances g " +
                "JOIN complaint_categories c ON g.CATEGORY_ID = c.CATEGORY_ID " +
                "JOIN students s ON g.STUDENT_ID = s.STUDENT_ID " +
                "JOIN staff st ON g.ASSIGNED_STAFF_ID = st.STAFF_ID " +
                "WHERE g.ASSIGNED_STAFF_ID = ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(staffId);

        if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
            sql.append("AND g.STATUS = ? ");
            params.add(statusFilter.trim().toUpperCase());
        }

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (g.TITLE LIKE ? OR g.DESCRIPTION LIKE ? OR c.CATEGORY_NAME LIKE ? OR s.FULL_NAME LIKE ? OR g.GRIEVANCE_ID LIKE ?) ");
            String term = "%" + search.trim() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
            params.add(term);
            params.add(term);
        }

        sql.append("ORDER BY g.UPDATED_AT DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFullComplaint(rs));
                }
            }
        }
        return list;
    }

    public List<Complaint> findAllFiltered(String complaintNumberOrId, String studentQuery,
                                          String department, Integer categoryId,
                                          String priority, String staffId,
                                          String status, String dateFrom, String dateTo) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT g.*, c.CATEGORY_NAME, c.DEPARTMENT AS CAT_DEPT, " +
                "s.FULL_NAME AS STUDENT_NAME, s.EMAIL AS STUDENT_EMAIL, s.MOBILE AS STUDENT_MOBILE, " +
                "s.DEPARTMENT AS STUDENT_DEPT, s.STUDENT_TYPE, s.HOSTEL_NAME, s.ROOM_NUMBER, " +
                "st.FULL_NAME AS STAFF_NAME, st.EMAIL AS STAFF_EMAIL " +
                "FROM grievances g " +
                "JOIN complaint_categories c ON g.CATEGORY_ID = c.CATEGORY_ID " +
                "JOIN students s ON g.STUDENT_ID = s.STUDENT_ID " +
                "LEFT JOIN staff st ON g.ASSIGNED_STAFF_ID = st.STAFF_ID " +
                "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (complaintNumberOrId != null && !complaintNumberOrId.trim().isEmpty()) {
            String q = complaintNumberOrId.trim();
            // Allow searching by GC1001 or 1001 or title keyword
            if (q.toUpperCase().startsWith("GC")) {
                q = q.substring(2);
            }
            try {
                long numId = Long.parseLong(q);
                sql.append("AND (g.GRIEVANCE_ID = ? OR g.TITLE LIKE ?) ");
                params.add(numId);
                params.add("%" + complaintNumberOrId.trim() + "%");
            } catch (NumberFormatException e) {
                sql.append("AND (g.TITLE LIKE ? OR g.DESCRIPTION LIKE ?) ");
                params.add("%" + q + "%");
                params.add("%" + q + "%");
            }
        }

        if (studentQuery != null && !studentQuery.trim().isEmpty()) {
            sql.append("AND (s.STUDENT_ID LIKE ? OR s.FULL_NAME LIKE ? OR s.EMAIL LIKE ?) ");
            String st = "%" + studentQuery.trim() + "%";
            params.add(st);
            params.add(st);
            params.add(st);
        }

        if (department != null && !department.trim().isEmpty() && !"ALL".equalsIgnoreCase(department)) {
            sql.append("AND (s.DEPARTMENT = ? OR c.DEPARTMENT = ?) ");
            params.add(department.trim());
            params.add(department.trim());
        }

        if (categoryId != null && categoryId > 0) {
            sql.append("AND g.CATEGORY_ID = ? ");
            params.add(categoryId);
        }

        if (priority != null && !priority.trim().isEmpty() && !"ALL".equalsIgnoreCase(priority)) {
            sql.append("AND g.PRIORITY = ? ");
            params.add(priority.trim().toUpperCase());
        }

        if (staffId != null && !staffId.trim().isEmpty() && !"ALL".equalsIgnoreCase(staffId)) {
            if ("UNASSIGNED".equalsIgnoreCase(staffId)) {
                sql.append("AND g.ASSIGNED_STAFF_ID IS NULL ");
            } else {
                sql.append("AND g.ASSIGNED_STAFF_ID = ? ");
                params.add(staffId.trim());
            }
        }

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND g.STATUS = ? ");
            params.add(status.trim().toUpperCase());
        }

        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            sql.append("AND g.CREATED_AT >= ? ");
            params.add(dateFrom.trim() + " 00:00:00");
        }

        if (dateTo != null && !dateTo.trim().isEmpty()) {
            sql.append("AND g.CREATED_AT <= ? ");
            params.add(dateTo.trim() + " 23:59:59");
        }

        sql.append("ORDER BY g.CREATED_AT DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFullComplaint(rs));
                }
            }
        }
        return list;
    }

    public List<ComplaintUpdate> findUpdatesByGrievanceId(long grievanceId) throws SQLException {
        List<ComplaintUpdate> list = new ArrayList<>();
        String sql = "SELECT u.*, " +
                     "CASE " +
                     "  WHEN u.UPDATED_BY_ROLE = 'STUDENT' THEN (SELECT FULL_NAME FROM students WHERE STUDENT_ID = u.UPDATED_BY_ID) " +
                     "  WHEN u.UPDATED_BY_ROLE = 'STAFF' THEN (SELECT FULL_NAME FROM staff WHERE STAFF_ID = u.UPDATED_BY_ID) " +
                     "  WHEN u.UPDATED_BY_ROLE = 'ADMIN' THEN (SELECT USERNAME FROM admins WHERE ADMIN_ID = u.UPDATED_BY_ID) " +
                     "  ELSE u.UPDATED_BY_ID " +
                     "END AS UPDATER_NAME " +
                     "FROM grievance_updates u " +
                     "WHERE u.GRIEVANCE_ID = ? " +
                     "ORDER BY u.UPDATED_AT ASC, u.UPDATE_ID ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, grievanceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ComplaintUpdate cu = new ComplaintUpdate(
                            rs.getLong("UPDATE_ID"),
                            rs.getLong("GRIEVANCE_ID"),
                            rs.getString("UPDATED_BY_ROLE"),
                            rs.getString("UPDATED_BY_ID"),
                            rs.getString("OLD_STATUS"),
                            rs.getString("NEW_STATUS"),
                            rs.getString("REMARKS"),
                            rs.getTimestamp("UPDATED_AT")
                    );
                    cu.setUpdaterName(rs.getString("UPDATER_NAME"));
                    list.add(cu);
                }
            }
        }
        return list;
    }

    public Map<String, Integer> getOverallCounts() throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT STATUS, COUNT(*) AS CNT FROM grievances GROUP BY STATUS";
        int total = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String st = rs.getString("STATUS");
                int count = rs.getInt("CNT");
                map.put(st, count);
                total += count;
            }
        }
        map.put("TOTAL", total);
        return map;
    }

    public Map<String, Integer> getStudentCounts(String studentId) throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT STATUS, COUNT(*) AS CNT FROM grievances WHERE STUDENT_ID = ? GROUP BY STATUS";
        int total = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String st = rs.getString("STATUS");
                    int count = rs.getInt("CNT");
                    map.put(st, count);
                    total += count;
                }
            }
        }
        map.put("TOTAL", total);
        return map;
    }

    public Map<String, Integer> getStaffCounts(String staffId) throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT STATUS, COUNT(*) AS CNT FROM grievances WHERE ASSIGNED_STAFF_ID = ? GROUP BY STATUS";
        int total = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String st = rs.getString("STATUS");
                    int count = rs.getInt("CNT");
                    map.put(st, count);
                    total += count;
                }
            }
        }
        map.put("TOTAL", total);
        return map;
    }

    private Complaint mapFullComplaint(ResultSet rs) throws SQLException {
        Complaint c = new Complaint(
                rs.getLong("GRIEVANCE_ID"),
                rs.getString("STUDENT_ID"),
                rs.getInt("CATEGORY_ID"),
                rs.getString("TITLE"),
                rs.getString("DESCRIPTION"),
                rs.getString("PRIORITY"),
                rs.getString("STATUS"),
                rs.getString("ASSIGNED_STAFF_ID"),
                rs.getTimestamp("CREATED_AT"),
                rs.getTimestamp("UPDATED_AT")
        );
        c.setCategoryName(rs.getString("CATEGORY_NAME"));
        c.setCategoryDepartment(rs.getString("CAT_DEPT"));
        c.setStudentName(rs.getString("STUDENT_NAME"));
        c.setStudentEmail(rs.getString("STUDENT_EMAIL"));
        c.setStudentMobile(rs.getString("STUDENT_MOBILE"));
        c.setStudentDepartment(rs.getString("STUDENT_DEPT"));
        c.setStudentType(rs.getString("STUDENT_TYPE"));
        c.setHostelName(rs.getString("HOSTEL_NAME"));
        c.setRoomNumber(rs.getString("ROOM_NUMBER"));
        c.setAssignedStaffName(rs.getString("STAFF_NAME"));
        c.setAssignedStaffEmail(rs.getString("STAFF_EMAIL"));
        return c;
    }
}
