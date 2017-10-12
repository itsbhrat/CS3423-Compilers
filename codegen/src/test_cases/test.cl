class Main {
    i : Int;
    k : Bool <- true;
    j : String <- "Hello";

    foo(a: Int, b: Bool, c: String) : String {
        {        
            i <- 2 + 3 * 4;
            i <- 5 * 7 / 8;
            i <- 1 + i;
            a <- a + i * 5 / 5;
            "Hi";
        }
    };

    main() : Object {
        {
            i <- 1;
            i;
        }
    };

    bar(e: String, f: String, g: Bool, h: Int, l: Int, m: Bool) : Object {
        {
            j <- foo(10, true, "HI____________________");
        }
    };

    honey(a: Int, b:Int, c: Int, d:Int, e:String) : Int {
        if a = b
        then
            if b <= c
            then b+2
            else a+9
            fi
        else 
            if "harsh" = e
            then  b+2
            else  a+9
            fi
        fi
    };

    loop_trial(a: Bool, b:Int, c: Int) : Int { {
        --while a = c 
        --loop b+2
        --pool;
        c+12;
        b*8;
        c;
    }
    };
};
