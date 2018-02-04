# COOL Compiler #

--------->  CODE EXPLANATION
Tokens: This section contains all the tokens that will map with the cool code based on certain conditions. The (operators / keywords / special characters) are defined by the 'literal'. To handle lower and upper case for the case insensitive part, I have used fragments mapping every character to its lower and upper case.

@members : In processString() , following operations are done. The string length is checked. All the escape sequences(\\n) are replaced by the actual escape sequences (\n). The nonescape sequences (\c) are replaced by the character(c).


StringExceptNewLineQuote : This is a fragment telling what valid characters are there for a string. ~[\n"\u0000] tells no \n , " , NULL Character are allowed in the string , but \\\n , \\\" are allowed.


VERIFICATION FOR Cool Manual Section 10.2

STR_CONST: The regular expressions are as follows. 
1 - The correct case is tested as the string starting with ",  containing (StringExceptNewLineQuote)* and ending with ".

2 - If the string contains NULL character: string starts with ", containing (StringExceptNewLineQuote)*, has at least 1 Null Character, and the remaining string, terminated by \n or " or EOF.
This is an invalid string and the error "String contains null character" is reported.

3 - If the string contains EOF: string starts with ", containing (StringExceptNewLineQuote)*, has an EOF.
This is an invalid string and the error "EOF in string constant" is reported.

4 - If the string contains \n : string starts with " , containing (StringExceptNewLineQuote)* , has \n (This is different that \\n).
This is an invalid string and the error "Unterminated string constant" is reported.

Hence all the cases in the Cool Manual Section 10.2 are taken care of.



VERIFICATION FOR Cool Manual Section 10.1, 10.4

TYPEID: This consists of a name beginning with a upperCase followed by any number of IdentifierName. SELF_TYPE is considered here.
OBJECTID: This consists of a name beginning with a lowerCase followed by any number of IdentifierName. self is considered here.
INT_CONST: This consists of any combination of digits.
Keywords: In the "Special Symbols in Cool" section, the keywords are defined. Using fragments, all keywords (except true, false), case insensitivity is ensured.
For true/false , the 1st character is lower case followed by characters of any case.

Hence all the cases in the Cool Manual Section 10.1, 10.4 are taken care of.


VERIFICATION FOR Cool Manual Section 10.5

WS : [\f\u000b\r\t\n ]+ -> skip ;
This takes care for all unused whitespaces in the Cool code.
Hence all the cases in the Cool Manual Section 10.5 are taken care of.


VERIFICATION FOR Cool Manual Section 10.3

In the "COMMENTS  CHECKING" section, the comments are checked. Using modes in ANTLR, the comments are lexed.
If any *) is found without any (*, the error "UNMATCHED *)" is printed.
If any (* is found, mode is changed to COMMENTSTARTED.
In COMMENTSTARTED id any (* is found mode is changed to COMMENTINCOMMENT.
Here all the balanced (* *) are eaten up. If before complete balancing EOF is encountered, the error "EOF in the comment" is printed.
Similarly when the recursion ends mode is popped back to COMMENTSTARTED, from where it is popped back to DEFAULT mode.

Hence all the cases in the Cool Manual Section 10.3 are taken care of.



 
--------->  EXPLANATION OF THE TEST CASES

comments.cl: This tests for all the possible comments lexical facts/errors, namely EOF, unmatched *), nesting comments, valid comments, etc.

EOFStringEnd.txt: This tests for a string ending with EOF.

Helloworld.cl: This is the original file that was included in the assignment.

keywordLiteraltTokens.cl : This tests for all the keywords and their case insensitivity, along with all the valid operators in Cool. For the operators, just the operator is printed back. For the keywords, the token name is printed.

nonTrivialProgram.cl : This is one of the non-trivial programs I made for Assignment 0. In this, no lexical errors are generated. Along with this the constructs for loops, conditionals, let statements, etc are tested.

stringHasNullCharacter.txt : This tests for a string containing a NULL character.

stringLexicalChecking.cl : This tests for all the possible string assignments in Cool. This test file contains some valid strings, some strings with escape sequences (the corresponding replacements are performed), some strings containing \n , some strings containing \\n,  a string longer than 1024 (max Length Of String in Cool).

typeIdObjectId.cl : This test checks for the TypeId, ObjectId names, taking into their consideration their particular case sensitivity, checks for true, false, True, False are also done.


