
public class DoubleCommand extends FrostCommand {

	private double value;

	public DoubleCommand(String value) {
		this.value = Double.parseDouble(value);
	}

	@Override
	public void execute(FrostThread frostThread) {
		frostThread.carry.set(value);
	}

	public String getInitString() {
		return this.getClass().getSimpleName()+"(\""+value+"\")";
	}
}
