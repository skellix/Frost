
public class EqualsCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		Object right = frostThread.carry.get();
		if (right instanceof Number) {
			frostThread.callbackNext();
			frostThread.carry.set(frostThread.carry.get().equals(right));
		} else if (right instanceof String) {
			frostThread.callbackNext();
			frostThread.carry.set(frostThread.carry.get().equals(right));
		} else {
			frostThread.callbackNext();
			frostThread.carry.set(frostThread.carry.get() == right);
		}
	}

}
