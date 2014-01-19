
public class NotEqualsCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		Object right = frostThread.carry.get();
		frostThread.getNextAndIncrement();
		frostThread.carry.set(frostThread.carry.get() != right);
	}

}
