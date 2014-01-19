
public class AddCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		Object carry = frostThread.carry.get();
		if (carry instanceof Number) {
			Number right = (Number) frostThread.carry.get();
			frostThread.callbackNext();
			carry = frostThread.carry.get();
			if (carry instanceof Number) {
				Number left = (Number) frostThread.carry.get();
				if (left instanceof Double || right instanceof Double) {
					frostThread.carry.set(left.doubleValue() + right.doubleValue());
				} else {
					frostThread.carry.set(left.intValue() + right.intValue());
				}
			} else {
				System.err.println("left value of operation '+' must be a number");
			}
		} else {
			System.err.println("right value of operation '+' must be a number");
		}
	}

}
