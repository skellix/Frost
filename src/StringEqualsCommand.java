
public class StringEqualsCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		String right = frostThread.carry.get().toString();
		frostThread.callbackNext();
		frostThread.carry.set(frostThread.carry.get().toString().equals(right));
	}

}
