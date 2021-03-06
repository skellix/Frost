
public class IndexPointer extends FrostCommand {

	private String name;

	public IndexPointer(String name) {
		this.name = name;
	}
	public String getInitString() {
		return this.getClass().getSimpleName()+"(\""+name+"\")";
	}
	@Override
	public void execute(FrostThread frostThread) {
		frostThread.indexes.put(name, frostThread.index.get(frostThread.index.size()-1).get());
	}

}
