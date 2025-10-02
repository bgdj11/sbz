package sbnz.integracija.example.dto;

import java.util.List;

public class PlaceDTO {
    private Long id;
    private String name;
    private String country;
    private String city;
    private String description;
    private Double averageRating;
    private List<String> hashtags;

    public PlaceDTO() {}

    public PlaceDTO(Long id, String name, String country, String city, String description,
                    Double averageRating, List<String> hashtags) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.city = city;
        this.description = description;
        this.averageRating = averageRating;
        this.hashtags = hashtags;
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

    public String getFullLocation() {
        return name + ", " + city + ", " + country;
    }
}