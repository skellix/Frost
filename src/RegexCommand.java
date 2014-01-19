import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		if (frostThread.carry.get() instanceof String) {
			String regex = frostThread.carry.get().toString();
			frostThread.callbackNext();
			if (frostThread.carry.get() instanceof String) {
				final Matcher matcher = Pattern.compile(regex).matcher(frostThread.carry.get().toString());
				if (matcher.find()) {
					frostThread.carry.set(new ArrayList<String>(){{
						for (int i = 0 ; i <= matcher.groupCount() ; i ++) {
							add(matcher.group(i));
						}
					}});
				}
				
			} else {
				System.err.println("Left value of =~ must be a string!");
				System.exit(0);
			}
		} else {
			System.err.println("Right value of =~ must be a string!");
			System.exit(0);
		}
	}

}
