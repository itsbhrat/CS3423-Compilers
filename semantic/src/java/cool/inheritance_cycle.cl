
Class A inherits E{ 
	a : Int;
};

Class B inherits A{ 
	b : Int;
};

Class C inherits B{ 
	c : Int;
};

Class D { --  To add cycle ::=> Class D inherits C{ 
	d : Int;
};

Class E inherits D{ 
	e : Int;
};

Class F inherits B{ 
	f : Int;
};

Class G inherits A{ 
	g : Int;
};

Class H inherits G{ 
	h : Int;
};

Class I inherits D{ 
	i : Int;
};

Class J inherits C{ 
	j : Int;
};

Class K inherits E{ 
	k : Int;
};

Class L inherits G{ 
	l : Int;
};

Class M inherits H{ 
	m : Int;
};

Class N inherits J{ 
	n : Int;
};

Class Main inherits A{ 
	b : Int;
	a : String;
	main() : Int {
	   {				
	   		b <- 1;
	   }
	};
};