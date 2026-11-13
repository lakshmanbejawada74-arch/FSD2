package com.campuscare.dao;

import com.campuscare.model.Admin;
import com.campuscare.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    public boolean insert(Admin a) throws SQLException {
        String sql = "INSERT INTO admins (ADMIN_ID, USERNAME, PASSWORD_HASH, ACCOUNT_STATUS) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getAdminId().trim());
            ps.setString(2, a.getUsername().trim());
            ps.setString(3, a.getPasswordHash());
            ps.setString(4, a.getAccountStatus() != null ? a.getAccountStatus() : "ACTIVE");
            return ps.executeUpdate() > 0;
        }
    }

    public Admin findById(String adminId) throws SQLException {
        String sql = "SELECT * FROM admins WHERE ADMIN_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, adminId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Admin findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM admins WHERE LOWER(USERNAME) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public int countTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM admins";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Admin mapRow(ResultSet rs) throws SQLException {
        return new Admin(
                rs.getString("ADMIN_ID"),
                rs.getString("USERNAME"),
                rs.getString("PASSWORD_HASH"),
                rs.getString("ACCOUNT_STATUS")
        );
    }
}
