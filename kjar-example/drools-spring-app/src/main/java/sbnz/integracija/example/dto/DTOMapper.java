package sbnz.integracija.example.dto;

import sbnz.integracija.example.entity.User;
import sbnz.integracija.example.entity.Post;
import sbnz.integracija.example.entity.Place;
import sbnz.integracija.example.entity.Rating;

import java.util.List;
import java.util.stream.Collectors;

public class DTOMapper {

    public static UserDTO toUserDTO(User user) {
        if (user == null) return null;
        
        return new UserDTO(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getCity(),
            user.isAdmin()
        );
    }

    public static PostDTO toPostDTO(Post post) {
        if (post == null) return null;
        
        return new PostDTO(
            post.getId(),
            post.getContent(),
            post.getCreatedAt(),
            post.getLikesCount(),
            post.getReportsCount(),
            toUserDTO(post.getAuthor()),
            post.getHashtags()
        );
    }

    public static PlaceDTO toPlaceDTO(Place place) {
        if (place == null) return null;
        
        return new PlaceDTO(
            place.getId(),
            place.getName(),
            place.getCountry(),
            place.getCity(),
            place.getDescription(),
            place.getAverageRating(),
            place.getHashtags()
        );
    }

    public static RatingDTO toRatingDTO(Rating rating) {
        if (rating == null) return null;
        
        return new RatingDTO(
            rating.getId(),
            rating.getScore(),
            rating.getDescription(),
            rating.getCreatedAt(),
            toUserDTO(rating.getUser()),
            toPlaceDTO(rating.getPlace()),
            rating.getHashtags()
        );
    }

    public static List<PostDTO> toPostDTOList(List<Post> posts) {
        return posts.stream()
            .map(DTOMapper::toPostDTO)
            .collect(Collectors.toList());
    }

    public static List<PlaceDTO> toPlaceDTOList(List<Place> places) {
        return places.stream()
            .map(DTOMapper::toPlaceDTO)
            .collect(Collectors.toList());
    }

    public static List<RatingDTO> toRatingDTOList(List<Rating> ratings) {
        return ratings.stream()
            .map(DTOMapper::toRatingDTO)
            .collect(Collectors.toList());
    }

    public static List<UserDTO> toUserDTOList(List<User> users) {
        return users.stream()
            .map(DTOMapper::toUserDTO)
            .collect(Collectors.toList());
    }
}