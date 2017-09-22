-- Program to demonstrate the scope checking capabilities
-- Note that a, b, c are global
-- In method func(), there are local a, b, and c with different types as compared to the ones in global
-- There are neither type conflicts, nor pre-defined error for these local variables
-- and the assignments also work

class Main {
	a : Int;
	b : String;
	c : Bool;
	func(a : Bool, b : Int, c : String) : Object {
		{
			a <- false;
			b <- 0;
			c <- "false";
		}
	};
	main() : Object {
		{
			a <- 1;
			b <- "true";
			c <- true;
		}
	};
};
