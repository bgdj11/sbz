package sbnz.integracija.example.service;

import demo.facts.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sbnz.integracija.example.entity.Post;
import sbnz.integracija.example.entity.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FeedService {
    private final KieContainer kieContainer;
    private final PostService postService;
    private final UserService userService;

    @Autowired
    public FeedService(KieContainer kieContainer, PostService postService, UserService userService) {
        this.kieContainer = kieContainer;
        this.postService = postService;
        this.userService = userService;
    }

    private List<PostFact> toPostFacts(List<Post> posts) {
        return posts.stream().map(p -> new PostFact(
                String.valueOf(p.getId()),
                p.getAuthor() != null ? String.valueOf(p.getAuthor().getId()) : null,
                p.getCreatedAt(),
                Optional.ofNullable(p.getHashtags()).orElse(Collections.emptyList())
        )).collect(Collectors.toList());
    }

    private Set<String> getUserLikedHashtags(User user) {
        if (user == null) return Collections.emptySet();
        String uid = String.valueOf(user.getId());
        return postService.getAllPosts().stream()
                .filter(p -> {
                    Set<User> likers = Optional.ofNullable(p.getLikedByUsers())
                            .orElseGet(Collections::emptySet);
                    return likers.stream().anyMatch(u -> String.valueOf(u.getId()).equals(uid));
                })
                .flatMap(p -> Optional.ofNullable(p.getHashtags()).orElse(Collections.emptyList()).stream())
                .collect(Collectors.toSet());
    }

    private Set<String> getUserAuthoredHashtags(User user) {
        if (user == null) return Collections.emptySet();
        return postService.getPostsByUser(user).stream()
                .flatMap(p -> Optional.ofNullable(p.getHashtags()).orElse(Collections.emptyList()).stream())
                .collect(Collectors.toSet());
    }

    private int getUserAuthoredCount(User user) {
        if (user == null) return 0;
        return postService.getPostsByUser(user).size();
    }

    private List<PopularHashtag> computePopularHashtags(List<Post> posts) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        Map<String, Long> counts = posts.stream()
                .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(since))
                .flatMap(p -> Optional.ofNullable(p.getHashtags()).orElse(Collections.emptyList()).stream())
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));
        return counts.entrySet().stream()
                .filter(e -> e.getValue() > 5)
                .map(e -> new PopularHashtag(e.getKey()))
                .collect(Collectors.toList());
    }

    private List<PopularPost> computePopularPosts(List<Post> posts) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return posts.stream()
                .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(since))
                .filter(p -> Optional.ofNullable(p.getLikesCount()).orElse(0) > 10)
                .map(p -> new PopularPost(String.valueOf(p.getId())))
                .collect(Collectors.toList());
    }

    /** PostLikers: postId -> skup userId koji su lajkovali */
    private List<PostLikers> buildPostLikersFacts(List<Post> posts) {
        List<PostLikers> out = new ArrayList<>();
        for (Post p : posts) {
            Set<User> likers = Optional.ofNullable(p.getLikedByUsers())
                    .orElseGet(Collections::emptySet);
            Set<String> likerIds = likers.stream()
                    .map(u -> String.valueOf(u.getId()))
                    .collect(Collectors.toSet());
            out.add(new PostLikers(String.valueOf(p.getId()), likerIds));
        }
        return out;
    }

    private UserLikedPosts buildUserLikedPostsFact(String userId, List<Post> posts) {
        Set<String> liked = posts.stream()
                .filter(p -> {
                    Set<User> likers = Optional.ofNullable(p.getLikedByUsers())
                            .orElseGet(Collections::emptySet);
                    return likers.stream().anyMatch(u -> String.valueOf(u.getId()).equals(userId));
                })
                .map(p -> String.valueOf(p.getId()))
                .collect(Collectors.toSet());
        return new UserLikedPosts(userId, liked);
    }

    private List<SimilarUser> computeSimilarUsers(String baseUserId, List<Post> posts) {
        Map<String, Set<String>> userLikes = new HashMap<>();
        for (Post p : posts) {
            String pid = String.valueOf(p.getId());
            Set<User> likers = Optional.ofNullable(p.getLikedByUsers())
                    .orElseGet(Collections::emptySet);
            for (User u : likers) {
                String uid = String.valueOf(u.getId());
                userLikes.computeIfAbsent(uid, k -> new HashSet<>()).add(pid);
            }
        }
        Set<String> base = userLikes.getOrDefault(baseUserId, Collections.emptySet());
        List<SimilarUser> out = new ArrayList<>();
        for (Map.Entry<String, Set<String>> e : userLikes.entrySet()) {
            String otherId = e.getKey();
            if (otherId.equals(baseUserId)) continue;
            double score = pearson01(base, e.getValue());
            out.add(new SimilarUser(baseUserId, otherId, score));
        }
        return out;
    }

    private double pearson01(Set<String> a, Set<String> b) {
        Set<String> union = new HashSet<>(a);
        union.addAll(b);
        if (union.isEmpty()) return 0.0;

        int n = union.size();
        int sumA = 0, sumB = 0;
        for (String id : union) {
            sumA += a.contains(id) ? 1 : 0;
            sumB += b.contains(id) ? 1 : 0;
        }
        double meanA = sumA / (double) n;
        double meanB = sumB / (double) n;

        double num = 0, denA = 0, denB = 0;
        for (String id : union) {
            double ai = a.contains(id) ? 1.0 : 0.0;
            double bi = b.contains(id) ? 1.0 : 0.0;
            double da = ai - meanA;
            double db = bi - meanB;
            num += da * db;
            denA += da * da;
            denB += db * db;
        }
        double den = Math.sqrt(denA) * Math.sqrt(denB);
        if (den == 0.0) return 0.0;
        return num / den;
    }

    public List<PostFact> getFriendsFeed(String userId) {
        User current = userService.findById(Long.valueOf(userId)).orElse(null);
        List<String> friendIds = userService.getFriendIds(userId);
        List<String> blockedIds = userService.getBlockedIds(userId);
        List<Post> allPosts = postService.getAllPosts();

        KieSession session = kieContainer.newKieSession();
        try {
            List<PostFact> out = new ArrayList<>();
            session.setGlobal("friendsFeedOut", out);
            session.setGlobal("NOW", LocalDateTime.now());
            session.insert(new FriendFeedRequest(userId));
            session.insert(new FriendIds(friendIds));
            session.insert(new BlockedIds(blockedIds));
            for (PostFact pf : toPostFacts(allPosts)) session.insert(pf);
            session.getAgenda().getAgendaGroup("feed-friends-select").setFocus();
            session.fireAllRules();
            return out;
        } finally {
            session.dispose();
        }
    }

    public List<CandidatePost> getRecommendFeed(String userId) {
        User current = userService.findById(Long.valueOf(userId)).orElse(null);
        List<String> friendIds = userService.getFriendIds(userId);
        List<Post> allPosts = postService.getAllPosts();

        Set<String> likedTags    = getUserLikedHashtags(current);
        Set<String> authoredTags = getUserAuthoredHashtags(current);
        int authoredCount        = getUserAuthoredCount(current);
        List<PopularHashtag> popularTags  = computePopularHashtags(allPosts);
        List<PopularPost>    popularPosts = computePopularPosts(allPosts);

        List<PostLikers>   postLikersFacts    = buildPostLikersFacts(allPosts);
        UserLikedPosts     userLikedPostsFact = buildUserLikedPostsFact(userId, allPosts);
        List<SimilarUser>  similarUserFacts   = computeSimilarUsers(userId, allPosts);

        KieSession session = kieContainer.newKieSession();
        try {
            List<CandidatePost> out = new ArrayList<>();
            session.setGlobal("recommendFeedOut", out);
            session.setGlobal("NOW", LocalDateTime.now());

            session.insert(new RecommendedFeedRequest(userId));
            session.insert(new FriendIds(friendIds));
            session.insert(new UserFeedContext(likedTags, authoredTags));
            session.insert(new UserAuthoredCount(authoredCount));

            for (PopularHashtag ph : popularTags) session.insert(ph);
            for (PopularPost pp : popularPosts)   session.insert(pp);

            session.insert(userLikedPostsFact);
            for (PostLikers pl : postLikersFacts) session.insert(pl);
            for (SimilarUser su : similarUserFacts) session.insert(su);

            for (PostFact pf : toPostFacts(allPosts)) session.insert(new CandidatePost(pf));

            session.getAgenda().getAgendaGroup("feed-recommend-output").setFocus();
            session.getAgenda().getAgendaGroup("feed-recommend-score").setFocus();
            session.getAgenda().getAgendaGroup("feed-recommend-router").setFocus();
            session.getAgenda().getAgendaGroup("feed-recommend-validate").setFocus();

            session.fireAllRules();

            return out.stream()
                    .sorted(Comparator
                            .comparingInt(CandidatePost::getScore).reversed()
                            .thenComparing(
                                    cp -> Optional.ofNullable(cp.getPost())
                                            .map(PostFact::getCreatedAt)
                                            .orElse(null),
                                    Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(20)
                    .collect(Collectors.toList());
        } finally {
            session.dispose();
        }
    }
}
