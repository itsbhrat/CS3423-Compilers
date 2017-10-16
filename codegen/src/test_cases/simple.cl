(*
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
            out_int(loop_trial(0, 0, 5));
          --  out_string(foo(1,true, "********", "+++++++"));
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



    honey2(a: Int, b:Int, c: Int, d:Int, e:String) : Int {
        {
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
        fi;
        9;
    }
    };
    

    simple_loop_trial(a: Int, b: Int, c: Int) : Int {
        {
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
        pool;
        b;
    }
    };
    
    loop_trial_3(a : Int) : Object {
        while 1 < 0
        loop 1
        pool
    };

    loop_trial(a: Int, b: Int, c: Int) : Int { {
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
                    else out_string("case2\n")
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
*)



Class A {
  a : Int;
  b : Bool;
  c : String <- "hello";
  f : IO <- new IO;
 
 sagar () : String {
    {
        f@IO.out_string("Inside class A function sagar");

        c;
    }
  };
};


Class XYZ inherits IO {
  a : Int <- 4 + 5;
  b : Bool;
  c : String <- "Harsh";
  d : A <- new A ;
  ra : String;
  e : XYZ;
  g : IO <- new IO;
  f : IO <- new IO;
  h : Object <- new Object;
  i : Object;

  do_this ( i : Int, j :String, k :Bool) : String { {
    i <- i / 8;
    d@A.sagar();
    f@IO.out_string("test_string\nEnter:");
    f@IO.out_string("sagar"@String.substr(1, 2));
    f@IO.out_int(f@IO.in_int());
    f@IO.out_string("\n\n\n");
    f@IO.out_string(j);
    f@IO.out_int(ra@String.length());
    f@IO.out_string("so");

    if j = "harsh"
        then j <- "omeaga"
        else j <- "++++++++"
    fi;
      j;
  }
  };

  do_that ( a : String , b : XYZ) : String {
    a <- b@XYZ.do_this(1, a, false)
  };
};

class Main {
    a : XYZ <- new XYZ;
    b : IO <- new IO;
    c : Int <- new Int;
    main () : Object {
      b@IO.out_string(a@XYZ.do_that("a", a))
    };
};
