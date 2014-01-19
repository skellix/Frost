import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class FrostThread implements Runnable {
	
	public static List<FrostThread> threads = Collections.synchronizedList(new ArrayList<FrostThread>(){{
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			
			@Override
			public void run() {
				boolean done = false;
				while (threads.size() == 0);
				while (!done) {
					if (FrostThread.threads.size() == 0) {
						done = true;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.exit(0);
			}
		});
	}});
	
	public AtomicBoolean running = new AtomicBoolean(false);
	public List<AtomicInteger> index = Collections.synchronizedList(new ArrayList<AtomicInteger>());
	public List<FrostClass> stack = Collections.synchronizedList(new ArrayList<FrostClass>());
	public ConcurrentHashMap<String, Object> variables = new ConcurrentHashMap<String, Object>();
	public AtomicReference<Object> carry = new AtomicReference<Object>();
	public void execute() {
		running.set(true);
		Executors.newSingleThreadExecutor().execute(this);
	}
	@Override
	public void run() {
		try {
			threads.add(this);
			while (running.get()) {
				getNextAndIncrement().execute(this);
			}
			threads.remove(this);
		} catch (Exception e) {
			error("java error '"+e.getLocalizedMessage()+"'");
		}
	}
	public FrostCommand getNextAndIncrement() {
		return ((FrostCommandSet) stack.get(stack.size()-1).children.get("#commands")).get(index.get(index.size()-1).getAndIncrement());
	}
	public FrostCommand getNext() {
		return ((FrostCommandSet) stack.get(stack.size()-1).children.get("#commands")).get(index.get(index.size()-1).get()+1);
	}
	public void callbackNext() {
		int startStackSize = stack.size();
		getNextAndIncrement().execute(this);
		while (stack.size() > startStackSize) {
			getNextAndIncrement().execute(this);
		}
	}
	public void callback(FrostCommand fc) {
		int startStackSize = stack.size();
		fc.execute(this);
		while (stack.size() > startStackSize) {
			getNextAndIncrement().execute(this);
		}
	}
	public void error(String message) {
		System.err.println(message+" at:");
		System.err.println(getStackTrace());
		System.exit(0);
	}
	private String getStackTrace() {
		StringBuilder out = new StringBuilder("");
		for (int i = 0 ; i < stack.size() ; i ++) {
			out.append(stack.get(i).name+": "+index.get(i).get()+"\n");
		}
		return out.toString();
	}
}
