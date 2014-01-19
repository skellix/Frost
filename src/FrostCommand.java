
public abstract class FrostCommand {

	public String getInitString() {
		return this.getClass().getSimpleName()+"()";
	}

	public abstract void execute(FrostThread frostThread);
	
}
