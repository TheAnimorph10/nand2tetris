// add
	@SP
	AM=M-1
	D=M
	A=A-1
	M=D+M
// push local 5
	@local
	D=A
	@5
	A=D+A
	D=M
	@SP
	M=M+1
	A=M-1
	M=D
// pop args 23
	@args
	D=M
	@23
	D=D+A
	@13
	M=D
	@SP
	AM=M-1
	D=M
	@13
	A=M
	M=D
