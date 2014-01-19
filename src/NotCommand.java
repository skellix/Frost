
public class NotCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		frostThread.carry.set( ! (boolean) frostThread.carry.get());
	}

}
