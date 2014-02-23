

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;

public class JFrostCompiler {
	
	String _nativeJavaCode = "";

	public JFrostCompiler() {
		// TODO Auto-generated constructor stub
	}

	public String compile(String src) {
		FrostClass fc = compileClasses(src);
		fc.name = "#global";
		StringBuilder out = new StringBuilder();
		generateCode(fc, out);
		return out.toString();
	}
	public FrostClass compileClasses(String src) {
		//src = src.replaceAll("\"\\s*\n\\s*", "\n\" ").replaceAll("\\s*\n\\s*\"", " \" \n");
		src = src.replaceAll("(\\{[^{}\n]*\n)", "\n$1").replaceAll("(\n[^{}\n]*\\})", "$1\n");
		//src = src.replaceAll("( ?(=~all|=~|==|!=|>|<|>=|<=|!|\\+|-|/|\\*|%|=) ?)", " $2 ");
		AtomicInteger index = new AtomicInteger(0);
		EnclosingSet set = getCurly(index, " "+src+"\ndie");
		return getFrostMain(set);
	}
	
	// convert to frost
	private FrostClass getFrostMain(EnclosingSet set) {
		FrostClass out = readSet(set);
		return out;
	}
	private FrostClass readSet(EnclosingSet set) {
		//set.src = set.src.replaceAll("\\$([^\\$\\[\\s]+)\\[\\s*([^\\[\\]]+)\\s*\\]", "#arrayGet$1 $2");
		set.src = set.src.replaceAll("( ?(=~all|=~|==|!=|>|<|>=|<=|!|\\+|-|/|\\*|%|=) ?)", " $2 ");
		FrostClass out = new FrostClass();
		out.children.put("#commands", new FrostCommandSet());
		String[] lines = set.src.split("\n");
		for (int lineIndex = 0 ; lineIndex < lines.length ; lineIndex ++) {
			String line = lines[lineIndex];
			List<String> words = new ArrayList<String>(Arrays.asList(line.split("\\s+")));
			Collections.reverse(words);
			for (int i = 0 ; i < words.size() ; i ++) {
				if (words.get(i).equals("")) {
					words.remove(i);
				}
			}
			for (int i = 0 ; i < words.size() ; i ++) {
				String word = words.get(i);
				if (word.startsWith("CurlySet")) {
					ArrayList<String> temp = new ArrayList<String>(words);
					if (lineIndex > 0) {
						List<String> t = new ArrayList<String>(Arrays.asList(lines[lineIndex-1].split("\\s+")));
						Collections.reverse(t);
						temp.addAll(t);
					}
					boolean found = false;
					if (i+2 < temp.size()) {
						if (temp.get(i+2).equals("class")) {
							found = true;
							FrostClass newSet = readSet(set.children.get(word));
							newSet.name = temp.get(i+1);
							newSet.parent = out;
							out.children.put(temp.get(i+1), newSet);
							i += 2;
						} else if (temp.get(i+2).equals("func")) {
							found = true;
							FrostClass newSet = readSet(set.children.get(word));
							newSet.name = temp.get(i+1);
							newSet.parent = out;
							out.children.put(temp.get(i+1), newSet);
							i += 2;
						}
					}
					if (!found) {
						FrostSet newSet = readSet(set.children.get(word));
						out.children.put(word, newSet);
						((FrostCommandSet) out.children.get("#commands")).add(new GoSubCommand(word));
					}
				}
				if (word.startsWith("ParenSet")) {
					if (words.get(i+1).equals("elsif")) {
						FrostCommand newSet = new FrostCommandUtils().commandForString(words.get(i+1));
						if (newSet != null) {
							((FrostCommandSet) out.children.get("#commands")).add(newSet);
						}
						FrostSet newSet2 = readSet(set.children.get(word));
						out.children.put(word, newSet2);
						((FrostCommandSet) out.children.get("#commands")).add(new GoSubCommand(word));
						i++;
					} else {
						FrostSet newSet = readSet(set.children.get(word));
						out.children.put(word, newSet);
						((FrostCommandSet) out.children.get("#commands")).add(new GoSubCommand(word));
					}
				} else if (word.startsWith("QuoteSet")) {
					String quoteSrc = set.children.get(word).src;
					((FrostCommandSet) out.children.get("#commands")).add(new StringCommand(quoteSrc));
				} else if (word.startsWith("GraveSet")) {
					String graveSrc = set.children.get(word).src;
					((FrostCommandSet) out.children.get("#commands")).add(new RunCommand(graveSrc));
				} else if (word.startsWith("#HashSet_")) {
					final HashSet hashSet = ((HashSet) set.children.get(word.substring(0, word.length()-1)));
					_nativeJavaCode += "public static void "+hashSet.name+"(FrostThread thread) {";
					_nativeJavaCode += hashSet.src+"\n";
					
					FrostClass newSet = new FrostClass() {{
						name = hashSet.name;
						children = new ConcurrentHashMap<String, FrostSet>() {{
							put("#commands", new FrostCommandSet() {{
								add(new StringCommand(JFrost.srcName+"."+hashSet.name));
								add(new RunnableCommand());
								add(new ReturnCommand());
							}});
						}};
					}};
					newSet.parent = out;
					out.children.put(hashSet.name, newSet);
				} else {
					if (word.contains("#SquareSet_")) {
						word = replaceSquareSets(word, set);
					}
					FrostCommand newSet = new FrostCommandUtils().commandForString(word);
					if (newSet != null) {
						((FrostCommandSet) out.children.get("#commands")).add(newSet);
					}
				}
			}
		}
		return out;
	}

	private String replaceSquareSets(String word, final EnclosingSet set) {
		word = new SuperReplace(word){

			@Override
			public String getReplacement(StringBuffer sb, Matcher matcher) {
				return "["+replaceSquareSets(set.children.get(matcher.group(1)).src, set).trim()+"]";
			}
		}.replace("#(SquareSet_\\d+)#");
		return word;
	}

	// convert to java code
	private void generateCode(FrostClass fc, StringBuilder out) {
		out.append("import java.util.ArrayList;\n");
		out.append("import java.util.concurrent.atomic.AtomicInteger;\n");
		out.append("import java.util.concurrent.ConcurrentHashMap;\n");
		out.append("public class "+JFrost.srcName+" {\n");
		out.append("public static void main(String[] args) {\n");
		out.append("new FrostThread(){{\n");
		out.append("index.add(new AtomicInteger(0));\n");
		out.append("stack.add(new FrostClass(){{\n");
		addSets(fc, out);
		out.append("}});\n");
		out.append("}}.execute();\n");
		out.append("}\n");
		addNatives(out);
		out.append("}");
	}
	private void addNatives(StringBuilder out) {
		out.append(_nativeJavaCode+"\n");
	}

	private void addSets(FrostSet set, StringBuilder out) {
		if (set instanceof FrostClass) {
			FrostClass fc = (FrostClass) set;
			out.append("name = \""+fc.name+"\";\n");
			out.append("children = new ConcurrentHashMap<String, FrostSet>(){{\n");
			for (Object key : fc.children.keySet().toArray()) {
				FrostSet thisSet = fc.children.get(key);
				out.append("put(\""+key+"\", new "+thisSet.getType()+"(){{\n");
				addSets(thisSet, out);
				out.append("}});\n");
			}
			out.append("}};\n");
		} else if (set instanceof FrostCommandSet) {
			FrostCommandSet fc = (FrostCommandSet) set;
			for (FrostCommand thisCommand : fc) {
				out.append("add(new "+thisCommand.getInitString()+");\n");
			}
		}
	}

	// get enclosing types
	private void seek(AtomicInteger index, String src, EnclosingSet out, char terminator) {
		index.getAndIncrement();
		for (; index.get() < src.length() ; index.getAndIncrement()) {
			if (src.charAt(index.get()) == terminator && src.charAt(index.get()-1) != '\\') {
				//index.getAndIncrement();
				return;
			} else {
				EnclosingSet result = getResult(index, src);
				if (result != null) {
					out.children.put(result.toString(), result);
					if (result instanceof SquareSet || result instanceof HashSet) {
						out.src += result.toString()+"#";
					} else {
						out.src += " "+result.toString()+" ";
					}
				} else {
					if (src.charAt(index.get()) == '/' && src.charAt(index.get()+1) == '/') {
						while (index.get()+1 < src.length() && src.charAt(index.get()+1) != '\n') {
							index.getAndIncrement();
						}
					} else {
						out.src += src.charAt(index.get());
					}
				}
			}
		}
	}
	private EnclosingSet getQuote(AtomicInteger index, String src) {
		QuoteSet out = new QuoteSet();
		index.getAndIncrement();
		for (; index.get() < src.length() ; index.getAndIncrement()) {
			if (src.charAt(index.get()) == '\"' && src.charAt(index.get()-1) != '\\') {
				break;
			} else {
				if (src.charAt(index.get()) == '\\') {
					out.src += src.charAt(index.get());
					index.getAndIncrement();
					out.src += src.charAt(index.get());
				} else {
					out.src += src.charAt(index.get());
				}
			}
		}
		//seek(index, src, out, '\"');
		return out;
	}
	private EnclosingSet getHash(AtomicInteger index, String src) {
		HashSet out = new HashSet();
		if (!(""+src.charAt(index.get()-1)).matches("\\s")) {
			return null;
		}
		int start = index.incrementAndGet();
		while (src.charAt(index.getAndIncrement()) != '{');
		String[] label = src.substring(start, index.get()-1).split("\\s+");
		if (label[0].equals("native")) {
			if (label.length > 2) {
				out.type = label[1];
				out.name = label[2];
			} else {
				System.err.println("More arguments expected in native method at: "+getLineNumber(index, src));
				System.exit(1);
			}
		}
		for (; index.get() < src.length() ; index.getAndIncrement()) {
			if (src.charAt(index.get()) == '#' && src.charAt(index.get()-1) != '\\') {
				break;
			} else {
				if (src.charAt(index.get()) == '\\') {
					out.src += src.charAt(index.get());
					index.getAndIncrement();
					out.src += src.charAt(index.get());
				} else {
					out.src += src.charAt(index.get());
				}
			}
		}
		//seek(index, src, out, '\"');
		return out;
	}

	private EnclosingSet getGrave(AtomicInteger index, String src) {
		GraveSet out = new GraveSet();
		seek(index, src, out, '`');
		return out;
	}
	private EnclosingSet getCurly(AtomicInteger index, String src) {
		CurlySet out = new CurlySet();
		seek(index, src, out, '}');
		out.src += "\nreturn";
		return out;
	}
	private EnclosingSet getParen(AtomicInteger index, String src) {
		ParenSet out = new ParenSet();
		seek(index, src, out, ')');
		out.src += "\nreturn";
		return out;
	}
	private EnclosingSet getSquare(AtomicInteger index, String src) {
		SquareSet out = new SquareSet();
		seek(index, src, out, ']');
		return out;
	}
	
	private EnclosingSet getResult(AtomicInteger index, String src) {
		switch (src.charAt(index.get())) {
		case '\"' : return getQuote(index, src);
		case '`' : return getGrave(index, src);
		case '{' : return getCurly(index, src);
		case '(' : return getParen(index, src);
		case '[' : return getSquare(index, src);
		case '#' : return getHash(index, src);
		default : return null;
		}
	}

	private int getLineNumber(AtomicInteger index, String src) {
		int count = 1;
		for (int i = 0 ; i < index.get() ; i ++) {
			if (src.charAt(i) == '\n') {
				count ++;
			}
		}
		return count;
	}
}
