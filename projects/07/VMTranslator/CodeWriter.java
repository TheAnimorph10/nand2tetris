import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Properties;

/**
 * A solution to Nisan, N., and Schocken, S., (2005) The Elements of Computing 
 * Systems
 *
 * Chapter 7: Virtual Machine 1: Stack Arithmetic
 *
 * Translates VM commands into Hack assembly code
 *
 * @author John Littlewood
 * @version 1
 *
 * Copyright John Littlewood, 2013, all rights reserved.
 */
public class CodeWriter {
	private OutputStreamWriter out;
	private String currentFile;
	private String currentFunction;
	private int instNo;
	
	private static HashMap<String, String> asm;
	private static IOException comLoadException;
	static {
		try {
			asm = loadVMCommands();
		} catch (IOException e) {
			comLoadException = e;
		}
	}
	
	private Properties segments;
					
	
	/**
	 * Opens the output stream and gets ready to write into it.
	 *
	 * @param out the output stream to write to
	 */
	public CodeWriter(OutputStreamWriter out) throws IOException {
		if (comLoadException != null) {
			throw comLoadException;
		}
		this.out = out;
		instNo = 0;
		segments = new Properties();
		segments.load(new FileReader("segments.properties"));
	}
	
	/**
	 * Loads assembly translations for VM commands.
	 */
	private static HashMap<String, String> loadVMCommands() throws IOException
	{
		HashMap<String, String> result = new HashMap<String, String>();
		File dir = new File("asm");
		for (String filename : dir.list()) {
			String[] name = {filename.substring(0,filename.length()-4),filename.substring(filename.length()-4)};
			if (name[1].equals(".asm")) {
				String s = "";
				File f = new File("asm\\" + filename);
				Scanner sc = new Scanner(f);
				while (sc.hasNextLine()) {
					s += sc.nextLine() + "\n";
				}
				result.put(name[0], s);
			}
		}
		return result;
	}
	
	/**
	 * Informs the code writer that the translation of a new VM file is 
	 * started.
	 *
	 * @param fileName the name of the new VM file
	 */ 
	public void setFileName(String fileName) {
		instNo = 0;
		currentFile = fileName;
	}
	
	/**
	 * Writes the assembly code that is the translation of the given arithmetic
	 * command.
	 * 
	 * @param command the command to be translated
	 */
	public void writeArithmetic(String command) throws IOException {
		String com = asm.get(command);
		if (com == null) {	
			throw new RuntimeException("Command not found: " + command);
		}
		String formattedAsm = format(com, null);
		write(formattedAsm);
	}
	
	/**
	 * Writes the assembly code that is the translation of the given 
	 * <code>command</code>, where <code>command</code> is either 
	 * <code>C_PUSH</code> or <code>C_POP</code>.
	 *
	 * @param command the command to be translated (either <code>C_PUSH</code>
	 *                or <code>C_POP</code>
	 * @param segment the segment which will be pushed to or popped from
	 * @param index   the index within the segment which will be pushed to or 
	 *                popped from
	 */
	public void writePushPop(CommandType command, String segment, int index) 
		throws IOException 
	{
		String com;
		String[] args = null;
		if (segment.equals("constant")) {
			com = asm.get("pushConstant");
			args = null;
		} else {
			if (segment.equals("local") || segment.equals("argument") ||
					   segment.equals("this") || segment.equals("that")) {
				args = new String[]{"M"};
			} else if (segment.equals("temp") || segment.equals("pointer")) {
				args = new String[]{"A"};
			} else if (segment.equals("static")) {
				args = new String[]{"A"};
				segment = currentFile + "." + index;
				index = 0;
			}
			com = asm.get(command.toString());
		}
		if (com == null) {	
			throw new RuntimeException("Command not found: " + command);
		}
		segment = translateSeg(segment);
		String formattedAsm = format(com, currentFile, instNo, segment, index, args);
		write(formattedAsm);
	}
	
	/**
	 * Writes assembly code that effects the VM initialization, also called 
	 * <em>bootstrap code</em>. This code must be placed at the beginning of
	 * the output file.
	 *
	 */
	public void writeInit()	throws IOException 
	{
		String com = asm.get("init");
		com = format(com, null, null, instNo, null, null, null);
		write(com);
	}
	
	/**
	 * Writes assembly code that effects the <code>label</code> command.
	 *
	 * @param label The name of the label.
	 */
	public void writeLabel(String label) throws IOException 
	{
		String com = asm.get("label");
		com = format(com, currentFile, "testFun", instNo, null, null, new String[]{label});
		write(com);
	}
	
	/**
	 * Writes assembly code that effects the <code>goto</code> command.
	 * @param label the label to goto.
	 */
	public void writeGoto(String label)	throws IOException 
	{
		String com = asm.get("goto");
		com = format(com, currentFile, "testFun", instNo, null, null, new String[]{label});
		write(com);
	}
	
	/**
	 * Writes assembly code that effects the <code>if-goto</code> command.
	 * 
	 * @param label the label to goto
	 */
	public void writeIf(String label) throws IOException 
	{
		String com = asm.get("if-goto");
		com = format(com, currentFile, "testFun", instNo, null, null, new String[]{label});
		write(com);
	}
	
	/**
	 * Writes assembly code that effects the <code>call</code> command.
	 *
	 * @param functionName the function to call
	 * @param numArgs the number of arguments used in the function call
	 */
	public void writeCall(String functionName, Integer numArgs)
		throws IOException 
	{
		// TODO: this
		write(format("// %n: call %0 %1\n", new String[]{functionName, numArgs.toString()}));
	}
	
	/**
	 * Writes assembly code that effects the <code>return</code> command.
	 *
	 */
	public void writeReturn() throws IOException 
	{
		// TODO: this
		write(format("// %n: return\n", null));
	}
	
	/**
	 * Writes assembly code that effects the <code>function</code> command.
	 * 
	 * @param functionName the name of the function
	 * @param numLocals the number of local variables used in the function.
	 */
	public void writeFunction(String functionName, Integer numLocals)
		throws IOException 
	{
		// TODO: this
		write(format("// %n: function %0 %1\n", new String[]{functionName, numLocals.toString()}));
	}
	
	/**
	 * Translate the VM segment into the relevant assembly symbol
	 *
	 * @param segment the segment to translate
	 * @return the translated segment
	 */
	public String translateSeg(String segment)
	{
		String str = segments.getProperty(segment);
		if (str == null) {
			return segment;
		} else {
			return str;
		}
	}
	
	/**
	 * Format a piece of assembly by replacing its parameters with the relevant
	 * arguments. Replaces the flags with arguments as follows:
	 * <table>
	 *     	<tr><td>Flag</td><td>Argument</td></tr>
	 * 	   	<tr><td>%f</td><td>filename</td></tr>
	 *		<tr><td>%u</td><td>function</td></tr>
	 *		<tr><td>%n</td><td>instNo</td></tr>
	 *		<tr><td>%s</td><td>segment</td></tr>
	 *		<tr><td>%i</td><td>index</td></tr>
	 *		<tr><td>%0..%9</td><td>args[0..9]</td></tr>
	 * </table>
	 *
	 * @param asm the assembly to be formatted
	 * @param filename the current file 
	 * @param function the name of the current function
	 * @param instNo the current VM instruction number
	 * @param segment a VM memory segment
	 * @param index an integer index
	 * @param args any other arguments to be included
	 * @return the editted assembly
	 */
	private String format(String asm, String filename, String function,
						  Integer instNo, String segment, Integer index, 
						  String[] args) {
		if (asm != null) {
			if (filename != null) {
				asm = asm.replace("%f", filename);
			} else {
				asm = asm.replace("%f", "");
			}
			if (function != null) {
				asm = asm.replace("%u", function);
			} else {
				asm = asm.replace("%u", "");
			}
			if (instNo != null) {
				asm = asm.replace("%n", instNo.toString());
			} else {
				asm = asm.replace("%n", "");
			}
			if (segment != null) {
				asm = asm.replace("%s", segment);
			} else {
				asm = asm.replace("%s", "");
			}
			if (index != null) {
				asm = asm.replace("%i", index.toString());
			} else {
				asm = asm.replace("%i", "");
			}
			if (args != null) {
				for (int i=0; i<args.length; i++) {
					asm = asm.replace("%" + i, args[i]);
				}
			}
		}
		return asm;				  
	}
	/**
	 * Format a piece of assembly by replacing its parameters with the relevant
	 * arguments. Replaces the flags with arguments as follows:
	 * <table>
	 *     	<tr><td>Flag</td><td>Argument</td></tr>
	 * 	   	<tr><td>%f</td><td>filename</td></tr>
	 *		<tr><td>%n</td><td>instNo</td></tr>
	 *		<tr><td>%s</td><td>segment</td></tr>
	 *		<tr><td>%i</td><td>index</td></tr>
	 *		<tr><td>%0..%9</td><td>args[0..9]</td></tr>
	 * </table>
	 *
	 * @param asm the assembly to be formatted
	 * @param filename the current file 
	 * @param instNo the current VM instruction number
	 * @param segment a VM memory segment
	 * @param index an integer index
	 * @param args any other arguments to be included
	 * @return the editted assembly
	 */
	private String format(String asm, String filename, Integer instNo, String segment, Integer index, 
						  String[] args) {
		return format(asm, filename, null, instNo, segment, index, args);
	}
	
	/**
	 * Format a piece of assembly by replacing its parameters with the relevant
	 * arguments. Replaces the flags <code>%0-%9</code> in the assembly with 
	 * <code>args[0..9]</code>. Also inserts filename and instruction numbers,
	 * replacing <code>%f</code> and <code>%n</code> in the assembly.
	 *
	 * @param asm the assembly to be formatted
	 * @param args the arguments to be included
	 * @return the editted assembly
	 */
	private String format(String asm, String[] args) {
		return format(asm, currentFile, instNo, "", null, args);
	}
	
	/** 
	 * Write a string to the output. Also increments the instruction counter.
	 *
	 * @param the string to be written
	 */
	private void write(String str) throws IOException {
		instNo ++;
		out.write(str);
		out.flush();
	}
	
	/**
	 * Closes the output file.
	 */
	public void close() throws IOException {
		String end = format(asm.get("end"), null);
		write(end);
		out.close();
	}
	 
}
	
	