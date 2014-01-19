import java.io.BufferedWriter;
import java.io.IOException;


public class WriteCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		if (frostThread.carry.get() instanceof BufferedWriter) {
			BufferedWriter fileOutputStream = (BufferedWriter) frostThread.carry.get();
			frostThread.callbackNext();
			try {
				fileOutputStream.write((String) frostThread.carry.get());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			frostThread.error("The right value of read must be a stream");
		}
	}

}
