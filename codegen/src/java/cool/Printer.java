package cool;

import java.io.PrintWriter;
import java.util.*;

public class Printer {
    public void escapedString(PrintWriter out, String str) {
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

    public void initConstant(PrintWriter out, String name, ConstValue op) {
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
}
