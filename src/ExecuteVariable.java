import java.util.HashMap;


public class ExecuteVariable extends FrostCommand {

	private String subName;

	public ExecuteVariable(String subName) {
		this.subName = subName;
	}
	public String getInitString() {
		return this.getClass().getSimpleName()+"(\""+subName+"\")";
	}
	@Override
	public void execute(FrostThread frostThread) {
		String[] args = subName.split("\\.");
		Object fc = null;
		if (frostThread.variables.containsKey(args[0])) {
			fc = frostThread.variables.get(args[0]);
		} else {
			frostThread.error("No variable named '"+args[0]+"' has been declared in this scope");
		}
		for (int i = 1 ; i < args.length-1 ; i ++) {
			String key = args[i];
			if (fc instanceof HashMap<?, ?>) {
				HashMap<?, ?> map = (HashMap<?, ?>) fc;
				if (map.containsKey(key)) {
					fc = map.get(key);
				} else {
					frostThread.error("hashmap '"+args[i-1]+"' does not contain '"+key+"'");
				}
			} else {
				frostThread.error(args[i-1]+"is not a hashmap");
			}
		}
		if (fc instanceof FrostCommand) {
			frostThread.callback((FrostCommand) fc);
		} else {
			frostThread.error("'"+subName+"' does not point to a command");
		}
	}

}
