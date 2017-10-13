class Main inherits IO {
    i : Int;
    --k : Bool <- true;
    j : String <- "Hello\n";
    main() : Object {
        {
            i <- 1;
            out_int(5);
            out_string("Hi");
            i <- in_int();
            out_int(i);
            out_string(j);
            j <- in_string();
            out_string(j);
        }
    };
};

