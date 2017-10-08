package cool;
import java.util.List;

public final class IRUtils {


    public static void my_print_escaped_string(PrintWriter out, string s) {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            switch (s[i]) {
            case '\\' : out.print("\\5C"); break;
            case '\"' : out.print("\\22"); break;
            case '\n' : out.print("\\0A"); break;
            case '\t' : out.print("\\09"); break;
            case '\b' : out.print("\\08"); break;
            case '\f' : out.print("\\0C"); break;

            default : out.print(s[i]);
            }
        }
    }

    /* Constant initialization
     * Format: @name = [internal] constant type value
     */
    void ValuePrinter::init_constant(PrintWriter out, string name, const_value op) {
        out.print("@" + name + " = " + (op.is_internal() ? "internal " : "")
        + "constant " + op.get_typename() + " ";
        if (op.get_type().get_id() == INT8) {
            o << "c\"";
            my_print_escaped_string(o, op.get_value().c_str());
            o << "\\00\"";
        } else
            o << op.get_value();
        o << "\n";
    }


}