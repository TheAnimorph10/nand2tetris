// %n: push constant %i
	@%i		// put %i in D
	D=A
	@SP		
	M=M+1	// increment the stack pointer
	A=M-1	// address the previous stack pointer loc, and assign to D
	M=D