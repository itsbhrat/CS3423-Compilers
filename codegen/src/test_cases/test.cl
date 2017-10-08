class Main {
    i : Int;
    j : String;
    k : Bool;
    foo(i : Int) : String {
        {        
            "Hi";
        }
    };

    main() : Int {
        {
            i <- 1;
        }
    };

    bar() : Object {
        {
            j <- foo(10);        
        }
    };
};
