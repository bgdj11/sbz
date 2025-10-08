package demo.facts.moderation;

import java.io.Serializable;
import java.util.Date;

public class BlockEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long blockerId;
    private Long targetId;
    private Date timestamp;
    
    public BlockEvent() {
        this.timestamp = new Date();
    }
    
    public BlockEvent(Long blockerId, Long targetId) {
        this.blockerId = blockerId;
        this.targetId = targetId;
        this.timestamp = new Date();
    }
    
    public BlockEvent(Long blockerId, Long targetId, Date timestamp) {
        this.blockerId = blockerId;
        this.targetId = targetId;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getBlockerId() {
        return blockerId;
    }
    
    public void setBlockerId(Long blockerId) {
        this.blockerId = blockerId;
    }
    
    public Long getTargetId() {
        return targetId;
    }
    
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
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
        return "BlockEvent{" +
                "id=" + id +
                ", blockerId=" + blockerId +
                ", targetId=" + targetId +
                ", timestamp=" + timestamp +
                '}';
    }
}
