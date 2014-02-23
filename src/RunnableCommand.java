import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class RunnableCommand extends FrostCommand {

	@Override
	public void execute(FrostThread frostThread) {
		String[] name = ((String) frostThread.carry.get()).split("\\.");
		try {
			Class main = Class.forName(name[0]);
			Method method = main.getDeclaredMethod(name[1], FrostThread.class);
			method.invoke(main, frostThread);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
