package sbnz.integracija.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sbnz.integracija.example.entity.Place;
import sbnz.integracija.example.entity.Rating;
import sbnz.integracija.example.entity.User;
import sbnz.integracija.example.repository.PlaceRepository;
import sbnz.integracija.example.repository.RatingRepository;


import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final RatingRepository ratingRepository;

    @Autowired
    public PlaceService(PlaceRepository placeRepository, RatingRepository ratingRepository) {
        this.placeRepository = placeRepository;
        this.ratingRepository = ratingRepository;
    }

    public Place createPlace(String name, String country, String city, String description) {
        Place place = new Place(name, country, city, description);

        List<String> hashtags = extractHashtags(description);
        place.setHashtags(hashtags);
        
        return placeRepository.save(place);
    }

    public Place createPlace(String name, String country, String city, String description, List<String> hashtags) {
        Place place = new Place(name, country, city, description);

        if (hashtags != null && !hashtags.isEmpty()) {
            place.setHashtags(hashtags);
        } else {
            List<String> extractedHashtags = extractHashtags(description);
            place.setHashtags(extractedHashtags);
        }
        
        return placeRepository.save(place);
    }

    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    public List<Place> getPlacesSortedByRating() {
        return placeRepository.findAllByOrderByAverageRatingDesc();
    }

    public Optional<Place> findById(Long id) {
        return placeRepository.findById(id);
    }

    public List<Place> searchPlaces(String searchTerm) {
        return placeRepository.findByNameContainingIgnoreCase(searchTerm);
    }

    public Rating ratePlace(Place place, User user, int score, String description) {

        Optional<Rating> existingRating = ratingRepository.findByUserAndPlace(user, place);
        
        Rating rating;
        if (existingRating.isPresent()) {
            rating = existingRating.get();
            rating.setScore(score);
            rating.setDescription(description);
        } else {
            rating = new Rating(score, description, user, place);
        }

        List<String> hashtags = extractHashtags(description);
        rating.setHashtags(hashtags);
        
        rating = ratingRepository.save(rating);

        place.updateAverageRating();
        placeRepository.save(place);
        
        return rating;
    }

    public List<Rating> getPlaceRatings(Place place) {
        return ratingRepository.findByPlaceOrderByCreatedAtDesc(place);
    }

    public List<Rating> getUserRatings(User user) {
        return ratingRepository.findByUserOrderByCreatedAtDesc(user);
    }

    private List<String> extractHashtags(String content) {
        if (content == null) return List.of();
        
        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(content);
        return matcher.results()
                .map(match -> match.group().substring(1)) // Remove # symbol
                .collect(Collectors.toList());
    }

    public boolean hasUserRatedPlace(Place place, User user) {
        return ratingRepository.findByUserAndPlace(user, place).isPresent();
    }
}