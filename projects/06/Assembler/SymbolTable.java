
import java.util.HashMap;

/**
 * A solution to Nisan, N., and Schocken, S., (2005) The Elements of Computing 
 * Systems
 *
 * Chapter 6: Assembler
 *
 * Keeps a correspondence between symbolic labels and numeric addresses.
 *
 * @author John Littlewood
 * @version 1
 *
 * Copyright John Littlewood, 2013, all rights reserved.
 */
public class SymbolTable {
	private HashMap<String, Integer> table;
	
	/**
	 * Creates a new empty symbol table
	 */
	public SymbolTable()
	{
		table = new HashMap<String, Integer>();
		
		// predefined symbols
		table.put("SP", 0x0000);
		table.put("LCL", 0x0001);
		table.put("ARG", 0x0002);
		table.put("THIS", 0x0003);
		table.put("THAT", 0x0004);
		for (int i=0; i<16; i++) {
			table.put("R" + i, i);
		}
		table.put("SCREEN", 0x4000);
		table.put("KBD", 0x6000);      		
	}
	
	/**
	 * Adds the pair (<code>symbol, address</code>) to the table.
	 *
	 * @param symbol the symbol
	 * @param address the address
	 */
	public void addEntry(String symbol, int address)
	{
		table.put(symbol, address);
	}
	
	/**
	 * Does the symbol table contain the given symbol?
	 *
	 * @param symbol the symbol to be checked
	 * @return true if the symbol is in the table
	 */
	public boolean contains(String symbol)
	{
		return table.containsKey(symbol);
	}
	
	/**
	 * Get the address associated with the symbol
	 *
	 * @param the symbol to be checked
	 * @return the address associated with the symbol
	 */
	public int getAddress(String symbol)
	{
		return table.get(symbol);
	}
}