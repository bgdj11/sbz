package demo.facts.moderation;

import java.io.Serializable;
import java.util.Date;

public class ReportEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long reporterId;
    private Long authorId;
    private Long postId;
    private String reason;
    private Date timestamp;
    
    public ReportEvent() {
        this.timestamp = new Date();
    }
    
    public ReportEvent(Long reporterId, Long authorId, Long postId, String reason) {
        this.reporterId = reporterId;
        this.authorId = authorId;
        this.postId = postId;
        this.reason = reason;
        this.timestamp = new Date();
    }
    
    public ReportEvent(Long reporterId, Long authorId, Long postId, String reason, Date timestamp) {
        this.reporterId = reporterId;
        this.authorId = authorId;
        this.postId = postId;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getReporterId() {
        return reporterId;
    }
    
    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }
    
    public Long getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
    
    public Long getPostId() {
        return postId;
    }
    
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public Date getTs() {
        return timestamp;
    }
    
    public void setTs(Date ts) {
        this.timestamp = ts;
    }
    
    @Override
    public String toString() {
        return "ReportEvent{" +
                "id=" + id +
                ", reporterId=" + reporterId +
                ", authorId=" + authorId +
                ", postId=" + postId +
                ", reason='" + reason + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
