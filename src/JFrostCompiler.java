

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class JFrostCompiler {

	public JFrostCompiler() {
		// TODO Auto-generated constructor stub
	}

	public String compile(String src) {
		//src = src.replaceAll("\"\\s*\n\\s*", "\n\" ").replaceAll("\\s*\n\\s*\"", " \" \n");
		//src = src.replaceAll("\\{\\s*\n\\s*", "\n{ ").replaceAll("\\s*\n\\s*\\}", " } \n");
		//src = src.replaceAll("\\(\\s*\n\\s*", "\n( ").replaceAll("\\s*\n\\s*\\)", " ) \n");
		AtomicInteger index = new AtomicInteger(0);
		EnclosingSet set = getCurly(index, " "+src+"\ndie");
		FrostClass fc = getFrostMain(set);
		fc.name = "#global";
		StringBuilder out = new StringBuilder();
		generateCode(fc, out);
		return out.toString();
	}
	
	// convert to frost
	private FrostClass getFrostMain(EnclosingSet set) {
		FrostClass out = readSet(set);
		return out;
	}
	private FrostClass readSet(EnclosingSet set) {
		set.src = set.src.replaceAll("\\$([^\\$\\[\\s]+)\\[\\s*([^\\[\\]]+)\\s*\\]", "#arrayGet$1 $2");
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
					FrostSet newSet = readSet(set.children.get(word));
					out.children.put(word, newSet);
					((FrostCommandSet) out.children.get("#commands")).add(new GoSubCommand(word));
				} else if (word.startsWith("QuoteSet")) {
					String quoteSrc = set.children.get(word).src;
					((FrostCommandSet) out.children.get("#commands")).add(new StringCommand(quoteSrc));
				} else if (word.startsWith("GraveSet")) {
					String graveSrc = set.children.get(word).src;
					((FrostCommandSet) out.children.get("#commands")).add(new RunCommand(graveSrc));
				} else {
					FrostCommand newSet = new FrostCommandUtils().commandForString(word);
					if (newSet != null) {
						((FrostCommandSet) out.children.get("#commands")).add(newSet);
					}
				}
			}
		}
		return out;
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
		out.append("}");
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
					out.src += " "+result.toString()+" ";
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
				out.src += src.charAt(index.get());
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
	
	private EnclosingSet getResult(AtomicInteger index, String src) {
		switch (src.charAt(index.get())) {
		case '\"' : return getQuote(index, src);
		case '`' : return getGrave(index, src);
		case '{' : return getCurly(index, src);
		case '(' : return getParen(index, src);
		default : return null;
		}
	}

}
