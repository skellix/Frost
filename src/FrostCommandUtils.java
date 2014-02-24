
public class FrostCommandUtils {

	public FrostCommand commandForString(String word) {
// boolean
		if (word.equals("if")) {
			return new IfCommand();
		} else if (word.equals("else")) {
			return new ElseCommand();
		} else if (word.equals("elsif")) {
			return new ElseIfCommand();
		} else if (word.equals("while")) {
			return new WhileCommand();
		} else if (word.matches("==")) {
			return new EqualsCommand();
		} else if (word.matches("!=")) {
			return new NotEqualsCommand();
		} else if (word.matches("<")) {
			return new LessThanCommand();
		} else if (word.matches("<=")) {
			return new LessThanEqualsCommand();
		} else if (word.matches(">")) {
			return new GreaterThanCommand();
		} else if (word.matches(">=")) {
			return new GreaterThanEqualsCommand();
		} else if (word.matches("!")) {
			return new NotCommand();
		} else if (word.matches("equals")) {
			return new StringEqualsCommand();
		}
// math
		else if (word.equals("+")) {
			return new AddCommand();
		} else if (word.equals("-")) {
			return new SubtractCommand();
		} else if (word.equals("/")) {
			return new DivideCommand();
		} else if (word.equals("*")) {
			return new MultiplyCommand();
		} else if (word.equals("%")) {
			return new ModulusCommand();
		}
// string
		else if (word.equals("print")) {
			return new PrintCommand();
		} else if (word.equals(".")) {
			return new AppendCommand();
		} else if (word.equals("endl")) {
			return new StringCommand("\\n");
		} else if (word.equals("=~")) {
			return new RegexCommand();
		} else if (word.equals("=~all")) {
			return new RegexAllCommand();
		}
// variable
		else if (word.endsWith("$")) {
			return new CreateVariable(word.substring(0, word.length()-1));
		} else if (word.startsWith("$")) {
			return new GetVariable(word.substring(1));
		} else if (word.startsWith("#arrayGet")) {
			return new ArrayGet(word.substring("#arrayGet".length()));
		} else if (word.matches("=")) {
			return new SetCommand();
		} else if (word.endsWith("new")) {
			return new NewVariable();
		} else if (word.startsWith("::$")) {
			return new ExecuteVariable(word.substring(3));
		}
// primitive
		else if (word.matches("\\d+\\.\\d+")) {
			return new DoubleCommand(word);
		} else if (word.matches("\\d+")) {
			return new IntegerCommand(word);
		}
// thread
		else if (word.equals("die")) {
			return new DieCommand();
		}
// file
		else if (word.equals("file")) {
			return new FileCommand();
		} else if (word.equals("read")) {
			return new ReadCommand();
		} else if (word.equals("readLine")) {
			return new ReadLineCommand();
		} else if (word.equals("hasNext")) {
			return new HasNextCommand();
		} else if (word.equals("hasNextLine")) {
			return new HasNextLineCommand();
		} else if (word.equals("readAll")) {
			return new ReadAllCommand();
		} else if (word.equals("write")) {
			return new WriteCommand();
		} else if (word.equals("close")) {
			return new CloseStreamCommand();
		} else if (word.equals("scanLine")) {
			return new ScanLineCommand();
		}
// collection
		else if (word.equals("ArrayList")) {
			return new ArrayListCommand();
		} else if (word.equals("HashMap")) {
			return new HashMapCommand();
		} else if (word.equals("add")) {
			return new ArrayAddCommand();
		} else if (word.equals("put")) {
			//return new HashMapPutCommand();
		} else if (word.equals("remove")) {
			//return new HashMapRemoveCommand();
		}
// misc
		else if (word.equals("runnable")) {
			return new RunnableCommand();
		}else if (word.equals("return")) {
			return new ReturnCommand();
		} else if (word.startsWith("::")) {
			return new GoSubCommand(word.substring(2));
		} else if (word.endsWith("::")) {
			return new SubPointerCommand(word.substring(0, word.length()-2));
		} else if (word.endsWith(":")) {
			return new IndexPointer(word.substring(0, word.length()-1));
		} else if (word.startsWith(":")) {
			return new GotoIndexPointer(word.substring(1));
		} else if (word.equals("compile")) {
			return new CompileCommand();
		} else if (word.equals("import")) {
			return new ImportCommand();
		}
		return null;
	}

}
