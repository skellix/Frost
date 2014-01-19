
public class DieCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		frostThread.running.set(false);
	}

}
