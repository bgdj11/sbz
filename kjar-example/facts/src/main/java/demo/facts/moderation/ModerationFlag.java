package demo.facts.moderation;

import java.io.Serializable;
import java.util.Date;

public class ModerationFlag implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long userId;
    private String reason;
    private Date suspendedUntil;
    private Date flaggedAt;
    private String suspensionType; // "POSTING" or "LOGIN"
    
    public ModerationFlag() {
        this.flaggedAt = new Date();
    }
    
    public ModerationFlag(Long userId, String reason, Date suspendedUntil, String suspensionType) {
        this.userId = userId;
        this.reason = reason;
        this.suspendedUntil = suspendedUntil;
        this.flaggedAt = new Date();
        this.suspensionType = suspensionType;
    }

    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
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
    
    @Override
    public String toString() {
        return "ModerationFlag{" +
                "userId=" + userId +
                ", reason='" + reason + '\'' +
                ", suspendedUntil=" + suspendedUntil +
                ", flaggedAt=" + flaggedAt +
                ", suspensionType='" + suspensionType + '\'' +
                '}';
    }
}
