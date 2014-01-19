import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;


public class RunCommand extends FrostCommand {

	private String text;
	
	public RunCommand(String text) {
		this.text = text;
	}

	public String getInitString() {
		return this.getClass().getSimpleName()+"(\""+text+"\")";
	}

	@Override
	public void execute(FrostThread frostThread) {
		final String[] args = text.split("\\s+");
		for (final String path : System.getenv().get("Path").split(""+File.pathSeparatorChar)) {
			try {
				Process process = new ProcessBuilder(new ArrayList<String>(){{
					add("\""+path+File.separatorChar+args[0]+"\"");
					for (String arg : Arrays.asList(args).subList(1, args.length)) add(arg);
				}}).start();
				Scanner scanner = new Scanner(process.getInputStream());
				
				StringBuilder stringBuilder = new StringBuilder("");
				while (scanner.hasNextLine()) {
					stringBuilder.append(scanner.nextLine()+"\n");
				}
				scanner.close();
				
				frostThread.carry.set(stringBuilder.toString());
				return;
			} catch (IOException e) {
				//e.printStackTrace();
				//System.err.println(path+File.separatorChar+text+" not found");
			}
		}
		System.err.println("Command '"+text+"' does not exits in the system path!");
		System.exit(0);
	}

}
