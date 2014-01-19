import java.util.ArrayList;
import java.util.HashMap;


public class ArrayGet extends FrostCommand {

	private String text;

	public ArrayGet(String text) {
		this.text = text;
	}

	public String getInitString() {
		return this.getClass().getSimpleName()+"(\""+text+"\")";
	}
	
	@Override
	public void execute(FrostThread frostThread) {
		String[] args = text.split("\\.");
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
		if (fc instanceof ArrayList<?>) {
			ArrayList<?> list = (ArrayList<?>) fc;
			if (frostThread.carry.get() instanceof Number) {
				frostThread.carry.set(list.get(((Number) frostThread.carry.get()).intValue()));
			} else {
				frostThread.error("arraylist index must be a number");
			}
		} else {
			frostThread.error(args[args.length-1]+"is not an arraylist");
		}
	}

}
