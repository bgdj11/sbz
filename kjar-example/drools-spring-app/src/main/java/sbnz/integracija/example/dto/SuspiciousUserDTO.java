package sbnz.integracija.example.dto;

import java.util.Date;

public class SuspiciousUserDTO {
    
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String reason;
    private Date suspendedUntil;
    private Date flaggedAt;
    private String suspensionType; // "POSTING" or "LOGIN"
    private int reportCount;
    private int blockCount;
    
    public SuspiciousUserDTO() {}
    
    public SuspiciousUserDTO(Long userId, String firstName, String lastName, String email, 
                           String reason, Date suspendedUntil, Date flaggedAt, 
                           String suspensionType, int reportCount, int blockCount) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.reason = reason;
        this.suspendedUntil = suspendedUntil;
        this.flaggedAt = flaggedAt;
        this.suspensionType = suspensionType;
        this.reportCount = reportCount;
        this.blockCount = blockCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getSuspendedUntil() {
        return suspendedUntil;
    }

    public void setSuspendedUntil(Date suspendedUntil) {
        this.suspendedUntil = suspendedUntil;
    }

    public Date getFlaggedAt() {
        return flaggedAt;
    }

    public void setFlaggedAt(Date flaggedAt) {
        this.flaggedAt = flaggedAt;
    }

    public String getSuspensionType() {
        return suspensionType;
    }

    public void setSuspensionType(String suspensionType) {
        this.suspensionType = suspensionType;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
    }
}
