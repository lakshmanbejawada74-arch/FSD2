package com.campuscare.util;

import com.campuscare.dao.AdminDAO;
import com.campuscare.dao.ComplaintCategoryDAO;
import com.campuscare.dao.StaffDAO;
import com.campuscare.dao.StudentDAO;
import com.campuscare.model.Admin;
import com.campuscare.model.Staff;
import com.campuscare.model.Student;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Utility class to seed initial Admin, Staff, and Student accounts securely.
 */
public class DatabaseSeeder {

    public static void main(String[] args) {
        System.out.println("Starting CampusCare Database Seeding...");
        try (Connection conn = DBConnection.getConnection()) {
            AdminDAO adminDAO = new AdminDAO();
            StaffDAO staffDAO = new StaffDAO();
            StudentDAO studentDAO = new StudentDAO();

            // 1. Seed Admin
            if (adminDAO.findByUsername("admin") == null) {
                Admin admin = new Admin();
                admin.setAdminId("ADM001");
                admin.setUsername("admin");
                admin.setPasswordHash(PasswordUtil.hashPassword("Admin@123"));
                admin.setAccountStatus("ACTIVE");
                adminDAO.insert(admin);
                System.out.println("Created default admin: admin / Admin@123");
            } else {
                System.out.println("Admin 'admin' already exists.");
            }

            // 2. Seed Staff members
            if (!staffDAO.isIdExists("STF001")) {
                Staff s1 = new Staff("STF001", "Prof. Alan Turing", "IT", "it_staff@campuscare.edu", "9876543210",
                        PasswordUtil.hashPassword("Staff@123"), "ACTIVE", null);
                staffDAO.insert(s1);
                System.out.println("Created staff: STF001 (Prof. Alan Turing) / Staff@123");
            }

            if (!staffDAO.isIdExists("STF002")) {
                Staff s2 = new Staff("STF002", "Mr. Wardell Hostel", "HOSTEL", "hostel_staff@campuscare.edu", "9876543211",
                        PasswordUtil.hashPassword("Staff@123"), "ACTIVE", null);
                staffDAO.insert(s2);
                System.out.println("Created staff: STF002 (Mr. Wardell Hostel) / Staff@123");
            }

            if (!staffDAO.isIdExists("STF003")) {
                Staff s3 = new Staff("STF003", "Dr. Sarah Connor", "FACILITIES", "facilities_staff@campuscare.edu", "9876543212",
                        PasswordUtil.hashPassword("Staff@123"), "ACTIVE", null);
                staffDAO.insert(s3);
                System.out.println("Created staff: STF003 (Dr. Sarah Connor) / Staff@123");
            }

            if (!staffDAO.isIdExists("STF004")) {
                Staff s4 = new Staff("STF004", "Mr. Clean Sanitation", "SANITATION", "sanitation_staff@campuscare.edu", "9876543213",
                        PasswordUtil.hashPassword("Staff@123"), "ACTIVE", null);
                staffDAO.insert(s4);
                System.out.println("Created staff: STF004 (Mr. Clean Sanitation) / Staff@123");
            }

            // 3. Seed Students
            if (!studentDAO.isIdExists("STU1001")) {
                Student st1 = new Student("STU1001", "Alex Johnson", "Computer Science and Engineering", 3,
                        "HOSTEL", "Aryabhata Hall", "B-204", "student@campuscare.edu", "9876543220",
                        PasswordUtil.hashPassword("Student@123"), "ACTIVE", null);
                studentDAO.insert(st1);
                System.out.println("Created student: STU1001 (Alex Johnson - Hostel) / Student@123");
            }

            if (!studentDAO.isIdExists("STU1002")) {
                Student st2 = new Student("STU1002", "Emily Davis", "Information Technology", 2,
                        "DAY_SCHOLAR", null, null, "emily@campuscare.edu", "9876543221",
                        PasswordUtil.hashPassword("Student@123"), "ACTIVE", null);
                studentDAO.insert(st2);
                System.out.println("Created student: STU1002 (Emily Davis - Day Scholar) / Student@123");
            }

            System.out.println("Database seeding completed successfully.");
        } catch (Exception e) {
            System.err.println("Database seeding failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
