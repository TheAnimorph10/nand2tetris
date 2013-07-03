import java.io.*;

/**
 * A solution to Nisan, N., and Schocken, S., (2005) The Elements of Computing 
 * Systems
 *
 * Chapter 6: Assembler
 *
 * Reads and parses Hack assembly files, and creates Hack binary executables.
 *
 * @author John Littlewood
 * @version 1
 *
 * Copyright John Littlewood, 2013, all rights reserved.
 */
 public class Assembler {
	
	public static void main(String[] args)
	{
		if (args.length < 2) {
			System.err.println("Insufficient arguments, need an input and output file.");
			System.exit(1);
		}
		
		File inputFile = new File(args[0]);
		File outputFile = new File(args[1]);
		
		try {
			Parser p = new Parser(inputFile);
			FileWriter fw = new FileWriter(outputFile);
			System.out.println("File parsed... beginning interpretation:");
			SymbolTable st = new SymbolTable();
			int index=0;
			while (p.hasMoreCommands()) {
				p.advance();
				Parser.CommandType type = p.commandType();
				switch (type) {
					case L_COMMAND:
						st.addEntry(p.symbol(), index);
						break;
					case C_COMMAND:
					case A_COMMAND:
						index ++;
				}
			}
			p.reset();
			int varAddress = 16;
			while (p.hasMoreCommands()) {
				p.advance();
				Parser.CommandType type = p.commandType();
				switch (type) {
					case A_COMMAND:
						int n;
						String symbol;
						try {
							n = Integer.parseInt(p.symbol());
						} catch (NumberFormatException e) {
							symbol = p.symbol();
							if (!st.contains(symbol)) {
								st.addEntry(symbol, varAddress);
								varAddress ++;
							}
							n = st.getAddress(symbol);
						}
						System.out.print("A: " + p.symbol() + "     \t\t\t\t Writing: ");
						for (int i=15; i>=0; i--) {
							char c = getBit(n, i);
							fw.write(c);
							System.out.print(c);
						}
						System.out.print('\n');
						fw.write('\n');
						break;
					case C_COMMAND:
						System.out.print("C: ");
						int comp = Code.comp(p.comp());
						int dest = Code.dest(p.dest());
						int jump = Code.jump(p.jump());
						System.out.print(p.comp() + " (" + comp + "), ");
						System.out.print(p.dest() + " (" + dest + "), ");
						System.out.print(p.jump() + " (" + jump + ")   \t\t Writing: ");
						for (int i=0; i<3; i++) {
							fw.write('1');
							System.out.print('1');
						}
						System.out.print(' ');
						for (int i=6; i>=0; i--) {
							fw.write(getBit(comp, i));
							System.out.print(getBit(comp, i));
						}
						System.out.print(' ');
						for (int i=2; i>=0; i--) {
							fw.write(getBit(dest, i));
							System.out.print(getBit(dest, i));
						}
						System.out.print(' ');
						for (int i=2; i>=0; i--) {
							fw.write(getBit(jump, i));
							System.out.print(getBit(jump, i));
						}
						System.out.print('\n');
						fw.write('\n');
						break;
					case L_COMMAND:
						break;
				}
			}
			fw.close();
		} catch (FileNotFoundException e) {
			System.err.println("Could not find input file");
			System.exit(2);
		} catch (IOException e) {
			System.err.println("Problem reading or writing to file.");
			System.exit(3);
		}
	
		
	}
	
	private static char getBit(int n, int index)
	{
		return (n >> index & 1) == 0 ? '0' : '1';
	}
}
	