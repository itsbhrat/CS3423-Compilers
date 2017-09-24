-- Program to demonstrate name checking in the classes
-- COOL mandates that classes should not be of same name as the basic classes
-- and classes should not inherit from int, bool, string

Class Int {
	a : Int;
};

Class IO {
	b : String;
};

Class A inherits String {
	c : Bool;
};

Class Bool inherits Int {
	a : Int;
};
