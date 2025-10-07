package demo.facts;
import java.io.Serializable;
import java.util.Set;
public class UserFeedContext implements Serializable {
    private Set<String> likedHashtags;
    private Set<String> authoredHashtags;
    public UserFeedContext() {}
    public UserFeedContext(Set<String> liked, Set<String> authored) {
        this.likedHashtags = liked;
        this.authoredHashtags = authored;
    }
    public Set<String> getLikedHashtags() { return likedHashtags; }
    public void setLikedHashtags(Set<String> likedHashtags) { this.likedHashtags = likedHashtags; }
    public Set<String> getAuthoredHashtags() { return authoredHashtags; }
    public void setAuthoredHashtags(Set<String> authoredHashtags) { this.authoredHashtags = authoredHashtags; }
}
