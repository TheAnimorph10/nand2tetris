// %n: pop segment (%s) %i
	@%s
	D=%0 	// M for local, argument, this, that, A for temp, pointer
	@%i
	D=D+A	// add %i to %s
	@13
	M=D		// store the address to pop to in r13
	@SP
	AM=M-1	
	D=M
	M=0		// pop the value off the stack
	@13
	A=M
	M=D		// store it at the address calculated