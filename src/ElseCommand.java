import java.util.ArrayList;


public class ElseCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		if (frostThread.carry.get().equals(false) && !(frostThread.carry.get() instanceof ArrayList<?>)) {
			frostThread.getNextAndIncrement().execute(frostThread);
		} else {
			frostThread.index.get(frostThread.index.size()-1).getAndIncrement();
		}
	}

}
