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
	
	int maxStringLength = 1024;       // The String Maximum Length


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
        
        
		//write your code to test strings here
		
		// checking length of string
		if(text.length() > maxStringLength)
		{
		    reportError("String constant too long");
		    return;
		}
        
        /* replacing escape sequences by their correct value
         * replacing \c with c , c is any character
        */
        
        text = text.substring(1,text.length()-1);       //replacing leading and trailing "
        text = text.replace("\\n" , "\n").replace("\"" , "\"").replace("\\b" , "\b").replace("\\t" , "\t").replace("\\f" , "\f");
        text = text.replaceAll("\\\\(.)" , "$1");       
        setText(text);
	}
}

/*
	WRITE ALL LEXER RULES BELOW
*/

    // Special Symbols in Cool
	LPAREN : '{' ;
	RPAREN : '}' ;
	COLON : ':' ;
	ATSYM : '@' ;
	SEMICOLON : ';' ;
	COMMA : ',' ; 
	PLUS : '+' ;
	MINUS : '-' ;
	STAR : '*' ;
	SLASH : '/' ;
	TILDE : '~' ;
	LT : '<' ;
	EQUALS : '=' ;
	LBRACE : '(' ;
	RBRACE : ')' ;
	DOT : '.' ;
	DARROW : '=>' ;
	LE : '<=' ;
	ASSIGN : '<-' ;
	CLASS : C L A S S ;
	ELSE : E L S E ;
	FI : F I ;
	IF : I F ;
	IN : I N ;
	INHERITS : I N H E R I T S ;
	LET : L E T ;
	LOOP : L O O P ;
	POOL : P O O L ;
	THEN : T H E N ;
	WHILE : W H I L E ;
	CASE : C A S E ;
	ESAC : E S A C ;
	OF : O F ;
	NEW : N E W ;
	ISVOID : I S V O I D ;
	NOT : N O T ;

    
// fragments fro matching lower case and upper case both for letters 
fragment A : ['a'|'A'] ;
fragment B : ['b'|'B'] ;
fragment C : ['c'|'C'] ;
fragment D : ['d'|'D'] ;
fragment E : ['e'|'E'] ;
fragment F : ['f'|'F'] ;
fragment G : ['g'|'G'] ;
fragment H : ['h'|'H'] ;
fragment I : ['i'|'I'] ;
fragment J : ['j'|'J'] ;
fragment K : ['k'|'K'] ;
fragment L : ['l'|'L'] ;
fragment M : ['m'|'M'] ;
fragment N : ['n'|'N'] ;
fragment O : ['o'|'O'] ;
fragment P : ['p'|'P'] ;
fragment Q : ['q'|'Q'] ;
fragment R : ['r'|'R'] ;
fragment S : ['s'|'S'] ;
fragment T : ['t'|'T'] ;
fragment U : ['u'|'U'] ;
fragment V : ['v'|'V'] ;
fragment W : ['w'|'W'] ;
fragment X : ['x'|'X'] ;
fragment Y : ['y'|'Y'] ;
fragment Z : ['z'|'Z'] ;
fragment DIGIT : [0-9] ;
fragment UpperCaseLetter : [A-Z] ;
fragment LowerCaseLetter : [a-z] ;
fragment Letter : [a-zA-Z] ;
fragment Score : '_' ;
fragment IdentifierName : [a-zA-z0-9_] ;

// The valid characters inside a string
fragment StringExceptNewLineQuote : ~[\n"\u0000]
                                  | ('\\\"')
                                  | ('\\\n');


    TYPEID : UpperCaseLetter (IdentifierName)* ;
    OBJECTID : LowerCaseLetter (IdentifierName)* ;
    INT_CONST : DIGIT+ ;

    BOOL_CONST : 't' R U E | 'f' A L S E ;

    STR_CONST : '"' (StringExceptNewLineQuote)* '"' {processString();}
              | '"' (StringExceptNewLineQuote)* [\u0000]+ (~[\n"])+ ('\n'|'"'|EOF) {reportError("String contains null character");}
              | '"' (StringExceptNewLineQuote)* (EOF) {reportError("EOF in string constant");}
              | '"' (StringExceptNewLineQuote)* ('\n') {reportError("Unterminated string constant");};

     WS : [\f\u000b\r\t\n ]+ -> skip ;

    /* COMMENTS  CHECKING */
    
    SingleLineValidComment : '--' .*? '\n' -> skip;
    EndComment :  '*)' {reportError("UNMATCHED *)");};
    EndCommentEOF : '*)' EOF {reportError("UNMATCHED *)");};
    StartComment : '(*' -> pushMode(COMMENTSTARTED) , skip; 
    INVALIDNUMBER : DIGIT+ Letter+ {reportError(getText());};
    INVALID : . {reportError(getText());};


    mode COMMENTSTARTED;
    EOFInCommentStarted : .(EOF) {reportError("EOF in comment");};
    StartCommentStarted : '(*' -> pushMode (COMMENTINCOMMENT) , skip;
    EndCommentStarted : '*)' -> popMode , skip;
    TextInComment : . -> skip;


    mode COMMENTINCOMMENT;
    EOFInCommentInComment : .(EOF) {reportError("EOF in comment");};    
    StartCommentInComment : '(*' -> pushMode (COMMENTINCOMMENT) , skip;
    EndCommentInComment : '*)' -> popMode , skip;
    EndEOFCommentInComment : '*)' EOF {reportError("EOF in comment");};
    TextInCommentInComment : . -> skip;
    
    

