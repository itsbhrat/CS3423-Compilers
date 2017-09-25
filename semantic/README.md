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

### Test Cases

We paid more importance to the test cases that were wrong, hence the higher number of wrong test cases versus correct ones. These test cases flag errors in the same way as the **coolc** compiler, but the error messages are more verbose than the **coolc** compiler's.
