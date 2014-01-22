import java.util.Scanner;


public class ScanLineCommand extends FrostCommand {

	public static Scanner scanner = new Scanner(System.in);
	@Override
	public void execute(FrostThread frostThread) {
		frostThread.carry.set(scanner.nextLine());
		System.out.print("");
	}

}
