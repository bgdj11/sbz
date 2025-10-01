package sbnz.integracija.example.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "places")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    @Column(length = 1000)
    private String description;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @ElementCollection
    @CollectionTable(name = "place_hashtags", joinColumns = @JoinColumn(name = "place_id"))
    @Column(name = "hashtag")
    private List<String> hashtags = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Rating> ratings = new HashSet<>();

    public Place() {}

    public Place(String name, String country, String city, String description) {
        this.name = name;
        this.country = country;
        this.city = city;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public List<String> getHashtags() { return hashtags; }
    public void setHashtags(List<String> hashtags) { this.hashtags = hashtags; }

    public Set<Rating> getRatings() { return ratings; }
    public void setRatings(Set<Rating> ratings) { this.ratings = ratings; }

    public String getFullLocation() {
        return name + ", " + city + ", " + country;
    }

    // Method to recalculate average rating
    public void updateAverageRating() {
        if (ratings.isEmpty()) {
            this.averageRating = 0.0;
        } else {
            this.averageRating = ratings.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);
        }
    }
}