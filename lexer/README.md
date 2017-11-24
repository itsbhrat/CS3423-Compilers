# COOL Compiler #
**Sections of interest in the COOL Manual**

* Section 10.1 through till Section 10.5 : For basic Lexical Rule Checking
* Section 7.1 : For maximum length of the string.

Comments have been provided throughout the entire Source Code except in sections where the Code is not supposed to be edited.

### The design/pipeline of the lexer is as follows: ###

* __Detect errors.__

	This was just to make sure that the erroneous strings are unambiguously detected. Erroneous strings either end on EOF, or do not have a terminating double quote, or exceed 1024 characters in length.

* __Scan through the special characters of the language__. 

* __Detect Keywords.__

	This is probably one of the most important design decision. Keywords cannot be used as Object identifiers or Type identifiers, and thus hold a special place in the nomenclature of language. Since COOL is _case-insensitive_ w.r.t. keywords, it could so happen that wHile can be misunderstood as a Object Identifier if the regex for detecting object identifiers was placed above the regex for detecting the keyword _WHILE_. This is why the detecting keywords part of the lexing is placed this high in the pipeline.

* __Detect Integer Constants, Type Identifiers and Object Identifiers.__

	This is placed at an unambiguous position because the detection of these are succeeded by Whitespace, Comments and String Literals detection.

* __Detect String Literals__

	Possibly the hardest part of the assignment was the detection of **correct** string literals. First the unterminated string is detected, then the correct strings are those which are terminated (basically). 

	The regex for detecting a unterminated string does this: 

	First the starting double quote is detected. 
  	
  	Then, all characters except for \n (newline), " (double quote), \\ (single back-slash), __= (~["\\\\\\n])*__
  	
  	or, all characters preceeded by a \\ (back-slash) are accepted. __= ('\\\\' (. | EOF))*__
  	      
  	Hence the regex for an unterminated string (without the quotes) is: __(~["\\\\\\n] | '\\\\' (. | EOF))*__
  	
	The erroneous strings were discussed previously, so if these characters that are consumed by the regex are succeeded by a double quote, then the string literal is valid.

* __WhiteSpace__

	Consume all newlines(\n), formfeeds(\f), carriage returns(\r), tabs(\t) and vertical tabs(\v) and skip it, because there are of no interest to us. Vertical tabs have to be represented in Unicode format since neither JAVA nor ANTLR accept '\v' escape sequence.

* __Detect Comments__

	Detecting single line comments were very direct. For detecting multi-line comments, I used **modes**. My understanding was that **mode** was a local stack. So when I see a '(\*', I push a START_COMMENT mode and then when I see a '\*)' I pop that mode off the stack. Unbalanced stack leads to Unmatched Braces.

	According to requirements, all valid strings are being processed and reset. 

### Test Cases: ###

* There are 6 test cases along with the source code.

	The test cases are fun to play with and are well annotated, so that the person checking the errors generated will know why they are being generated.

	`test_1.cl` and `test_6.cl` are entirely clean code (`test_6.cl` was submitted as a non-trivial program), whereas the rest have some or the other error in them. 

	All errors generate pertain to some rule/definition in the COOL Manual is the aforementioned sections of interest being violated.
