package com.campuscare.model;

import java.io.Serializable;

public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;

    private String adminId;
    private String username;
    private String passwordHash;
    private String accountStatus; // ACTIVE or INACTIVE

    public Admin() {}

    public Admin(String adminId, String username, String passwordHash, String accountStatus) {
        this.adminId = adminId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.accountStatus = accountStatus;
    }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(this.accountStatus);
    }
}
