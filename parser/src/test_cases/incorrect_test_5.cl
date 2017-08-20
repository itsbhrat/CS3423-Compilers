-- demonstrates an error in the class syntax
-- There is a missing semicolon at the end of out_string in line# 13
(*
	Error Generated:
	"incorrect_test_5.cl", line 13: syntax error at or near '}'
	Compilation halted due to lex and parse errors
*)

class Main inherits IO {
	main() : Object {
	   {
		out_string("Hello World, this program will not compile") --missing semicolon
	   }
	};
};	   	
