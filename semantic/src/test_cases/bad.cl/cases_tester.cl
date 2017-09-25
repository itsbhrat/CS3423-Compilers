-- Program to demonstrate the case checking capabilities
-- This is erroneous program, since the branches in case have the same type

Class A {
	something (b : B) : Int {
		1729
	};
};

Class B inherits A {
};

Class Main {
	a : Int <- 1729;
	main() : Object {
		{
			if a <= 10 then a <- 0 else a <- 1 fi;
			case (new B) of
				some1 : A => (new B);				-- same type as the one below
				some2 : A => ((new A).something(some2));	-- case is used for type checking in some sense
				some3 : B => ((new A).something(some3));
			esac;
		}
	};
};
