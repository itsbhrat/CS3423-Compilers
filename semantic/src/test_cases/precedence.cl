-- A simple cool program to show arithmetic precedence in action
-- * and / have higher precedence than + and -
-- This program's AST will show that the operators with higher precedence are lower in the AST, which is correct.

Class Main{		
	global_var : Int;	

	main() : Object {	
	   {				
			global_var <- 1;	-- Assign Child Of Block => Int Child of Assign Child
			global_var <- global_var - 1729 * 153;	-- Weird arithmetic 1
			global_var <- global_var + 576 / 24 + 324 - 676 * 125;	-- Weird arithmetic 2
	   }
	};
};
