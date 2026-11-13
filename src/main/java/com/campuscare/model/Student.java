package com.campuscare.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentId;
    private String fullName;
    private String department;
    private int studyYear;
    private String studentType; // HOSTEL or DAY_SCHOLAR
    private String hostelName;
    private String roomNumber;
    private String email;
    private String mobile;
    private String passwordHash;
    private String accountStatus; // ACTIVE or INACTIVE
    private Timestamp createdAt;

    public Student() {}

    public Student(String studentId, String fullName, String department, int studyYear,
                   String studentType, String hostelName, String roomNumber,
                   String email, String mobile, String passwordHash,
                   String accountStatus, Timestamp createdAt) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.department = department;
        this.studyYear = studyYear;
        this.studentType = studentType;
        this.hostelName = hostelName;
        this.roomNumber = roomNumber;
        this.email = email;
        this.mobile = mobile;
        this.passwordHash = passwordHash;
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getStudyYear() { return studyYear; }
    public void setStudyYear(int studyYear) { this.studyYear = studyYear; }

    public String getStudentType() { return studentType; }
    public void setStudentType(String studentType) { this.studentType = studentType; }

    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(this.accountStatus);
    }

    public boolean isHostel() {
        return "HOSTEL".equalsIgnoreCase(this.studentType);
    }
}
