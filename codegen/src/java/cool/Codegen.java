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

        for (AST.class_ cl : program.classes) {
            filename = cl.filename;
            insert_class(cl);

            // Taking the attributes of the class first and generating code for it
            List<OpType> attribute_types = new ArrayList<OpType>();
            for (AST.attr attribute : classList.get(cl.name).attributes) {
                attribute_types.add(get_optype(attribute.typeid, true));
            }
            print_util.typeDefine(out, cl.name, attribute_types); // Emits the code

            // Taking the methods of the class now and generating code for it
            for (AST.method mtd : classList.get(cl.name).methods) {

                /* Per method operations: 
                    1. Make a list of Operand for the arguments. First Operand is a pointer to the class with name "this"
                    2. Make a ret_type of type OpType for the return type
                    3. Mangle the name of the class with the name of the function
                    4. Call the define function
                */
                List<Operand> arguments = new ArrayList<Operand>();
                arguments.add(new Operand(get_optype(cl.name, false), "this"));
                for (AST.formal f : mtd.formals) {
                    Operand cur_arg = new Operand(get_optype(f.typeid, true), f.name);
                    arguments.add(cur_arg);
                }
                String method_name = cl.name + "_" + mtd.name; 
                OpType mtd_type = get_optype(mtd.typeid, true);
                print_util.define(out, mtd_type, method_name, arguments); 

                // Required to do here: Build expressions
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

    public OpType get_optype(String typeid, boolean isClass) {
        if (typeid.equals("String") && isClass == true) {
            return new OpType(OpTypeId.INT8_PTR);
        } else if (typeid.equals("Int") && isClass == true) {
            return new OpType(OpTypeId.INT32);
        } else if (typeid.equals("Bool") && isClass == true) {
            return new OpType(OpTypeId.INT1);
        } else {
            return new OpType("class." + typeid, 1);
        }
    }
}
