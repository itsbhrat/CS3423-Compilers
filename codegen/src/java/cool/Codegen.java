package cool;

import java.io.PrintWriter;
import java.util.*;

class ClassNode {
    public String name;
    public HashMap<String, AST.attr> attributes = new HashMap<String, AST.attr>();
    public HashMap<String, AST.method> methods = new HashMap<String, AST.method>();

    ClassNode(String class_name, HashMap<String, AST.attr> class_attributes, HashMap<String, AST.method> class_methods) {
        name = class_name;
        attributes.putAll(class_attributes);
        methods.putAll(class_methods);
    }
}

public class Codegen {

    String filename;
    HashMap<String, String> types_map = new HashMap<String, String>();
    HashMap<String, ClassNode> classList = new HashMap<String, ClassNode>();

	public Codegen(AST.program program, PrintWriter out){
		// Write Code generator code here
        out.print("; I am a comment in LLVM-IR. Feel free to remove me.\n");

        // HashMap for the type conversion between COOL and LLVM-IR
        types_map.put("String", "[1024 x i8]");
        types_map.put("Int", "i32");
        types_map.put("Bool", "i1");

        for (AST.class_ cl : program.classes) {
            filename = cl.filename;
            insert_class(cl);
            List<AST.attr> attributes_list = new ArrayList<AST.attr>(classList.get(cl.name).attributes.values());
            type_define(out, cl.name, attributes_list);

            for (AST.method m : classList.get(cl.name).methods.values()) {
                    declare(out, m.typeid, cl.name, m.name, m.formals);
            }
        }
    }

    public void insert_class(AST.class_ cur_class) {
        HashMap<String, AST.attr> cur_class_attributes = new HashMap<String, AST.attr>();
        HashMap<String, AST.method> cur_class_methods = new HashMap<String, AST.method>();

        for (AST.feature f : cur_class.features) {
            if (f instanceof AST.attr) {
                AST.attr cur_attr = (AST.attr)f;
                cur_class_attributes.put(cur_attr.name, cur_attr);
            } else if (f instanceof AST.method) {
                AST.method cur_method = (AST.method)f;
                cur_class_methods.put(cur_method.name, cur_method);
            }
        }           
        classList.put(cur_class.name, new ClassNode(cur_class.name, cur_class_attributes, cur_class_methods));
    }

    public void type_define(PrintWriter out, String class_name, List<AST.attr> attributes) {
        out.print("%class." + class_name + " = type {");
        for (int i = 0; i < attributes.size(); i++) {
            if (i != attributes.size() - 1) {
                out.print(" " + types_map.get(attributes.get(i).typeid) + ",");
            } else {
                out.print(" " + types_map.get(attributes.get(i).typeid) + "");
            }
        }
        out.print(" }\n");
    }

    public void declare(PrintWriter out, String ret_type, String class_name, String method_name, List<AST.formal> formals) {
        out.print("declare " + types_map.get(ret_type) + " @" + class_name + "_" + method_name + "(");
        for (int i = 0; i < formals.size(); i++) {
            if (i != formals.size() - 1) {
                out.print(" " + types_map.get(formals.get(i).typeid) + ",");
            } else {
                out.print(" " + types_map.get(formals.get(i).typeid) + "");
            }
        }
        out.print(" )\n");
    }
}
