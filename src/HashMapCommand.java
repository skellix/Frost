import java.util.HashMap;


public class HashMapCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		frostThread.carry.set(new HashMap<String, Object>());
	}

}
