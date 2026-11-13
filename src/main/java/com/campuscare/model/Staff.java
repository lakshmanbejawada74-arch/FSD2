package com.campuscare.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Staff implements Serializable {
    private static final long serialVersionUID = 1L;

    private String staffId;
    private String fullName;
    private String department;
    private String email;
    private String mobile;
    private String passwordHash;
    private String accountStatus; // ACTIVE or INACTIVE
    private Timestamp createdAt;

    public Staff() {}

    public Staff(String staffId, String fullName, String department, String email,
                 String mobile, String passwordHash, String accountStatus, Timestamp createdAt) {
        this.staffId = staffId;
        this.fullName = fullName;
        this.department = department;
        this.email = email;
        this.mobile = mobile;
        this.passwordHash = passwordHash;
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
    }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

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
}
