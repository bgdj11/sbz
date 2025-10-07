package demo.facts;
import java.io.Serializable;
public class PopularPost implements Serializable {
    private String postId;
    public PopularPost() {}
    public PopularPost(String postId) { this.postId = postId; }
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
}
