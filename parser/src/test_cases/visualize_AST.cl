-- A simple cool program to visualize pictorially the AST generated

-- Parent Node Is Program

Class Main{		-- Class Child of Program 
	global_var : Int;	-- Attribute Child of Class of type Int
	global_var : Int;	-- Attribute Child of Class of type Int

	main() : Object {	-- Method Child of Class of _no_type
	   {				-- Block Child Of Method Main 
			global_var <- 1;	-- Assign Child Of Block => Int Child of Assign Child
	   }
	};
};