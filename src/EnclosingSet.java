import java.util.ArrayList;
import java.util.HashMap;


public class EnclosingSet {

	private static long classId = 0;
	public long id = classId ++;
	public String src = "";
	public EnclosingSet parent;
	public HashMap<String, EnclosingSet> children = new HashMap<String, EnclosingSet>();
	public String toString() {
		return getType()+"_"+id;
	}
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
