package sbnz.integracija.example.dto;

import java.time.LocalDateTime;
import java.util.List;

public class RatingDTO {
    private Long id;
    private int score;
    private String description;
    private LocalDateTime createdAt;
    private UserDTO user;
    private PlaceDTO place;
    private List<String> hashtags;

    public RatingDTO() {}

    public RatingDTO(Long id, int score, String description, LocalDateTime createdAt,
                     UserDTO user, PlaceDTO place, List<String> hashtags) {
        this.id = id;
        this.score = score;
        this.description = description;
        this.createdAt = createdAt;
        this.user = user;
        this.place = place;
        this.hashtags = hashtags;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public PlaceDTO getPlace() { return place; }
    public void setPlace(PlaceDTO place) { this.place = place; }

    public List<String> getHashtags() { return hashtags; }
    public void setHashtags(List<String> hashtags) { this.hashtags = hashtags; }
}