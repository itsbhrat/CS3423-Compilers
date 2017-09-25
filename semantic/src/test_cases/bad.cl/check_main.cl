-- Checking for existence of Main class is easy, so it is not covered
-- However, there is one issue. If Main contains a main() function and inherits from another class with another main() function
-- then, the return types are checked. If they don't match then the inheritance fails.
-- Due to this conflict, the main() function in Class Main is not added as a feature of it, leading to absence of main() also being flagged
-- Also only methods with same name are allowed in parent and child class (satisfying some conditions)
-- objects are not allowed

Class A {
	a : String;
	b : Int;
	c : Int;
	foo(x : Int) : Int {
		{
			c <- c * 10 + x / 5 - 3;	
		}
	};
	main() : String {
		{
			a <- "1";
		}
	};
};
 
class Main inherits A {
	b : Int;
	foo(y : Int) : Int {
		{
			b <- b * 1729 + 576 / 153 - 24;
		}
	};
	main() : Object {
		{
			b <- 1;
		}
	};
};
