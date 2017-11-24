Class Main inhERits IO {
	main() : Object {
	   {
	   	out_string("This is awesome. \
	   	I have my first lexer!!!\n");
	        if 3 >= 2 then out_string("Awesome") else out_string("Not Awesome!!") fi;
	   }
	};
};

-- There are no issues in this code.
(* The list of tokens generated should be as follows:
	CLASS, TYPEID Main, INHERITS, TYPEID IO, {, OBJECTID main, (, ), :, TYPEID Object, {, OBJECTID out_string, (, STR_CONST, ), ;, }, }, ;, }, ;

   Note that the single line comment on top is omitted/skipped.
   All these lines are also skipped.
(* This code checks cases pertaining to:
   	Section 10.1: Type Identifiers and Object Identifiers, 
   	Section 10.2: String constants with an escaped newline, 
   	Section 10.3: Comments, both single line and multi-line (nested).
   	Section 10.4: Case Insensitivity of Keywords
   	Section 10.5: White Space (which is everywhere *)
   
   Cheers!! *)
