import java.util.Scanner;


public class ReadAllCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		if (frostThread.carry.get() instanceof Scanner) {
			String out = "";
			Scanner scanner = (Scanner) frostThread.carry.get();
			while (scanner.hasNextLine()) {
				out += scanner.nextLine()+"\n";
			}
			scanner.close();
			frostThread.carry.set(out);
		} else {
			frostThread.error("The right value of read must be a stream");
		}
	}

}
