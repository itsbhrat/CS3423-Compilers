class Main inherits IO {
    i : Int;
    k : Bool <- true;
    j : String <- "Hello";


  foo(a: Int, b: Bool, c: String, d: String) : String {
        {        
            i <- 2 + 3 * 4;
            i <- 5 * 7 / 8;
            i <- 1 + i;
            i <- ~1;
            a <- a + i * 5 / 5;
            k <- not k;
            out_string("hi boys");
            out_string(c);
            out_int(a);
            i <- loop_trial_3(1);
            i <- if_then_else_trial(1729);
            c <- "Hi";
        }
    };


  

    harsh_main() : Int {
        {
            i <- 1;
            i;
            5;
        }
    };

    main() : Object {
        {
            i <- 1;
            out_int(i);
        }
    };

    bar(e: String, f: String, g: Bool, h: Int, l: Int, m: Bool) : Object {
        {
            j <- foo(5 + 5, true, "HI____________________", j);
        }
    };

  
   honey(a: Int, b:Int, c: Int, d:Int, e:String) : Int {
        if a = b
        then{
            e;
            a+b;
        }
        else 
            a+d
        fi
    };


    honey2(a: Int, b:Int, c: Int, d:Int, e:String) : Int {
        if a = b
        then
            if b <= c
            then {
            e;
            2+b;
        }
            else a+9
            fi
        else 
            if "harsh" = e
            then  b+2
            else  a+9
            fi
        fi
    };
    
    


    while_inside_if(a: Int, b:Int, c: Int, d:Int, e:String) : Int {
        if a = b
        then
            while a <= b
            loop    a <- a + 1
            pool
        else 
            while a <= b
            loop    a <- a + 1
            pool
        fi
    };
(*    
    loop_trial(a: Int, b: Int, c: Int) : Int { {
        while a <= 10 
        loop
            while a < 8
            loop
                if a = c
                then
                    if a <= c
                    then {
                    a;
                    2+b;
                }
                    else a+9
                    fi
                else 
                    if a = b
                    then  b+2
                    else  a+9
                    fi
                fi
            pool
        pool;
    }
    };
 *)
    simple_loop_trial(a: Int, b: Int, c: Int) : Int {
        while a <= 10 
        loop    a+5
        pool
        
    };

    loop_trial_3(a : Int) : Int {
        while 1 < 0
        loop 1
        pool
    };

    if_then_else_trial(a : Int) : Int {
        if 1 < 0 then 3 else 5 fi
    };
     
    simple_if_loop_trial(a: Int, b: Int, c: Int) : Int {
        while a <= 10 
        loop
            if a = b
            then    c
            else    a
            fi
        pool
    }; 

};

(*
Class OMG {
  do_this ( i : Int, j :String, k :Bool) : String {
      j
  };

  do_that ( a : String ) : String {
    a <- do_this(1, "OMG", false)
  };
};

*)
  
