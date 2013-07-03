
import java.util.ArrayList;
import java.io.*;

/**
 * A solution to Nisan, N., and Schocken, S., (2005) The Elements of Computing 
 * Systems
 *
 * Chapter 6: Assembler
 *
 * Encapsulates access to the input code. Reads an assembly language command,
 * parses it, and provides convenient access to the command's components
 * (fields and symbols). In addition, removes all white space and comments.
 *
 * @author John Littlewood
 * @version 1
 *
 * Copyright John Littlewood, 2013, all rights reserved.
 */
class Parser {
	
	private ArrayList<String> commands;
	private int nextCommand;
	private String command;
	
	/**
	 * Opens the input file/stream and gets ready to parse it.
	 *
	 * @param f the file to be parsed
	 */
	public Parser(File f)
		throws FileNotFoundException, IOException
	{
		commands = new ArrayList<String>();
		nextCommand = 0;
		FileReader fr = new FileReader(f);
		String text = "";
		int c = 0;
		while ((c = fr.read()) != -1) {
			text += (char) c;
		}
		text = strip(text);
		commands = split(text);
	}
	
	/**
	 * Strip the text of white space and comments
	 */
	private String strip(String text)
	{
		// strip comments
		String commentless = "";
		for (int i=0; i<text.length(); i++) {
			char c = text.charAt(i);
			if (c == '/' && (text.charAt(i+1)) == '/') {
				while (i < text.length() && text.charAt(i) != '\n') {
					i++;
				}
				i--; // the while loop goes one past the newline - go back here
			}
			commentless += text.charAt(i);
		}
		// strip spaces and tabs
		String spaceless = "";
		for (int i=0; i<commentless.length(); i++) {
			char c = 0;
			while (i < commentless.length() && (c = commentless.charAt(i)) == ' ' || c == '\t') {
				i++;
			}
			spaceless += commentless.charAt(i);
		}
		// strip empty lines
		String result = "";
		for (int i=0; i<spaceless.length(); i++) {
			char c = 0;
			boolean newline = false;
			while (i < spaceless.length() && ((c = spaceless.charAt(i)) == '\n' || c == '\r')) {
				i++;
				newline = true;
			}
			if (newline && result.length() > 1 && result.charAt(result.length() - 1) != '\n') result += '\n';
			if (i<spaceless.length()) {
				c = spaceless.charAt(i);
				result += c;
			}
		}
		if (result.charAt(result.length()-1) == '\n') {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
	
	/**
	 * Split a string into lines, with each line being a separate entry in an
	 * ArrayList<String>
	 */
	private ArrayList<String> split(String text)
	{
		ArrayList<String> result = new ArrayList<String>();
		for (int i=0; i<text.length(); i++) {
			char c = 0;
			String line = "";
			while (i < text.length() && (c = text.charAt(i)) != '\n') {
				line += c;
				i++;
			}
			result.add(line);
		}
		return result;
	}
	
	/**
	 * Are there more commands in the input?
	 * 
	 * @return true if so
	 */
	public boolean hasMoreCommands()
	{
		return nextCommand < commands.size();
	}
	
	/**
	 * Reads the next command from the input and makes it the current command.
	 * Should be called only if <code>hasMoreCommands()</code> is true.
	 * Initially there is no current command.
	 */
	public void advance()
	{
		command = commands.get(nextCommand);
		nextCommand ++;
	}
	
	public void reset()
	{
		nextCommand = 0;
	}
	
	/**
	 * Returns the type of the current command:
	 * <ul>
	 * <li><code>A_COMMAND</code> for <code>@Xxx</code> where <code>Xxx</code> 
	 *     is either a symbol or a decimal number.</li>
	 * <li><code>C_COMMAND</code> for <code>dest=comp;jump</code>.</li>
	 * <li><code>L_COMMAND</code> (actually, pseudo-command) for <code>(Xxx)
	 *     </code> where <code>Xxx</code> is a symbol.
	 *
	 * @return A_COMMAND, C_COMMAND, or L_COMMAND
	 */
	public CommandType commandType()
	{			
		char c = command.charAt(0);
		switch (c) {
			case '@':
				return CommandType.A_COMMAND;
			case '(':
				return CommandType.L_COMMAND;
			default:
				return CommandType.C_COMMAND;
		}
	}
	
	/**
	 * Returns the symbol or decimal <code>Xxx</code> of the current command 
	 * <code>@Xxx</code> or <code>(Xxx)</code>. Should be called only when 
	 * <code>commandType()</code> is <code>A_COMMAND</code> or <code>L_COMMAND
	 * </code>.
	 *
	 * @return a string containing the symbol or decimal
	 */
	public String symbol()
	{
		String symbol;
		if (commandType() == CommandType.A_COMMAND) {
			symbol = command.substring(1);
		} else {
			symbol = command.substring(1, command.length()-1);
		}
		return symbol;
	}
	
	/**
	 * Returns the dest mnemonic in the current <em>C</em>-command (8 
	 * possibilities). Should be called only when <code>commandType()</code> is
	 * <code>C_COMMAND</code>.
	 *
	 * @return a string containing the mnemonic for the destination of the 
	 *         C-command
	 */
	public String dest()
	{
		if (!command.contains("=")) {
			return "";
		} else {
			return command.substring(0, command.indexOf('='));
		}
	}
	
	/**
	 * Returns the <code>comp</code> mnemonic in the current <em>C</em>-command
	 * (28 possibilities). Should be called only when <code>commandType()
	 * </code> is <code>C_COMMAND</code>.
	 *
	 * @return a string containing the computation in the C-command
	 */
	public String comp()
	{
		String comm = command;
		if (comm.contains("=")) {
			comm = comm.substring(comm.indexOf('=')+1);
		}
		if (comm.contains(";")) {
			comm = comm.substring(0, comm.indexOf(';'));
		}
		return comm;	
	}
	
	/**
	 * Returns the jump mnemonic in the current <em>C-command</em> (8 
	 * possibilities). Should be called only when <code>commandType()
	 * </code> is <code>C_COMMAND</code>.
	 */
	public String jump()
	{
		String comm = command;
		if (!comm.contains(";")) {
			return "";
		} else {
			return comm.substring(comm.indexOf(';')+1);
		}
	}	 
	
	public enum CommandType {
		A_COMMAND("A"), C_COMMAND("C"), L_COMMAND("L");
		
		String type;
		CommandType(String t)
		{
			type = t;
		}
	}
}	
	