
public class CompileCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		if (frostThread.carry.get() instanceof String) {
			frostThread.carry.set(new JFrostCompiler().compileClasses(frostThread.carry.get().toString()));
		} else {
			frostThread.error("the compiler must be passed a string");
		}
	}

}
