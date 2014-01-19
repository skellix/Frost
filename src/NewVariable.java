import java.util.ArrayList;


public class NewVariable extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		final Object carry = frostThread.carry.get();
		if (carry instanceof FrostSet) {
			if (carry instanceof FrostClass) {
				final FrostClass stackParent = frostThread.stack.get(frostThread.stack.size()-1);
				final FrostClass src = (FrostClass) carry;
				frostThread.carry.set(new FrostClass(){{
					name = src.name;
					parent = stackParent;
					children.putAll(src.children);
				}});
			} else if (carry instanceof ArrayList<?>) {
				frostThread.carry.set(new ArrayList(){{
					for (Object value : (ArrayList) carry) {
						add(value);
					}
				}});
			}
		}
	}

}
