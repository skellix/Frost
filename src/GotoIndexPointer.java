
public class GotoIndexPointer extends FrostCommand {

	private String name;

	public GotoIndexPointer(String name) {
		this.name = name;
	}

	@Override
	public void execute(FrostThread frostThread) {
		frostThread.index.get(frostThread.index.size()-1).set(frostThread.indexes.get(name));
	}

}
