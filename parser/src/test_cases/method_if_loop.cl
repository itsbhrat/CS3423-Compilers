-- demonstration of class with no parent(inherited type)

class A2I {

-- demonstration of a typical method with arguments(attribute list) returning an object of type
     c2i(char : String) : Int {

-- demonstration of if-then-else-fi syntax

(*

    if char = "0" then 0 else is represented in AST node as follows

        _cond       (non terminal of condition)
            _eq     (non terminal of '=')
                _object     (terminal of char (variable of Cool) => LHS of equality check)
                _string     (terminal of '0' => RHS of equality check)
            _int            (terminal of 0 : then condition of if)
            _cond           (non terminal specifying else of if)


    nested conditions are represented in AST as follows
    
    topmost if is the parent of 1st nested else if , 
    which in turn is the parent of 2nd nested else if and so on

*)
	if char = "0" then 0 else
	if char = "1" then 1 else
	if char = "2" then 2 else
        if char = "3" then 3 else
        if char = "4" then 4 else
        if char = "5" then 5 else
        if char = "6" then 6 else
        if char = "7" then 7 else
        if char = "8" then 8 else
        if char = "9" then 9 else

-- demonstration of dispatch with the caller being the class (self) itself
-- demonstration of nested/block expressions as well
        { abort(); 0; }
        fi fi fi fi fi fi fi fi fi fi
     };

(*
    class method is represented in AST node as follows
    
    _class
        _method
            _formal     (representing a parameter)
                param_name
                param_type
            return_type
        
            function body   (the remaining statements in the function)
*)
     a2i(s : String) : Int {

-- demonstration of dispatch with the caller being the class String
        if s.length() = 0 then 0 else

        (*
            dispatch s.length() is represented in a AST node as follows 
            
            _dispatch
                _object (terminal object name 's' => followed by length())
        
        *)

-- demonstration of the negate expression
	if s.substr(0,1) = "-" then ~a2i_aux(s.substr(1,s.length()-1)) else
        if s.substr(0,1) = "+" then a2i_aux(s.substr(1,s.length()-1)) else

-- demonstration of a function call
           a2i_aux(s)
        fi fi fi
     };

     a2i_aux(s : String) : Int {
-- demonstration of nested let block 
	(let int : Int <- 0 in	
           {	
               (let j : Int <- s.length() in
	          (let i : Int <- 0 in

-- demonstration of while-loop-pool statement

(*

    while i < j loop is represented in AST node as follows

        _loop       (non terminal of loop)
            _lt     (terminal of '<')
                _object     (terminal of i (object in program) => LHS of < check)
                _object     (terminal of j (object in program) => RHS of < check)
            _block          (non terminal telling loop body of loop)

*)
            while i < j loop
			{

			    int <- int * 10 + c2i(s.substr(i,1));
			    i <- i + 1;
			}
		    pool
		  )
	       );
              int;
	    }
        )
     };
};
