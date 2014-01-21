
public class WhileCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		int whileIndex = frostThread.index.get(frostThread.index.size()-1).get()-1;
		while (frostThread.carry.get() instanceof Boolean && (boolean) frostThread.carry.get()) {
			frostThread.callbackNext();
			frostThread.index.get(frostThread.index.size()-1).set(whileIndex-1);
			frostThread.callbackNext();
		}
	}

}
