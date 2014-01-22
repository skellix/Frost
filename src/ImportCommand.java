
public class ImportCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		String string = frostThread.carry.get().toString();
		String fileName = string.split("\\.")[0];
		new StringCommand("<"+string).execute(frostThread);
		new FileCommand().execute(frostThread);
		new ReadAllCommand().execute(frostThread);
		new CompileCommand().execute(frostThread);
		frostThread.stack.get(frostThread.stack.size()-1).children.put(fileName, (FrostClass) frostThread.carry.get());
		//new CreateVariable(fileName).execute(frostThread);
	}

}
