# COOL Compiler #
**Sections of interest in the COOL Manual**

* The grammar of COOL presented after the section 10 in the COOL Manual (refer to page number 16)

### Design/pipeline of the parser: ###
* __The Grammar__

	The Grammar provided in the COOL Manual was required for this assignment. 
	
* __Grammar Rules__

	The Grammar rules have to be split token by token to extract the textual information captured by every token. The ANTLR tool provided an API to help extract this information. In our code, we made use of these special functions which included `getLine()`, `getText()`. 
	
	Every grammar rule returns an AST Node, with some value. This value is determined by the token stream that occur on the right-hand-side of the grammar rule. For example: the node for a boolean constant i.e., `true` or `false` is given by `bool_const(<truth_value>, line_\number)`. The truth value and the line number is determined by examining the token content which is emitted by the lexer using the functions `getText()` and `getLine()`. 
	
	If a rule requires some variables to be used in the rules defined for a given node, then we use the `@init` section for declarations. We make use of it in a few places such as in `expression_list`, `branch_list` and `formal_list`, where we deem it necessary. These declarations help us modulate our code to make it look better and isolate errors if any in an easier manner.
	
* __Syntactical Correctness__

	Since we have made use of the exact same rules as mentioned in the COOL grammar for generating the AST, we believe and trust that our code is well-functional and correct.
	
	In the case of programs, if the given test code is syntactical correct, then the code written will print out a parse tree in the console. There will be no errors whatsoever. On the otherhand, if the given test code is syntactical incorrect, then the code written will print the first error detected with its line number. This may be used to evaluate the correctness of the parser/AST generator that we have written.


### Common Observations In Test Cases
<ol><li> In the AST of any Cool Program the parent node is always <b>PROGRAM</b> . </li>
<li>Every Class Has children of 2 types  ATTRIBUTE or METHOD.</li>
<li> Every child of <b>METHOD</b> & <b>ATTRIBUTE</b> node has a <b>: _no_type</b> label associated with it at it's end.</li>
</ol>

### Explanation Of Test Cases
 (More Details Explained in the test_cases programs )
* __visualize_AST.cl__
 This test case is a simple Cool Program which is assigning value to a class attribute inside a function.
 The purpose of this test case is  to visualize pictorially the AST generated.<br>
  
 AST Pictorial Representation             |  AST Screenshot Representation
:-------------------------:|:-------------------------:
<img src="ast_picture.png" alt="Test 1 AST Image" style="width: 40em;"/>  |  <img src="ast_screenshot.png" alt="Test 1 AST Image" style="width: 20em;"/>

 <span>
 


 </span>

* __class_attributes_switch.cl__
This test case demonstrates how the following are represented in the AST.
	<ul><li>Class Declarations</li><li>Class Attributes</li><li>Switch Case</li></ul>


* __method_if_loop.cl__
This test case demonstrates how the following are represented in the AST.
	<ul><li>If then else conditions</li><li>Class Methods</li><li>Dispatch Mechanism of Cool</li><li>while loop</li></ul>
 
* __let_equivalence.cl__
This test case demonstrates how let statements  are represented in the AST and proves the semantic equivalence of
let a:Int in let b:Int in let c:Int in
 and
let a:Int , b:Int , c:Int in
 
* __incorrect_test_4.cl__
This test case demonstrates a syntactically incorrect program : A class name starting with a lower case letter.

* __incorrect_test_5.cl__
This test case demonstrates a syntactically incorrect program : a missing semicolon at the end of out_string function.


* __incorrect_test_6.cl__
This test case demonstrates a syntactically incorrect program : invalid function call.