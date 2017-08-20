-- demonstration of class with no parent(inherited type)
class A2I {

-- demonstration of a typical method with arguments(attribute list) returning an object of type
     c2i(char : String) : Int {

-- demonstration of if-then-else-fi syntax
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

     a2i(s : String) : Int {

-- demonstration of dispatch with the caller being the class String
        if s.length() = 0 then 0 else

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
