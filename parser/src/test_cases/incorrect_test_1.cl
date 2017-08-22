-- demonstrates an error in the class syntax
-- The class not_type_so_wrong should start with a capital N. 
(*
	Error Generated:
	"incorrect_test_4.cl", line 4: syntax error at or near OBJECTID = not_type_so_wrong
	Compilation halted due to lex and parse errors
*)

class Main inherits not_type_so_wrong {

};
