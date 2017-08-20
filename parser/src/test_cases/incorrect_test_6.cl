-- demonstrates an error in the function call syntax
-- The function call on line #19 has ": String" as invalid.
(*
	Error Generated:
	"incorrect_test_6.cl", line 20: syntax error at or near ':'
	Compilation halted due to lex and parse errors
*)

class Main {
	func(string : String) : IO {
	   {
	   	new IO.out_string(string);
	   }
	};

	main() : Object {
	   {		
		func("Hello World" : String); -- invalid function call
	   }
	};
};
