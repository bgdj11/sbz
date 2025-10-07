package demo.facts;
import java.io.Serializable;
public class UserAuthoredCount implements Serializable {
    private int count;
    public UserAuthoredCount() {}
    public UserAuthoredCount(int count) { this.count = count; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
