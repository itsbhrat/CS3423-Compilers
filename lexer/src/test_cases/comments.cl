-- test comment
class Main inherits IO {

    \
    (* COMMENT1 *)
    -- This is a valid single line comment
	main():IO {
	    {
	    -- many new lines -- 
	    
	    
	    --- valid single line comment
	    
	    
	    -- test cases
	        (* COMMENT2 *)
		    out_string("Hello World");
	    }
	    (*(* (* VALID NESTED COMMENTS *) *) *)
	};
	
	(*
	    -- single line inside multi line comment 
	*)
};
unmatched *) comments test case *)

(*(* EOF in Comment test case *)

