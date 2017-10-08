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
        if (op.isInternal() == true) {
            out.print("internal ");
        }
        out.print("constant " + op.getTypename() + " ");
        if (op.get_type().getId() == Operand.INT8) {
            out.print("c\"");
            escapedString(out, op.getValue());
            out.print("\\00\"\n");
        } else {
            out.print(op.getValue() + "\n");
        }
    }

    void initExtConstant(PrintWriter out, String name, OpType type) {
        out.print("@" + name + " = " + "external constant " + type.getName() + "\n");
    }

    void define(PrintWriter out, OpType retType, String name, List<Operand> args) {
        out.print("define " + retType.getName() + " @" + name + "( ");
        for(int i = 0; i < args.size(); i++) {
            if (i != args.size() - 1) {
                out.print(args.get(i).getTypename() + " " + args.get(i).getName() + ", ");
            } else {
                out.print(args.get(i).getTypename() + " " + args.get(i).getName() + ") {\n");
            }
        }
    }

    void declare(PrintWriter out, OpType retType, String name, List<Operand> args) {
        out.print("declare " + retType.getName() + " @" + name + "( ");
        for(int i = 0; i < args.size(); i++) {
            if (i != args.size() - 1) {
                out.print(args.get(i).getName() + ", ");
            } else {
                out.print(args.get(i).getName() + ")\n");
            }
        }
    }

    void typeDefine(PrintWriter out, String className, List<OpType> attributes) {
        out.print("%class." + className + " = type { ");
        for(int i = 0; i < args.size(); i++) {
            if (i != args.size() - 1) { 
                out.print(attributes.get(i).getName() + ", ");
            } else {
                out.print(attributes.get(i).getName() + " }\n");
            }
        }
    }

    void typeAliasDefine(PrintWriter out, String aliasName, OpType type) {
        out.print("%alias." + aliasName + " = type " + type.getName() + "\n");
    }

    void initStructConstant(PrintWriter out, Operand constant, List<OpType> fieldTypes, List<ConstValue> InitVals) {
        out.print(constant.getName(), " = constant " + constant.getTypename() + "{\n");
        for(int i = 0; i < InitVals.size(); i++) {
            out.print("\t" + fieldTypes.get(i).getName() + " ");
            if (InitVals.get(i).getType().getId() == Operand.INT8 && fieldTypes.get(i).getId() == Operand.INT8_PTR) {
                getElementPtr(out, InitVals.get(i).getType(), InitVals.get(i), IntValue(0), IntValue(0));
            } else {
                out.print(InitVals.get(i).getValue());
            }

            if (i != InitVals.size() - 1) {
                out.print(", ");
            } else {
                out.print("}\n");
            }
        }
    }

    void beginBlock(PrintWriter out, String label) {
        out.print("\n" + label + ":\n");
    }

    void arithOp(PrintWriter out, String operation, Operand op1, Operand op2, Operand result) {
        out.print("\t");
        if (result.isEmpty() == false) {
            out.print(result.getName() + " = ");
        } 
        out.print(operation + " " + op1.getTypename() + " " + op1.getName() + ", "  + op2.getName() + "\n");
    }

    void mallocOp(PrintWriter out, Operand size, Operand result) {
        out.print("\t" + result.getName() + " = call i8* @malloc(i64 " + size.getName() + ")\n");
    }

    void allocaOp(PrintWriter out, OpType type, Operand result) {
        out.print("\t" + result.getName() + " = alloca " + type.getName() + "\n");
    }

    void loadOp(PrintWriter out, OpType type, Operand op, Operand result) {
        out.print("\t" + result.getName() + " = load " + type.getName() + ", " + op.getTypename() + " "
                    + op.getName() + "\n");
    }

    
}
