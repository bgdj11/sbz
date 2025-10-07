package sbnz.integracija.example.controller;

import demo.facts.PostFact;
import demo.facts.CandidatePost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sbnz.integracija.example.service.FeedService;
import sbnz.integracija.example.service.PostService;
import sbnz.integracija.example.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/feed")
@CrossOrigin(origins = "*")
public class FeedController {
    private final FeedService feedService;
    private final UserService userService;
    private final PostService postService;

    @Autowired
    public FeedController(FeedService feedService, UserService userService, PostService postService) {
        this.feedService = feedService;
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/friends")
    public ResponseEntity<?> getFriendsFeed(@RequestParam String userId) {
        List<PostFact> feed = feedService.getFriendsFeed(userId);
        return ResponseEntity.ok(feed);
    }

    @GetMapping("/recommend")
    public ResponseEntity<?> getRecommendFeed(@RequestParam String userId) {
        List<CandidatePost> feed = feedService.getRecommendFeed(userId);
        List<Object> response = feed.stream().map(c -> {
            return new java.util.LinkedHashMap<String, Object>() {{
                put("post", c.getPost());
                put("score", c.getScore());
                put("reasons", c.getReasons());
            }};
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
