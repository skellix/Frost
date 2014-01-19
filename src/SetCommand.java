
public class SetCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		frostThread.getNextAndIncrement().execute(frostThread);
	}

}
