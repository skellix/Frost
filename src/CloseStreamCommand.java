import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;


public class CloseStreamCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		if (frostThread.carry.get() instanceof Scanner) {
			((Scanner) frostThread.carry.get()).close();
		} else if (frostThread.carry.get() instanceof BufferedWriter) {
			try {
				((BufferedWriter) frostThread.carry.get()).close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			frostThread.error("The right value of close must be a stream");
		}
	}

}
