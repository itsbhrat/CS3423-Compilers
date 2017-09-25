-- Program to demonstrate name checking in the classes
-- COOL mandates that there should not two classes with same name
-- COOL mandates that a class cannot inherit from an undefined class


Class A {
	a : Int;
};

Class B {
	b : String;
};

Class C {
	c : Bool;
};

Class A {
	a : Int;
};

Class D inherits Not_Defined_Class {
	d : Int;
};