import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class FrostClass extends FrostCommand implements FrostSet {

	public String name;
	public FrostSet parent;
	public ConcurrentHashMap<String, FrostSet> children = new ConcurrentHashMap<String, FrostSet>();
	@Override
	public String getType() {
		return "FrostClass";
	}
	public String toString() {
		return name+"@"+super.toString().split("\\@")[1];
	}
	@Override
	public void execute(FrostThread frostThread) {
		frostThread.stack.add(this);
		frostThread.index.add(new AtomicInteger(0));
	}
}
