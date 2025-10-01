package sbnz.integracija.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sbnz.integracija.example.entity.Place;
import sbnz.integracija.example.entity.Post;
import sbnz.integracija.example.entity.User;
import sbnz.integracija.example.service.PlaceService;
import sbnz.integracija.example.service.PostService;
import sbnz.integracija.example.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MainController {

    private final PostService postService;
    private final UserService userService;
    private final PlaceService placeService;

    @Autowired
    public MainController(PostService postService, UserService userService, PlaceService placeService) {
        this.postService = postService;
        this.userService = userService;
        this.placeService = placeService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        List<Post> allPosts = postService.getAllPosts();
        List<Place> topPlaces = placeService.getPlacesSortedByRating();

        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("currentUser", currentUser);
        dashboardData.put("posts", allPosts);
        dashboardData.put("places", topPlaces);
        
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        List<Post> userPosts = postService.getPostsByUser(currentUser);
        
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("currentUser", currentUser);
        profileData.put("posts", userPosts);
        
        return ResponseEntity.ok(profileData);
    }

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody Map<String, String> request, HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        String content = request.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Content cannot be empty");
        }

        try {
            Post post = postService.createPost(content, currentUser);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create post: " + e.getMessage());
        }
    }

    @PostMapping("/posts/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable Long id, HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        Optional<Post> postOpt = postService.findById(id);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            Map<String, Object> response = new HashMap<>();
            
            if (postService.hasUserLikedPost(post, currentUser)) {
                postService.unlikePost(post, currentUser);
                response.put("liked", false);
                response.put("count", post.getLikesCount());
            } else {
                postService.likePost(post, currentUser);
                response.put("liked", true);
                response.put("count", post.getLikesCount());
            }
            
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/posts/{id}/report")
    public ResponseEntity<?> reportPost(@PathVariable Long id, HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        Optional<Post> postOpt = postService.findById(id);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            if (!postService.hasUserReportedPost(post, currentUser)) {
                postService.reportPost(post, currentUser);
                return ResponseEntity.ok(Map.of("success", "Post reported"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Already reported"));
            }
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String q, HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        List<User> users = userService.searchUsers(q);
        List<Place> places = placeService.searchPlaces(q);

        Map<String, Object> searchResults = new HashMap<>();
        searchResults.put("query", q);
        searchResults.put("users", users);
        searchResults.put("places", places);

        return ResponseEntity.ok(searchResults);
    }

    @PostMapping("/users/{id}/friend")
    public ResponseEntity<?> addFriend(@PathVariable Long id, HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        Optional<User> friendOpt = userService.findById(id);
        if (friendOpt.isPresent()) {
            userService.addFriend(currentUser, friendOpt.get());
            return ResponseEntity.ok(Map.of("success", "Friend added"));
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/users/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long id, HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        Optional<User> userToBlockOpt = userService.findById(id);
        if (userToBlockOpt.isPresent()) {
            userService.blockUser(currentUser, userToBlockOpt.get());
            return ResponseEntity.ok(Map.of("success", "User blocked"));
        }

        return ResponseEntity.notFound().build();
    }

    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
}