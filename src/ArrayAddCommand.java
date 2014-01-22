import java.util.ArrayList;


public class ArrayAddCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		Object value = frostThread.carry.get();
		frostThread.callbackNext();
		if (frostThread.carry.get() instanceof ArrayList<?>) {
			((ArrayList) frostThread.carry.get()).add(value);
		} else {
			frostThread.error("can only add items to an arrayList");
		}
	}

}
