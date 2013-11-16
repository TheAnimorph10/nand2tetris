// %n: eq
	@SP
	AM=M-1
	D=M
	M=0
	A=A-1
	D=M-D
	@%f.EQ.%n
	D;JEQ
	D=0
	@%f.ASSIGN.%n
	0;JMP
(%f.EQ.%n)
	D=-1
(%f.ASSIGN.%n)
	@SP
	A=M-1
	M=D