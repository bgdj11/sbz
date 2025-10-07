package sbnz.integracija.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sbnz.integracija.example.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:search% OR u.lastName LIKE %:search% OR u.email LIKE %:search%")
    List<User> searchUsers(@Param("search") String search);

    @Query("SELECT u FROM User u WHERE u.isAdmin = false")
    List<User> findAllRegularUsers();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO user_friends (user_id, friend_id) VALUES (:userId, :friendId), (:friendId, :userId) " +
                    "ON CONFLICT DO NOTHING",
            nativeQuery = true
    )
    int addFriendRelationship(@Param("userId") Long userId,
                              @Param("friendId") Long friendId);
}