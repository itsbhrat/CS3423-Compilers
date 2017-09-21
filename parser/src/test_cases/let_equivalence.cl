(* 

demonstration of syntactic equivalence of
 (let a:Int in let b:Int in let c:Int in {
 and 
 (let a:Int ,b:Int, c:Int in {


When we visualize the AST  generated for the above 2 cases , we get the same AST.
Hence the above 2 statements are syntactically equivalent in Cool.

    let a : Int	is represented as a node in the AST with the following properties
	a (specifying variable name)
	Int (telling type of variable)
	__no_expr (telling this let has no assignment expression/value if expr is given)
        : _no_type ( acc to point #2)


        A small extract of the AST is given below.
        In both the cases we get a nested AST with the following characteristics of the let statement

            main method is parent of let a ,
            let a is parent of let b , 
		& let b is parent of let c ,
		let c is parent of bloc ,
		block is parent of assign ,
		assign is parent of int.

		_let
		     a
		     Int
		     _no_expr
		     : _no_type

		     _let
		     b
		     Int
		     _no_expr
		     : _no_type

			_let
		        c
		        Int
		        _no_expr
		        : _no_type

		         _block
		            #6
		             _assign
		              a
		              #6
		              _int
		                9
*)

class Let1 inherits IO
{
    main() : Object
    { 
        (let a:Int in let b:String in let c:Bool in {
            a <- 9;
        })
    };
};

class Let2
{
	main() : Object
	{
		(let a: Bool, b: String, c: Int in {
			b <- "10";		
		})
	};
};

class Main
{
	a : Int;
	main() : Object {
	{
		a <- 1;
	}
 };
};
