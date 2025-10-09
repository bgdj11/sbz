package sbnz.integracija.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sbnz.integracija.example.entity.Report;

import java.util.Date;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    List<Report> findByAuthorId(Long authorId);
    
    @Query("SELECT r FROM Report r WHERE r.author.id = :authorId AND r.timestamp >= :since")
    List<Report> findByAuthorIdSince(@Param("authorId") Long authorId, @Param("since") Date since);
    
    @Query("SELECT r FROM Report r WHERE r.author.id = :authorId ORDER BY r.timestamp DESC")
    List<Report> findByAuthorIdOrderByTimestampDesc(@Param("authorId") Long authorId);
}
