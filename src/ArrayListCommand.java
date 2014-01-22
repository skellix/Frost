import java.util.ArrayList;


public class ArrayListCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		frostThread.carry.set(new ArrayList<>());
	}

}
