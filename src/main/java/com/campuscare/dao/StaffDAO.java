package com.campuscare.dao;

import com.campuscare.model.Staff;
import com.campuscare.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffDAO {

    public boolean insert(Staff s) throws SQLException {
        String sql = "INSERT INTO staff (STAFF_ID, FULL_NAME, DEPARTMENT, EMAIL, MOBILE, PASSWORD_HASH, ACCOUNT_STATUS) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStaffId().trim());
            ps.setString(2, s.getFullName().trim());
            ps.setString(3, s.getDepartment().trim());
            ps.setString(4, s.getEmail().trim().toLowerCase());
            ps.setString(5, s.getMobile().trim());
            ps.setString(6, s.getPasswordHash());
            ps.setString(7, s.getAccountStatus() != null ? s.getAccountStatus() : "ACTIVE");
            return ps.executeUpdate() > 0;
        }
    }

    public Staff findById(String staffId) throws SQLException {
        String sql = "SELECT * FROM staff WHERE STAFF_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, staffId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Staff findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM staff WHERE LOWER(EMAIL) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean isIdExists(String staffId) throws SQLException {
        String sql = "SELECT 1 FROM staff WHERE STAFF_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, staffId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isEmailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM staff WHERE LOWER(EMAIL) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Staff> findAll() throws SQLException {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM staff ORDER BY FULL_NAME ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Staff> findAllActive() throws SQLException {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM staff WHERE ACCOUNT_STATUS = 'ACTIVE' ORDER BY FULL_NAME ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean updateStatus(String staffId, String status) throws SQLException {
        String sql = "UPDATE staff SET ACCOUNT_STATUS = ? WHERE STAFF_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, staffId);
            return ps.executeUpdate() > 0;
        }
    }

    public int countTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM staff";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Map<String, Object>> getStaffWorkload() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT s.STAFF_ID, s.FULL_NAME, s.DEPARTMENT, s.ACCOUNT_STATUS, " +
                     "COUNT(g.GRIEVANCE_ID) AS TOTAL_ASSIGNED, " +
                     "SUM(CASE WHEN g.STATUS = 'ASSIGNED' THEN 1 ELSE 0 END) AS STATUS_ASSIGNED, " +
                     "SUM(CASE WHEN g.STATUS = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS STATUS_IN_PROGRESS, " +
                     "SUM(CASE WHEN g.STATUS = 'RESOLVED' THEN 1 ELSE 0 END) AS STATUS_RESOLVED " +
                     "FROM staff s " +
                     "LEFT JOIN grievances g ON s.STAFF_ID = g.ASSIGNED_STAFF_ID " +
                     "GROUP BY s.STAFF_ID, s.FULL_NAME, s.DEPARTMENT, s.ACCOUNT_STATUS " +
                     "ORDER BY TOTAL_ASSIGNED DESC, s.FULL_NAME ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("staffId", rs.getString("STAFF_ID"));
                map.put("fullName", rs.getString("FULL_NAME"));
                map.put("department", rs.getString("DEPARTMENT"));
                map.put("accountStatus", rs.getString("ACCOUNT_STATUS"));
                map.put("totalAssigned", rs.getInt("TOTAL_ASSIGNED"));
                map.put("assignedCount", rs.getInt("STATUS_ASSIGNED"));
                map.put("inProgressCount", rs.getInt("STATUS_IN_PROGRESS"));
                map.put("resolvedCount", rs.getInt("STATUS_RESOLVED"));
                list.add(map);
            }
        }
        return list;
    }

    private Staff mapRow(ResultSet rs) throws SQLException {
        return new Staff(
                rs.getString("STAFF_ID"),
                rs.getString("FULL_NAME"),
                rs.getString("DEPARTMENT"),
                rs.getString("EMAIL"),
                rs.getString("MOBILE"),
                rs.getString("PASSWORD_HASH"),
                rs.getString("ACCOUNT_STATUS"),
                rs.getTimestamp("CREATED_AT")
        );
    }
}
