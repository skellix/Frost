
public class ReturnCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		frostThread.index.remove(frostThread.index.size()-1);
		frostThread.stack.remove(frostThread.stack.size()-1);
	}

}
