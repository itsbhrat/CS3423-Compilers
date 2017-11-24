class Main inherits IO {
	_am_I_in_C : String;
	clearly_not: Bool;
	
	main() : SELF_TYPE {
	   {
	   	out_string("The output of this string 
	   	will be wrong and you know why");
		out_string("\H\e\l\l\o\ \f\r\i\e\n\d\s");
	    }
	};
};

-- Code pertains to Section 10.1 : Object Identifiers and 10.2 : Strings
-- Line 2 will give an error pertaining to invalid identifier, since all identifiers are supposed to start with small letter only
-- Line 7 and 8 will give Unterminated String Constant error since there is a missing escape character for the newline.
-- All the words before the double-quote in line 8 will be considered as tokens.
-- String that will be printed by out_string in line 9 will be Hello \frie\nds since \f and \n are valid escape sequences.
