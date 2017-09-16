
Class A inherits C{ 
	a : Int;
};

Class B inherits A{ 
	c : Int;
};

Class C inherits B{ 
	c : Int;
};

Class H inherits C{ 
	h : Int;
};
 

class HHH inherits R {
	w : Int;
};


class R inherits A {
	r : Int;
};

 


Class Main inherits B{ 
	b : Int;
	main() : Int {
	   {				
	   		b <- 1;
	   }
	};
};