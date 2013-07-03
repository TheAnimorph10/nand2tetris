
/**
 * A solution to Nisan, N., and Schocken, S., (2005) The Elements of Computing 
 * Systems
 *
 * Chapter 6: Assembler
 *
 * Translates Hack assembly language mnemonics into binary codes.
 *
 * @author John Littlewood
 * @version 1
 *
 * Copyright John Littlewood, 2013, all rights reserved.
 */ 
public class Code {
	
	/**
	 * Returns the binary code of the dest mnemonic
	 *
	 * @param m the string mnemonic for the dest
	 * @return 3 bits representing the binary value of the dest
	 */
	public static char dest(String m)
	{
		char d = 0;
		if (m.contains("A")) {
			d += 1;
		} 
		d <<= 1;
		if (m.contains("D")) {
			d += 1;
		}
		d <<= 1;
		if (m.contains("M")) {
			d += 1;
		}
		return d;
	}
	
	/** Returns the binary code of the comp mnemonic
	 *
	 * @param m the string mnemonic for the comp
	 * @return 7 bits representing the binary value of the dest
	 */
	public static char comp(String m)
	{
		char c = 0;
		if (m.contains("M")) {
			c = 0b1000000;
		}
		m = m.replace('M', 'A');
		switch (m) {
			case ("0"):
				c += 0b101010;
				break;
			case ("1"):
				c += 0b111111;
				break;
			case ("-1"):
				c += 0b111010;
				break;
			case ("D"):
				c += 0b001100;
				break;
			case ("A"):
				c += 0b110000;
				break;
			case ("!D"):
				c += 0b001101;
				break;
			case ("!A"):
				c += 0b110001;
				break;
			case ("-D"):
				c += 0b001111;
				break;
			case ("-A"):
				c += 0b110011;
				break;
			case ("D+1"):
				c += 0b011111;
				break;
			case ("A+1"):
				c += 0b110111;
				break;
			case ("D-1"):
				c += 0b001110;
				break;
			case ("A-1"):
				c += 0b110010;
				break;
			case ("D+A"):
				c += 0b000010;
				break;
			case ("D-A"):
				c += 0b010011;
				break;
			case ("A-D"):
				c += 0b000111;
				break;
			case ("D&A"):
				c += 0b000000;
				break;
			case ("D|A"):
				c += 0b010101;
				break;
			default:
				c += 0;
		}
		return c;
	}
	
	/**
	 * @return the binary code of the jump mnemonic as a char
	 *
	 * @param m the mnemonic string to be encoded
	 */
	public static char jump(String m)
	{
		switch (m) {
			case "":
			case "null":
				return 0;
			case "JGT":
				return 1;
			case "JEQ":
				return 2;
			case "JGE":
				return 3;
			case "JLT":
				return 4;
			case "JNE":
				return 5;
			case "JLE":
				return 6;
			case "JMP":
				return 7;
			default:
				return 0;
		}
	}
}
	 
			