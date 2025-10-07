package demo.facts;
import java.io.Serializable;
public class RecommendedFeedRequest implements Serializable {
    private String userId;
    public RecommendedFeedRequest() {}
    public RecommendedFeedRequest(String userId) { this.userId = userId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
