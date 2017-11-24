(* Program to check for balanced parentheses using a simulation of stack machine with a string*)
class Main inherits IO {
	input_string : String;		-- String to be read as input
	stack_string : String;		-- String to be used as a stack
	iter : Int;			-- Iterator variable
	unbalanced : Bool;		-- Flag variable
	main() : Object {
	   {
	   	out_string("Enter an input string to check for balanced parentheses: ");
	   	input_string <- in_string();	-- Input

		-- Initialization of unbalanced and iterator variable
		unbalanced <- false;	
	   	iter <- 0;

		-- Running the loop till we reach the end of the input string
	   	while iter < input_string.length() loop {

			if input_string.substr(iter, 1) = ")" then	-- If the entry is a ")"

				if stack_string.length() = 0 then 	-- If the stack is empty, nothing to "pop"
					{
						iter <- input_string.length();	-- This is the logical break statement
						unbalanced <- true;		-- Since we are force quitting, set unbalanced to true
					}

				else					-- If the stack is not empty

					if stack_string.substr(stack_string.length()-1, 1) = "(" then	-- If the "top" is a "("
						{
							-- This is a placeholder "pop" statement
							stack_string <- stack_string.substr(0, stack_string.length()-1);
						}						

					else 				-- Only if the "top" is a "(", then "pop" is possible, else quit/break
					{
						iter <- input_string.length();	-- This is the logical break statement
						unbalanced <- true;		-- Since we are force quitting, set unbalanced to true
					}
					fi
				fi

			else 						-- The input could be "]" or "(" or "["

				if input_string.substr(iter, 1) = "]" then	-- If the entry is a "]"

					if stack_string.length() = 0 then 	-- If the stack is empty, nothing to "pop"
						{
							iter <- input_string.length();	-- This is the logical break statement
							unbalanced <- true;		-- Since we are force quitting, set unbalanced to true
						}

					else 					-- If the stack is not empty
						
						if stack_string.substr(stack_string.length()-1, 1) = "[" then -- If the "top" is "[" 
							{
								-- Same placeholder "pop" statement from above
								stack_string <- stack_string.substr(0, stack_string.length()-1);
							}

						else 			-- Only if the "top" is a "[", then "pop" is possible, else quit/break
						{
							iter <- input_string.length();	-- This is the logical break statement
							unbalanced <- true;		-- Since we are force quitting, set unbalanced to true
						}
						fi
					fi

				else 						-- If the entry is a "(" or "["

					if input_string.substr(iter, 1) = "(" then 
						stack_string <- stack_string.concat("(") -- "Push" statement
					else 
						stack_string <- stack_string.concat("[") -- "Push" statement
					fi
				fi
			fi;
			
	   		iter <- iter + 1;		-- Increment the iterator
	   	} pool;
	   	
	   	if unbalanced = true then 		-- If flag is true, then unbalanced
	   		{
	   			out_string(input_string).out_string(" is unbalanced.\n");
	   		}
	   	else 
	   		if not stack_string.length() = 0 then	-- If stack is non empty, then unbalanced
	   			{
	   				out_string(input_string).out_string(" is unbalanced.\n");
		   		}
	   		else 					-- If both stack is empty and flag is false, then only balanced
	   		{
	   			out_string(input_string).out_string(" is balanced.\n");
	   		}
	   		fi
	   	fi;
	   }
	};
};
