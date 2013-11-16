// %n: push segment (%s) %i
	@%s
	D=%0	// A for temp/pointer, M for local, argument, this, that
	@%i
	A=D+A
	D=M
	@SP
	M=M+1
	A=M-1
	M=D