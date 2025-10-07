package demo.facts;

import java.util.Set;

public class PostLikers {
    private String postId;
    private Set<String> userIds;

    public PostLikers() { }
    public PostLikers(String postId, Set<String> userIds) {
        this.postId = postId;
        this.userIds = userIds;
    }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public Set<String> getUserIds() { return userIds; }
    public void setUserIds(Set<String> userIds) { this.userIds = userIds; }
}
