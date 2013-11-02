package com.littlewood.nand2tetris.vmtranslator;

import java.io.*;

/**
 * A solution to Nisan, N., and Schocken, S., (2005) The Elements of Computing 
 * Systems
 *
 * Chapter 7: Virtual Machine 1: Stack Arithmetic
 *
 * Reads and translates Hack VM files into Hack Assembly
 *
 * @author John Littlewood
 * @version 1
 *
 * Copyright John Littlewood, 2013, all rights reserved.
 */
public class VMTranslator {
	CodeWriter output;
	
    /**
	 * Parses a vm file or collection of vm files and translates them into 
	 * Hack assembly code.
	 */ 
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			throw new InvalidArgumentException("Usage is 'VMTranslator <.vm file or directory>');
		}
		VMTranslator vmt = new VMTranslator(args[0]);
	}
	
	public VMTranslator(String filename) throws IOException {
		File f = new File(filename);
		if (f.isDirectory()) {
			setOutput(filename);
			for (String s : f.list()) {
				loadAndTranslate(s);
			}
		} else {
			setOutput(filename.substring(0, name.length()-3));
			loadAndTranslate(filename);
		}
		output.close();
	}
	
	/**
	 * Prepare the output file for writing
	 */
	private void setOutput(String filename) throws IOException {
		File f = new File(filename + ".asm");
		FileOutputStream out = new FileOutputStream(f);
		output = new CodeWriter(out);
	}
	
	/**
	 * Load and translate the file with the given name.
	 */
	public void loadAndTranslate(String s) {
		File file = load(s);
		if (file != null) {
			translate(file);
		}
	}
	
	/**
	 * Checks whether the file is a .vm file, and loads it.
	 */
	private File load(String name) {
		String extension = name.substring(name.length()-3);
		if (extension.equals(".vm")) {
			return new File(name);
		}
		else return null;
	}
	
	/**
	 * Translate the given file to assembly, and write to the output.
	 */
	 private void translate(File file) throws IOException
	 {
		output.setFileName(file.getName());
		Parser parser = new Parser(file);
		while (parser.hasMoreCommands()) {
			parser.advance();
			CommandType ct = parser.commandType();
			switch (ct) {
				case C_ARITHMETIC: 
					String cmd = parser.arg1();
					output.writeArithmetic(cmd);
					break;
				case C_PUSH:
				case C_POP:
					String seg = parser.arg1();
					int index = parser.arg2();
					writePushPop(ct, seg, index);
					break;
				case C_LABEL:
				case C_GOTO:
				case C_IF:
				case C_FUNCTION:
				case C_RETURN:
				case C_CALL:
					break;
			}
		}		
	}		
			
}