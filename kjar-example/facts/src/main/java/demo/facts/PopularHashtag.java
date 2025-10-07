package demo.facts;
import java.io.Serializable;
public class PopularHashtag implements Serializable {
    private String tag;
    public PopularHashtag() {}
    public PopularHashtag(String tag) { this.tag = tag; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
}
