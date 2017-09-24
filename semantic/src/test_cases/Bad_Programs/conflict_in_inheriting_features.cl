-- Program to demonstrate name checking in the classes
-- COOL mandates that there should not two classes with same name
-- COOL mandates that a class cannot inherit from an undefined class


Class A {
	a : Int;
	c : Bool;
	a : String;
};

Class B inherits A{
	a : Int;
	b : String;
	c : Int;
};

Class C {
	c : Int;
	conflicting_function_return_type() : Int {
		{
			c <- 1;
		}
	};

	conflicting_function_formal_parameters(a : Int, b : String) : Int {
		{
			c <- 1;
		}
	};
};

Class D inherits C{

	a : String;

	conflicting_function_return_type() : String {
		{
			a <- "1";
		}
	};

	conflicting_function_formal_parameters(a : Int, b : Int) : Int {
		{
			c <- 1;
		}
	};
};