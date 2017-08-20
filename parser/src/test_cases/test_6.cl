class Main {
	func(string : S) : IO {
	   {
	   	new IO.out_string(string);
	   }
	};

	main() : Object {
	   {		
-- demonstrates invalid function call
		func("Hello World" : String);
	   }
	};
};
