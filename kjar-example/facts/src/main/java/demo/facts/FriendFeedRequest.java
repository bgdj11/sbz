package demo.facts;
import java.io.Serializable;
public class FriendFeedRequest implements Serializable {
    private String userId;
    public FriendFeedRequest() {}
    public FriendFeedRequest(String userId) { this.userId = userId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
