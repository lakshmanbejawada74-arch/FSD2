-- =====================================================================
-- CampusCare: Initial Seed Data Script (Safe & Non-Destructive)
-- =====================================================================

USE `campuscare`;

-- 1. Insert Categories (if not already present)
INSERT IGNORE INTO `complaint_categories` (`CATEGORY_ID`, `CATEGORY_NAME`, `DESCRIPTION`, `DEPARTMENT`, `ACTIVE`) VALUES
(1, 'HOSTEL', 'Hostel room, water, electricity or maintenance issues', 'HOSTEL', 1),
(2, 'SANITATION', 'Cleaning and hygiene complaints', 'SANITATION', 1),
(3, 'CLASSROOM', 'Classroom and academic facility issues', 'FACILITIES', 1),
(4, 'TRANSPORT', 'College bus and transport complaints', 'TRANSPORT', 1),
(5, 'INTERNET', 'Wi-Fi and internet connectivity issues', 'IT', 1),
(6, 'ELECTRICITY', 'Power and electrical maintenance', 'ELECTRICAL', 1),
(7, 'OTHER', 'Other campus complaints', 'GENERAL', 1);

-- 2. Initial Administrator Account
-- Username: admin | Password: Admin@123
INSERT IGNORE INTO `admins` (`ADMIN_ID`, `USERNAME`, `PASSWORD_HASH`, `ACCOUNT_STATUS`) VALUES
('ADM001', 'admin', '$2a$12$iOpdBJ6TIlVimr3Wd7oSROW0JeJST3jjB3IBoJuvyE.4q68OWsAPq', 'ACTIVE');

-- 3. Initial Staff Accounts
-- Passwords: Staff@123
INSERT IGNORE INTO `staff` (`STAFF_ID`, `FULL_NAME`, `DEPARTMENT`, `EMAIL`, `MOBILE`, `PASSWORD_HASH`, `ACCOUNT_STATUS`) VALUES
('STF001', 'Prof. Alan Turing', 'IT', 'it_staff@campuscare.edu', '9876543210', '$2a$12$y9Tv5jQ9HRQvgoWRw6luAerkZ8a3sHSnLfOc5XKaiVgH4jDqQkMFq', 'ACTIVE'),
('STF002', 'Mr. Wardell Hostel', 'HOSTEL', 'hostel_staff@campuscare.edu', '9876543211', '$2a$12$y9Tv5jQ9HRQvgoWRw6luAerkZ8a3sHSnLfOc5XKaiVgH4jDqQkMFq', 'ACTIVE'),
('STF003', 'Dr. Sarah Connor', 'FACILITIES', 'facilities_staff@campuscare.edu', '9876543212', '$2a$12$y9Tv5jQ9HRQvgoWRw6luAerkZ8a3sHSnLfOc5XKaiVgH4jDqQkMFq', 'ACTIVE'),
('STF004', 'Mr. Clean Sanitation', 'SANITATION', 'sanitation_staff@campuscare.edu', '9876543213', '$2a$12$y9Tv5jQ9HRQvgoWRw6luAerkZ8a3sHSnLfOc5XKaiVgH4jDqQkMFq', 'ACTIVE');

-- 4. Sample Student Accounts
-- Passwords: Student@123
-- Student 1: Hostel Student (Aryabhata Hall, Room B-204)
INSERT IGNORE INTO `students` (`STUDENT_ID`, `FULL_NAME`, `DEPARTMENT`, `STUDY_YEAR`, `STUDENT_TYPE`, `HOSTEL_NAME`, `ROOM_NUMBER`, `EMAIL`, `MOBILE`, `PASSWORD_HASH`, `ACCOUNT_STATUS`) VALUES
('STU1001', 'Alex Johnson', 'Computer Science and Engineering', 3, 'HOSTEL', 'Aryabhata Hall', 'B-204', 'student@campuscare.edu', '9876543220', '$2a$12$hG4JZGu0d.D3MYZQ8vBJQeIL/.h7mZbcVuMW7LRfOhejEi/ZfKtiS', 'ACTIVE');

-- Student 2: Day Scholar (Hostel Name and Room Number must be NULL)
INSERT IGNORE INTO `students` (`STUDENT_ID`, `FULL_NAME`, `DEPARTMENT`, `STUDY_YEAR`, `STUDENT_TYPE`, `HOSTEL_NAME`, `ROOM_NUMBER`, `EMAIL`, `MOBILE`, `PASSWORD_HASH`, `ACCOUNT_STATUS`) VALUES
('STU1002', 'Emily Davis', 'Information Technology', 2, 'DAY_SCHOLAR', NULL, NULL, 'emily@campuscare.edu', '9876543221', '$2a$12$hG4JZGu0d.D3MYZQ8vBJQeIL/.h7mZbcVuMW7LRfOhejEi/ZfKtiS', 'ACTIVE');
