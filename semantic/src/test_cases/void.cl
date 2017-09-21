
Class A {
	a : Int;
	func(b : Int) : Int {
	   {			
			a <- 1;
	   }
	};
};
 
class Main inherits IO{
	b : new A;
	c : Bool;
	main() : IO {
		{
			if (isvoid(b))
			then
				out_string("YO")
			else
				out_string("NO")
			fi;
		}
	};
};

