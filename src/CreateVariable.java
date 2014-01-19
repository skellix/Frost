
public class CreateVariable extends FrostCommand {

	private String variableName;

	public CreateVariable(String variableName) {
		this.variableName = variableName;
	}

	@Override
	public void execute(FrostThread frostThread) {
		frostThread.variables.put(variableName, frostThread.carry.get());
	}

	public String getInitString() {
		return this.getClass().getSimpleName()+"(\""+variableName+"\")";
	}
}
