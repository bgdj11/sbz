package sbnz.integracija.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sbnz.integracija.example.entity.Place;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    List<Place> findByCountryIgnoreCase(String country);
    
    List<Place> findByCityIgnoreCase(String city);
    
    List<Place> findByNameContainingIgnoreCase(String name);
    
    List<Place> findAllByOrderByAverageRatingDesc();
}