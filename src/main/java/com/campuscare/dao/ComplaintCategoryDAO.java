package com.campuscare.dao;

import com.campuscare.model.ComplaintCategory;
import com.campuscare.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ComplaintCategoryDAO {

    public List<ComplaintCategory> findAllActive() throws SQLException {
        List<ComplaintCategory> list = new ArrayList<>();
        String sql = "SELECT * FROM complaint_categories WHERE ACTIVE = 1 ORDER BY CATEGORY_NAME ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<ComplaintCategory> findAll() throws SQLException {
        List<ComplaintCategory> list = new ArrayList<>();
        String sql = "SELECT * FROM complaint_categories ORDER BY CATEGORY_NAME ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public ComplaintCategory findById(int categoryId) throws SQLException {
        String sql = "SELECT * FROM complaint_categories WHERE CATEGORY_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean insert(ComplaintCategory c) throws SQLException {
        String sql = "INSERT INTO complaint_categories (CATEGORY_NAME, DESCRIPTION, DEPARTMENT, ACTIVE) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCategoryName().trim());
            ps.setString(2, c.getDescription() != null ? c.getDescription().trim() : null);
            ps.setString(3, c.getDepartment().trim());
            ps.setBoolean(4, c.isActive());
            return ps.executeUpdate() > 0;
        }
    }

    private ComplaintCategory mapRow(ResultSet rs) throws SQLException {
        return new ComplaintCategory(
                rs.getInt("CATEGORY_ID"),
                rs.getString("CATEGORY_NAME"),
                rs.getString("DESCRIPTION"),
                rs.getString("DEPARTMENT"),
                rs.getBoolean("ACTIVE")
        );
    }
}
