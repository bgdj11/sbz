package sbnz.integracija.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "likes_count")
    private int likesCount = 0;

    @Column(name = "reports_count")
    private int reportsCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnoreProperties({"posts", "ratings", "friends", "blockedUsers", "password"})
    private User author;

    @ElementCollection
    @CollectionTable(name = "post_hashtags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "hashtag")
    private List<String> hashtags = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "post_likes",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnoreProperties({"posts", "ratings", "friends", "blockedUsers", "password"})
    private Set<User> likedByUsers = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "post_reports",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnoreProperties({"posts", "ratings", "friends", "blockedUsers", "password"})
    private Set<User> reportedByUsers = new HashSet<>();

    public Post() {}

    public Post(String content, User author) {
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
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

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public List<String> getHashtags() { return hashtags; }
    public void setHashtags(List<String> hashtags) { this.hashtags = hashtags; }

    public Set<User> getLikedByUsers() { return likedByUsers; }
    public void setLikedByUsers(Set<User> likedByUsers) { this.likedByUsers = likedByUsers; }

    public Set<User> getReportedByUsers() { return reportedByUsers; }
    public void setReportedByUsers(Set<User> reportedByUsers) { this.reportedByUsers = reportedByUsers; }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}