import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class FileCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		if (frostThread.carry.get() instanceof String) {
			String fileName = (String) frostThread.carry.get();
			if (fileName.startsWith("<")) {
				try {
					frostThread.carry.set(new Scanner(new File(fileName.substring(1))));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else if (fileName.startsWith(">")) {
				try {
					File file = new File(fileName.substring(1));
					if (!file.exists()) {
						file.createNewFile();
					}
					frostThread.carry.set(new BufferedWriter(new FileWriter(file)));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
