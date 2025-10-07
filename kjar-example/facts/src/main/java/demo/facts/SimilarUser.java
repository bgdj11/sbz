package demo.facts;

public class SimilarUser {
    private String baseUserId;
    private String otherUserId;
    private double score;

    public SimilarUser() { }
    public SimilarUser(String baseUserId, String otherUserId, double score) {
        this.baseUserId = baseUserId;
        this.otherUserId = otherUserId;
        this.score = score;
    }

    public String getBaseUserId() { return baseUserId; }
    public void setBaseUserId(String baseUserId) { this.baseUserId = baseUserId; }

    public String getOtherUserId() { return otherUserId; }
    public void setOtherUserId(String otherUserId) { this.otherUserId = otherUserId; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
}
