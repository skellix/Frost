import java.util.HashMap;


public class ExecuteVariable extends FrostCommand {

	private String subName;

	public ExecuteVariable(String subName) {
		this.subName = subName;
	}
	public String getInitString() {
		return this.getClass().getSimpleName()+"(\""+subName+"\")";
	}
	@Override
	public void execute(FrostThread frostThread) {
		FrostThread frostThread2 = new FrostThread();
		frostThread2.carry.set(frostThread.carry.get());
		frostThread2.variables.putAll(frostThread.variables);
		new GetVariable(subName).execute(frostThread2);
		
		Object fc = frostThread2.carry.get();
		if (fc instanceof FrostCommand) {
			frostThread.callback((FrostCommand) fc);
		} else {
			frostThread.error("'"+subName+"' does not point to a command");
		}
	}

}
