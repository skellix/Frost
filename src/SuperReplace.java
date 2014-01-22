import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class SuperReplace {

	private String text;

	public SuperReplace(String text) {
		this.text = text;
	}

	public String replace(String regex) {
		Matcher matcher = Pattern.compile(regex).matcher(text);
		StringBuffer sb = new StringBuffer("");
		while (matcher.find()) {
			matcher.appendReplacement(sb, getReplacement(sb, matcher));
		}
		if (sb.length() == 0) {
			return text;
		} else {
			matcher.appendTail(sb);
			return sb.toString();
		}
	}
	public abstract String getReplacement(StringBuffer sb, Matcher matcher);
}
