package cool;

import java.io.PrintWriter;
import java.util.*;

class ClassNode {
    public String name;
    public List<AST.attr> attributes = new ArrayList<AST.attr>();
    public List<AST.method> methods = new ArrayList<AST.method>();

    ClassNode(String class_name, List<AST.attr> class_attributes, List<AST.method> class_methods) {
        name = class_name;
        attributes = class_attributes;
        methods = class_methods;
    }
}

public class Codegen {

    String filename;
    HashMap<String, ClassNode> classList = new HashMap<String, ClassNode>();
    Printer print_util = new Printer();

	public Codegen(AST.program program, PrintWriter out){
		// Write Code generator code here
        out.print("; I am a comment in LLVM-IR. Feel free to remove me.\n");

        // C String Functions required: strlen, strcat, str(n)cpy
        OpType string_type = get_optype("String", true, 0);
        OpType int_type = get_optype("Int", true, 0);
        OpType bool_type = get_optype("Bool", true, 0);
        List<OpType> params;

        // String concatenation
        params = new ArrayList<OpType>();
        params.add(string_type);
        params.add(string_type);
        print_util.declare(out, string_type, "strcat", params);

        // String copy
        print_util.declare(out, string_type, "strcpy", params);

        // String n copy
        params.add(int_type);
        print_util.declare(out, string_type, "strcat", params);

        // String length
        params = new ArrayList<OpType>();
        params.add(string_type);
        print_util.declare(out, int_type, "strlen", params);

        // C IO Functions required: scanf, printf
        params = new ArrayList<OpType>();
        params.add(string_type);
        params.add(new OpType(OpTypeId.VAR_ARG));
        print_util.declare(out, int_type, "printf", params);
        print_util.declare(out, int_type, "scanf", params);

        for (AST.class_ cl : program.classes) {
            filename = cl.filename;
            insert_class(cl);

            // Taking the attributes of the class first and generating code for it
            List<OpType> attribute_types = new ArrayList<OpType>();
            for (AST.attr attribute : classList.get(cl.name).attributes) {
                attribute_types.add(get_optype(attribute.typeid, true, 0));
            }
            print_util.typeDefine(out, cl.name, attribute_types); // Emits the code

            /* Now we need to build a constructor for the class to initialize the values of the 
            var_name : type_name <- assignment of type_name
            */
            build_constructor(out, cl.name);

            // Taking the methods of the class now and generating code for it
            for (AST.method mtd : classList.get(cl.name).methods) {

                /* Per method operations: 
                    1. Make a list of Operand for the arguments. First Operand is a pointer to the class with name "this"
                    2. Make a ret_type of type OpType for the return type
                    3. Mangle the name of the class with the name of the function
                    4. Call the define function
                */
                List<Operand> arguments = new ArrayList<Operand>();
                arguments.add(new Operand(get_optype(cl.name, false, 1), "this"));
                for (AST.formal f : mtd.formals) {
                    Operand cur_arg = new Operand(get_optype(f.typeid, true, 0), f.name);
                    arguments.add(cur_arg);
                }
                String method_name = cl.name + "_" + mtd.name; 
                OpType mtd_type = get_optype(mtd.typeid, true, 0);
                print_util.define(out, mtd_type, method_name, arguments);
                print_util.beginBlock(out, "entry");
                
                // Required to do here: Build expressions
                // Placeholder completion added
                out.print("}\n");
            }
        }
    }

    public void insert_class(AST.class_ cur_class) {
        List<AST.attr> cur_class_attributes = new ArrayList<AST.attr>();
        List<AST.method> cur_class_methods = new ArrayList<AST.method>();

        for (AST.feature f : cur_class.features) {
            if (f instanceof AST.attr) {
                AST.attr cur_attr = (AST.attr)f;
                cur_class_attributes.add(cur_attr);
            } else if (f instanceof AST.method) {
                AST.method cur_method = (AST.method)f;
                cur_class_methods.add(cur_method);
            }
        }           
        classList.put(cur_class.name, new ClassNode(cur_class.name, cur_class_attributes, cur_class_methods));
    }

    public OpType get_optype(String typeid, boolean isClass, int depth) {
        if (typeid.equals("String") && isClass == true) {
            return new OpType(OpTypeId.INT8_PTR);
        } else if (typeid.equals("Int") && isClass == true) {
            return new OpType(OpTypeId.INT32);
        } else if (typeid.equals("Bool") && isClass == true) {
            return new OpType(OpTypeId.INT1);
        } else {
            return new OpType("class." + typeid, depth);
        }
    }

    // Function to generate the constructor of a given class
    public void build_constructor(PrintWriter out, String class_name) {

        // Name of constructor (mangled)
        String method_name = class_name + "_Cons_" + class_name;

        // List of Operand for attributes
        List<Operand> cons_arg_list = new ArrayList<Operand>();
        cons_arg_list.add(new Operand(get_optype(class_name, false, 1), "this"));

        // Define the constructor and establish pointer information
        print_util.define(out, new OpType(OpTypeId.VOID), method_name, cons_arg_list);
        print_util.beginBlock(out, "entry");
        print_util.allocaOp(out, get_optype(class_name, false, 1), new Operand(get_optype(class_name, false, 1), "this.addr"));
        load_store_classOp(out, class_name, "this");

        /* For each attribute:
           1. Perform allocation by calling that attribute from the class' definition
           2. Store a default value if you have to. Apparently, we have to, by the definition of Cool
           So far only Ints have been taken care of
           Strings and Bools have to taken care of too. Once "new" is ready, we can integrate that too.
        */
        List<AST.attr> cur_class_attr_list = classList.get(class_name).attributes;
        for (int i = 0; i < cur_class_attr_list.size(); i++) {
            AST.attr cur_attr = cur_class_attr_list.get(i);         // Get the current attribute
            Operand result = new Operand(get_optype("Int", true, 0), cur_attr.name);    // Generate Operand
            List<Operand> operand_list = new ArrayList<Operand>();                      // Generate List<Operand> to be passed to a func
            operand_list.add(new Operand(get_optype(class_name, false, 1), "this1"));

            // Int attribute codegen
            if (cur_attr.typeid.equals("Int")) {
                operand_list.add((Operand)new IntValue(0));
                operand_list.add((Operand)new IntValue(i));
                print_util.getElementPtr(out, get_optype(class_name, false, 0), operand_list, result, true);    // That func is here
                OpType ptr = new OpType(OpTypeId.INT32_PTR);
                if (cur_attr.value instanceof AST.no_expr) {
                    print_util.storeOp(out, (Operand)new IntValue(0), new Operand(ptr, cur_attr.name));
                } else {
                    print_util.storeOp(out, (Operand)new IntValue(((AST.int_const)cur_attr.value).value), new Operand(ptr, cur_attr.name));
                }
            }

            // String attribute codegen
            else if (cur_attr.typeid.equals("String")) {
                // Do something
            }

            // Bool attribute codegen
            else if (cur_attr.typeid.equals("Bool")) {
                // Do something
            }
        }
        out.print("}\n");
    }

    // Utility function to perform load store pair operation for constructors
    public void load_store_classOp(PrintWriter out, String type_name, String obj_name) {
        OpType ptr = get_optype(type_name, false, 1);
        OpType dptr = get_optype(type_name, false, 2);
        Operand op = new Operand(ptr, obj_name);
        Operand op_addr = new Operand(dptr, obj_name + ".addr");
        print_util.storeOp(out, op, op_addr);
        print_util.loadOp(out, ptr, op_addr, new Operand(ptr, "this1")); 
    }

    // Utility function to generate body for all String functions
    public void string_functions(PrintWriter out, String f_name) {
        if (f_name.equals("length")) {
            
        }
    }
}
