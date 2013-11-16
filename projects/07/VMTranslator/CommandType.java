
/**
 * A solution to Nisan, N., and Schocken, S., (2005) The Elements of Computing 
 * Systems
 *
 * Chapter 7: Virtual Machine 1: Stack Arithmetic
 *
 * Enumeration of different command types available in VM code.
 *
 * @author John Littlewood
 * @version 1
 *
 * Copyright John Littlewood, 2013, all rights reserved.
 */
public enum CommandType {
	
	C_ARITHMETIC("arithmetic"),
	C_PUSH("push"),
	C_POP("pop"),
	C_LABEL("label"),
	C_GOTO("goto"),
	C_IF("if-goto"),
	C_FUNCTION("function"),
	C_RETURN("return"),
	C_CALL("call");
	
	private String command;
	CommandType(String command) {
		this.command = command;
	}
	
	public String toString()
	{
		return command;
	}
}