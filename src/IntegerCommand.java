
public class IntegerCommand extends FrostCommand {

	private int value;

	public IntegerCommand(String value) {
		this.value = Integer.parseInt(value);
	}

	@Override
	public void execute(FrostThread frostThread) {
		frostThread.carry.set(value);
	}

	public String getInitString() {
		return this.getClass().getSimpleName()+"(\""+value+"\")";
	}
}
