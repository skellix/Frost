
public class StringCommand extends FrostCommand {

	private String text;

	public StringCommand(String text) {
		this.text = text;
	}
	public String getInitString() {
		return this.getClass().getSimpleName()+"(\""+text+"\")";
	}
	@Override
	public void execute(FrostThread frostThread) {
		frostThread.carry.set(text);
	}
}
