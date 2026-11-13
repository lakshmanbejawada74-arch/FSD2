package com.campuscare.dao;

import com.campuscare.model.Student;
import com.campuscare.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public boolean insert(Student s) throws SQLException {
        String sql = "INSERT INTO students (STUDENT_ID, FULL_NAME, DEPARTMENT, STUDY_YEAR, " +
                     "STUDENT_TYPE, HOSTEL_NAME, ROOM_NUMBER, EMAIL, MOBILE, PASSWORD_HASH, ACCOUNT_STATUS) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStudentId().trim());
            ps.setString(2, s.getFullName().trim());
            ps.setString(3, s.getDepartment().trim());
            ps.setInt(4, s.getStudyYear());
            ps.setString(5, s.getStudentType().trim());

            if ("HOSTEL".equalsIgnoreCase(s.getStudentType())) {
                ps.setString(6, s.getHostelName() != null ? s.getHostelName().trim() : null);
                ps.setString(7, s.getRoomNumber() != null ? s.getRoomNumber().trim() : null);
            } else {
                // Must be null for DAY_SCHOLAR per DB constraint
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.VARCHAR);
            }

            ps.setString(8, s.getEmail().trim().toLowerCase());
            ps.setString(9, s.getMobile().trim());
            ps.setString(10, s.getPasswordHash());
            ps.setString(11, s.getAccountStatus() != null ? s.getAccountStatus() : "ACTIVE");

            return ps.executeUpdate() > 0;
        }
    }

    public Student findById(String studentId) throws SQLException {
        String sql = "SELECT * FROM students WHERE STUDENT_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Student findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM students WHERE LOWER(EMAIL) = ?";
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

    public boolean isIdExists(String studentId) throws SQLException {
        String sql = "SELECT 1 FROM students WHERE STUDENT_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isEmailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM students WHERE LOWER(EMAIL) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Student> findAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY CREATED_AT DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean updateStatus(String studentId, String status) throws SQLException {
        String sql = "UPDATE students SET ACCOUNT_STATUS = ? WHERE STUDENT_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    public int countTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("STUDENT_ID"),
                rs.getString("FULL_NAME"),
                rs.getString("DEPARTMENT"),
                rs.getInt("STUDY_YEAR"),
                rs.getString("STUDENT_TYPE"),
                rs.getString("HOSTEL_NAME"),
                rs.getString("ROOM_NUMBER"),
                rs.getString("EMAIL"),
                rs.getString("MOBILE"),
                rs.getString("PASSWORD_HASH"),
                rs.getString("ACCOUNT_STATUS"),
                rs.getTimestamp("CREATED_AT")
        );
    }
}
