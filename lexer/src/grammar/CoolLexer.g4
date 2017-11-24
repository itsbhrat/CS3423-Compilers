lexer grammar CoolLexer;

tokens{
	ERROR,
	TYPEID,
	OBJECTID,
	BOOL_CONST,
	INT_CONST,
	STR_CONST,
	LPAREN,
	RPAREN,
	COLON,
	ATSYM,
	SEMICOLON,
	COMMA,
	PLUS,
	MINUS,
	STAR,
	SLASH,
	TILDE,
	LT,
	EQUALS,
	LBRACE,
	RBRACE,
	DOT,
	DARROW,
	LE,
	ASSIGN,
	CLASS,
	ELSE,
	FI,
	IF,
	IN,
	INHERITS,
	LET,
	LOOP,
	POOL,
	THEN,
	WHILE,
	CASE,
	ESAC,
	OF,
	NEW,
	ISVOID,
	NOT
}

/*
  DO NOT EDIT CODE ABOVE THIS LINE
*/

@members{

	/*
		YOU CAN ADD YOUR MEMBER VARIABLES AND METHODS HERE
	*/

	/**
	* Function to report errors.
	* Use this function whenever your lexer encounters any erroneous input
	* DO NOT EDIT THIS FUNCTION
	*/
	public void reportError(String errorString){
		setText(errorString);
		setType(ERROR);
	}

	public void processString() {
		Token t = _factory.create(_tokenFactorySourcePair, _type, _text, _channel, _tokenStartCharIndex, getCharIndex()-1, _tokenStartLine, _tokenStartCharPositionInLine);
		String text = t.getText();
		text = text.substring(1, text.length()-1);
		
		//write your code to test strings here
		StringBuilder new_text = new StringBuilder (0);
		
		// Converting the string with separate escape characters into one string where the escape sequences are merged
		int i = 0;
		while(i < text.length())
		{
			if(text.charAt(i) == '\\')
			{
				if(text.charAt(i+1) == 'b')
				{
					new_text.append('\b');	
				}
				else if(text.charAt(i+1) == 't')
				{
					new_text.append('\t');
				}
				else if(text.charAt(i+1) == 'n')
				{
					new_text.append('\n');
				}
				else if(text.charAt(i+1) == 'f')
				{
					new_text.append('\f');
				}
				else if(text.charAt(i+1) == '\"')
				{
					new_text.append('\"');
				}
				else if(text.charAt(i+1) == '\\')
				{
					new_text.append('\\');
				}
				else
				{
					new_text.append(text.charAt(i+1));
				}
				i = i + 1;
			}
			else
			{
				new_text.append(text.charAt(i));
			}
			i = i + 1;			
		}
		
		String new_text_string = new_text.toString();
		setText(new_text_string);
	}
}

/*
	WRITE ALL LEXER RULES BELOW
*/

ERROR	: UNTERM_STR_CONST .(EOF) { reportError("EOF in string constant"); } 		// EOF seen before string termination
	| UNTERM_STR_CONST { reportError("Unterminated string constant"); }		// String not terminated before newline
	| UNTERM_STR_CONST ('\\u0000') UNTERM_STR_CONST '"' { reportError("String contains null character"); }	// String containing null
	;
		
// Symbols in the Grammar
LPAREN	: '(' ;										// Left Parentheses
RPAREN	: ')' ;										// Right Parentheses

COLON		: ':' ;									// Colon
ATSYM		: '@' ;									// At Symbol
COMMA		: ',' ;									// Comma
SEMICOLON	: ';' ;									// Semicolon

PLUS 	: '+' ;										// Plus
MINUS	: '-' ;										// Minus
STAR	: '*' ;										// Star
SLASH	: '/' ;										// Slash
TILDE	: '~' ;										// Tilde

LT	: '<' ;										// Less than
EQUALS	: '=' ;										// Equal to
LBRACE	: '{' ;										// Left Curly Brace
RBRACE	: '}' ;										// Right Curly Brace
DOT	: '.' ;										// Dot operator
DARROW	: '=>' ;									// Thick arrow
LE	: '<=' ;									// Less than or equal to
ASSIGN	: '<-' ;									// Assignment operator

//Section 10.4 in the COOL Manual ---- Keywords
CLASS		: [Cc][Ll][Aa][Ss][Ss] ;
ELSE		: [Ee][Ll][Ss][Ee] ;
FI		: [Ff][Ii] ;
IF		: [Ii][Ff] ;
IN		: [Ii][Nn] ;
INHERITS	: [Ii][Nn][Hh][Ee][Rr][Ii][Tt][Ss] ;
LET		: [Ll][Ee][Tt] ;
LOOP		: [Ll][Oo][Oo][Pp] ;
POOL		: [Pp][Oo][Oo][Ll] ;
THEN		: [Tt][Hh][Ee][Nn] ;
WHILE		: [Ww][Hh][Ii][Ll][Ee] ;
CASE		: [Cc][Aa][Ss][Ee] ;
ESAC		: [Ee][Ss][Aa][Cc] ;
OF		: [Oo][Ff] ;
NEW		: [Nn][Ee][Ww] ;
ISVOID		: [Ii][Ss][Vv][Oo][Ii][Dd] ;
NOT		: [Nn][Oo][Tt] ;
BOOL_CONST	: TRUE | FALSE ;
TRUE		: [t][Rr][Uu][Ee] ;			// true and false need to have the first characters in lowercase
FALSE		: [f][Aa][Ll][Ss][Ee] ;

//Section 10.1 in the COOL Manual ----- Integer and Identifier
INT_CONST	: [0-9]+ ;				// Integer constants
TYPEID		: [A-Z][A-Za-z0-9_]*;			// Type IDs: Begin with Uppercase alphabet
OBJECTID	: [a-z][a-zA-Z0-9_]*;			// Object IDs: Begin with Lowercase alphabet
INV_ID_1	: [_0-9][A-Za-z0-9_]*			// Invalid identifiers
			{
				reportError("Invalid Identifier");
			};
			
//Section 10.2 in the COOL Manual and Section 7.1 ----- String Constants
STR_CONST	: UNTERM_STR_CONST '"'
			{
				processString();
				String s = getText();
				if(s.length() > 1024)
				{
					reportError("String constant too long");
					setType(ERROR);
				}
			};
UNTERM_STR_CONST: '"' (~["\\\n] | '\\' (. | EOF))*;

//Section 10.5 in the COOL Manual ------ White Spaces
WHITESPACE	: (' ' | '\n' | '\f' | '\r' | '\t' | '\u000B')+ -> skip ;


//Section 10.3 in the COOL Manual ------ Comments
SINGLE_LINE_COMMENT	: ('--') ((.)*?) ('\n') -> skip ;
MULTI_LINE_COMMENT	: '(*' -> pushMode(START_COMMENT), skip;

// This is generally printed by ANTLR itself, but here since "we" need to report this error, it is being reported.
// Placed in the end so that this is the last matched.
NO_TOKEN_FOUND		: .
				{
					String s = getText();
					reportError(s);
				};

INV_COMMENT_1		: '*)' (EOF | '\n')?
				{
					reportError("Unmatched *)");
				};

mode START_COMMENT;
NEST_START_COMMENT	: '(*' -> pushMode(START_COMMENT), skip;
NEST_END_COMMENT	: '*)' -> popMode, skip;
COMMENT_CONTENT		: . -> skip;
INV_COMMENT_2		: .(EOF)
				{
					reportError("EOF in comment");
				};
