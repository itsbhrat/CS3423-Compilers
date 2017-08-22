-- A simple cool program to show arithmetic precedence in action

-- Parent Node Is Program

Class Main{		-- Class Child of Program 
	global_var : Int;	-- Attribute Child of Class of type Int

	main() : Object {	-- Method Child of Class of _no_type
	   {				-- Block Child Of Method Main 
			global_var <- 1;	-- Assign Child Of Block => Int Child of Assign Child
			global_var <- global_var - 1729 * 153;
			global_var <- global_var + 576 / 24 + 324 - 676 * 125;
	   }
	};
};
