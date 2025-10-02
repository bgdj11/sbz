package sbnz.integracija.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sbnz.integracija.example.entity.Post;
import sbnz.integracija.example.entity.User;
import sbnz.integracija.example.repository.PostRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post createPost(String content, User author) {
        Post post = new Post(content, author);

        List<String> hashtags = extractHashtags(content);
        post.setHashtags(hashtags);
        
        return postRepository.save(post);
    }

    public Post createPost(String content, User author, List<String> hashtags) {
        Post post = new Post(content, author);

        if (hashtags != null && !hashtags.isEmpty()) {
            post.setHashtags(hashtags);
        } else {
            List<String> extractedHashtags = extractHashtags(content);
            post.setHashtags(extractedHashtags);
        }
        
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllOrderByCreatedAtDesc();
    }

    public List<Post> getPostsByUser(User user) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(user);
    }

    public List<Post> getFriendsPosts(List<User> friends) {
        return postRepository.findByAuthorInOrderByCreatedAtDesc(friends);
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public void likePost(Post post, User user) {
        if (!post.getLikedByUsers().contains(user)) {
            post.getLikedByUsers().add(user);
            post.setLikesCount(post.getLikesCount() + 1);
            postRepository.save(post);
        }
    }

    public void unlikePost(Post post, User user) {
        if (post.getLikedByUsers().contains(user)) {
            post.getLikedByUsers().remove(user);
            post.setLikesCount(post.getLikesCount() - 1);
            postRepository.save(post);
        }
    }

    public void reportPost(Post post, User user) {
        if (!post.getReportedByUsers().contains(user)) {
            post.getReportedByUsers().add(user);
            post.setReportsCount(post.getReportsCount() + 1);
            postRepository.save(post);
        }
    }

    public List<Post> getReportedPosts() {
        return postRepository.findReportedPosts(0);
    }

    private List<String> extractHashtags(String content) {
        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(content);
        return matcher.results()
                .map(match -> match.group().substring(1)) // Remove # symbol
                .collect(Collectors.toList());
    }

    public boolean hasUserLikedPost(Post post, User user) {
        return post.getLikedByUsers().contains(user);
    }

    public boolean hasUserReportedPost(Post post, User user) {
        return post.getReportedByUsers().contains(user);
    }
}