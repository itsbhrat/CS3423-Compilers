class Main inherits IO {
    i : Int;
    k : Bool <- true;
    j : String <- "Hello";

(*    foo(a: Int, b: Bool, c: String, d: String) : String {
        {        
            i <- 2 + 3 * 4;
            i <- 5 * 7 / 8;
            i <- 1 + i;
            a <- a + i * 5 / 5;
            k <- not k;
            out_string("hi boys");
            out_int(a);
            c <- "Hi";
        }
    };
*)
    main() : Object {
        {
            i <- 1;
            out_int(i);
        }
    };
(*
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
 


    honey(a: Int, b:Int, c: Int, d:Int, e:String) : Int {
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

    
    loop_trial(a: Int, b: Int, c: Int) : Int { {
        while a <= 10 
        loop{
                a;
                2+b;
            }
        pool;
        }
    };
    *)
};
