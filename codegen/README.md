# COOL Code Generator using ANTLR and Java to LLVM IR

**Sections of interest in the COOL Manual**

* Section 13 : Operational Semantics of Cool
* Section 11, 12 : Parsing COOL and Type Rules and Environments

### Design/Pipeline of the Code Generator
This section will discuss our design decisions and access methods.

##### Directory Structure
------------------------------------------
We have three files: `Codegen.java`, `Printer.java` and `dataType.java`. `Printer.java` contains the utilities for printing a line of code in LLVM IR, given the arguments to the functions. `dataType.java` contains the utilities such as `Operand`, `OpType` and `OpTypeId`, for ease of use. Dependence wise:
+ `Codegen.java` depends on both `Printer.java` and `dataType.java`
+ `Printer.java` depends on `dataType.java`

**DISCLAIMER: There seem to be multiple warning based on this directory structure. Please note that these warnings are just mere warnings, and do not affect the working of the code generator in any way possible.**

##### Building the classes
-------------------------------------
After observing C++ code translated into LLVM IR using `-Xclang -disable-O0-optnone -S -emit-llvm`, we realized that a class is maintained a typographical sense like `type {}`. This holds only for the attributes i.e., variables. For functions, some version of name mangling is done. 

We decided to incorporate these into the code generator. For every class, we invoke a `typeDefine` method from `Printer.java` which prints a type structure. For functions we perform name mangling as: `<class_name>_<func_name>`.

Every class has a constructor, which sets initial values if any. According to *COOL*, an `Int` object has a default value of `0`, a `String` object has a default value of `""` and a `Bool` object has a default value of `false`. All this is initialized in the constructor.

##### Building the functions in the classes
---------------------------------------------------------
Since all of the functionality of *COOL* resides in functions, we considered the usage of a `NodeVisit` function similar to that in our semantic analyzer. The `NodeVisit` functions traverses `AST.expression` nodes and emits code along the way. To keep track of the register number, we make use a data structure called `Tracker`, with some design specific attributes such as `register`: to maintain the next register that can be used, `last_instruction_type`: to maintain the type of the last instruction and `last_basic_block`: used in conditional/branch bodies, to maintain the label of the last basic block, the `NodeVisit` method traversed to.

For arithmetic expressions, we use a function named `arith_capture`, which recursively evaluates expressions in the AST as and when they appear. This takes care addition, subtraction, multiplication, division, negation, complement, equality, less than (or equal to).

For loops, we use a function named `loop_capture`, which evaluates the loop body and also recursively finds loops within the existing loop body (in the case of nested loops). This emits labels which are of the form `for.cond<i>`, `for.body<i>` and `for.end<i>`. 

We found conditional statements i.e., the infamous `if - then - else` statement to be the most trickiest to handle. We resorted to the usage of `phi` functions courtesy of LLVM IR, which helped in a good amount of code reduction. The same thing is done here, just as in loops, we call `NodeVisit` over the bodies of the `then` and `else` clause. This section also generates labels which are of the form `if.cond<i>`, `if.then<i>`, `if.else<i>` and `if.end<i>`.

For the rest of grammar of COOL, we relied on inline coding since we believed that a function would not be required as such. For string constants, again drawing observations from the LLVM IR generated from C++ code, we realized that string constants known at compile error are stored away. For this, we make use of a method `string_capture`, which detects string constants and adds them as global variables in the IR. We use a HashMap `string_table` to map strings to their respective identities (integers), so that we can call them in the IR without hassle. 

##### Building the special functions
--------------------------------------------------
The `String` special functions are:
+ `concat` : referred to as `String_concat` in the LLVM IR: for concatenation
+ `length` : referred to as `String_length` in the LLVM IR: for finding the length of a string
+ `substr` : referred to as `String_substr` in the LLVM IR: for producing a substring a given string.

These functions are hardcoded by making observations from the LLVM IR generated from C code of these functions. 

The `IO` special functions are:
+ `in_int` : referred to as `IO_in_int` in the LLVM IR: for inputting an integer from `stdin`.
+ `in_string` : referred to as `IO_in_string` in the LLVM IR: for inputting a string from `stdin`.
The `stdout` analogues of the above are `out_int` and `out_string` named are `IO_out_int` and `IO_out_string`, respectively.

Just as done for the special `String` functions, these functions were hardcoded by observing the underlying C code of these functions.

The `Object` special functions are:
+ `abort`: referred to as `Object_abort` in the LLVM IR: for aborting in the event of runtime error.

These functions specified above (all of them) make sure of C routines. We declare the C routines in the beginning of the LLVM IR. 
