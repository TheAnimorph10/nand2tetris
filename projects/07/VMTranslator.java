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
 public class VMTranslator {
	private static CodeWriter cw;
 
	public static void main(String[] args)
	{
		try {
			String fileName = args[0];
			try {
				if (fileName.substring(fileName.length()-3).equals(".vm")) {
					fileName = fileName.substring(0, fileName.length()-3);
				}
			} catch (StringIndexOutOfBoundsException e) {
				System.err.println("String oob.");
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("String oob.");
			}
			cw = new CodeWriter(new File(fileName + ".asm"));
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file or directory for writing.");
			System.exit(2);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("No output file name specified.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Error reading or writing file.");
			System.exit(3);
		}
		
		try {
			writeSetup();
			File f = new File(args[0]);
			if (f.isDirectory()) {
				loadDirectory(f);
			} else {
				loadFile(f);
			}
			cw.close();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("No input file name specified.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Error reading or writing file.");
			System.exit(3);
		}
	}
		
	private static void writeSetup()
		throws IOException
	{
		cw.writeSetup();	
	}
	
	private static void loadDirectory(File f)
		throws IOException
	{
		File[] files = f.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				String name = pathname.getName();
				int len = name.length();
				String extension = name.substring(len-3, len);
				return extension.equals(".vm");
			}});
		for (int i=0; i<files.length; i++) {
			loadFile(files[i]);
		}
	}
	
	private static void loadFile(File f)
		throws IOException
	{
		cw.setFileName(f.getName());
		VMParser p = new VMParser(f);
		while (p.hasMoreCommands())
		{
			p.advance();
			CommandType ct = p.commandType();
			String a1 = p.arg1();
			String a2 = p.arg2();
			System.out.println("Op: " + ct);
			System.out.println("Arg1: " + a1);
			System.out.println("Arg2: " + a2);
			switch(ct) {
				case C_ARITHMETIC:
					cw.writeArithmetic(p.arg1());
					break;
				case C_PUSH:
				case C_POP:
					cw.writePushPop(ct, p.arg1(), Integer.parseInt(p.arg2()));
					break;
			}
		}
	}
			
	
}