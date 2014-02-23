

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;


public class JFrost {
	
	public static Long version = 1L;
	
	public static boolean compile = true;
	public static boolean execute = false;
	
	public static String srcName;

	public static void main(String[] args) {
		if (args.length == 0) {
			args = "--help".split("\\s+");
		}
		for (int i = 0 ; i < args.length ; i ++) {
			if (args[i].startsWith("--")) {
				String arg = args[i].substring(2);
				if (arg.equals("help")) printHelp();
				else if (arg.equals("version")) printVersion();
			} else if (args[i].startsWith("-")) {
				String arg = args[i].substring(1);
				if (arg.equals("c")) compile = true;
				else if (arg.equals("C")) compile = false;
				else if (arg.equals("x")) execute = true;
				else if (arg.equals("X")) execute = false;
			}
		}
		File src = new File(args[args.length-1]);
		File srcBase = null;
		try {
			srcBase = new File(src.getCanonicalPath()).getParentFile();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		String className = args[args.length-1].replaceAll("(.+)\\.[^\\.]+$", "$1");
		srcName = new File(className).getName();
		if (compile) {
			//FrostClass mainClass = new FrostClass();
			JFrostCompiler jFrostCompiler = new JFrostCompiler();
			String code = jFrostCompiler.compile(new FrostUtills().readFile(src));
			//code = jFrostCompiler.createMain(mainClass, className, code);
			code = new FrostUtills().fixTabs(code);
			
			File frostBase = new File(JFrost.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
			File frostSrcBase = new File(frostBase.getAbsolutePath()+File.separatorChar+"src");
			
			new FrostUtills().printFile(srcName+".java", code);
			
			ArrayList<String> filesToBeCleaned = new ArrayList<String>();
			for (String fileName : frostSrcBase.list()) {
				String filePath = frostSrcBase.getAbsolutePath()+File.separatorChar+fileName;
				if (new File(filePath).isFile()) {
					if (!fileName.startsWith(".")) {
						if (!fileName.equals(srcName+".java")) {
							new FrostUtills().printFile(fileName, new FrostUtills().readFile(new File(filePath)));
							filesToBeCleaned.add(fileName);
						}
					}
				}
			}
			String javaRoot = "";
			for (String path : System.getenv("PATH").split(File.pathSeparator)) {
				if (path.contains("jdk")) {
					javaRoot = "\""+path+File.separatorChar;
				}
			}
			//String javaRoot = "\""+System.getenv().get("ProgramW6432")+"\\Java\\jdk1.7.0_45\\bin\\";
			String javaPath = new File(javaRoot.substring(1)).getParentFile().getAbsolutePath();
			System.setProperty("java.home", javaPath);
			JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
			if (javaCompiler != null) {
				StringBuilder filesToCompile = new StringBuilder();
				filesToCompile.append(new File(srcName).getName()+".java\n");
				for (String fileName : filesToBeCleaned) {
					filesToCompile.append(fileName+"\n");
				}
				new FrostUtills().printFile("fileList.txt", filesToCompile.toString());
				final ArrayList<String> createdClassFiles = new ArrayList<String>();
				javaCompiler.run(System.in, System.out, new OutputStream() {
					
					private String work = "";
					
					@Override
					public void write(int b) throws IOException {
						if (b == '\n') {
							//System.err.println(work);
							Matcher matcher = Pattern.compile("\\[wrote RegularFileObject\\[(.*)\\]\\]").matcher(work);
							if (matcher.find()) {
								createdClassFiles.add(matcher.group(1));
							}
							work = "";
						} else {
							work += (char) b;
						}
					}
				}, "-verbose", "@fileList.txt");
				new FrostUtills().printFile("manifest.txt", "Main-Class: "+srcName+"\n");
				try {
					List<String> command = new ArrayList<String>();
					command.addAll(Arrays.asList(javaRoot+"jar.exe\"", "cfm", "\""+srcName+".jar\"", "\"manifest.txt\""));
					for (String fileName : createdClassFiles) {
						command.add(fileName);
					}
					//System.out.println(command.toString().replace(", ", " "));
					Process process = new ProcessBuilder(command).start();
					Scanner scanner = new Scanner(process.getErrorStream());
					while (scanner.hasNextLine()) {
						System.err.println(scanner.nextLine());
					}
					scanner.close();
					process.waitFor();
					process = new ProcessBuilder("cmd", "/c", "copy", "\""+srcName+".jar\"", "\""+srcBase.getAbsolutePath()+File.separatorChar+srcName+".jar\"").start();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (String fileName : createdClassFiles) {
					new File(fileName).delete();
				}
				new File("manifest.txt").delete();
				new File("fileList.txt").delete();
				System.out.println("compile done!");
				for (String fileName : filesToBeCleaned) {
					if (!fileName.equals(srcName+".java")) new File(fileName).delete();
				}
//				new FrostUtills().printFile(
//						srcBase.getAbsolutePath()+File.separatorChar+srcName+".jar",
//						new FrostUtills().readFile(new File(frostBase.getAbsolutePath()+File.separatorChar+srcName+".jar")));
				if (execute) {
					try {
						String command = javaRoot+"java\"";
						final Process process = new ProcessBuilder(command,"-jar",srcBase.getAbsolutePath()+File.separatorChar+srcName+".jar").start();
						final AtomicBoolean processRunning = new AtomicBoolean(true);
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								Scanner scanner = new Scanner(process.getInputStream());
								while (processRunning.get() && scanner.hasNextLine()) {
									System.out.println(scanner.nextLine());
								}
							}
						}).start();
						new Thread(new Runnable() {

							@Override
							public void run() {
								Scanner errScanner = new Scanner(process.getErrorStream());
								while (processRunning.get() && errScanner.hasNextLine()) {
									System.err.println(errScanner.nextLine());
								}
							}
						}).start();
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								PrintWriter printWriter = new PrintWriter(process.getOutputStream());
								Scanner scanner = new Scanner(System.in);
								while (processRunning.get() && scanner.hasNextLine()) {
									printWriter.println(scanner.nextLine());
								}
							}
						}).start();
						process.waitFor();
						processRunning.set(false);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.err.println("java compiler not found in"+javaRoot);
			}
		}
		System.exit(0);
	}

	private static void printVersion() {
		System.out.printf("version %s", version);
		System.exit(0);
	}

	private static void printHelp() {
		File frostBase = new File(JFrost.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
		System.out.println(new FrostUtills().readFile(new File(frostBase.getAbsolutePath()+File.separatorChar+"help.txt")));
		System.exit(0);
	}

}