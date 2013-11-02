
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
	C_ARITHMETIC,
	C_PUSH,
	C_POP,
	C_LABEL,
	C_GOTO,
	C_IF,
	C_FUNCTION,
	C_RETURN,
	C_CALL;
}