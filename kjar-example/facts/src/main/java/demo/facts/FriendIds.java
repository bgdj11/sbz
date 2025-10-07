package demo.facts;
import java.io.Serializable;
import java.util.List;
public class FriendIds implements Serializable {
    private List<String> ids;
    public FriendIds() {}
    public FriendIds(List<String> ids) { this.ids = ids; }
    public List<String> getIds() { return ids; }
    public void setIds(List<String> ids) { this.ids = ids; }
}
