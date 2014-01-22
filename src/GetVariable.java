import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class GetVariable extends FrostCommand {

	private String text;

	public GetVariable(String text) {
		this.text = text;
	}

	@Override
	public void execute(FrostThread frostThread) {
		String[] args = text.split("\\.");
		Object fc = null;
		if (args[0].contains("[")) {
			fc = getArrayElement(frostThread, args[0], frostThread.variables);
		} else {
			if (frostThread.variables.containsKey(args[0])) {
				fc = frostThread.variables.get(args[0]);
			} else {
				frostThread.error("No variable named '"+args[0]+"' has been declared in this scope");
			}
		}
		for (int i = 1 ; i < args.length ; i ++) {
			String key = args[i];
			if (fc instanceof FrostClass) {
				fc = ((FrostClass) fc).children;
			}
			if (fc instanceof ConcurrentHashMap<?, ?>) {
				ConcurrentHashMap<?, ?> map = (ConcurrentHashMap<?, ?>) fc;
				if (map.containsKey(key)) {
					fc = map.get(key);
				} else {
					frostThread.error("hashmap '"+args[i-1]+"' does not contain '"+key+"'");
				}
			} else {
				frostThread.error(args[i-1]+" is not a hashmap");
			}
		}
		frostThread.carry.set(fc);
	}

	private Object getArrayElement(FrostThread frostThread, String arg, ConcurrentHashMap<String, Object> map) {
		if (arg.contains("[")) {
			String name = arg.split("\\[")[0];
			String text = arg.substring(name.length()+1, arg.lastIndexOf(']'));
			if (map.containsKey(name)) {
				if (text.matches("\\d+")) {
					if (map.get(name) instanceof ArrayList<?>) {
						return ((ArrayList) map.get(name)).get(Integer.parseInt(text));
					} else {
						frostThread.error("'"+name+"' is not an arrayList");
					}
				} else {
					FrostThread frostThread2 = new FrostThread();
					frostThread2.variables.putAll(map);
					new GetVariable(text).execute(frostThread2);
					return frostThread2.carry.get();
				}
			} else {
				frostThread.error("No variable named '"+name+"' has been declared in this scope");
			}
			return null;
		} else {
			return arg;
		}
	}

	public String getInitString() {
		return this.getClass().getSimpleName()+"(\""+text+"\")";
	}
}
