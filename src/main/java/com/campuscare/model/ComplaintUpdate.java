package com.campuscare.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ComplaintUpdate implements Serializable {
    private static final long serialVersionUID = 1L;

    private long updateId;
    private long grievanceId;
    private String updatedByRole; // STUDENT, STAFF, ADMIN
    private String updatedById;
    private String oldStatus;
    private String newStatus;
    private String remarks;
    private Timestamp updatedAt;

    // View helper
    private String updaterName;

    public ComplaintUpdate() {}

    public ComplaintUpdate(long updateId, long grievanceId, String updatedByRole,
                           String updatedById, String oldStatus, String newStatus,
                           String remarks, Timestamp updatedAt) {
        this.updateId = updateId;
        this.grievanceId = grievanceId;
        this.updatedByRole = updatedByRole;
        this.updatedById = updatedById;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.remarks = remarks;
        this.updatedAt = updatedAt;
    }

    public long getUpdateId() { return updateId; }
    public void setUpdateId(long updateId) { this.updateId = updateId; }

    public long getGrievanceId() { return grievanceId; }
    public void setGrievanceId(long grievanceId) { this.grievanceId = grievanceId; }

    public String getUpdatedByRole() { return updatedByRole; }
    public void setUpdatedByRole(String updatedByRole) { this.updatedByRole = updatedByRole; }

    public String getUpdatedById() { return updatedById; }
    public void setUpdatedById(String updatedById) { this.updatedById = updatedById; }

    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getUpdaterName() { return updaterName; }
    public void setUpdaterName(String updaterName) { this.updaterName = updaterName; }
}
