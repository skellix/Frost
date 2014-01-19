import java.util.concurrent.atomic.AtomicInteger;


public class SubPointerCommand extends FrostCommand {

	private String subName;
	
	public SubPointerCommand(String subName) {
		this.subName = subName;
	}
	public String getInitString() {
		return this.getClass().getSimpleName()+"(\""+subName+"\")";
	}
	@Override
	public void execute(FrostThread frostThread) {
		FrostClass fc = frostThread.stack.get(frostThread.stack.size()-1);
		String[] keys = subName.split("\\.");
		FrostSet set = fc;
		for (String key : keys) {
			if (set instanceof FrostClass) {
				fc = (FrostClass) set;
				if (fc.children.containsKey(key)) {
					set = fc.children.get(key);
				} else {
					System.err.println("Class '"+fc.name+"' does not contain a field named '"+key+"'");
					System.exit(0);
				}
			} else {
				System.err.println("Object '"+set.toString()+"' is not a class!");
				System.exit(0);
			}
		}
		if (set instanceof FrostClass) {
			frostThread.carry.set((FrostClass) set);
		} else {
			System.err.println("Object '"+set.toString()+"' is not a class!");
			System.exit(0);
		}
	}

}
