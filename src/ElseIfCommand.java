

public class ElseIfCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		if (frostThread.carry.get().equals(false)) {
			frostThread.callbackNext();
			if (frostThread.carry.get().equals(true)) {
				frostThread.callbackNext();
			} else {
				frostThread.index.get(frostThread.index.size()-1).getAndIncrement();
			}
		} else {
			frostThread.index.get(frostThread.index.size()-1).getAndIncrement();
			frostThread.index.get(frostThread.index.size()-1).getAndIncrement();
		}
	}

}
