package sbnz.integracija.example.controller;

import demo.facts.moderation.BlockEvent;
import demo.facts.moderation.ModerationFlag;
import demo.facts.moderation.ReportEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sbnz.integracija.example.dto.SuspiciousUserDTO;
import sbnz.integracija.example.entity.Report;
import sbnz.integracija.example.entity.User;
import sbnz.integracija.example.repository.ReportRepository;
import sbnz.integracija.example.repository.UserRepository;
import sbnz.integracija.example.service.ModerationService;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/moderation")
@CrossOrigin(origins = "*")
public class ModerationController {

    @Autowired
    private ModerationService moderationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    private List<BlockEvent> getBlockEventsForUser(Long targetUserId) {
        String sql = "SELECT user_id, blocked_user_id, created_at FROM user_blocked " +
                     "WHERE blocked_user_id = :targetId ORDER BY created_at DESC";
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("targetId", targetUserId);
        
        List<Object[]> results = query.getResultList();
        List<BlockEvent> blockEvents = new ArrayList<>();
        
        for (Object[] row : results) {
            Long blockerId = ((Number) row[0]).longValue();
            Long targetId = ((Number) row[1]).longValue();
            Date timestamp = row[2] instanceof java.sql.Timestamp 
                ? new Date(((java.sql.Timestamp) row[2]).getTime())
                : (Date) row[2];
            
            blockEvents.add(new BlockEvent(blockerId, targetId, timestamp));
        }
        
        return blockEvents;
    }

    @PostMapping("/detect-suspicious")
    public ResponseEntity<?> detectSuspiciousUsers(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Morate biti ulogovani"));
        }

        if (!currentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Samo administratori mogu pokrenuti detekciju"));
        }

        try {
            List<SuspiciousUserDTO> suspiciousUsers = new ArrayList<>();
            
            // svi blokirani ili reportovani useri
            Set<Long> userIdsToCheck = new HashSet<>();
            
            List<Report> allReports = reportRepository.findAll();

            allReports.forEach(r -> userIdsToCheck.add(r.getAuthor().getId()));

            String blockSql = "SELECT DISTINCT blocked_user_id FROM user_blocked";
            Query blockQuery = entityManager.createNativeQuery(blockSql);
            List<Object> blockedUserIds = blockQuery.getResultList();
            blockedUserIds.forEach(id -> userIdsToCheck.add(((Number) id).longValue()));

            for (Long userId : userIdsToCheck) {
                Optional<User> userOpt = userRepository.findById(userId);
                if (!userOpt.isPresent()) {
                    continue;
                }
                
                User user = userOpt.get();

                List<ReportEvent> reportEvents = reportRepository.findByAuthorId(userId)
                    .stream()
                    .map(r -> new ReportEvent(
                        r.getReporter().getId(),
                        r.getAuthor().getId(),
                        r.getPost() != null ? r.getPost().getId() : null,
                        r.getReason(),
                        r.getTimestamp()
                    ))
                    .collect(Collectors.toList());

                List<BlockEvent> blockEvents = getBlockEventsForUser(userId);

                List<ModerationFlag> flags = moderationService.detectSuspiciousBehavior(
                    userId, reportEvents, blockEvents
                );

                for (ModerationFlag flag : flags) {
                    SuspiciousUserDTO dto = new SuspiciousUserDTO(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        flag.getReason(),
                        flag.getSuspendedUntil(),
                        flag.getFlaggedAt(),
                        flag.getSuspensionType(),
                        reportEvents.size(),
                        blockEvents.size()
                    );
                    suspiciousUsers.add(dto);
                }
            }

            suspiciousUsers.sort((a, b) -> {
                int totalA = a.getReportCount() + a.getBlockCount();
                int totalB = b.getReportCount() + b.getBlockCount();
                return Integer.compare(totalB, totalA);
            });

            Map<String, Object> response = new HashMap<>();
            response.put("suspiciousUsers", suspiciousUsers);
            response.put("totalCount", suspiciousUsers.size());
            response.put("detectedAt", new Date());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Greska pri detekciji: " + e.getMessage()));
        }
    }


    @PostMapping("/detect-user/{userId}")
    public ResponseEntity<?> detectSuspiciousUser(@PathVariable Long userId, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Morate biti ulogovani"));
        }

        if (!currentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Samo administratori mogu pokrenuti detekciju"));
        }

        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Korisnik nije pronađen"));
            }

            User user = userOpt.get();

            List<ReportEvent> reportEvents = reportRepository.findByAuthorId(userId)
                .stream()
                .map(r -> new ReportEvent(
                    r.getReporter().getId(),
                    r.getAuthor().getId(),
                    r.getPost() != null ? r.getPost().getId() : null,
                    r.getReason(),
                    r.getTimestamp()
                ))
                .collect(Collectors.toList());

            List<BlockEvent> blockEvents = getBlockEventsForUser(userId);

            List<ModerationFlag> flags = moderationService.detectSuspiciousBehavior(
                userId, reportEvents, blockEvents
            );

            Map<String, Object> response = new HashMap<>();
            response.put("user", Map.of(
                "id", user.getId(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail()
            ));
            response.put("reportCount", reportEvents.size());
            response.put("blockCount", blockEvents.size());
            response.put("flags", flags);
            response.put("isSuspicious", !flags.isEmpty());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Greška pri detekciji: " + e.getMessage()));
        }
    }

    @PostMapping("/apply-suspension/{userId}")
    public ResponseEntity<?> applySuspension(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> suspensionData,
            HttpSession session) {
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Morate biti ulogovani"));
        }

        if (!currentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Samo administratori mogu primenjivati kazne"));
        }

        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Korisnik nije pronađen"));
            }

            User user = userOpt.get();
            String suspensionType = (String) suspensionData.get("suspensionType");
            String suspendedUntilStr = (String) suspensionData.get("suspendedUntil");
            
            Date suspendedUntil = new Date(Long.parseLong(suspendedUntilStr));

            if ("POSTING".equals(suspensionType)) {
                user.setPostingSuspendedUntil(suspendedUntil);
            } else if ("LOGIN".equals(suspensionType)) {
                user.setLoginSuspendedUntil(suspendedUntil);
            }

            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                "message", "Kazna uspešno primenjena",
                "user", user.getId(),
                "suspensionType", suspensionType,
                "suspendedUntil", suspendedUntil
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Greska pri primeni kazne: " + e.getMessage()));
        }
    }
}
