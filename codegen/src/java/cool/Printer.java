package cool;

import java.io.PrintWriter;
import java.util.*;

class Printer {
    void escapedString(PrintWriter out, String str) {
        for(int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\\') {
                out.print("\\5C");
            } else if (str.charAt(i) == '\"') {
                out.print("\\22");
            } else if (str.charAt(i) == '\n') {
                out.print("\\0A");
            } else if (str.charAt(i) == '\t') {
                out.print("\\09");
            } else {
                out.print(str.charAt(i));
            }
        }   
    }

    void initConstant(PrintWriter out, String name, ConstValue op) {
        out.print("@" + name + " = ");
        if (op.is_internal() == true) {
            out.print("internal ");
        }
        out.print("constant " + op.get_typename() + " ");
        if (op.get_type().get_id() == Operand.INT8) {
            out.print("c\"");
            escapedString(out, op.get_value());
            out.print("\\00\"\n");
        } else {
            out.print(op.get_value() + "\n");
        }
    }

    void initExtConstant(PrintWriter out, String name, OpType type) {
        out.print("@" + name + " = " + "external constant " + type.get_name() + "\n");
    }

    void define(PrintWriter out, OpType retType, String name, List<Operand> args) {
        out.print("define " + retType.get_name() + " @" + name + "( ");
        for(int i = 0; i < args.size(); i++) {
            if (i != args.size() - 1) {
                out.print(args.get(i).get_typename() + " " + args.get(i).get_name() + ", ");
            } else {
                out.print(args.get(i).get_typename() + " " + args.get(i).get_name() + ") {\n");
            }
        }
    }

    void declare(PrintWriter out, OpType retType, String name, List<Operand> args) {
        out.print("declare " + retType.get_name() + " @" + name + "( ");
        for(int i = 0; i < args.size(); i++) {
            if (i != args.size() - 1) {
                out.print(args.get(i).get_name() + ", ");
            } else {
                out.print(args.get(i).get_name() + ")\n");
            }
        }
    }

    void typeDefine(PrintWriter out, String class_name, List<OpType> attributes) {
        out.print("%class." + class_name + " = type { ");
        for(int i = 0; i < args.size(); i++) {
            if (i != args.size() - 1) {            
                out.print(attributes.get(i).get_name() + ", ");
            } else {
                out.print(attributes.get(i).get_name() + " }\n");
            }
        }
    }

    void typeAliasDefine(PrintWriter out, String alias_name, OpType type) {
        out.print("%alias." + alias_name + " = type " + type.get_name() + "\n");
    }
}
