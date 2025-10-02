package sbnz.integracija.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sbnz.integracija.example.dto.DTOMapper;
import sbnz.integracija.example.dto.PlaceDTO;
import sbnz.integracija.example.dto.RatingDTO;
import sbnz.integracija.example.entity.Place;
import sbnz.integracija.example.entity.Rating;
import sbnz.integracija.example.entity.User;
import sbnz.integracija.example.service.PlaceService;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/places")
@CrossOrigin(origins = "*")
public class PlaceController {

    private final PlaceService placeService;

    @Autowired
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public ResponseEntity<?> getPlaces(HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        List<Place> places = placeService.getPlacesSortedByRating();
        
        Map<String, Object> response = new HashMap<>();
        response.put("places", DTOMapper.toPlaceDTOList(places));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlaceDetails(@PathVariable Long id, HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        Optional<Place> placeOpt = placeService.findById(id);
        if (!placeOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Place place = placeOpt.get();
        List<Rating> ratings = placeService.getPlaceRatings(place);
        boolean hasRated = placeService.hasUserRatedPlace(place, currentUser);

        Map<String, Object> response = new HashMap<>();
        response.put("place", DTOMapper.toPlaceDTO(place));
        response.put("ratings", DTOMapper.toRatingDTOList(ratings));
        response.put("hasRated", hasRated);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/rate")
    public ResponseEntity<?> ratePlace(@PathVariable Long id,
                                      @RequestBody Map<String, Object> ratingData,
                                      HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        Optional<Place> placeOpt = placeService.findById(id);
        if (!placeOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        try {
            int score = Integer.parseInt(ratingData.get("score").toString());
            String description = ratingData.get("description").toString();
            
            Rating rating = placeService.ratePlace(placeOpt.get(), currentUser, score, description);
            RatingDTO ratingDTO = DTOMapper.toRatingDTO(rating);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Rating submitted successfully!", "rating", ratingDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to submit rating: " + e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addPlace(@RequestBody Map<String, Object> placeData,
                                     HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        if (!currentUser.isAdmin()) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        String name = (String) placeData.get("name");
        String country = (String) placeData.get("country");
        String city = (String) placeData.get("city");
        String description = (String) placeData.get("description");
        
        // Get hashtags from request
        @SuppressWarnings("unchecked")
        List<String> hashtags = (List<String>) placeData.get("hashtags");
        
        if (name == null || country == null || city == null || description == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "All fields are required"));
        }

        try {
            Place place = placeService.createPlace(name, country, city, description, hashtags);
            PlaceDTO placeDTO = DTOMapper.toPlaceDTO(place);
            return ResponseEntity.ok(Map.of("success", true, "message", "Place added successfully!", "place", placeDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to add place: " + e.getMessage()));
        }
    }

    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
}