
import java.io.*;
import java.util.logging.*;
import java.util.HashMap;
/**
 * A solution to Nisan, N., and Schocken, S., (2005) The Elements of Computing 
 * Systems
 *
 * Chapter 7: Virtual Machine 1: Stack Arithmetic
 *
 * Handles the parsing of a single .vm file, and encapsulates access to the 
 * input code. It reads VM commands, parses them, and provides convenient 
 * access to their components. In addition, it removes all white space and 
 * comments.
 *
 * @author John Littlewood
 * @version 1
 *
 * Copyright John Littlewood, 2013, all rights reserved.
 */
 public class Parser {
	private static final Level logLevel = Level.WARNING;
	private Logger log;
 
	private FileReader input;
	private boolean eof = false;
	private Command currentCommand;
	private Command nextCommand;
	private boolean hasMoreCommands;
	private int lineNumber;
	private static HashMap<String, CommandType> commandTypes = new HashMap<String, CommandType>();
	static {
		commandTypes.put("add", CommandType.C_ARITHMETIC);
		commandTypes.put("sub", CommandType.C_ARITHMETIC);
		commandTypes.put("neg", CommandType.C_ARITHMETIC);
		commandTypes.put("eq", CommandType.C_ARITHMETIC);
		commandTypes.put("gt", CommandType.C_ARITHMETIC);
		commandTypes.put("lt", CommandType.C_ARITHMETIC);
		commandTypes.put("and", CommandType.C_ARITHMETIC);
		commandTypes.put("or", CommandType.C_ARITHMETIC);
		commandTypes.put("not", CommandType.C_ARITHMETIC);
		commandTypes.put("push", CommandType.C_PUSH);
		commandTypes.put("pop", CommandType.C_POP);
		commandTypes.put("label", CommandType.C_LABEL);
		commandTypes.put("goto", CommandType.C_GOTO);
		commandTypes.put("if-goto", CommandType.C_IF);
		commandTypes.put("function", CommandType.C_FUNCTION);
		commandTypes.put("call", CommandType.C_CALL);
		commandTypes.put("return", CommandType.C_RETURN);
	}
		
	
	/**
	 * Opens the input file/stream and gets ready to parse it.
	 *
	 * @param file .vm file for input
	 */
	public Parser(File file) throws IOException {
		log = Logger.getLogger("Parser");
		log.setUseParentHandlers(false);
		Handler h = new ConsoleHandler();
		h.setFormatter(new Formatter() {
			public String format(LogRecord record) {
				return record.getMessage() + '\n';
			}
		});
		h.setLevel(Level.INFO);
		log.addHandler(h);
		log.setLevel(logLevel);
		
		input = new FileReader(file);
		nextCommand = null;
		hasMoreCommands = nextLine();
		log.log(Level.FINE, "Parsing: \'" + file + "\'");
	}
	
	public static void main(String[] args) throws IOException {
		Logger testLog = Logger.getLogger("ParserTest");
		testLog.setLevel(Level.INFO);
		
		String file = args[0];
		Parser p = new Parser(new File(file));
		while (p.hasMoreCommands()) {
			String msg = "";
			p.advance();
			CommandType ct = p.commandType();
			msg += "CommandType=" + ct;
			if (ct != CommandType.C_RETURN) {
				String arg1 = p.arg1();
				msg += ", arg1=" + arg1;
				if (ct == CommandType.C_PUSH || ct == CommandType.C_POP || 
					ct == CommandType.C_FUNCTION || ct == CommandType.C_CALL) {
					int arg2 = p.arg2();
					msg += ", arg2=" + arg2;
				}
			}
			msg += '\n';
			testLog.info(msg);
		}		
	}
	
	/**
	 * Gets the next line from the input file.
	 */
	private boolean nextLine() throws IOException
	{
		log.info("Reading line");
		currentCommand = nextCommand;
		String line;
		boolean gotLine = false;
		while (!eof && !gotLine) {
			line = readLine();
			line = strip(line);
			if (line != null) {
				nextCommand = new Command(line);
				log.info("\tCommand input: <" + nextCommand.toString().concat("> "));
				gotLine = true;
			}
		}
		return gotLine;
	}
	
	/** 
	 * Reads a line from the input file. Sets eof if end of file is reached
	 * while reading the line.
	 */
	private String readLine() throws IOException
	{
		String result = "";
		int c = input.read();
		while (c != '\n' && c != -1) {
			if (c != '\r') {
				result += (char) c;
			}
			c = input.read();
		}
		
		if (c == -1) {
			eof = true;
		}
		lineNumber++;
		log.info("Line read: <" + result.concat("> "));
		return result;
	}
	
	/**
	 * Strip all white space and comments from a line
	 *
	 * @param line the line to be stripped
	 * @return the line without any comments, tabs, or more than one space in a
	 *         row, or null if the line is empty after stripping
	 */
	private String strip(String line)
	{
		log.info("\tStripping line: <" + line.concat(">"));
		// strip comments
		String commentless;
		int index = line.indexOf("//");
		if (index == -1) {
			commentless = line;
		} else {
			commentless = line.substring(0, index);
		}
		log.info("\t\tStripped comments: <" + commentless.concat(">"));
		// strip tabs
		String tabless = commentless.replace("\t", "");
		log.info("\t\tStripped tabs: <" + tabless.concat(">"));
		// strip consecutive spaces
		String spaceless = tabless;
		while (spaceless.contains("  ")) {
			spaceless = spaceless.replace("  ", " ");
		}
		log.info("\t\tStripped consecutive spaces: <" + spaceless.concat(">"));
		// strip the leading and trailing space, if there is one
		if (spaceless.length() > 0 && spaceless.charAt(0) == ' ') {
			spaceless = spaceless.substring(1);
		}
		if (spaceless.length() > 0 && spaceless.charAt(spaceless.length()-1) == ' ') {
			spaceless = spaceless.substring(0, spaceless.length()-1);
		}
		
		log.info("\t\tStripped line: <" + spaceless.concat(">"));
		if (spaceless.length() == 0) {
			return null;
		}
		return spaceless;
	}
	
	/**
	 * Are there more commands in the input?
	 *
	 * @return true if so
	 */
	public boolean hasMoreCommands() {
		return hasMoreCommands;
	}
	
	/**
	 * Reads the next command from the input and makes it the current command. 
	 * Should be called only if {@link #hasMoreCommands()} is true. Initally
	 * there is no current command.
	 */
	public void advance() throws IOException {
		hasMoreCommands = nextLine();
	}
	
	/**
	 * Returns the type of the current VM command. <code>C_ARITHMETIC</code> is returned for
	 * all the arithmetic commands.
	 *
	 * @return the command type
	 */
	public CommandType commandType() {
		return currentCommand.ct;
	}
	
	/**
	 * Returns the first argument of the current command. In the case of
	 * <code>C_ARITHMETIC</code> the command itself (<code>add</code>, 
	 * <code>sub</code>, etc.) is returned. Should not be called if the current
	 * command is <code>C_RETURN</code>.
	 *
	 * @return A string containing the first argument of the current command
	 */
	public String arg1() {
		return currentCommand.arg1;
	}
	
	/**
	 * Returns the second argument of the current command. Should only be 
	 * called if the current command is <code>C_PUSH</code>, 
	 * <code>C_POP</code>, <code>C_FUNCTION</code>, or <code>C_CALL</code>.
	 *
	 * @return the second argument (in <code>int</code> form)
	 */
	public int arg2() {
		return currentCommand.arg2;
	}
	
	private class Command {
		public final CommandType ct;
		public final String arg1;
		public final Integer arg2;
		
		/**
		 * Creates a new command object. Takes a line of VM code, and splits it
		 * into its constituent parts: <code><command> <arg1> <arg2></code>,
		 * then stores these for later use.
		 *
		 * @param text the command text
		 */
		public Command(String text) {
			String cm = "";
			int i=0;
			for (; i<text.length() && text.charAt(i)!=' '; i++) {
				cm += text.charAt(i);
			}
			i++;
			String a1 = null;
			String a2 = null;
			if (i < text.length()) {
				a1 = "";
				for (; i<text.length() && text.charAt(i)!=' '; i++) {
					a1 += text.charAt(i);
				}
				i++;
				if (i < text.length()) {
					a2 = "";
					for (; i<text.length(); i++) {
						a2 += text.charAt(i);
					}
				}
			}
			ct = commandTypes.get(cm);
			if (ct == CommandType.C_ARITHMETIC) {
				arg1 = cm;
			} else {
				arg1 = a1;
			}
			if (a2 != null) {
				arg2 = Integer.parseInt(a2);
			} else {
				arg2 = null;
			}
		}
		
		public String toString() {
			return ""+ ct + ":" + arg1 + ":" + arg2;
		}
	}
}