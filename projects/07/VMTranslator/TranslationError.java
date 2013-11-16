package com.littlewood.nand2tetris.vmtranslator;

public class TranslationError extends RunTimeException {
	private final int lineNumber;
	private final String messages;
	
	public String getMessage() {
		return message;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	@Override
	public String toString() {
		return "VM Translation Error:\n\t" + lineNumber + ": " + message;
	}
}