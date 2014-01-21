import java.util.ArrayList;


public class IfCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		if (frostThread.carry.get().equals(true)) {
			frostThread.callbackNext();
		} else {
			frostThread.index.get(frostThread.index.size()-1).getAndIncrement();
		}
	}

}
