package demo.facts;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class PostFact implements Serializable {
    private String id;
    private String authorId;
    private LocalDateTime createdAt;
    private List<String> hashtags;

    public PostFact() {}

    public PostFact(String id, String authorId, LocalDateTime createdAt, List<String> hashtags) {
        this.id = id;
        this.authorId = authorId;
        this.createdAt = createdAt;
        this.hashtags = hashtags;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<String> getHashtags() { return hashtags; }
    public void setHashtags(List<String> hashtags) { this.hashtags = hashtags; }
}