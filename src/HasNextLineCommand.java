import java.util.Scanner;


public class HasNextLineCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		if (frostThread.carry.get() instanceof Scanner) {
			frostThread.carry.set(((Scanner) frostThread.carry.get()).hasNextLine());
		} else {
			frostThread.error("The right value of read must be a stream");
		}
	}

}
