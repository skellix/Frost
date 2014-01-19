
public class AppendCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		Object right = frostThread.carry.get();
		frostThread.callbackNext();
		frostThread.carry.set(""+frostThread.carry.get().toString()+right.toString());
	}

}
