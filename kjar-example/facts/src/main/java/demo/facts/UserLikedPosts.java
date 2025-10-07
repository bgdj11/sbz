package demo.facts;

import java.util.Set;

public class UserLikedPosts {
    private String userId;
    private Set<String> postIds;

    public UserLikedPosts() { }
    public UserLikedPosts(String userId, Set<String> postIds) {
        this.userId = userId;
        this.postIds = postIds;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Set<String> getPostIds() { return postIds; }
    public void setPostIds(Set<String> postIds) { this.postIds = postIds; }
}
