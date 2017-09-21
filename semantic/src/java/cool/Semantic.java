package cool;

import java.util.*;

class ClassNode {
    public String name;
    public String parent;
    public int height;
    public HashMap<String, AST.attr> attributes = new HashMap<String, AST.attr>();
    public HashMap<String, AST.method> methods = new HashMap<String, AST.method> ();

    ClassNode(String class_name, String class_parent, int class_height, HashMap<String, AST.attr> class_atributes, HashMap<String, AST.method> class_methods) {
        name = class_name;
        parent = class_parent;
        height = class_height;
        attributes.putAll(class_atributes);
        methods.putAll(class_methods);
    }
}

public class Semantic {
    private Boolean errorFlag = false;

    public void reportError(String filename, int lineNo, String error) {
        errorFlag = true;
        System.err.println(filename + ":" + lineNo + ": " + error);
    }

    public Boolean getErrorFlag() {
        return errorFlag;
    }

    /*
        Don't change code above this line
    */

    // Data Structure sections
    private ScopeTable<AST.attr> the_scope_table = new ScopeTable<AST.attr>();
    public HashMap<String, ClassNode> classList = new HashMap<String, ClassNode>();
    private String filename;

    // Functions
    public Semantic(AST.program program) {

        //Write Semantic analyzer code here
        define_built_in_classes();
        process_graph(program.classes);

        // debug_class_list();

        for (AST.class_ e : program.classes) {
            filename = e.filename;              // filename for each class
            the_scope_table.enterScope();          // enter new scope for a class
            the_scope_table.insert("self", new AST.attr("self", e.name, new AST.no_expr(e.lineNo), e.lineNo));     // self is available as attribute within the class
            the_scope_table.insertAll(classList.get(e.name).attributes);        // insert all inherited and other declared attributes within the class into the scope
            NodeVisit(e);
            
            the_scope_table.exitScope();               // once class is processed, exit the scope.
        }

        // checking Main Class here so that filename attribute gets initialized  
        if (classList.containsKey("Main") == false) {   //check the existence of Main class
            reportError(filename, 1, "Program does not contain class 'Main'");
        } else if (classList.get("Main").methods.containsKey("main") == false) {    //check the existence of main method in Main class
            reportError(filename, 1, "'Main' class does not contain 'main' method");
        }
    }

    
    private void debug_class_list() {
        for (HashMap.Entry<String, ClassNode> entry : classList.entrySet()) {
            ClassNode value = entry.getValue();
            System.out.println("\n\n\t CLASS ==> " +  value.name);
            System.out.println("\t\t PARENT ==> " +  value.parent);
            System.out.println("\t\t HEIGHT ==> " +  value.height);
            System.out.print("\t\t ATTRIBUTES ==> ");

            for (HashMap.Entry<String, AST.attr> key : value.attributes.entrySet()) {
                System.out.print("(" + key.getKey() + "," + key.getValue().typeid + ")  :: ");
            }
            System.out.print("\n\t\t METHODS ==> ");

            for (HashMap.Entry<String, AST.method> key : value.methods.entrySet()) {
                System.out.print("(" + key.getKey() + "," + key.getValue().typeid + ")  :: ");
            }
        }
    }


    private void define_built_in_classes() {
        /*
            consider line number = 0 for all the basic classes
            for functions returning SELF_TYPE , return the same class name as typeid fot the function
        */

        // Refer to Page 13 of the COOL Manual for basic classes definition

        /****** adding object class members ******/

        HashMap <String, AST.method> Object_methods = new HashMap <String, AST.method>();

        Object_methods.put("abort", new AST.method("abort", new ArrayList<AST.formal>(), "Object", new AST.no_expr(0), 0));

        Object_methods.put("type_name", new AST.method("type_name", new ArrayList<AST.formal>(), "String", new AST.no_expr(0), 0));

        Object_methods.put("copy", new AST.method("copy", new ArrayList<AST.formal>(), "Object", new AST.no_expr(0), 0));


        classList.put("Object", new ClassNode("Object", null, 0, new HashMap <String, AST.attr>(), Object_methods));


        /****** adding IO class members ******/

        HashMap <String, AST.method> IO_methods = new HashMap <String, AST.method>();

        IO_methods.put("out_string", new AST.method("out_string", Arrays.asList(new AST.formal("x", "String", 0)), "IO", new AST.no_expr(0), 0));

        IO_methods.put("out_int", new AST.method("out_int", Arrays.asList(new AST.formal("x", "Int", 0)), "IO", new AST.no_expr(0), 0));

        IO_methods.put("in_string", new AST.method("in_string", new ArrayList<AST.formal>(), "String", new AST.no_expr(0), 0));

        IO_methods.put("in_int", new AST.method("in_int", new ArrayList<AST.formal>(), "Int", new AST.no_expr(0), 0));

        IO_methods.putAll(Object_methods);      //inheriting all object methods


        classList.put("IO", new ClassNode("IO", "Object", 1, new HashMap <String, AST.attr>(), IO_methods));


        /****** adding Int class members ******/

        //inheriting all object methods
        classList.put("Int", new ClassNode("Int", "Object", 1, new HashMap <String, AST.attr>(), new HashMap <String, AST.method>(Object_methods)));


        /****** adding Bool class members ******/

        //inheriting all object methods
        classList.put("Bool", new ClassNode("Bool", "Object", 1, new HashMap <String, AST.attr>(), new HashMap <String, AST.method>(Object_methods)));


        /****** adding String class members ******/

        HashMap <String, AST.method> String_methods = new HashMap <String, AST.method>();

        String_methods.put("length", new AST.method("length", new ArrayList<AST.formal>(), "Int", new AST.no_expr(0), 0));

        String_methods.put("concat", new AST.method("concat", Arrays.asList(new AST.formal("s", "String", 0)), "String", new AST.no_expr(0), 0));

        List<AST.formal> substr_formal = new ArrayList<AST.formal>();
        substr_formal.add(new AST.formal("i", "Int", 0));
        substr_formal.add(new AST.formal("l", "Int", 0));
        String_methods.put("substr", new AST.method("substr", substr_formal, "String", new AST.no_expr(0), 0));

        String_methods.putAll(Object_methods);      //inheriting all object methods


        classList.put("String", new ClassNode("String", "Object", 1, new HashMap <String, AST.attr>(), String_methods));
    }

    private void process_graph(List<AST.class_> classes) {

        HashMap<String, Integer> class_int = new HashMap<String, Integer>();        // map class name to node index in graph
        ArrayList<AST.class_> class_node = new ArrayList<AST.class_>();             // map node index to AST.class_
        ArrayList<ArrayList<Integer>> adjacency_list = new ArrayList<ArrayList<Integer>>();

        int no_nodes_in_graph = 5;

        /*
            adding basic classes to graph
            We are inserting Int, Bool, String basic classes in graph
            to take care of cases if user class inherits from one of the non-inheritable classes
         */

        class_int.put("Object", 0);
        class_int.put("IO", 1);
        class_int.put("Int", 2);
        class_int.put("String", 3);
        class_int.put("Bool", 4);

        class_node.add(null);
        class_node.add(null);
        class_node.add(null);
        class_node.add(null);
        class_node.add(null);

        adjacency_list.add(new ArrayList<Integer>(Arrays.asList(1)));   //adding Object to graph
        adjacency_list.add(new ArrayList<Integer>());   //adding IO to graph
        adjacency_list.add(new ArrayList<Integer>());   //adding Int to graph
        adjacency_list.add(new ArrayList<Integer>());   //adding String to graph
        adjacency_list.add(new ArrayList<Integer>());   //adding Bool to graph

        boolean ok = true;

        //adding the classes to the graph
        for (AST.class_ c : classes) {

            //checking if user class is one of the basic class
            boolean cond_name = (c.name.equals("Object") || c.name.equals("IO") || c.name.equals("Int") || c.name.equals("String") || c.name.equals("Bool"));
            if (cond_name) {
                reportError(c.filename, c.lineNo, "Redefinition of basic class '" + c.name + "'.");
                ok = false;
            }
            //checking if user class inherits from one of the non-inheritable classes
            boolean cond_parent = (c.parent.equals("Int") || c.parent.equals("String") || c.parent.equals("Bool"));
            if (cond_parent) {
                reportError(c.filename, c.lineNo, "Class '" + c.name + "' cannot inherit basic class '" + c.parent + "'.");
                ok = false;
            }
            //checking if user class is redefined
            if (class_int.containsKey(c.name)) {
                reportError(c.filename, c.lineNo, "Class '" + c.name + "' was previously defined.");
                ok = false;
            } else {
                class_int.put(c.name, no_nodes_in_graph);
                class_node.add(c);
                no_nodes_in_graph++;
                adjacency_list.add(new ArrayList<Integer>());   //adding user class to graph
            }
        }

        //adding the edges among the classes
        for (AST.class_ c : classes) {

            //checking if parent class is defined
            if (class_int.containsKey(c.parent) == false) {
                reportError(c.filename, c.lineNo, "Class '" + c.name + "' inherits from an undefined class '" + c.parent + "'.");
                ok = false;
            }

            if (ok) {
                (adjacency_list.get(class_int.get(c.parent))).add(class_int.get(c.name));     //adding the inheritance edge
            }
        }

        if (ok == false || isCyclic(adjacency_list, class_node, no_nodes_in_graph)) {
            System.exit(0);
        }


        Queue<Integer> q = new LinkedList<Integer>();
        List<Boolean> visited = new ArrayList<Boolean>(Collections.nCopies(no_nodes_in_graph, false));

        /*
            Add classes in a BFS manner so that the derived classes can inherit features from it's parent classes
            as we are inserting the parent class first
        */

        Integer node = 0;
        q.add(0);   //Object class
        visited.set(0, true);
        while (!q.isEmpty()) {
            node = q.remove();
            for (Integer i : adjacency_list.get(node)) {
                if (!visited.get(i)) {
                    q.add(i);
                    visited.set(i, true);
                    if (i > 1) {
                        // do not add Object & IO class
                        insert_class(class_node.get(i));
                    }
                }
            }
        }


    }


    /*
        Detection of cycle implementation is taken from http://www.geeksforgeeks.org/detect-cycle-in-a-graph/
    */

    private Integer isCyclicUtil(ArrayList<ArrayList<Integer>> adjacency_list, int v, boolean[] visited, boolean[] recursion_stack) {

        if (visited[v] == false) {
            // Mark the current node as visited and part of recursion stack
            visited[v] = true;
            recursion_stack[v] = true;

            // Recur for all the vertices adjacent to this vertex
            for (Integer i : adjacency_list.get(v)) {

                if (visited[i] == false) {
                    Integer dfs = isCyclicUtil(adjacency_list, i, visited, recursion_stack);
                    if (dfs != -1) {        // if graph has a back-edge
                        recursion_stack[v] = false;  // remove the vertex from recursion stack
                        return dfs;
                    }
                }

                else if (recursion_stack[i]) {  // if graph has a back-edge
                    recursion_stack[v] = false;  // remove the vertex from recursion stack
                    return i;
                }
            }
        }

        recursion_stack[v] = false;  // remove the vertex from recursion stack
        return -1;
    }

    /*
        detect cycle using DFS
    */
    private Boolean isCyclic(ArrayList<ArrayList<Integer>> adjacency_list, ArrayList<AST.class_> class_node, int no_nodes_in_graph) {
        // Mark all the vertices as not visited and not part of recursion stack
        boolean[] visited = new boolean[no_nodes_in_graph];
        boolean[] recursion_stack = new boolean[no_nodes_in_graph];

        // Call the recursive helper function to detect cycle in different subgraphs
        boolean ok = false;
        for (int i = 0; i < no_nodes_in_graph; i++) {
            if (visited[i] == false) {

                Integer node = isCyclicUtil(adjacency_list, i, visited, recursion_stack);
                if (node != -1) {
                    ok = true;
                    printCycle(adjacency_list, class_node, node, no_nodes_in_graph);
                }
            }
        }
        return ok;
    }

    /*
        In Cool if even a single cycle is detected, then original Cool compiler
        flags every classes that is reachable from the cycle node's as error nodes

        This function takes as input a node that is part of the cycle and
        prints all the nodes reachable from all the cycle nodes (using BFS).

    */
    private void printCycle(ArrayList<ArrayList<Integer>> adjacency_list, ArrayList<AST.class_> class_node, Integer node, int no_nodes_in_graph) {

        Queue<Integer> q = new LinkedList<Integer>();
        List<Boolean> visited = new ArrayList<Boolean>(Collections.nCopies(no_nodes_in_graph, false));

        q.add(node);
        visited.set(node, true);

        reportError(class_node.get(node).filename, class_node.get(node).lineNo, " Class " + class_node.get(node).name + ", or an ancestor of " + class_node.get(node).name + ", is involved in an inheritance cycle.");

        // doing a BFS traversal of the cycle nodes (and the reachable nodes)
        while (q.isEmpty() == false) {
            node = q.remove();
            for (Integer i : adjacency_list.get(node)) {
                if (visited.get(i) == false) {
                    q.add(i);
                    visited.set(i, true);
                    reportError(class_node.get(i).filename, class_node.get(i).lineNo, " Class " + class_node.get(i).name + ", or an ancestor of " + class_node.get(i).name + ", is involved in an inheritance cycle.");
                }
            }
        }
    }

    /*
        This function initializes the attributes
        Refer to Section 5 Page 8 of the COOL Manual for default initialization
    */
    private void add_default_value(AST.attr atr) {
        if (atr.typeid.equals("Int")) {
            atr.value = new AST.int_const(0, atr.lineNo);
        } else if (atr.typeid.equals("String")) {
            atr.value = new AST.string_const("", atr.lineNo);
        } else if (atr.typeid.equals("Bool")) {
            atr.value = new AST.bool_const(false, atr.lineNo);
        } else {
            // System.out.println("default_not_weird_attrs is " + atr.value + " of Class " + atr.value.getClass());
            // atr.value = new AST.int_const(0, atr.lineNo);
            // TODO: Confirm with Abhishek Patwardhan about void type
        }
    }

    private void insert_class(AST.class_ user) {
        String parent = user.parent;

        HashMap <String, AST.attr> user_attributes = new HashMap<String, AST.attr>();
        HashMap <String, AST.method> user_method = new HashMap <String, AST.method>();

        HashMap <String, AST.attr> parent_attributes = classList.get(parent).attributes;
        HashMap <String, AST.method> parent_method = classList.get(parent).methods;

        ClassNode user_node = new ClassNode(user.name, parent, classList.get(parent).height + 1, parent_attributes, parent_method); // adding the parents attribute & method list

        boolean ok = true;
        for (AST.feature e : user.features) {

            // iterating over the attributes of class
            if (e instanceof AST.attr) {
                AST.attr atr = (AST.attr)e;
                ok = true;

                // checking for duplicate attributes within the class attributes
                if (user_attributes.containsKey(atr.name)) {
                    reportError(user.filename, atr.lineNo, "Attribute '" + atr.name + "' in class '" + user.name + "' is multiply defined in class '" + user.name + "'.");
                    ok = false;
                }
                // checking for duplicate inherited class attributes
                if (parent_attributes.containsKey(atr.name)) {
                    reportError(user.filename, atr.lineNo, "Attribute '" + atr.name + "' in class '" + user.name + "' is an attribute of an inherited class '" + parent + "'.");
                    ok = false;
                }

                if (ok) {
                    // adding the default values
                    if (atr.value.getClass() == AST.no_expr.class)
                        add_default_value(atr);
                    user_attributes.put(atr.name, atr);
                }
            }

            // iterating over the methods of class
            else if (e instanceof AST.method) {
                AST.method me = (AST.method)e;
                ok = true;

                // checking for duplicate methods definition within the class methods
                if (user_method.containsKey(me.name)) {
                    reportError(user.filename, me.lineNo, "Method '" + me.name + "' is defined multiply in class '" + user.name + "'.");
                    ok = false;
                }
                // checking for duplicate inherited class methods
                // Refer to Page 8 of the COOL Manual for redifining inherited methods in class
                if (parent_method.containsKey(me.name)) {
                    AST.method pr_met = parent_method.get(me.name);
                    // checking no of formal parameters
                    if (pr_met.formals.size() != me.formals.size()) {
                        reportError(user.filename, me.lineNo, "Incompatible number of formal parameters of redefined method '" + me.name + "' in class '" + user.name + "'.");
                        ok = false;
                    }
                    // checking return type
                    if (!pr_met.typeid.equals(me.typeid)) {
                        reportError(user.filename, me.lineNo, "In redefined method '" + me.name + "' in class '" + user.name + "', return type '" + me.typeid + "' is different from original return type '" + pr_met.typeid + "'.");
                        ok = false;
                    }
                    // checking parameter types
                    for (int i = 0; i < me.formals.size(); i++) {
                        if (pr_met.formals.get(i).typeid.equals(me.formals.get(i).typeid) == false) {
                            reportError(user.filename, me.lineNo, "In redefined method '" + me.name + "' in class '" + user.name + "', parameter type " + me.formals.get(i).typeid + " is different from original type " + pr_met.formals.get(i).typeid + ".");
                            ok = false;
                        }
                    }
                }

                if (ok) {
                    user_method.put(me.name, me);
                }
            } else {
                reportError(user.filename, user.lineNo, "Undefined feature in class '" + user.name + "'.");
            }
        }

        user_node.methods.putAll(user_method);
        user_node.attributes.putAll(user_attributes);

        classList.put(user.name, user_node);
    }

    // Overloaded function to visit the class node in the AST
    private void NodeVisit(AST.class_ the_class) {
        List<AST.feature> feature_list = the_class.features;
        for (int i = 0; i < feature_list.size(); i++) {
            AST.feature cur_feature = feature_list.get(i);
            if (cur_feature instanceof AST.method) {
                NodeVisit((AST.method)cur_feature);
            } else if (cur_feature instanceof AST.attr) {
                NodeVisit((AST.attr)cur_feature);
            }
        }
    }

    // Overloaded function to now visit the method nodes in the AST
    private void NodeVisit(AST.method the_method) {
        
        // Entering the scope of the method
        the_scope_table.enterScope();
        List<AST.formal> formal_list = the_method.formals;
        for (int i = 0; i < formal_list.size(); i++) {
            AST.formal cur_formal = formal_list.get(i);
            
            if (the_scope_table.lookUpLocal(cur_formal.name) == null) {
                the_scope_table.insert(cur_formal.name, new AST.attr(cur_formal.name, cur_formal.typeid, new AST.no_expr(cur_formal.lineNo), cur_formal.lineNo));
            } else {
                reportError(filename, cur_formal.lineNo, "Multiple declarations for formal parameters");
            }
        }
        NodeVisit(the_method.body);
        
        // Refer to page 8 for conformance of types in methods
        if (conformance_check(the_method.body.type, the_method.typeid) == false) {
            reportError(filename, the_method.lineNo, "Non-conformance of types " + the_method.body.type + " & " + the_method.typeid);
        }
        the_scope_table.exitScope();
    }

    // Overloaded function to now visit attr nodes in the AST
    private void NodeVisit(AST.attr the_attribute) {
        // Visiting nodes further if they are not of no_expr type
        if (the_attribute.value instanceof AST.no_expr == false) {
            NodeVisit(the_attribute.value);

            // Refer to page 8 for conformance of types in attributes
            if (conformance_check(the_attribute.value.type, the_attribute.typeid) == false) {
                reportError(filename, the_attribute.lineNo, "Non-conformance of types " + the_attribute.value.type + " & " + the_attribute.typeid);
            }
        }
    }

    // Overloaded function to now visit expression nodes in the AST
    private void NodeVisit(AST.expression expr) {

        // Setting types for simple leaf-based nodes in the tree
        if (expr instanceof AST.bool_const) {
            ((AST.bool_const)expr).type = "Bool";
        }

        else if (expr instanceof AST.string_const) {
            ((AST.string_const)expr).type = "String";
        }

        else if (expr instanceof AST.int_const) {
            ((AST.int_const)expr).type = "Int";
        }

        else if (expr instanceof AST.object) {
            AST.object the_object = (AST.object)expr;
            AST.attr return_val = the_scope_table.lookUpGlobal(the_object.name);
            if (return_val == null) {
                reportError(filename, the_object.lineNo, "Identifier " + the_object.name + " not present in scope");
                the_object.type = "Object";
            } else {
                the_object.type = return_val.typeid;
            }
        }

        // Setting types for binary/unary expressions in the tree
        // To make the code look more readable, the "big" functions have been added as separate overloaded functions
        // This can be evident from the only function call of NodeVisit made in the body of the else if

        else if (expr instanceof AST.comp) {
            AST.comp the_complement = (AST.comp)expr;
            NodeVisit(the_complement.e1);
            String e1_type = the_complement.e1.type;
            if (e1_type.equals("Bool") == false) {
                reportError(filename, the_complement.lineNo, "Non-bool argument (type = " + e1_type + ") for complement");
            }
            the_complement.type = "Bool";
        }

        else if (expr instanceof AST.eq) {
            AST.eq the_equality = (AST.eq)expr;
            NodeVisit(the_equality.e1);
            NodeVisit(the_equality.e2);
            String e1_type = the_equality.e1.type;
            String e2_type = the_equality.e2.type;

            // Refer to Page 21 of the COOL Manual for equality type checking
            Boolean cond_e1_type = (e1_type.equals("Int") || e1_type.equals("String") || e1_type.equals("Bool"));
            Boolean cond_e2_type = (e2_type.equals("Int") || e2_type.equals("String") || e2_type.equals("Bool"));
            if ((cond_e1_type || cond_e2_type)) {
                if (e1_type.equals(e2_type) == false) {
                    reportError(filename, the_equality.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for equality testing");
                }
            }
            the_equality.type = "Bool";
        }

        else if (expr instanceof AST.leq) {
            AST.leq the_less_equal = (AST.leq)expr;
            NodeVisit(the_less_equal.e1);
            NodeVisit(the_less_equal.e2);
            String e1_type = the_less_equal.e1.type;
            String e2_type = the_less_equal.e2.type;

            // Refer to Page 21 of the COOL Manual for less than or equal to type checking
            Boolean condition = (e1_type.equals("Int") && e2_type.equals("Int"));
            if (condition == false) {
                reportError(filename, the_less_equal.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for making less than or equal to comparison");
            }
            the_less_equal.type = "Bool";
        }

        else if (expr instanceof AST.lt) {
            AST.lt the_less = (AST.lt)expr;
            NodeVisit(the_less.e1);
            NodeVisit(the_less.e2);
            String e1_type = the_less.e1.type;
            String e2_type = the_less.e2.type;

            // Refer to Page 21 of the COOL Manual for less than type checking
            Boolean condition = (e1_type.equals("Int") && e2_type.equals("Int"));
            if (condition == false) {
                reportError(filename, the_less.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for making less than comparison");
            }
            the_less.type = "Bool";
        }

        else if (expr instanceof AST.neg) {
            AST.neg the_negation = (AST.neg)expr;
            NodeVisit(the_negation.e1);
            String e1_type = the_negation.e1.type;

            // Refer to Page 21 of the COOL Manual for negation type checking
            Boolean condition = (e1_type.equals("Int"));
            if (condition == false) {
                reportError(filename, the_negation.lineNo, "Non-int argument (type = " + e1_type + ") for negation");
            }
            the_negation.type = "Int";
        }

        else if (expr instanceof AST.divide) {
            AST.divide the_arith = (AST.divide)expr;
            NodeVisit(the_arith.e1);
            NodeVisit(the_arith.e2);
            String e1_type = the_arith.e1.type;
            String e2_type = the_arith.e2.type;

            // Refer to Page 21 of the COOL Manual for division type checking
            Boolean condition = (e1_type.equals("Int") && e2_type.equals("Int"));
            if (condition == false) {
                reportError(filename, the_arith.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for performing division");
            }
            the_arith.type = "Int";
        }

        else if (expr instanceof AST.mul) {
            AST.mul the_arith = (AST.mul)expr;
            NodeVisit(the_arith.e1);
            NodeVisit(the_arith.e2);
            String e1_type = the_arith.e1.type;
            String e2_type = the_arith.e2.type;

            // Refer to Page 21 of the COOL Manual for multiplication type checking
            Boolean condition = (e1_type.equals("Int") && e2_type.equals("Int"));
            if (condition == false) {
                reportError(filename, the_arith.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for performing multiplication");
            }
            the_arith.type = "Int";
        }

        else if (expr instanceof AST.sub) {
            AST.sub the_arith = (AST.sub)expr;
            NodeVisit(the_arith.e1);
            NodeVisit(the_arith.e2);
            String e1_type = the_arith.e1.type;
            String e2_type = the_arith.e2.type;

            // Refer to Page 21 of the COOL Manual for subtraction type checking
            Boolean condition = (e1_type.equals("Int") && e2_type.equals("Int"));
            if (condition == false) {
                reportError(filename, the_arith.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for performing subtraction");
            }
            the_arith.type = "Int";
        }

        else if (expr instanceof AST.plus) {
            AST.plus the_arith = (AST.plus)expr;
            NodeVisit(the_arith.e1);
            NodeVisit(the_arith.e2);
            String e1_type = the_arith.e1.type;
            String e2_type = the_arith.e2.type;

            // Refer to Page 21 of the COOL Manual for addition type checking
            Boolean condition = (e1_type.equals("Int") && e2_type.equals("Int"));
            if (condition == false) {
                reportError(filename, the_arith.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for performing addition");
            }
            the_arith.type = "Int";
        }

        else if (expr instanceof AST.isvoid) {
            // Refer to Page 21 of the COOL Manual for isvoid type checking
            ((AST.isvoid)expr).type = "Bool";
        }

        else if (expr instanceof AST.new_) {
            AST.new_ the_new = (AST.new_)expr;
	    

            // Refer to Page 19 of the COOL Manual for new object type checking
            if (classList.containsKey(the_new.typeid) == false) {
                reportError(filename, the_new.lineNo, "Cannot create object with undefined class " + the_new.typeid);
                the_new.type = "Object";
            } else {
                the_new.type = the_new.typeid;
            }
        }

        else if (expr instanceof AST.block) {
            AST.block the_block = (AST.block)expr;
            for (AST.expression cur_expr : the_block.l1) {
                NodeVisit(cur_expr);
            }

            // Refer to Page 17 of the COOL Manual for block statement type checking
            the_block.type = the_block.l1.get(the_block.l1.size() - 1).type;
        }

        else if (expr instanceof AST.loop) {
            AST.loop the_loop = (AST.loop)expr;
            NodeVisit(the_loop.predicate);
            NodeVisit(the_loop.body);
            String predicate_type = the_loop.predicate.type;
            Boolean condition = predicate_type.equals("Bool");

            // Refer to Page 20 of the COOL Manual for loop statement type checking
            if (condition == false) {
                reportError(filename, the_loop.predicate.lineNo, "Loop predicate must have type Bool instead of " + predicate_type);
            }
            the_loop.type = "Object";
        }

        else if (expr instanceof AST.cond) {
            AST.cond the_if_else = (AST.cond)expr;
            NodeVisit(the_if_else.predicate);
            NodeVisit(the_if_else.ifbody);
            NodeVisit(the_if_else.elsebody);
            String predicate_type = the_if_else.predicate.type;
            Boolean cond_pred = predicate_type.equals("Bool");

            // Refer to Page 20 of the COOL Manual for if-else statement type checking
            if (cond_pred == false) {
                reportError(filename, the_if_else.predicate.lineNo, "If-else predicate must have type Bool instead of " + predicate_type);
            }
            the_if_else.type = lowest_common_ancestor(the_if_else.ifbody.type, the_if_else.elsebody.type);
        }
        
        else if (expr instanceof AST.assign) {
        	AST.assign the_assign = (AST.assign)expr;
        	NodeVisit(the_assign.e1);
        	
        	if (the_scope_table.lookUpGlobal(the_assign.name) == null) {
        		reportError(filename, the_assign.lineNo, "Trying to assign to undeclared variable");
        		the_assign.type = "Object";
        	} else {
        		String the_assign_typeid = the_scope_table.lookUpGlobal(the_assign.name).typeid;
        		Boolean cond_conf = conformance_check(the_assign.e1.type, the_assign_typeid);
        		if (cond_conf == false) {
        			reportError(filename, the_assign.lineNo, "Non-conformance of types " + the_assign.e1.type + " & " + the_assign_typeid);
        			the_assign.type = "Object";
        		}
        		the_assign.type = the_assign.e1.type;
        	}
        }
        
        else if (expr instanceof AST.typcase) {
            NodeVisit((AST.typcase)expr);
        }
        
        else if (expr instanceof AST.let) {
            NodeVisit((AST.let)expr);
        }
        
        else if (expr instanceof AST.dispatch) {
            NodeVisit((AST.dispatch)expr);
        }
        
        else if (expr instanceof AST.static_dispatch) {
            NodeVisit((AST.static_dispatch)expr);
        }
    }

    // The "big" function to check case expressions
    private void NodeVisit(AST.typcase cases) {
        NodeVisit(cases.predicate);
        String predicate_type = cases.predicate.type;
        Boolean cond_pred = predicate_type.equals("Bool");

        // Refer to Page 21 of the COOL Manual for case statement type checking
        if (cond_pred) {
            List<AST.branch> branch_list = cases.branches;
            for (int i = 0; i < branch_list.size(); i++) {
                for (int j = i + 1; j < branch_list.size(); j++) {
                    AST.branch branch_1 = branch_list.get(i);
                    AST.branch branch_2 = branch_list.get(j);
                    Boolean cond_brnch = branch_1.type.equals(branch_2.type);
                    if (cond_brnch) {
                        reportError(filename, branch_1.lineNo, "Non-distinct branch types in case statement");
                    }
                }
            }

            for (AST.branch single_branch : branch_list) {
                the_scope_table.enterScope();
                String ins_type = "Object";
                if (classList.containsKey(single_branch.type) == false) {
                    reportError(filename, single_branch.lineNo, "Class " + single_branch.type + " is undefined");
                } else {
                    ins_type = single_branch.type;
                }
                the_scope_table.insert(single_branch.name, new AST.attr(single_branch.name, ins_type, single_branch.value, single_branch.lineNo));
                NodeVisit(single_branch.value);
                the_scope_table.exitScope();
            }

            // Performing LCA pair-wise to obtain the join as mentioned in Page 20 of the COOL Manual for case type checking

            String the_case_type = null;
            for (int i = 0; i < branch_list.size(); i++) {
                for (int j = i + 1; j < branch_list.size(); j++) {
                    String b1_type = branch_list.get(i).type;
                    String b2_type = branch_list.get(j).type;
                    Boolean cond_brnch = b1_type.equals(b2_type);
                    if (cond_brnch) {
                        reportError(filename, branch_list.get(i).lineNo, "Non-distinct branch types in case expression");
                    }
                }
                if (i == 0) {
                    the_case_type = branch_list.get(i).value.type;
                } else {
                    the_case_type = lowest_common_ancestor(the_case_type, branch_list.get(i).value.type);
                }
            }
            cases.type = the_case_type;
        }

        // If the predicate is not Bool, then we assign the type of the case as the type of the predicate itself
        else {
            reportError(filename, cases.lineNo, "Non-Boolean predicate for case statement");
            cases.type = predicate_type;
        }
    }

    // Another "big" function to check let expressions
    private void NodeVisit(AST.let the_let) {
        the_scope_table.enterScope();
        // Checking if the object are from valid classes
        if (classList.containsKey(the_let.typeid) == false) {
            reportError(filename, the_let.lineNo, "Class of let expression in undefined");
        }

        // If let with no initialization, then there is no need to check for conformance
        // In that case, the value of the_let will be no_expr
        if (the_let.value instanceof AST.no_expr == false) {
            NodeVisit(the_let.value);

            if (conformance_check(the_let.value.type, the_let.typeid) == false) {
                reportError(filename, the_let.lineNo, "Non-conformance of types " + the_let.value.type + " & " + the_let.typeid);
            }
        } else {
            if (the_let.typeid.equals("Int")) {
                the_let.value = new AST.int_const(0, the_let.lineNo);
            } else if (the_let.typeid.equals("String")) {
                the_let.value = new AST.string_const("", the_let.lineNo);
            } else if (the_let.typeid.equals("Bool")) {
                the_let.value = new AST.bool_const(false, the_let.lineNo);
            } else {
                // System.out.println("default_not_weird_attrs is " + atr.value + " of Class " + atr.value.getClass());
                // atr.value = new AST.int_const(0, atr.lineNo);
                // TODO: Confirm with Abhishek Patwardhan about void type
            }
        }

        the_scope_table.insert(the_let.name, new AST.attr(the_let.name, the_let.typeid, the_let.value, the_let.lineNo));
        NodeVisit(the_let.body);
        the_let.type    = the_let.body.type;

        the_scope_table.exitScope();
    }

    // Another "big" function to check dispatches
    private void NodeVisit(AST.dispatch the_dispatch) {
	
        boolean return_true_type = true;
        String true_type = null;

        NodeVisit(the_dispatch.caller);
        List<AST.expression> the_actuals = the_dispatch.actuals;
        for (int i = 0; i < the_actuals.size(); i++) {
            NodeVisit(the_actuals.get(i));
        }

        // Checking if caller's type is a valid type
        String caller_ret_type = the_dispatch.caller.type;
        if (classList.containsKey(caller_ret_type) == false) {
            reportError(filename, the_dispatch.caller.lineNo, "Class " + caller_ret_type + " not found");
            return_true_type = return_true_type && false;
        } else {
            // Checking if the caller's type contains the function that is being called
            if (classList.get(caller_ret_type).methods.containsKey(the_dispatch.name) == false) {
                reportError(filename, the_dispatch.lineNo, "Method " + the_dispatch.name + " is undefined");
                return_true_type = return_true_type && false;
            } else {
                AST.method the_method = classList.get(caller_ret_type).methods.get(the_dispatch.name);
                true_type = the_method.typeid;
                // Check if the number of arguments are the correct number
                if (the_method.formals.size() != the_actuals.size()) {
                    reportError(filename, the_dispatch.lineNo, "Method " + the_dispatch.name + " is dispatched with " + the_actuals.size() + " number of arguments instead of " + the_method.formals.size());
                    return_true_type = return_true_type && false;
                } else {
                    for (int i = 0; i < the_method.formals.size(); i++) {
                        // Actually we need to check for conformance.
                        // But since we are not dealing with SELF_TYPE, the conformance check actually boils down
                        // equal type-checking

                        // Refer to page 19 of the COOL Manual for dispatch type checking
                        boolean cond = (the_method.formals.get(i).typeid).equals(the_actuals.get(i).type);
                        if (cond == false) {
                            reportError(filename, the_dispatch.lineNo, "Required type " + the_method.formals.get(i).typeid + " as argument " + i + 1 + " instead of " + the_actuals.get(i).type + " for the dispatch of method " + the_dispatch.name);
                            return_true_type = return_true_type && false;
                        }
                    }
                }
            }
        }
        if (return_true_type) {
            the_dispatch.type = true_type;
        } else {
            the_dispatch.type = "Object";
        }
    }

    // Another "big" function to check static_dispatches
    private void NodeVisit(AST.static_dispatch the_static_dispatch) {
        boolean return_true_type = true;
        String true_type = null;

        NodeVisit(the_static_dispatch.caller);
        List<AST.expression> the_actuals = the_static_dispatch.actuals;
        for (int i = 0; i < the_actuals.size(); i++) {
            NodeVisit(the_actuals.get(i));
        }

        // Checking if the static dispatch type exists
        boolean cond_stat_dis_type = classList.containsKey(the_static_dispatch.typeid);
        if (cond_stat_dis_type == false) {
            reportError(filename, the_static_dispatch.lineNo, "Static dispatch to non-existent type " + the_static_dispatch.typeid);
            return_true_type = return_true_type && false;
        } else {
            // Checking conformance between the caller type and the static dispatch type
            String caller_ret_type = the_static_dispatch.caller.type;
            boolean cond_conf = conformance_check(caller_ret_type, the_static_dispatch.typeid);
            if (cond_conf == false) {
                reportError(filename, the_static_dispatch.lineNo, "Non-conformance of types " + caller_ret_type + " & " + the_static_dispatch.typeid);
                return_true_type = return_true_type && false;
            } else {
                AST.method the_method = classList.get(caller_ret_type).methods.get(the_static_dispatch.name);
                true_type = the_method.typeid;
                // Check if number of arguments are the correct number
                if (the_method.formals.size() != the_actuals.size()) {
                    reportError(filename, the_static_dispatch.lineNo, "Method " + the_static_dispatch.name + " is dispatched with " + the_actuals.size() + " number of arguments instead of " + the_method.formals.size());
                    return_true_type = return_true_type && false;
                } else {
                    for (int i = 0; i < the_method.formals.size(); i++) {
                        // Actually we need to check for conformance.
                        // But since we are not dealing with SELF_TYPE, the conformance check actually boils down
                        // equal type-checking

                        // Refer to page 19 of the COOL Manual for static dispatch type checking
                        boolean cond = (the_method.formals.get(i).typeid).equals(the_actuals.get(i).type);
                        if (cond == false) {
                            reportError(filename, the_static_dispatch.lineNo, "Required type " + the_method.formals.get(i) + " as argument " + i + 1 + " instead of " + the_actuals.get(i).type + " for the dispatch of method " + the_static_dispatch.name);
                            return_true_type = return_true_type && false;
                        }
                    }
                }
            }
        }
        if (return_true_type) {
            the_static_dispatch.type = true_type;
        } else {
            the_static_dispatch.type = "Object";
        }
    }

    // Function to get the lowest common ancestor of two types
    // This is used to compute the join of two types
    private String lowest_common_ancestor(String type_1, String type_2) {
        if (type_1.equals(type_2)) {
            return type_1;
        } else if (classList.get(type_1).height < classList.get(type_2).height) {
            return lowest_common_ancestor(type_2, type_1);
        } else {
            return lowest_common_ancestor(classList.get(type_1).parent, type_2);
        }
    }

    // Function used to check conformance generally between an inferred type and declared type
    // Refer to page 6 for the definition of conformance of two types
    private boolean conformance_check(String inferred_type, String declared_type) {
        String LCA_infer_decl = lowest_common_ancestor(inferred_type, declared_type);
        boolean confrm_cond_1 = LCA_infer_decl.equals(declared_type);
        boolean confrm_cond_2 = (classList.get(inferred_type).height >= classList.get(declared_type).height);
        return (confrm_cond_1 && confrm_cond_2);
    }
}
