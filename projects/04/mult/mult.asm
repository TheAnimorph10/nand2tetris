// Multiply r0 by r1, and store result in r2
// 
// High level:
//
// while (r1 > 0) r2 = r2 + r0;

	@2		// set r2 to 0
	M=0
	@1
	D=M
	@3
	M=D
(BEGIN)
	@3		
	D=M		
	@END
	D;JLE	// check whether r1 is less than or equal to 0
	@0
	D=M		
	@2
	M=D+M	// add r0 to r2
	@3
	M=M-1	// decrement r1
	@BEGIN
	0;JMP	// return to start of loop
(END)
	@END
	0;JMP
	
	