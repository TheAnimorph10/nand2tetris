// Fill the entire screen black when a key is pressed,
// and white when it is not.
//
// High level:
//
// while (true) { if (keyboard != 0) fillScreen()}
// fillScreen() {
//     for (i=screen; i<screenMax; i++) {
//			i = 0xffff
//     }
// }

	@24576		// store a pointer to the keyboard
	D=A
	@keyb
	M=D
	@16384		// store a pointer to the screen
	D=A
	@scr
	M=D
	@24575		// store a pointer to the end of the screen mem map
	D=A
	@scr_end
	M=D
	
(START)
	@keyb
	A=M			// check content of keyb
	D=M
	@FILL_BLACK
	D;JNE		// if not 0, fill black, else, fill white
(FILL_WHITE)
	@colour
	M=0
	@FILL
	0;JMP
(FILL_BLACK)
	@colour
	M=-1
(FILL)
	@scr
	D=M
	@i
	M=D			// i = scr
(LOOP)
	@i
	D=M
	@scr_end
	D=D-M
	@END_FILL
	D;JGT		// end loop if i>scr_end
	@colour
	D=M			// D = colour
	@i
	A=M			// M[i] = D:  screen[i] = colour
	M=D
	@i
	M=M+1		// increment i
	@LOOP
	0;JMP
(END_FILL)
	@START
	0;JMP
	
	