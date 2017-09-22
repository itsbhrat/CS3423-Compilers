-- Program to demonstrate the versatility of the cycle checker in the inheritance
-- The inheritance is visually represented in the README.md file (or) inheritance_cycle.png
-- It is evident that there is a cycle in this inheritance graph

Class A inherits E {
	a : Int;
};

Class B inherits A { 
	b : Int;
};

Class C inherits B { 
	c : Int;
};

Class D inherits C { 
	d : Int;
};

Class E inherits D { 
	e : Int;
};

Class F inherits B { 
	f : Int;
};

Class G inherits A { 
	g : Int;
};

Class H inherits G { 
	h : Int;
};

Class I inherits D { 
	i : Int;
};

Class J inherits C { 
	j : Int;
};

Class K inherits E { 
	k : Int;
};

Class L inherits G { 
	l : Int;
};

Class M inherits H { 
	m : Int;
};

Class N inherits J { 
	n : Int;
};

Class Main inherits A{ 
	some_object : B;
	main() : Object {
	   {				
	   	some_object;
	   }
	};
};
