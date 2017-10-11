class Main {
    i : Int;
    j : String;
    k : Bool <- true;
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
