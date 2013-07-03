import java.io.*;

/**
 * A solution to Nisan, N., and Schocken, S., (2005) The Elements of Computing 
 * Systems
 *
 * Chapter 7: Virtual Machine I: Stack Arithmetic
 * 
 * Translates VM commands into Hack assembly code.
 *
 * @author John Littlewood
 * @version 1
 *
 * Copyright John Littlewood, 2013, all rights reserved.
 */
public class CodeWriter {
	private FileWriter fw;
	private String vmFile;
	/**
	 * Opens the output file and gets ready to write into it.
	 * 
	 * @param file the file to be output to
	 */
	public CodeWriter(File file)
		throws FileNotFoundException, IOException
	{
		fw = new FileWriter(file);
		vmFile = null;
	}
	
	/**
	 * Informs the code writer that the translation of a new VM file is 
	 * started.
	 *
	 * @param fileName the name of the file being translated
	 */
	public void setFileName(String fileName)
	{
		vmFile = fileName;
	}
	
	/**
	 * Writes the assembly code that is the translation of the given arithmetic
	 * command.
	 *
	 * @param command the arithmetic command to be translated
	 */
	public void writeArithmetic(String command)
		throws IOException
	{
		switch (command) {
			case "add":
				writeBinaryOperation("+");
				break;
			case "sub":
				writeBinaryOperation("-");
				break;
			case "neg":
				writeUnaryOperation("-");
				break;
			case "eq":
			case "gt":
			case "lt":
				writeBooleanBinaryOperation(command);
				break;
			case "and":
				writeBinaryOperation("&");
				break;
			case "or":
				writeBinaryOperation("|");
				break;
			case "not":
				writeUnaryOperation("!");
				break;
		}
		
	}
	
	private void writeBooleanBinaryOperation(String op)
		throws IOException
	{
		
		fw.write("@sp		// Get the loc of the stack\n" +
				 "M=M-1		// Decrement the stack pointer\n" +
				 "A=M		// Address the location of the sp\n" +
				 "D=M		// Put the content of that loc in D\n" +
				 "A=A-1		// Address the loc below the new sp\n" +
				 "D=D-M		// Perform the calculation\n" +
				 "@TRUE\n" +
				 "D;J" + op	+ "// Jump if " + op + "\n" +
				 "@sp		// put the result on the top of the stack\n" +
				 "A=M-1		\n" +
				 "M=0		// result is false\n" +
				 "@DONE\n" +
				 "0;JMP\n" +
				 "(TRUE)\n" +
				 "@sp\n" +
				 "A=M-1\n" +
				 "M=-1		// result is true\n" +
				 "(DONE)\n");
	}
	
	private void writeBinaryOperation(String op)
		throws IOException
	{
		fw.write("@sp		// Get the first value from the stack\n" +					
				  "A=M-1	// Address the location below the sp\n" +	
				  "D=M		// Put the content of that loc in D\n" +
				  "@sp		// Back to the SP\n" +
				  "M=M-1	// Decrement it\n" +			
				  "A=M-1	// Address the loc below the new sp\n" +
				  "D=D" + op + "M	// Perform the operation\n" + 
				  "@sp		// Put the result back on top of the stack\n" +
				  "A=M-1	\n" +
				  "M=D		\n");
	}
	
	private void writeUnaryOperation(String op)	
		throws IOException
	{
		fw.write("@sp		\n" +
				 "A=M-1		\n" +
				 "M=" + op + "M	\n");
	}
	
	/**
	 * Writes the assembly code that is the translation of the given command,
	 * where <code>command</code> is either <code>C_PUSH</code> or <code>C_POP
	 * </code>.
	 *
	 * @param commandType push or pop
	 * @param segment 
	 * @param index
	 */
	public void writePushPop(CommandType command, String segment, int index)
		throws IOException
	{
		switch (command) {
			case C_PUSH:
				switch (segment) {
					case "constant":
						fw.write(
						  "@" + index + "\n" +
						  "D=A			 \n" +
						  "@sp			 \n" +
						  "M=M+1		 \n" +
						  "A=M-1		 \n" +
						  "M=D			 \n");
						break;
				}
		}
	}
	
	/**
	 * Closes the output file.
	 *
	 */
	public void close()
		throws IOException
	{
		fw.write("(END) \n" +
		         "@END  \n" +
				 "0;JMP"); 
		fw.close();
	}
}