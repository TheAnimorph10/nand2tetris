import java.io.*;
import java.util.HashMap;

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
	
	private int labelIndex;
	
	private static final HashMap<String, String> segText = new HashMap<String, String>();
	static {
		segText.put("local", "LCL");
		segText.put("argument", "ARG");
		segText.put("this", "THIS");
		segText.put("that", "THAT");
	};
	
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
		labelIndex = 0;
	}
	
	/**
	 * Initializes memory locations.
	 *
	 */
	public void writeSetup()
		throws IOException 
	{
		fw.write("@256\n" +
				 "D=A\n" +
				 "@SP\n" +
				 "M=D\n");
	}
	
	/**
	 * Write a comment into the output file
	 *
	 * @param comment the comment to be written
	 */
	 public void writeComment(String comment)
		throws IOException
	{
		fw.write("// " + comment + "\n");
	}
	
	/**
	 * Initializes memory locations.
	 *
	 */
	public void writeSetup()
	{
		fw.write("@256		\n" +
				 "D=A		\n" +
				 "@sp		\n" +
				 "M=D		\n");
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
				writeBooleanBinaryOperation(command.toUpperCase());
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
		
		fw.write("@SP \n" +
				 "M=M-1 \n" +
				 "A=M \n" +
				 "D=M \n" +
				 "A=A-1 \n" +
				 "D=M-D \n" +
				 "@TRUE." + labelIndex + "\n" +
				 "D;J" + op	+ "\n" +
				 "@SP \n" +
				 "A=M-1 \n" +
				 "M=0 \n" +
				 "@DONE." + (labelIndex + 1) + "\n" +
				 "0;JMP \n" +
				 "(TRUE." + labelIndex + ") \n" +
				 "@SP \n" +
				 "A=M-1 \n" +
				 "M=-1 \n" +
				 "(DONE." + (labelIndex + 1) + ") \n");
		labelIndex += 2;
	}
	
	private void writeBinaryOperation(String op)
		throws IOException
	{
		fw.write("@SP \n" +					
				  "A=M-1 \n" +	
				  "D=M \n" +
				  "@SP \n" +
				  "M=M-1 \n" +			
				  "A=M-1 \n" +
				  "D=M" + op + "D \n" + 
				  "@SP \n" +
				  "A=M-1 \n" +
				  "M=D \n");
	}
	
	private void writeUnaryOperation(String op)	
		throws IOException
	{
		fw.write("@SP \n" +
				 "A=M-1 \n" +
				 "M=" + op + "M \n");
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
						  "D=A \n" +
						  "@SP \n" +
						  "M=M+1 \n" +
						  "A=M-1 \n" +
						  "M=D \n");
						break;
					case "local":
					case "argument":
					case "this":
					case "
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