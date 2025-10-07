package demo.facts;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import demo.facts.PostFact;
public class CandidatePost implements Serializable {
    private PostFact post;
    private int score;
    private Set<String> reasons = new HashSet<>();
    public CandidatePost() {}
    public CandidatePost(PostFact post) { this.post = post; }
    public PostFact getPost() { return post; }
    public void setPost(PostFact post) { this.post = post; }
    public int getScore() { return score; }
    public void addScore(int value, String reason) { this.score += value; this.reasons.add(reason); }
    public Set<String> getReasons() { return reasons; }
}
