
public class LessThanCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		Object right = frostThread.carry.get();
		frostThread.getNextAndIncrement();
		frostThread.carry.set(((Number) frostThread.carry.get()).doubleValue() < ((Number) right).doubleValue());
	}

}
