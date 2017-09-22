-- Program to demonstrate all types of dispatches that have been taken care of
-- Note that the inheritance graph is a unary tree A --> B --> Main
-- Hence foo in Main overrides foo in B and foo in B overrides foo in A in object of B

class A {
	foo (a : Int) : String {
		if a = 1 then "1" else
		if a = 2 then "2" else
		if a = 3 then "3" else
		"4"
		fi fi fi
	};
};

class B inherits A {
	foo (a : Int) : String {
		if a = 6 then "6" else
		if a = 7 then "7" else
		if a = 8 then "8" else
		"9"
		fi fi fi
	};
};

class Main inherits B {
	a : String ;
	i : Int <- 5;
	foo (a : Int) : String {
		if a = 5 then "5" else
		"0"
		fi
	};
	main() : Object {
		{
			a <- foo(i);
			new IO.out_string(a);	-- Will print 5 (by foo in main)
			i <- 3;

			a <- new A.foo(i);
			new IO.out_string(a);	-- Will print 3 (by foo in A (dispatch))
			i <- 1;

			a <- (new B)@A.foo(i);
			new IO.out_string(a);	-- Will print 1 (by foo in A called through B)
		}
	};
};
