
public class PrintCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		System.out.print(frostThread.carry.toString());
	}

}
