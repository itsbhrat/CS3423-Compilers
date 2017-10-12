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
            c <- "Hi";
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

    honey(a: Bool, b:Int, c: Bool, d:Bool, e:Int) : Int {
        if "hello" = j
        then b+2+4+e
        else b+2+8+e
        fi
    };

    loop_trial(a: Bool, b:Int, c: Bool) : Int { 
        while a = c 
        loop b+2
        pool
    };
};
