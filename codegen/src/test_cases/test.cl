class Main {
    i : Int;
    k : Bool <- true;
    j : String <- "Hello";

    foo(a: Int, b: Bool, c: String) : String {
        {        
            "Hi";
        }
    };

    main() : Int {
        {
            i <- 1;
        }
    };

    bar(e: String, f: String, g: Bool, h: Int, l: Int, m: Bool) : Object {
        {
            j <- foo(10, true, "Hi");
        }
    };
};
