
parser grammar CoolParser;

options {
	tokenVocab = CoolLexer;
}

@header{
	import java.util.List;
}

@members{
	String filename;
	public void setFilename(String f){
		filename = f;
	}

/*
	DO NOT EDIT THE FILE ABOVE THIS LINE
	Add member functions, variables below.
*/
	int curLineNo;
	String className, parentName;
	String funcName, returnType;
	String objectName, typeName;
	String boolName, stringContent, integerString;
	int integerVal;
}

/*
	Add appropriate actions to grammar rules for building AST below.
*/

program		returns [AST.program value]: 
			cl=class_list EOF 
			{
				//Program begins with the first class
				$value = new AST.program($cl.value, $cl.value.get(0).lineNo);
			}
			;
			
//Program : [[class]]+
//Program has atleast one class
class_list	returns [ArrayList<AST.class_> value]
		@init
		{
			$value = new ArrayList<AST.class_>();
		}
		:
			(clss = class_ SEMICOLON {$value.add($clss.value);})+;

// Class definition
class_		returns [AST.class_ value]:
		// Class : CLASS TYPEID { features; } . No inheritance
		cl = CLASS type = TYPEID LBRACE ftr_lst = feature_list RBRACE
		{
			className 	= ($type.getText());
			curLineNo	= $cl.getLine();
			$value		= new AST.class_(className, filename, "Object", $ftr_lst.value, curLineNo);
		}
		// Class : CLASS TYPEID INHERITS TYPEID { features; } . With inheritance
		| cl = CLASS type = TYPEID INHERITS parent = TYPEID LBRACE ftr_lst = feature_list RBRACE
		{
			className 	= ($type.getText());
			curLineNo	= $cl.getLine();
			parentName	= ($parent.getText());
			$value 		= new AST.class_(className, filename, parentName, $ftr_lst.value, curLineNo);
		}
		;
// Class : CLASS TYPEID [INHERITS TYPEID] { [[FEATURE]]*; }
// feature_list defines the [[FEATURE]]*
feature_list	returns [ArrayList<AST.feature> value]
		@init
		{
			$value	= new ArrayList<AST.feature>();
		}
		:
			(ftr = feature SEMICOLON {$value.add($ftr.value);})*
		;

// Feature : OBJECTID LPAREN formal_parameters(optional) RPAREN COLON TYPEID LBRACE expression RBRACE -> methods/functions
// Feature : OBJECTID COLON TYPEID ASSIGN expression(optional)	-> variables
feature		returns [AST.feature value]:
		function = method
		{
			$value	= $function.value;
		}
		| variable = attr
		{
			$value	= $variable.value;
		}
		;

// Method : OBJECTID LPAREN PAREN COLON TYPEID LBRACE expression RBRACE		-> argument less functions
// Method : OBJECTID LPAREN formal_parameters RPAREN COLON TYPEID LBRACE expression RBRACE -> functions with arguments
method		returns [AST.method value]:
		function = OBJECTID LPAREN RPAREN COLON type = TYPEID LBRACE expr = expression RBRACE
		{
			funcName	= ($function.getText());
			returnType	= ($type.getText());
			curLineNo	= $function.getLine();
			$value		= new AST.method(funcName, new ArrayList<AST.formal>(), returnType, $expr.value, curLineNo);
		}
		| function = OBJECTID LPAREN formal_params = formal_list RPAREN COLON type = TYPEID LBRACE expr = expression RBRACE
		{
			funcName	= ($function.getText());
			returnType	= ($type.getText());
			curLineNo	= $function.getLine();
			$value		= new AST.method(funcName, $formal_params.value, returnType, $expr.value, curLineNo);
		}
		;
// Attribute list : A list of attribute
// Created for let expressions
attr_list	returns [ArrayList<AST.attr> value]
		@init
		{
			$value	= new ArrayList<AST.attr>();
		}
		:
			first_attr	= attr {$value.add($first_attr.value);}
			(COMMA more_attr= attr {$value.add($more_attr.value);})*
		;

// Attribute : OBJECTID COLON TYPEID
// Attribute : OBJECTID COLON TYPEID ASSIGN expression
attr		returns [AST.attr value]:
		variable = OBJECTID COLON type = TYPEID
		{
			objectName 	= ($variable.getText());
			typeName	= ($type.getText());
			curLineNo	= $variable.getLine();
			$value 		= new AST.attr(objectName, typeName, new AST.no_expr(curLineNo), curLineNo);
		}
		| variable = OBJECTID COLON type = TYPEID ASSIGN expr = expression
		{
			objectName	= ($variable.getText());
			typeName	= ($type.getText());
			curLineNo	= $variable.getLine();
			$value		= new AST.attr(objectName, typeName, $expr.value, curLineNo);
		}
		;
// formal_parameters is formal_list
// Formal_list : formal [[, formal]]*
// Formal_list contains atleast one Formal parameters
formal_list	returns [ArrayList<AST.formal> value]
		@init
		{
			$value	= new ArrayList<AST.formal>();
		}
		:
			f		= formal {$value.add($f.value);}
			(COMMA more_f	= formal {$value.add($more_f.value);})*
		;
			
// Formal : Used for declarations/function specifications
// Formal : OBJECTID COLON TYPEID
formal		returns [AST.formal value]:
		object_id = OBJECTID COLON type = TYPEID 
		{
			objectName	= ($object_id.getText());
			typeName 	= ($type.getText());
			curLineNo	= $object_id.getLine();
			$value		= new AST.formal(objectName, typeName, curLineNo);
		}
		;
// Branch_list is a list of branches.
// Occurs in CASE statements, where each conditionally split statement is a Branch.
// Branch_list : [[OBJECTID COLON TYPEID DARROW expression SEMICOLON]]+
// contains at least one Branch
branch_list	returns [ArrayList<AST.branch> value]
		@init
		{
			$value	= new ArrayList<AST.branch>();
		}
		:
			(brnch = branch SEMICOLON {$value.add($brnch.value);})+
		;

// Branch : OBJECTID COLON TYPEID DARROW expression
branch		returns [AST.branch value]:
		object_id = OBJECTID COLON type = TYPEID DARROW expr = expression
		{
			objectName	= ($object_id.getText());
			typeName	= ($type.getText());
			curLineNo	= $object_id.getLine();
			$value		= new AST.branch(objectName, typeName, $expr.value, curLineNo);
		}
		;

// Blocked_expr : nested expression
// Blocked_expr : { [[expression SEMICOLON]]+
blocked_expr	returns [ArrayList<AST.expression> value]
			@init
			{
				$value	= new ArrayList<AST.expression>();
			}
			:
			(expr = expression SEMICOLON {$value.add($expr.value);})+
			;

// Expression_list : list of expressions seperated by COMMA
// Expression_list : expression [[, expression]]*
// Used in a rule for expression
expression_list	returns [ArrayList<AST.expression> value]
			@init
			{
				$value	= new ArrayList<AST.expression>();
			}
			:
			(first_expr	= expression {$value.add($first_expr.value);}
			(COMMA more_expr= expression {$value.add($more_expr.value);})*)?
			;

// Expression rules
// Going from bottom-up in the grammar in the book
expression	returns [AST.expression value]:

		// Here we consider the precedence rules.
		// '.' greater than '@' greater than `~` greater than 'is void' greater than 'MDAS (Arithmetic)' greater than 
		// 'Comp(LE, LT, EQ)' greater than 'not' greater than 'assignment'
		
		// First in precedence is DISPATCH
		// function call expression / dispatch from an object :: expr DOT OBJECTID LPAREN expr_list RPAREN
		expr = expression DOT object_id = OBJECTID LPAREN expr_list = expression_list RPAREN
		{
			objectName	= ($object_id.getText());
			curLineNo	= $expr.value.lineNo;
			$value		= new AST.dispatch($expr.value, objectName, $expr_list.value, curLineNo);
		}

		// Next is '@'
		// function call expression / dynamic dispatch from an object :: expr ATSYM TYPEID DOT OBJECTID LPAREN expr_list RPAREN
		| expr = expression ATSYM type = TYPEID DOT object_id = OBJECTID LPAREN expr_list = expression_list RPAREN
		{
			objectName	= ($object_id.getText());
			typeName	= ($type.getText());
			curLineNo	= $expr.value.lineNo;
			$value 		= new AST.static_dispatch($expr.value, typeName, objectName, $expr_list.value, curLineNo);
		}

		// function call expression / dispatch expression :: OBJECTID LPAREN expr_list RPAREN
		// By definition: expr_list can be empty
		| object_id = OBJECTID LPAREN expr_list = expression_list RPAREN
		{
			objectName	= ($object_id.getText());
			curLineNo	= $object_id.getLine();
			$value		= new AST.dispatch(new AST.object("self", curLineNo), objectName, $expr_list.value, curLineNo);
		}

		// if expressions :: IF expression THEN expression ELSE expression FI
		| if_cond = IF expr1 = expression THEN expr2 = expression ELSE expr3 = expression FI
		{
			curLineNo	= $if_cond.getLine();
			$value		= new AST.cond($expr1.value, $expr2.value, $expr3.value, curLineNo);
		}
		
		// while expressions :: WHILE expression LOOP expression POOL
		| whl = WHILE expr1 = expression LOOP expr2 = expression POOL
		{
			curLineNo	= $whl.getLine();
			$value		= new AST.loop($expr1.value, $expr2.value, curLineNo);
		}

		// Nested expressions :: LBRACE blocked_expr RBRACE
		| lb = LBRACE nested_exprs = blocked_expr RBRACE
		{
			curLineNo	= $lb.getLine();
			$value		= new AST.block($nested_exprs.value, curLineNo);
		}

		// Let expressions :: LET attr_list (defined above) IN expression
		// The for loop basically removes each attribute and places them in an expression list pertaining to let
		| let_ = LET attributes = attr_list IN expr = expression
		{
			$value		= $expr.value;
			curLineNo	= $let_.getLine();  
			AST.attr this_attr;
			int i		= $attributes.value.size() - 1;
			while(i >= 0)
			{
				this_attr	= $attributes.value.get(i);
				$value		= new AST.let(this_attr.name, this_attr.typeid, this_attr.value, $value, curLineNo);
				i = i - 1;
			}
		}			

		// Case expressions :: CASE expression OF branch_list (defined above) ESAC
		| cse = CASE expr = expression OF branches = branch_list ESAC
		{
			curLineNo	= $cse.getLine();
			$value		= new AST.typcase($expr.value, $branches.value, curLineNo);
		}

		// Creating new object :: NEW TYPEID
		| nw = NEW type = TYPEID
		{
			typeName	= ($type.getText());
			curLineNo	= $nw.getLine();
			$value		= new AST.new_(typeName, curLineNo);
		}

		// Next is '~'
		// Complement of expression(integers) :: TILDE expression
		| tld = TILDE expr = expression
		{
			curLineNo	= $tld.getLine();
			$value		= new AST.comp($expr.value, curLineNo);
		}

		// Next is 'isvoid'
		// ISVOID checking with expression :: ISVOID expression
		| ivd = ISVOID expr = expression
		{
			curLineNo	= $ivd.getLine();
			$value		= new AST.isvoid($expr.value, curLineNo);
		}

		// MDAS -> Multiplication, Division, Addition, Subtraction
		// Arithmetic rules follow ::
		// expression * or / or + or -
		| expr1 = expression STAR expr2 = expression
		{
			curLineNo	= $expr1.value.lineNo;
			$value		= new AST.mul($expr1.value, $expr2.value, curLineNo);
		}
		| expr1 = expression SLASH expr2 = expression
		{
			curLineNo	= $expr1.value.lineNo;
			$value		= new AST.divide($expr1.value, $expr2.value, curLineNo);
		}
		| expr1 = expression PLUS expr2 = expression
		{
			curLineNo	= $expr1.value.lineNo;
			$value		= new AST.plus($expr1.value, $expr2.value, curLineNo);
		}
		| expr1 = expression MINUS expr2 = expression
		{
			curLineNo	= $expr1.value.lineNo;
			$value		= new AST.sub($expr1.value, $expr2.value, curLineNo);
		}

		// Next in precedence is Comparison: <=, <, =
		// Less than or equal to comparison of expressions(integers) :: expression LE expression
		| expr1 = expression LE expr2 = expression
		{
			curLineNo	= $expr1.value.lineNo;
			$value		= new AST.leq($expr1.value, $expr2.value, curLineNo);
		}

		// Less than comparison of expressions(integers) :: expression LT expression
		| expr1 = expression LT expr2 = expression
		{
			curLineNo	= $expr1.value.lineNo;
			$value		= new AST.lt($expr1.value, $expr2.value, curLineNo);
		}

		// equality of expressions(integers, bools, strings) :: expression EQUALS expression
		| expr1 = expression EQUALS expr2 = expression
		{
			curLineNo	= $expr1.value.lineNo;
			$value		= new AST.eq($expr1.value, $expr2.value, curLineNo);
		}

		// Next is NOT operator
		// NOT expression
		| nt = NOT expr = expression
		{
			curLineNo	= $nt.getLine();
			$value		= new AST.neg($expr.value, curLineNo);
		}

		// Then comes assignment
		// assignment expression :: OBJECTID ASSIGN expression
		|<assoc=right> object_id = OBJECTID ASSIGN expr1 = expression
		{
			objectName	= ($object_id.getText());
			curLineNo	= $expr1.value.lineNo;
			$value		= new AST.assign(objectName, $expr1.value, curLineNo);
		}
		
		// parenthesized expression have the same value as non parenthesized expression. That is why the value is passed on.
		| LPAREN expr = expression RPAREN
		{
			$value		= $expr.value;
		}

		// object = OBJECTID (objects)
		| object_id = OBJECTID
		{
			objectName	= ($object_id.getText());
			curLineNo	= $object_id.getLine();
			$value		= new AST.object(objectName, curLineNo);
		}

		// integer_const = int_const (integer constants)
		| integer = INT_CONST
		{
			integerString	= ($integer.getText());
			integerVal	= Integer.parseInt(integerString);
			curLineNo	= $integer.getLine();
			$value		= new AST.int_const(integerVal, curLineNo);
		}

		// string_const = str_const (string literals)
		| string_literal = STR_CONST
		{
			stringContent	= ($string_literal.getText());
			curLineNo	= $string_literal.getLine();
			$value 		= new AST.string_const(stringContent, curLineNo);
		}

		// bool_const = true | false (with case-insensitivity)
		| bool = BOOL_CONST
		{
			boolName	= ($bool.getText());
			curLineNo	= $bool.getLine();
			if(boolName.charAt(0) == 't')
			{
				$value = new AST.bool_const(true, curLineNo);
			}
			else
			{
				$value = new AST.bool_const(false, curLineNo);
			}
		}
		;
