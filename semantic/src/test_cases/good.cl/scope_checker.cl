-- Program to demonstrate the scope checking capabilities
-- Note that a, b, c are global
-- In method func(), there are local a, b, and c with different types as compared to the ones in global
-- There are neither type conflicts, nor pre-defined error for these local variables
-- and the assignments also work
-- Similarly for the let as well

class Main {
	a : Int;
	b : String;
	c : Bool;
	d : Int <- 1;
	func(a : Bool, b : Int, c : String) : Object {
		{
			a <- false;
			b <- 0;
			c <- "false";		-- c is Bool globally, but here String
		}
	};
	main() : Object {
		{
			a <- 1;
			b <- "true";
			c <- true;
			
			let a : String <- "Hello", b : Bool <- true, c : Int <- 1729 in {	-- b is String globally, but here Bool
				let c : String <- "Hi there!!" in {				-- c in Int in previous scope, Bool globally
					while b loop 						-- that is why this line works w.r.t. types
					{
						d <- d + 1;
						b <- d < 10;
					}
					pool;
				};
			};
		}
	};
};
