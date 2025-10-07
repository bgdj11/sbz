package sbnz.integracija.example.controller;

import demo.facts.PostFact;
import demo.facts.CandidatePost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sbnz.integracija.example.dto.DTOMapper;
import sbnz.integracija.example.dto.PostDTO;
import sbnz.integracija.example.entity.Post;
import sbnz.integracija.example.service.FeedService;
import sbnz.integracija.example.service.PostService;
import sbnz.integracija.example.service.UserService;

import java.util.List;
import java.util.Optional;
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

        List<PostDTO> posts = feed.stream()
                .map(pf -> {
                    try {
                        Long postId = Long.parseLong(pf.getId());
                        Optional<Post> postOpt = postService.findById(postId);
                        return postOpt.map(DTOMapper::toPostDTO).orElse(null);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/recommend")
    public ResponseEntity<?> getRecommendFeed(@RequestParam String userId) {
        List<CandidatePost> feed = feedService.getRecommendFeed(userId);

        List<Object> response = feed.stream().map(c -> {
            Long postId = Long.parseLong(c.getPost().getId());
            Optional<Post> postOpt = postService.findById(postId);

            return new java.util.LinkedHashMap<String, Object>() {{
                put("post", postOpt.map(DTOMapper::toPostDTO).orElse(null));
                put("score", c.getScore());
                put("reasons", c.getReasons());
            }};
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
