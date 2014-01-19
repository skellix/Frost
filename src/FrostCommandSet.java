import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


public class FrostCommandSet extends ArrayList<FrostCommand> implements FrostSet {

	@Override
	public String getType() {
		return "FrostCommandSet";
	}
	
}
