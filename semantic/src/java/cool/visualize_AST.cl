
Class Func { 
	a : Int;
	abc(a : Int ,b : String) : Object {
	   {
			a <- 4;
	   }
	};
};

Class Main inherits Func{ 

	abc(a : String ,d: Int) : Object {
	   {
			a <- 5;
	   }
	};

	main() : Object {
	   {				
	   		a <- 1;
	   }
	};
};