class Main inherits IO {
    i : Int;
    k : Bool <- true;
    j : String <- "harsh";

    main() : Object {
        {
            out_string("Enter int : ");
            i <- in_int();
            i <- i*8+9*7;
            i <- ~i;
            out_int(i);
            k <- k = true = false;
            k <- not k;
            k <- not k;
            k <- "harsh" = j;
            if k
                then out_string("harsh")
                else out_string("sid")
            fi;
        }
    };

(*
    simple_loop_trial(a: Int, b: Int, c: Int) : Int {
        while a <= 10 
        loop   { 
            out_string("a : ");
            out_int(a);
            out_string("\n");

            out_string("b : ");
            out_int(b);
            out_string("\n");
            a <- a + 1;
            b <-b + a;
                }
        pool
    };

*)
  foo(a: Int, b: Bool, c: String, d: String) : String {
        {        
            i <- 2 + 3 * 4;
            out_string("1 : ");
            out_int(i);
            out_string("\n");
            i <- 5 * 7 / 8;
            

            out_string("1 : ");
            out_int(i);
            out_string("\n");

            i <- 1 + i;
            

            out_string("1 : ");
            out_int(i);
            out_string("\n");

            i <- ~1;
            
            out_string("1 : ");
            out_int(i);
            out_string("\n");

            a <- a + i * 5 / 5;
            
            out_string("1 : ");
            out_int(a);
            out_string("\n");

            k <- not k;

            out_string("hi boys");
            out_string(c);
            -- i <- loop_trial_3(1);
           -- i <- if_then_else_trial(1729);
            c <- "Hi";
        }
    };
};

(*

Class Main inherits A {
  do_this ( i : Int, j :String, k :Bool) : String {
      j
  };

  do_that ( a : String ) : String {
    a <- do_this(1, "OMG", false)
  };

*)