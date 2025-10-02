package sbnz.integracija.example.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private int likesCount;
    private int reportsCount;
    private UserDTO author;
    private List<String> hashtags;

    public PostDTO() {}

    public PostDTO(Long id, String content, LocalDateTime createdAt, int likesCount, 
                   int reportsCount, UserDTO author, List<String> hashtags) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.likesCount = likesCount;
        this.reportsCount = reportsCount;
        this.author = author;
        this.hashtags = hashtags;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }

    public int getReportsCount() { return reportsCount; }
    public void setReportsCount(int reportsCount) { this.reportsCount = reportsCount; }

    public UserDTO getAuthor() { return author; }
    public void setAuthor(UserDTO author) { this.author = author; }

    public List<String> getHashtags() { return hashtags; }
    public void setHashtags(List<String> hashtags) { this.hashtags = hashtags; }
}