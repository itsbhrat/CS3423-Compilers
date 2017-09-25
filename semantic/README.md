# COOL Semantic Analyzer using ANTLR and Java
**Sections of interest in the COOL Manual**

* Section 3 - Classes
* Section 4 - Types
* Section 5 - Attributes (excluding _void_ : will explain a design decision w.r.t. _void_)
* Section 6 - Methods
* Section 7 - Expressions
* Section 8 - Basic Classes
* Section 9 - Main Class
* Section 12 - Type Rules and Environments

### Design/pipeline of the Semantic Analyzer

This section will discuss our design decisions, choice of algorithms, and access methods. We have added this is a FAQ format, for the benefit of the reader.

##### Scope Maintenance and Handling
---------------------------------------------
We make use a `ScopeTable` object and its methods defined in `ScopeTable.java`, which was provided to us, for maintaining and handling scope.

Scope was of major importance in the following areas: 
+ **Methods** - functions in _COOL_ have their own scope of variables
+ **Case Expression** - wherein the branches of the `case` expression have their own local scope
+ **Let Expression** - obviously, the `let` expression has its own scope within the let

There were other places where scope was of importance, for instance the assign operator, where the left-hand side of the assign expression needs to exist within the scope.

##### Name Checking
-----------------------------------
###### Name Checking in Classes
According to the _COOL_ manual, we are not allowed to redefine the basic classes: `Object`, `IO`, `Int`, `String` and `Bool`. Hence in the first pass, we check if any of the classes have the same name as these, and then flag if this happens using `reportError`. 

Similarly, we are not allowed to inherit from `Int`, `String` and `Bool`, and this is also checked in the first pass and flagged using `reportError`. We are also forbidden from redefining the user-defined same class in a program, which is also checked in the first pass.

It is also necessary that there exists a `Main` class with a `main` method in it. The checks for this are performed in first pass and second pass (?) respectively.

###### Name Checking in Attributes
We check for multiple declarations of attributes in the second pass (?). First we check same attributes in the user definitions. If the class inherits from a parent, then we check if the attributes in the user class are distinct from those in the parent. This is done for every attribute, and only if all these checks pass, we add it in the hash-map named `user_attributes`. To maximize the efficiency of our Semantic Checker, we print errors for all checks that have not passed using the `reportError` function.

###### Name Checking in Methods
_COOL_ mandates that methods need to have distinct names in a class. Additionally, if a class, say `Child` inherits from another class, say `Parent` and if there is a common method, say `live` in both `Child` and `Parent`, then the function signature should be the same in both definitions in either class. If these checks pass for every method in a given class, only then do we add it in hash-map named `user_method`. 

In Methods, the formal parameters have to be distinct. This is checked using the scope table, as mentioned above. Redefinitions are erroneous, and flagged appropriately using the `reportError` function.

###### Name Checking in Let Expressions
Let expressions allow name redefinitions. This is done from a right-to-left manner, meaning that the latest definition of the Variable is considered. This is taken care of by the usage of a `HashMap` in Java.

###### Name Checking in Dispatch and Static Dispatch
If the method-to-be-dispatched is not present in the Class' definition, then an error is flagged using `reportError` function.

##### Type Checking
----------------------------------------------
The type rules were obtained from Section 12. A few points to be noted:
+ If an expression has an error with respect to the type environment, then the type of that expression is set to `Object`. The error is flagged using `reportError` function.
+ Join of two types: The join of type `A` with type `B` is defined to be the Lowest Common Ancestor (LCA). Getting the LCA is recursive based on our implementation. 
+ Conformance checking: The definition of conformance of a type `A` with another type `B` is found in Page 6 of the _COOL_ Manual. We used the LCA to check for conformance, essentially by telling that `A` conforms to `B` if LCA(`A`, `B`) = `B` and height of `A` &#8805; height of `B` in the inheritance graph.

Conformance Checking was a crucial part of type checking in dispatch, static dispatch, let and assign expressions. It was also used with method and attribute types.

For effective Code generator interface, we have assigned the type of `no_expr` to `No_type`. We did not have to deal with `SELF_TYPE`. 

### Test Cases

We paid more importance to the test cases that were wrong, hence the higher number of wrong test cases versus correct ones. These test cases flag errors in the same way as the **coolc** compiler, but the error messages are more verbose than the **coolc** compiler's.

Our incorrect programs are found in `Bad_programs` subdirectory and the correct programs are found in `Cool_programs` subdirectory. The names of the programs are self-explanatory, and we have included a short description in the beginning of every program to suggest the kind of behaviour.
