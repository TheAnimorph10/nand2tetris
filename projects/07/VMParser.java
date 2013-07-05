import java.io.*;

/**
 * A solution to Nisan, N., and Schocken, S., (2005) The Elements of Computing 
 * Systems
 *
 * Chapter 7: Virtual Machine I: Stack Arithmetic
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
public class VMParser {
	private RandomAccessFile file;
	
	private String current;
	private String nextLine;
	
	/**
	 * Opens the input file and gets ready to parse it.
	 *
	 * @param file the input file
	 */
	public VMParser(File file)
	{
		try {
			this.file = new RandomAccessFile(file, "r");
			getNextLine();
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file.");
			System.exit(2);
		} catch (IOException e) {
			System.err.println("Error reading file.");
			System.exit(3);
		}
		current = "";		
	}
	
	/**
	 * Are there more commands in the input?
	 *
	 * @return true if there are more commands
	 */
	public boolean hasMoreCommands()
	{
		return nextLine != null;
	}
	
	public String getWholeCommand()
	{
		return current;
	}
	
	/**
	 * Reads the next command from the input and makes it the current command.
	 * Should only be called if <code>hasMoreCommands()</code> is true.
	 * Initially there is no current command.
	 */
	public void advance()
	{
		current = nextLine;
		try {
			getNextLine();
		} catch (IOException e) {
			System.err.println("Could not read file.");
			System.exit(2);
		}
	}
	
	/** 
	 * Reads the next line from the file, and prepares it for use by the
	 * rest of the parser. 
	 */
	private void getNextLine()
		throws IOException
	{
		boolean gotLine = false;
		while (!gotLine) {
			String line = file.readLine();
			if (line != null) {
				line = stripLine(line);
			}
			if (line == null || line.length() >= 1) {
				gotLine = true;
				nextLine = line;
			}
		}
	}
		
	
	/**
	 * Strips all leading whitespace, tabs, and spaces after the first between
	 * commands and arguments, and comments from a string. 
	 *
	 * @param str the string to strip
	 * @return the stripped string
	 */
	public String stripLine(String str)
	{
		/* replace tabs with spaces, and skip to the end of the line when a 
		 * comment is started */
		String tr = "";
		for (int i=0; i<str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\t') {
				c = ' ';
			}
			if (c != '/') {
				tr += c;
			} else if (i+1<str.length() && str.charAt(i+1) == '/') {
				i = str.length();
			}
		}
		
		/* copy the first character, only if it is not a space, then replace 
		 * each cluster of spaces with a single space */
		String result = "";
		if (tr.length() > 0 && tr.charAt(0) != ' ') {
			result += tr.charAt(0);
		}
		for (int i=1; i<tr.length()-1; i++) {
			char c = tr.charAt(i);
			if (!(c == ' ' && tr.charAt(i-1) == ' ')) {
				result += c;
			}
		}
		if (tr.length() > 0) {
			char c = tr.charAt(tr.length()-1);
			if (c != ' ') {
				result += c;
			}
		}
		return result;
	}
	
	/**
	 * Returns the type of the current VM command. <code>C_ARITHMETIC</code> is
	 * returned for all the arithmetic commands.
	 *
	 * @return the type of the current command.
	 */
	public CommandType commandType()
	{
		String command = getCommandStr();
		
		switch (command) {
			case "add":
			case "sub":
			case "neg":
			case "eq":
			case "gt":
			case "lt":
			case "and":
			case "or":
			case "not":
				return CommandType.C_ARITHMETIC;
			case "push":
				return CommandType.C_PUSH;
			case "pop":
				return CommandType.C_POP;
			case "label":
				return CommandType.C_LABEL;
			case "goto":
				return CommandType.C_GOTO;
			case "if-goto":
				return CommandType.C_IF;
			case "function":
				return CommandType.C_FUNCTION;
			case "return":
				return CommandType.C_RETURN;
			case "call":
				return CommandType.C_CALL;
			default:
				throw new RuntimeException("Invalid command encountered: " + command);
		}
	}
	
	private String getCommandStr()
	{
		String command = "";
		for (int i=0; i<current.length() && current.charAt(i) != ' '; i++) {
			command += current.charAt(i);
		}
		return command;
	}
	
	/**
	 * Returns the first argument of the current command. In the case of 
	 * <code>C_ARITMETIC</code>, the command itself (<code>add, sub</code>, 
	 * etc.) is returned. Should not be called if the current command is 
	 * <code>C_RETURN</code>.
	 *
	 * @return the first argument of the current command.
	 */
	public String arg1()
	{
		String result = "";
		if (commandType() == CommandType.C_ARITHMETIC) {
			result = getCommandStr();
		} else {
			int i=0;
			while (i < current.length() && current.charAt(i) != ' ') {
				i++;
			}
			i++;
			while (i < current.length() && current.charAt(i) != ' ') {
				result += current.charAt(i);
				i++;
			}
		}
		return result;
	}
	
	/**
	 * Returns the second argument of the current command. Should be called
	 * only if the current command is <code>C_PUSH, C_POP, C_FUNCTION</code>,
	 * or <code>C_CALL</code>.
	 *
	 * @return the second argument of the current command.
	 */
	public String arg2()
	{
		String result = "";
		int i=0;
		for (int j=0; j<2; j++) {
			while (i < current.length() && current.charAt(i) != ' ') {
				i++;
			}
			i++;
		}
		while (i < current.length() && current.charAt(i) != ' ') {
			result += current.charAt(i);
			i++;
		}
		return result;
	}
}
	 