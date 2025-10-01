package sbnz.integracija.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sbnz.integracija.example.entity.Post;
import sbnz.integracija.example.entity.User;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorOrderByCreatedAtDesc(User author);
    
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllOrderByCreatedAtDesc();

    @Query("SELECT p FROM Post p WHERE p.author IN :friends ORDER BY p.createdAt DESC")
    List<Post> findByAuthorInOrderByCreatedAtDesc(@Param("friends") List<User> friends);

    @Query("SELECT p FROM Post p JOIN p.hashtags h WHERE h IN :hashtags")
    List<Post> findByHashtagsIn(@Param("hashtags") List<String> hashtags);

    @Query("SELECT p FROM Post p WHERE p.reportsCount > :threshold ORDER BY p.reportsCount DESC")
    List<Post> findReportedPosts(@Param("threshold") int threshold);
}