package sbnz.integracija.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sbnz.integracija.example.entity.Rating;
import sbnz.integracija.example.entity.Place;
import sbnz.integracija.example.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByPlaceOrderByCreatedAtDesc(Place place);
    
    List<Rating> findByUserOrderByCreatedAtDesc(User user);
    
    Optional<Rating> findByUserAndPlace(User user, Place place);
    
    List<Rating> findByScoreOrderByCreatedAtDesc(int score);
}