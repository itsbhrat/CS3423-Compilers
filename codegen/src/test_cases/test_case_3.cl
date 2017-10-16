-- Program to demonstrate weird loop constructs with and without if-then-else statements in them
class Main inherits IO {
	a : Looper <- new Looper;
	main() : Object {
		{
			out_int(a@Looper.loop_trial_1(1, 12, 2));
			out_int(a@Looper.loop_trial_2(11, 3, 10));
			out_int(a@Looper.loop_trial_3());
		}
	};
};

class Looper inherits IO {
	loop_trial_1(a : Int, b : Int, c : Int) : Int {
		{
			while a <= 10 
			loop {
				out_string("a : ");
				out_int(a);
				out_string("\n");

				out_string("b : ");
				out_int(b);
				out_string("\n");

				out_string("c : ");
				out_int(c);
				out_string("\n");

				a <- a + 1;
				b <- b + a;
			} pool;
			b;
		}
	};

	loop_trial_2(a : Int, b : Int, c : Int) : Int {
        {
			while a <= 10 
	        loop {
    	        while a < 8
    	        loop {
    	            if a = c
    	            then
    	                if a <= c
    	                then {
    	                	out_string("case1\n");
    	            	}
    	                else 
							out_string("case2\n")
    	                fi
    	            else 
    	                if a = b
    	                then  out_string("case3\n")
    	                else  out_string("case4\n")
    	                fi
    	            fi;
    	        a <- a + 1;
    	        }
    	        pool;
    	        a <- a + 1;
    	    }
    	    pool;
        	5;
		}
	};

	loop_trial_3() : Int {
		{
			while 1 = 1 loop {
				out_string("woohoo\n");
			} pool;
			1729;
		}
	};
};
