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

This section will discuss our design decisions, choice of algorithms, and access methods. We have added this in a FAQ format, for the benefit of the reader.


### Building the Inheritance Graph
This step is divided into 2 parts : <b>Node Creation</b> & <b>Edge Creation</b>.

<strong> Node Creation </strong>  : We iterate over the list of classes(obtained from the parser) and after checking the validity of the class & it's parent's names append this class to the inheritance graph adjacency list.

<strong> Edge Creation </strong> : After all the nodes have been created , we iterate over the list of classes and add a directed edge from the parent class to it's child class.

The reason for separating these 2 steps is that in a semantically wrong Cool program, a  class can inherit from an undefined class. If we check the existence of the parent class for each node in the list of classes(obtained from the parser) during the node creation step , it can lead to a O(V<sup>2</sup>) solution.
By separating the 2 steps we have obtained a O(2*V) solution.


### Cycle-Detection
For cycle detection , an iterative DFS traversal of the inheritance graph is done. This is done to avoid recursive functions( & prevent stack overflow errors for large programs).

At first our plan was to use [Johnson's Algorithm](https://www.hackerearth.com/practice/notes/finding-all-elementry-cycles-in-a-directed-graph/) to detect all simple cycles in a directed graph.
But after experimenting with the original cool compiler we found that if a cycle is found in the (connected) inheritance graph , all the graph nodes reachable from the cycle nodes are flagged as being involved in the inheritance cycle.
Hence using DFS approach as soon as we encounter 1 cycle node (already visited node during DFS)  , we terminate the DFS and print all the nodes reachable from all the cycle nodes in that subgraph. By this approach if another cycle is also present in the graph , it's nodes will be automatically flagged as an error.

### Building the classList HashMap
After ensuring that the inheritance graph is valid , we add the class features to the classList Hashmap in a BFS manner , starting from Object class.
This is done to add parent classes before their corresponding child classes in classList.
 By doing this we ensure that a child class can always inherit the features of it's parent class.


### Attribute overrides

Before adding a class to the classList , certain checks are made about the class features.

If any child class attempts to redefine it's parent class attributes, an error is reported and the inherited attribute is retained in the child class.

If any child class attempts to redefine it's parent class method, the following checks are made
<ol>
	<li> Redefined method has the same data type as the parent class method.</li>
	<li> Redefined method has the same number of formal parameters.</li>
	<li> Redefined method has the same types of formal parameters .</li>
</ol>
If any check fails, an error is reported and the inherited attribute is retained in the child class.

### Test Cases

We paid more importance to the test cases that were wrong, hence the higher number of wrong test cases versus correct ones. These test cases flag errors in the same way as the **coolc** compiler, but the error messages are more verbose than the **coolc** compiler's.
