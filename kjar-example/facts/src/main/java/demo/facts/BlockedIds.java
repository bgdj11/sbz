package demo.facts;
import java.io.Serializable;
import java.util.List;
public class BlockedIds implements Serializable {
    private List<String> ids;
    public BlockedIds() {}
    public BlockedIds(List<String> ids) { this.ids = ids; }
    public List<String> getIds() { return ids; }
    public void setIds(List<String> ids) { this.ids = ids; }
}
