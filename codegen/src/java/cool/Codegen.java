package cool;

import java.io.PrintWriter;
import java.util.*;

class ClassNode {
  public String name;
  public String parent;
  public List<AST.attr> attributes = new ArrayList<AST.attr>();
  public List<AST.method> methods = new ArrayList<AST.method>();

  ClassNode(String class_name, String class_parent, List<AST.attr> class_attributes, List<AST.method> class_methods) {
    name = class_name;
    parent = class_parent;
    attributes = class_attributes;
    methods = class_methods;
  }
}

class Tracker {
  public int register;
  OpType last_instruction;
  String last_basic_block;
  Tracker() {
    register = 0;
    last_instruction = new OpType();
    last_basic_block = "";
  }

  Tracker(int reg , OpType op, String lb) {
    register = reg;
    last_instruction = op;
    last_basic_block = lb;
  }
}

public class Codegen {

  String filename;
  HashMap<String, ClassNode> classList = new HashMap<String, ClassNode>();
  Printer print_util = new Printer();
  HashMap<String, Integer> string_table = new HashMap<String, Integer>();
  Integer string_counter = 0;

  OpType string_type = new OpType(OpTypeId.INT8_PTR);
  OpType int_type = new OpType(OpTypeId.INT32);
  OpType bool_type = new OpType(OpTypeId.INT1);
  OpType void_type = new OpType(OpTypeId.VOID);

  Integer loop_basic_block_counter = 0;
  Integer if_basic_block_counter = 0;
  String CLASS_NAME = null;
  OpType method_return_type;
  List<String> function_formal_arguments;   // store the method formal parameters

  public Codegen(AST.program program, PrintWriter out) {
    // Write Code generator code here
    out.print("; I am a comment in LLVM-IR. Feel free to remove me.\n");

    // Assuming that the code runs on linux machines with 64 bit
    // Also taking into fact that all classes are in one file
    filename = program.classes.get(0).filename;
    out.println("source_filename = \"" + filename + "\"");
    out.println("target triple = \"x86_64-unknown-linux-gnu\"\n");

    // C String Functions required: strlen, strcat, str(n)cpy
    List<OpType> params;

    // String concatenation
    params = new ArrayList<OpType>();
    params.add(string_type);
    params.add(string_type);
    print_util.declare(out, string_type, "strcat", params);

    // String copy
    print_util.declare(out, string_type, "strcpy", params);

    // String compare
    print_util.declare(out, int_type, "strcmp", params);

    // String n copy
    params.add(int_type);
    print_util.declare(out, string_type, "strncpy", params);

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

    // C Misc Function required: malloc and exit
    params = new ArrayList<OpType>();
    params.add(int_type);
    print_util.declare(out, string_type, "malloc", params);
    print_util.declare(out, new OpType(OpTypeId.VOID), "exit", params);

    // Format specifiers in C : %d and %s
    out.println("@strfmt = private unnamed_addr constant [3 x i8] c\"%s\\00\"");
    out.println("@intfmt = private unnamed_addr constant [3 x i8] c\"%d\\00\"");
    out.println("@.str.empty = private unnamed_addr constant [1 x i8] c\"\\00\"");
    out.println("@divby0err = private unnamed_addr constant [31 x i8] c\"Runtime Error: Divide by Zero\\0A\\00\"");
    out.println("@staticdispatchonvoiderr = private unnamed_addr constant [47 x i8] c\"Runtime Error: Static Dispatch on void object\\0A\\00\"");
    out.println("@nostaticdispatchonvoiderr = private unnamed_addr constant [29 x i8] c\"Problem Error:**********\\0A\\0A\\0A\\0A\\00\"");

    // adding built in classes to the AST
    append_built_in_classes(program);

    for (AST.class_ cl : program.classes) {
      filename = cl.filename;
      insert_class(cl);
    }

    for (AST.class_ cl : program.classes) {
      filename = cl.filename;

      if (cl.name.equals("Main")) {

        // Define the main function here
        print_util.define(out, int_type, "main", new ArrayList<Operand>());
        print_util.allocaOp(out, get_optype("Main", true, 0), new Operand(get_optype("Main", true, 1), "obj"));
        List<Operand> op_list = new ArrayList<Operand>();
        op_list.add(new Operand(get_optype("Main", true, 1), "obj"));
        print_util.callOp(out, new ArrayList<OpType>(), "Main_Cons_Main", true, op_list, new Operand(get_optype("Main", true, 1), "obj1"));
        op_list.set(0, new Operand(get_optype("Main", true, 1), "obj1"));
        print_util.callOp(out, new ArrayList<OpType>(), "Main_main", true, op_list, new Operand(void_type, "null"));
        print_util.retOp(out, (Operand)new IntValue(0));
      }

      // If the class is one of the predefined classes in COOL, we only require the functions
      // and not the class itself
      if (cl.name.equals("Int") || cl.name.equals("String") || cl.name.equals("Bool") || cl.name.equals("Object") || cl.name.equals("IO")) {

        if (cl.name.equals("String")) {
          pre_def_string(out, "length");
          pre_def_string(out, "concat");
          pre_def_string(out, "substr");
          pre_def_string(out, "copy");
          pre_def_string(out, "strcmp");
        } else if (cl.name.equals("Object")) {
          pre_def_object(out, "abort");
          print_util.typeDefine(out, cl.name, new ArrayList<OpType>());
          build_constructor(out, cl.name, new Tracker());
        } else if (cl.name.equals("IO")) {
          pre_def_io(out, "in_int");
          pre_def_io(out, "in_string");
          pre_def_io(out, "out_int");
          pre_def_io(out, "out_string");
          print_util.typeDefine(out, cl.name, new ArrayList<OpType>());
          build_constructor(out, cl.name, new Tracker());
        }
        continue;
      }

      // Taking the attributes of the class first and generating code for it
      List<OpType> attribute_types = new ArrayList<OpType>();
      for (AST.attr attribute : classList.get(cl.name).attributes) {
        attribute_types.add(get_optype(attribute.typeid, true, 1));
        if (attribute.typeid.equals("String") && attribute.value instanceof AST.string_const) { // Getting existing string constants
          string_capture(out, attribute.value);
        }
      }
      print_util.typeDefine(out, cl.name, attribute_types); // Emits the code

      /* Now we need to build a constructor for the class to initialize the values of the
      var_name : type_name <- assignment of type_name
      */
      build_constructor(out, cl.name, new Tracker());

      Tracker counter = new Tracker();

      // Taking the methods of the class now and generating code for it
      for (AST.method mtd : classList.get(cl.name).methods) {

        /* Per method operations:
            1. Make a list of Operand for the arguments. First Operand is a pointer to the class with name "this"
            2. Make a ret_type of type OpType for the return type
            3. Mangle the name of the class with the name of the function
            4. Call the define function
        */
        // this operand of function
        string_capture(out, mtd.body);
        List<Operand> arguments = new ArrayList<Operand>();
        arguments.add(new Operand(get_optype(cl.name, true, 1), "this"));
        function_formal_arguments = new ArrayList<String>();

        for (AST.formal f : mtd.formals) {
          Operand cur_arg = new Operand(get_optype(f.typeid, true, 1), f.name);
          arguments.add(cur_arg);
          function_formal_arguments.add(f.name);
        }
        String method_name = cl.name + "_" + mtd.name;
        OpType mtd_type;
        if (mtd.typeid.equals("Object")) {
          mtd_type = new OpType(OpTypeId.VOID);
        } else {
          mtd_type = get_optype(mtd.typeid, true, 0);
        }
        print_util.define(out, mtd_type, method_name, arguments);

        // printing the retval for storing return values
        method_return_type = get_optype(mtd.typeid, true, 0);
        if (!mtd.typeid.equals("Object")) {
          Operand retval = new Operand(get_optype(mtd.typeid, true, 0), "retval");
          print_util.allocaOp(out, get_optype(mtd.typeid, true, 0), retval);
          // print_util.storeOp(out, new Operand(method_return_type, String.valueOf(x - 1)), new Operand(method_return_type.getPtrType(), "retval"));
        }
        allocate_function_parameters(out, arguments);
        get_class_attributes(out, cl.name);

        // initializing basic blocks util variables
        loop_basic_block_counter = 0;
        if_basic_block_counter = 0;
        counter.register = 0;
        counter.last_instruction = method_return_type;
        counter.last_basic_block = "%entry";

        CLASS_NAME = cl.name;
        // Required to do here: Build expressions
        counter = NodeVisit(out, mtd.body, counter);
        if (! ((mtd.body instanceof AST.block) || (mtd.body instanceof AST.loop) || (mtd.body instanceof AST.cond) ||
               (mtd.body instanceof AST.static_dispatch)) ) {
          attempt_assign_retval(out, counter.last_instruction, counter.register - 1);
        }

        // Placeholder completion added
        print_util.branchUncondOp(out, "fun_returning_basic_block");
        // Add label to print and abort
        out.println("dispatch_on_void_basic_block:");
        print_util.allocaOp(out, string_type, new Operand(string_type, "err_msg_void_dispatch"));
        out.println("\tstore i8* getelementptr inbounds ([47 x i8], [47 x i8]* @staticdispatchonvoiderr, i32 0, i32 0), i8** %err_msg_void_dispatch");
        print_util.loadOp(out, string_type, new Operand(string_type.getPtrType(), "err_msg_void_dispatch"), new Operand(string_type, "print_err_msg_void_dispatch"));
        List<Operand> prnt_args = new ArrayList<Operand>();
        prnt_args.add(new Operand(string_type, "print_err_msg_void_dispatch"));
        print_util.callOp(out, new ArrayList<OpType>(), "IO_out_string", true, prnt_args, new Operand(void_type, "null"));
        print_util.callOp(out, new ArrayList<OpType>(), "Object_abort", true, new ArrayList<Operand>(), new Operand(void_type, "null"));
        print_util.branchUncondOp(out, "fun_returning_basic_block");

        // Add label to print and abort
        out.println("func_div_by_zero_abort:");
        print_util.allocaOp(out, string_type, new Operand(string_type, "err_msg"));
        out.println("\tstore i8* getelementptr inbounds ([31 x i8], [31 x i8]* @divby0err, i32 0, i32 0), i8** %err_msg");
        print_util.loadOp(out, string_type, new Operand(string_type.getPtrType(), "err_msg"), new Operand(string_type, "print_err_msg"));
        prnt_args = new ArrayList<Operand>();
        prnt_args.add(new Operand(string_type, "print_err_msg"));
        print_util.callOp(out, new ArrayList<OpType>(), "IO_out_string", true, prnt_args, new Operand(void_type, "null"));
        print_util.callOp(out, new ArrayList<OpType>(), "Object_abort", true, new ArrayList<Operand>(), new Operand(void_type, "null"));
        print_util.branchUncondOp(out, "fun_returning_basic_block");

        // Placeholder completion added
        out.println("fun_returning_basic_block:");

        if (mtd.typeid.equals("Object")) {
          print_util.retOp(out, new Operand(void_type, "null"));
        } else {
          print_util.loadOp(out, method_return_type, new Operand(method_return_type.getPtrType(), "retval"), new Operand(method_return_type, String.valueOf(counter.register)));
          print_util.retOp(out, new Operand(method_return_type, String.valueOf(counter.register)));
          counter.register++;
        }
      }
    }
  }

  public void append_built_in_classes(AST.program program) {
    /* Classes already present in the table:
     * - Object
     * - IO
     * - String
     * - Int
     * - Bool
     *
     * Object has methods:
     * - abort() : Object
     * - type_name(): String
     * IO has methods:
     * - out_string(x : String) : IO
     * - out_int(x : Int) : IO
     * - in_string() : String
     * - in_int() : String
     * String has methods:
     * - length() : Int
     * - concat(s: String) : String
     * - substr(i : Int, l : Int) : String
     */

    // object class
    List <AST.feature> ol = new ArrayList <AST.feature>();
    ol.add(new AST.method("abort", new ArrayList<AST.formal>(), "Object", new AST.no_expr(0), 0));
    ol.add(new AST.method("type_name", new ArrayList<AST.formal>(), "String", new AST.no_expr(0), 0));
    program.classes.add(new AST.class_("Object", filename, null, ol, 0));

    // IO class
    List <AST.feature> iol = new ArrayList<AST.feature>(ol);  // IO inherits from Object

    List <AST.formal> os_formals = new ArrayList<AST.formal>();
    os_formals.add(new AST.formal("out_string", "String", 0));
    List <AST.formal> oi_formals = new ArrayList<AST.formal>();
    oi_formals.add(new AST.formal("out_int", "Int", 0));

    iol.add(new AST.method("out_string", os_formals, "Object", new AST.no_expr(0), 0));
    iol.add(new AST.method("out_int", oi_formals, "Object", new AST.no_expr(0), 0));
    iol.add(new AST.method("in_string", new ArrayList<AST.formal>(), "String", new AST.no_expr(0), 0));
    iol.add(new AST.method("in_int", new ArrayList<AST.formal>(), "Int", new AST.no_expr(0), 0));
    program.classes.add(new AST.class_("IO", filename, "Object", iol, 0));

    // Int class
    program.classes.add(new AST.class_("Int", filename, "Object", ol, 0));  // Int inherits from Object

    // Bool class
    program.classes.add(new AST.class_("Bool", filename, "Object", ol, 0));  // Bool inherits from Object

    List <AST.feature> sl = new ArrayList<AST.feature>(ol);                // String Inherits from Object
    List<AST.formal> concat_formal = new ArrayList<AST.formal>();
    concat_formal.add(new AST.formal("s", "String", 0));
    List<AST.formal> substr_formal = new ArrayList<AST.formal>();
    substr_formal.add(new AST.formal("i", "Int", 0));
    substr_formal.add(new AST.formal("l", "Int", 0));

    sl.add(new AST.method("length", new ArrayList<AST.formal>(), "Int", new AST.no_expr(0), 0));
    sl.add(new AST.method("concat", concat_formal, "String", new AST.no_expr(0), 0));
    sl.add(new AST.method("substr", substr_formal, "String", new AST.no_expr(0), 0));

    program.classes.add(new AST.class_("String", filename, "Object", sl, 0));
  }
  /*
    public Operand get_initial_value(String type) {
      if(type.equals("Int"))
        return (Operand)(new IntValue(0));
      else if(type.equals("Bool"))
        return (Operand)(new BoolValue(false));
      else if(type.equals("String"))
        return (Operand)(new IntValue(0));
    }*/

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

    if (cur_class.parent == null) {
      classList.put(cur_class.name, new ClassNode(cur_class.name, cur_class.name, cur_class_attributes, cur_class_methods));
    } else {
      classList.put(cur_class.name, new ClassNode(cur_class.name, cur_class.parent, cur_class_attributes, cur_class_methods));
    }
  }

  public OpType get_optype(String typeid, boolean isClass, int depth) {
    if (typeid.equals("void") ) {
      return void_type;
    } else if (typeid.equals("String") && isClass == true) {
      return string_type;
    } else if (typeid.equals("Int") && isClass == true) {
      return int_type;
    } else if (typeid.equals("Bool") && isClass == true) {
      return bool_type;
    } else if (isClass == true) {
      return new OpType("class." + typeid, depth);
    } else {
      return new OpType(typeid, depth);
    }
  }

  // Function to generate the constructor of a given class
  public void build_constructor(PrintWriter out, String class_name, Tracker counter) {

    // Name of constructor (mangled)
    String method_name = class_name + "_Cons_" + class_name;

    // List of Operand for attributes
    List<Operand> cons_arg_list = new ArrayList<Operand>();
    cons_arg_list.add(new Operand(get_optype(class_name, true, 1), "this"));

    // Define the constructor and establish pointer information
    print_util.define(out, get_optype(class_name, true, 1), method_name, cons_arg_list);
    print_util.allocaOp(out, get_optype(class_name, true, 1), new Operand(get_optype(class_name, true, 1), "this.addr"));
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
      Operand result = new Operand(int_type, cur_attr.name);    // Generate Operand
      List<Operand> operand_list = new ArrayList<Operand>();                      // Generate List<Operand> to be passed to a func
      operand_list.add(new Operand(get_optype(class_name, true, 1), "this1"));

      // Int attribute codegen
      if (cur_attr.typeid.equals("Int")) {
        operand_list.add((Operand)new IntValue(0));
        operand_list.add((Operand)new IntValue(i));
        print_util.getElementPtr(out, get_optype(class_name, true, 0), operand_list, result, true);    // That func is here
        OpType ptr = new OpType(OpTypeId.INT32_PTR);
        if (cur_attr.value instanceof AST.no_expr || cur_attr.value instanceof AST.new_) {
          print_util.storeOp(out, (Operand)new IntValue(0), new Operand(ptr, cur_attr.name));
        } else {
          counter = NodeVisit(out, cur_attr.value, counter);
          print_util.storeOp(out, new Operand(counter.last_instruction, String.valueOf(counter.register - 1)), new Operand(counter.last_instruction.getPtrType(), cur_attr.name));
        }
      }

      // String attribute codegen
      else if (cur_attr.typeid.equals("String")) {
        operand_list.add((Operand)new IntValue(0));
        operand_list.add((Operand)new IntValue(i));
        print_util.getElementPtr(out, get_optype(class_name, true, 0), operand_list, result, true);     // That func is here
        String length_string = null;
        if (cur_attr.value instanceof AST.no_expr || cur_attr.value instanceof AST.new_) {
          length_string = "[" + 1 + " x i8]";
          out.println("\tstore i8* getelementptr inbounds (" + length_string + ", " + length_string + "* @.str.empty , i32 0, i32 0), i8** %" + cur_attr.name);
        } else {
          counter = NodeVisit(out, cur_attr.value, counter);
          print_util.storeOp(out, new Operand(counter.last_instruction, String.valueOf(counter.register - 1)), new Operand(counter.last_instruction.getPtrType(), cur_attr.name));
        }
      }

      // Bool attribute codegen
      else if (cur_attr.typeid.equals("Bool")) {
        operand_list.add((Operand)new IntValue(0));
        operand_list.add((Operand)new IntValue(i));
        print_util.getElementPtr(out, get_optype(class_name, true, 0), operand_list, result, true);    // That func is here
        OpType ptr = new OpType(OpTypeId.INT1_PTR);
        if (cur_attr.value instanceof AST.no_expr || cur_attr.value instanceof AST.new_) {
          print_util.storeOp(out, (Operand)new BoolValue(false), new Operand(ptr, cur_attr.name));
        } else {
          counter = NodeVisit(out, cur_attr.value, counter);
          print_util.storeOp(out, new Operand(counter.last_instruction, String.valueOf(counter.register - 1)), new Operand(counter.last_instruction.getPtrType(), cur_attr.name));
        }
      }

      // // Bool attribute codegen
      // else if (cur_attr.typeid.equals("IO") || cur_attr.typeid.equals("Object")) {
      //   continue;
      // }

      // other cases
      else {
        if (cur_attr.typeid.equals(class_name) && cur_attr.value instanceof AST.new_) {
          out.println("; ***************** Possible stack recursion error possible. Please remove this statement in the program ****************");
        }
        operand_list.add((Operand)new IntValue(0));
        operand_list.add((Operand)new IntValue(i));
        print_util.getElementPtr(out, get_optype(class_name, true, 0), operand_list, result, true);    // That func is here
        OpType ptr = get_optype(class_name, true, 1);
        if ((cur_attr.value instanceof AST.no_expr)) {
          String null_type = get_optype(cur_attr.typeid, true, 1).getName();
          out.println("\tstore " + null_type + " null , " + null_type + "* %" + (cur_attr.name));
        } else {
          counter = NodeVisit(out, cur_attr.value, counter);
          print_util.storeOp(out, new Operand(get_optype(cur_attr.typeid, true, 1), String.valueOf(counter.register - 1)), new Operand(get_optype(cur_attr.typeid, true, 1).getPtrType(), cur_attr.name));
        }
      }
    }
    print_util.retOp(out, new Operand(get_optype(class_name, true, 1), "this1"));
  }

  public void allocate_function_parameters(PrintWriter out, List<Operand> arguments) {

    Operand return_val = null, op = null, op_addr = null;

    // this operand of function
    return_val = new Operand(arguments.get(0).getType(), "this.addr");
    print_util.allocaOp(out, arguments.get(0).getType(), return_val);

    // alloca all the function parameters
    for (int i = 1; i < arguments.size(); i++) {
      return_val = new Operand(arguments.get(i).getType(), arguments.get(i).getOriginalName() + ".addr");
      print_util.allocaOp(out, arguments.get(i).getType(), return_val);
    }

    // store all the function parameters
    for (int i = 1; i < arguments.size(); i++ ) {
      op = new Operand(arguments.get(i).getType(), arguments.get(i).getOriginalName());
      op_addr = new Operand(arguments.get(i).getType().getPtrType(), arguments.get(i).getOriginalName() + ".addr");
      print_util.storeOp(out, op, op_addr);
    }
  }

  // Utility function to load the "global" class attributes into the functions
  public void get_class_attributes(PrintWriter out, String class_name) {
    ClassNode cur_class = classList.get(class_name);
    load_store_classOp(out, class_name, "this");
    for (int i = 0; i < cur_class.attributes.size(); i++) {

      if (is_clash_with_method_formal(cur_class.attributes.get(i).name))    // checking function formal parameter masks global attribute
        continue;

      List<Operand> operand_list = new ArrayList<Operand>();
      Operand result = new Operand(int_type, cur_class.attributes.get(i).name);
      operand_list.add(new Operand(get_optype(class_name, true, 1), "this1"));
      operand_list.add((Operand)new IntValue(0));
      operand_list.add((Operand)new IntValue(i));
      print_util.getElementPtr(out, get_optype(class_name, true, 0), operand_list, result, true);     // That func is here
    }
  }

  public boolean is_clash_with_method_formal(String variable_name) {
    for (String a : function_formal_arguments) {
      if ( a.equals(variable_name) )
        return true;
    }
    return false;
  }

  public String get_attribute_address(String objname) {
    if (!is_clash_with_method_formal(objname)) {    // checking function formal parameter masks global attribute
      for (AST.attr check_attr : classList.get(CLASS_NAME).attributes) {
        if (objname.equals(check_attr.name)) {
          return objname;
        }
      }
    }
    return (objname + ".addr");
  }

  // Utility function to perform load store pair operation for constructors
  public void load_store_classOp(PrintWriter out, String type_name, String obj_name) {
    OpType ptr = get_optype(type_name, true, 1);
    OpType dptr = get_optype(type_name, true, 2);
    Operand op = new Operand(ptr, obj_name);
    Operand op_addr = new Operand(dptr, obj_name + ".addr");
    print_util.storeOp(out, op, op_addr);
    print_util.loadOp(out, ptr, op_addr, new Operand(ptr, "this1"));
  }

  // Utility function to generate body for all String functions
  public void pre_def_string(PrintWriter out, String f_name) {
    String new_method_name = "String_" + f_name;
    Operand return_val = null;
    List<Operand> arguments = null;

    // Emitting code for length
    if (f_name.equals("length")) {
      return_val = new Operand(int_type, "retval");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "this"));
      print_util.define(out, return_val.getType(), new_method_name, arguments);
      print_util.callOp(out, new ArrayList<OpType>(), "strlen", true, arguments, return_val);
      print_util.retOp(out, return_val);
    }

    // Emitting code for cat
    else if (f_name.equals("concat")) {
      return_val = new Operand(string_type, "retval");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "this"));
      arguments.add(new Operand(string_type, "that"));
      print_util.define(out, return_val.getType(), new_method_name, arguments);

      return_val = new Operand(string_type, "memnew");
      arguments = new ArrayList<Operand>();
      arguments.add((Operand)new IntValue(1024));
      print_util.callOp(out, new ArrayList<OpType>(), "malloc", true, arguments, return_val);

      return_val = new Operand(string_type, "copystring");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "memnew"));
      arguments.add(new Operand(string_type, "this"));
      print_util.callOp(out, new ArrayList<OpType>(), "strcpy", true, arguments, return_val);

      return_val = new Operand(string_type, "retval");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "copystring"));
      arguments.add(new Operand(string_type, "that"));
      print_util.callOp(out, new ArrayList<OpType>(), "strcat", true, arguments, return_val);

      print_util.retOp(out, return_val);
    }

    // Emitting code for substr
    // This needs to be checked

    else if (f_name.equals("substr")) {
      return_val = new Operand(string_type, "retval");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "this"));
      arguments.add(new Operand(int_type, "start"));
      arguments.add(new Operand(int_type, "len"));
      print_util.define(out, return_val.getType(), new_method_name, arguments);

      return_val = new Operand(string_type, "0");
      arguments = new ArrayList<Operand>();
      arguments.add((Operand)new IntValue(1024));
      print_util.callOp(out, new ArrayList<OpType>(), "malloc", true, arguments, return_val);

      return_val = new Operand(string_type, "1");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "this"));
      arguments.add(new Operand(int_type, "start"));
      print_util.getElementPtr(out, new OpType(OpTypeId.INT8), arguments, return_val, true);

      return_val = new Operand(string_type, "2");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "0"));
      arguments.add(new Operand(string_type, "1"));
      arguments.add(new Operand(int_type, "len"));
      print_util.callOp(out, new ArrayList<OpType>(), "strncpy", true, arguments, return_val);
      out.println("\t%3 = getelementptr inbounds [1 x i8], [1 x i8]* @.str.empty, i32 0, i32 0");
      out.println("\t%retval = call i8* @strcat( i8* %2, i8* %3 )");
      out.println("\tret i8* %retval\n}");
    }

    // Emitting code for strcmp
    else if (f_name.equals("strcmp")) {
      return_val = new Operand(bool_type, "retval");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "this"));
      arguments.add(new Operand(string_type, "start"));
      print_util.define(out, return_val.getType(), new_method_name, arguments);

      return_val = new Operand(int_type, "0");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "this"));
      arguments.add(new Operand(string_type, "start"));
      print_util.callOp(out, new ArrayList<OpType>(), "strcmp", true, arguments, return_val);

      print_util.compareOp(out, "EQ", return_val, (Operand)new IntValue(0), new Operand(bool_type, "1"));

      print_util.retOp(out, new Operand(bool_type, "1"));
    }
  }

  public void pre_def_object(PrintWriter out, String f_name) {
    String new_method_name = "Object_" + f_name;
    Operand return_val = null;
    List<Operand> arguments = null;

    // Method for generating the abort method
    if (f_name.equals("abort")) {
      return_val = new Operand(void_type, "null");
      arguments = new ArrayList<Operand>();
      print_util.define(out, return_val.getType(), new_method_name, arguments);

      out.println("call void (i32) @exit(i32 0)");
      out.println("ret void\n}");
    }
  }

  public void pre_def_io(PrintWriter out, String f_name) {
    String new_method_name = "IO_" + f_name;
    Operand return_val = null;
    List<Operand> arguments = null;

    // Method for generating the out_string method
    if (f_name.equals("out_string")) {
      return_val = new Operand(void_type, "null");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "given"));
      print_util.define(out, return_val.getType(), new_method_name, arguments);

      out.println("\t%0 = getelementptr inbounds [3 x i8], [3 x i8]* @strfmt, i32 0, i32 0");
      out.println("%call = call i32 ( i8*, ... ) @printf(i8* %0, i8* %given)");
      out.println("ret void\n}");
    }

    // Method for generating the out_int method
    else if (f_name.equals("out_int")) {
      return_val = new Operand(void_type, "null");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(int_type, "given"));
      print_util.define(out, return_val.getType(), new_method_name, arguments);

      out.println("\t%0 = getelementptr inbounds [3 x i8], [3 x i8]* @intfmt, i32 0, i32 0");
      out.println("%call = call i32 ( i8*, ... ) @printf(i8* %0, i32 %given)");
      out.println("ret void\n}");
    }

    // Method for generating the in_string method
    else if (f_name.equals("in_string")) {
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(get_optype("IO", true, 1), "this"));
      print_util.define(out, string_type, new_method_name, arguments);

      out.println("\t%0 = bitcast [3 x i8]* @strfmt to i8*");

      return_val = new Operand(string_type, "retval");
      arguments = new ArrayList<Operand>();
      arguments.add((Operand)new IntValue(1024));
      print_util.callOp(out, new ArrayList<OpType>(), "malloc", true, arguments, return_val);

      return_val = new Operand(int_type, "1");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "0"));
      arguments.add(new Operand(string_type, "retval"));
      List<OpType> argTypes = new ArrayList<OpType>();
      argTypes.add(string_type);
      argTypes.add(new OpType(OpTypeId.VAR_ARG));
      print_util.callOp(out, argTypes, "scanf", true, arguments, return_val);
      print_util.retOp(out, arguments.get(1));
    }

    // Method for generating the in_int method
    else if (f_name.equals("in_int")) {
      arguments = new ArrayList<Operand>();
      print_util.define(out, int_type, new_method_name, arguments);

      out.println("\t%0 = bitcast [3 x i8]* @intfmt to i8*");

      return_val = new Operand(string_type, "1");
      arguments = new ArrayList<Operand>();
      arguments.add((Operand)new IntValue(4));
      print_util.callOp(out, new ArrayList<OpType>(), "malloc", true, arguments, return_val);

      out.println("\t%2 = bitcast i8* %1 to i32*");

      return_val = new Operand(int_type, "3");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "0"));
      arguments.add(new Operand(new OpType(OpTypeId.INT32_PTR), "2"));
      List<OpType> argTypes = new ArrayList<OpType>();
      argTypes.add(string_type);
      argTypes.add(new OpType(OpTypeId.VAR_ARG));
      print_util.callOp(out, argTypes, "scanf", true, arguments, return_val);

      return_val = new Operand(int_type, "retval");
      print_util.loadOp(out, int_type, arguments.get(1), return_val);
      print_util.retOp(out, return_val);
    }
  }

  // Function to find pre-defined strings and print them in LLVM-IR format
  // String name encoding done like this: @.str.<lineNo>
  // Assuming two strings cannot be in the same line
  public void string_capture(PrintWriter out, AST.expression expr) {
    if (expr instanceof AST.string_const) {
      String cap_string = ((AST.string_const)expr).value;
      string_table.put(cap_string, string_counter);
      string_counter++;
      out.print("@.str." + string_table.get(cap_string) + " = private unnamed_addr constant [" + String.valueOf(cap_string.length() + 1) + " x i8] c\"");
      print_util.escapedString(out, cap_string);
      out.println("\\00\"");
    } else if (expr instanceof AST.eq) {
      string_capture(out, ((AST.eq)expr).e1);
      string_capture(out, ((AST.eq)expr).e2);
    } else if (expr instanceof AST.assign) {
      string_capture(out, ((AST.assign)expr).e1);
    } else if (expr instanceof AST.block) {
      for (AST.expression e : ((AST.block)expr).l1) {
        string_capture(out, e);
      }
    } else if (expr instanceof AST.loop) {
      string_capture(out, ((AST.loop)expr).predicate);
      string_capture(out, ((AST.loop)expr).body);
    } else if (expr instanceof AST.cond) {
      string_capture(out, ((AST.cond)expr).predicate);
      string_capture(out, ((AST.cond)expr).ifbody);
      string_capture(out, ((AST.cond)expr).elsebody);
    }/* else if (expr instanceof AST.dispatch) {
      string_capture(out, ((AST.dispatch)expr).caller);
      for (AST.expression e : ((AST.dispatch)expr).actuals) {
        string_capture(out, e);
      }
    }*/
    else if (expr instanceof AST.static_dispatch) {
      string_capture(out, ((AST.static_dispatch)expr).caller);
      for (AST.expression e : ((AST.static_dispatch)expr).actuals) {
        string_capture(out, e);
      }
    }
    return ;
  }

  public void attempt_assign_retval(PrintWriter out, OpType op, int register) {
    if (register >= 0 && method_return_type.getName().equals(op.getName()) && (!(method_return_type.getName().equals("void"))) ) {
      print_util.storeOp(out, new Operand(method_return_type, String.valueOf(register)), new Operand(method_return_type.getPtrType(), "retval"));
    }
  }
  // TODO discuss return values
  public Tracker NodeVisit(PrintWriter out, AST.expression expr, Tracker counter) {

    // code for making the IR for bool constatnts
    if (expr instanceof AST.bool_const) {
      print_util.allocaOp(out, bool_type, new Operand(bool_type, String.valueOf(counter.register)));
      print_util.storeOp(out, (Operand)new BoolValue(((AST.bool_const)expr).value), new Operand(bool_type.getPtrType(), String.valueOf(counter.register)));
      print_util.loadOp(out, bool_type, new Operand(bool_type.getPtrType(), String.valueOf(counter.register)), new Operand(bool_type, String.valueOf(counter.register + 1)));
      return new Tracker(counter.register + 2, bool_type, counter.last_basic_block);
    }

    // code for making the IR for string constatnts
    else if (expr instanceof AST.string_const) {
      String cur_assign_string = ((AST.string_const)expr).value;
      print_util.allocaOp(out, new OpType(OpTypeId.INT8_PTR), new Operand(new OpType(OpTypeId.INT8_PTR), String.valueOf(counter.register)));
      String length_string = "[" + String.valueOf(cur_assign_string.length() + 1) + " x i8]";
      out.print("\tstore i8* getelementptr inbounds (" + length_string + ", " + length_string + "* @.str." + string_table.get(cur_assign_string));
      out.println(", i32 0, i32 0), i8** %" + String.valueOf(counter.register));
      print_util.loadOp(out, new OpType(OpTypeId.INT8_PTR), new Operand(new OpType(OpTypeId.INT8_PPTR), String.valueOf(counter.register)), new Operand(new OpType(OpTypeId.INT8_PTR), String.valueOf(counter.register + 1)));
      return new Tracker(counter.register + 2, new OpType(OpTypeId.INT8_PTR), counter.last_basic_block);
    }

    // code for making the IR for int constatnts
    else if (expr instanceof AST.int_const) {
      print_util.allocaOp(out, int_type, new Operand(int_type, String.valueOf(counter.register)));
      print_util.storeOp(out, (Operand)new IntValue(((AST.int_const)expr).value), new Operand(int_type.getPtrType(), String.valueOf(counter.register)));
      print_util.loadOp(out, int_type, new Operand(int_type.getPtrType(), String.valueOf(counter.register)), new Operand(int_type, String.valueOf(counter.register + 1)));
      return new Tracker(counter.register + 2, int_type, counter.last_basic_block);
    }

    // handle IR for expr ::== ID
    else if (expr instanceof AST.object) {
      AST.object obj = (AST.object)expr;
      OpType op = get_optype(obj.type, true, 1);
      Operand non_cons = new Operand(op, String.valueOf(counter.register));

      if (obj.name.equals("self")) {
        print_util.loadOp(out, op, new Operand(op, "this1"), non_cons);
      } else {
        print_util.loadOp(out, op, new Operand(op.getPtrType(), get_attribute_address(obj.name)), non_cons);
      }

      return new Tracker(counter.register + 1, op, counter.last_basic_block);
    }

    // handle IR for expr ::== expr arith_op expr
    else if (expr instanceof AST.mul || expr instanceof AST.divide || expr instanceof AST.plus || expr instanceof AST.sub ||
             expr instanceof AST.leq || expr instanceof AST.lt || expr instanceof AST.eq || expr instanceof AST.comp || expr instanceof AST.neg) {
      return arith_capture(out, expr, counter);
    }

    // handle IR for expr ::== {[expr;]*}
    else if (expr instanceof AST.block) {
      AST.block the_block = (AST.block)expr;
      for (AST.expression cur_expr : the_block.l1) {
        counter = NodeVisit(out, cur_expr, counter);
      }
      attempt_assign_retval(out, counter.last_instruction, counter.register - 1);
      return counter;
    }

    //handle IR for expr ::= condition
    else if (expr instanceof AST.cond) {
      return cond_capture(out, (AST.cond)expr, counter);
    }

    //handle IR for expr ::= loop
    else if (expr instanceof AST.loop) {
      return loop_capture(out, (AST.loop)expr, counter);
    }

    //handle IR for expr ::= ID <- expr
    else if (expr instanceof AST.assign) {
      AST.assign cur_expr = (AST.assign)expr;
      counter = NodeVisit(out, cur_expr.e1, counter);
      print_util.storeOp(out, new Operand(counter.last_instruction, String.valueOf(counter.register - 1)), new Operand(counter.last_instruction.getPtrType(), get_attribute_address(cur_expr.name)));
      return counter;
    }

    //handle IR for expr ::= new ID
    else if (expr instanceof AST.new_) {

      AST.new_ cur_expr = (AST.new_)expr;

      if (cur_expr.typeid.equals("Int")) {
        print_util.allocaOp(out, int_type, new Operand(int_type, String.valueOf(counter.register)));
        print_util.storeOp(out, (Operand)new IntValue(0), new Operand(int_type.getPtrType(), String.valueOf(counter.register)));
        print_util.loadOp(out, int_type, new Operand(int_type.getPtrType(), String.valueOf(counter.register)), new Operand(int_type, String.valueOf(counter.register + 1)));
        return new Tracker(counter.register + 2, int_type, counter.last_basic_block);
      }

      else if (cur_expr.typeid.equals("Bool")) {
        print_util.allocaOp(out, bool_type, new Operand(bool_type, String.valueOf(counter.register)));
        print_util.storeOp(out, (Operand)new BoolValue(false), new Operand(bool_type.getPtrType(), String.valueOf(counter.register)));
        print_util.loadOp(out, bool_type, new Operand(bool_type.getPtrType(), String.valueOf(counter.register)), new Operand(bool_type, String.valueOf(counter.register + 1)));
        return new Tracker(counter.register + 2, bool_type, counter.last_basic_block);
      }

      else if (cur_expr.typeid.equals("String")) {
        String length_string = "[" + 1 + " x i8]";
        print_util.allocaOp(out, string_type, new Operand(string_type, String.valueOf(counter.register)));
        out.println("store i8* getelementptr inbounds (" + length_string + ", " + length_string + "* @.str.empty , i32 0, i32 0), i8** %" + String.valueOf(counter.register));
        print_util.loadOp(out, string_type, new Operand(string_type.getPtrType(), String.valueOf(counter.register)), new Operand(string_type, String.valueOf(counter.register + 1)));
        return new Tracker(counter.register + 2, string_type, counter.last_basic_block);
      }

      print_util.allocaOp(out, get_optype(cur_expr.typeid, true, 0), new Operand(get_optype(cur_expr.typeid, true, 0), String.valueOf(counter.register)));
      List<Operand> op_list = new ArrayList<Operand>();
      OpType constructor_type = get_optype(cur_expr.typeid, true, 1);
      op_list.add(new Operand(constructor_type, String.valueOf(counter.register)));
      print_util.callOp(out, new ArrayList<OpType>(), cur_expr.typeid + "_Cons_" + cur_expr.typeid, true, op_list, new Operand(constructor_type, String.valueOf(counter.register + 1)));
      return new Tracker(counter.register + 2, constructor_type, counter.last_basic_block);
    }
    // This case covers static dispatch
    else if (expr instanceof AST.static_dispatch) {

      AST.static_dispatch cur_func = (AST.static_dispatch)expr;

      // recursing on the caller
      /*  checking the voidness of the callee */
      if (! (cur_func.typeid.equals("Int") || cur_func.typeid.equals("Bool") || cur_func.typeid.equals("String"))) {
        counter = NodeVisit(out, cur_func.caller, counter);
        String callee_type_name = counter.last_instruction.getName();

        out.println("\t%" + counter.register + " = icmp eq " + callee_type_name + " null , %" + (counter.register - 1) + "\t\t\t\t\t\t\t\t;checking the voidness of the callee");
        print_util.branchCondOp(out, new Operand(bool_type, String.valueOf(counter.register)), "dispatch_on_void_basic_block" , "proceed_" + counter.register);

        out.println("proceed_" + counter.register + ":");
        counter.register++;
        counter.last_basic_block = "proceed_" + counter.register + ":";
      }

      String function_being_called = cur_func.typeid + "_" + cur_func.name;
      List<Operand> arg_list = new ArrayList<Operand>();

      // recursing on the arguements
      for (AST.expression e : cur_func.actuals) {
        counter = NodeVisit(out, e, counter);
        arg_list.add(new Operand(counter.last_instruction, String.valueOf(counter.register - 1)));
      }

      // calling the caller expression
      counter = NodeVisit(out, cur_func.caller, counter);

      if (! (cur_func.typeid.equals("IO"))) {
        // IO class will not have %Class.IO* this pointer
        arg_list.add(0, new Operand(counter.last_instruction, String.valueOf(counter.register - 1)));
      }
      Operand returned = new Operand(get_func_type(cur_func.typeid, cur_func.name), String.valueOf(counter.register));

      print_util.callOp(out, new ArrayList<OpType>(), function_being_called, true, arg_list, returned);

      if (returned.getTypename().equals("void"))
        return new Tracker(counter.register, get_func_type(cur_func.typeid, cur_func.name), counter.last_basic_block);
      else {
        return new Tracker(counter.register + 1, get_func_type(cur_func.typeid, cur_func.name), counter.last_basic_block);
      }
    }
    return counter;
  }

  public OpType get_func_type(String func_class, String method) {
    for (AST.method m : classList.get(func_class).methods) {
      if (m.name.equals(method)) {
        if (m.typeid.equals("Object"))
          return void_type;
        else
          return get_optype(m.typeid, true, 1);
      }
    }
    return void_type;
  }

  public String get_class_name(String func_name) {
    String name = "";
    for (AST.method m : classList.get(classList.get(CLASS_NAME).parent).methods) {
      if (func_name.equals(m.name)) {
        name = classList.get(CLASS_NAME).parent;
        break;
      }
    }

    for (AST.method m : classList.get(CLASS_NAME).methods) {
      if (func_name.equals(m.name)) {
        name = CLASS_NAME;
        break;
      }
    }
    return name;
  }

  public Tracker cond_capture(PrintWriter out, AST.cond expr, Tracker counter) {
    // adding current basic block to stack for taking car of nested if
    //
    int curr_if_bb_counter = if_basic_block_counter;
    if_basic_block_counter++;

    Tracker predicate_block, then_block , else_block;
    predicate_block = NodeVisit(out, expr.predicate, new Tracker(counter.register, counter.last_instruction, counter.last_basic_block));
    // print_util.storeOp(out, new Operand(method_return_type, String.valueOf(x - 1)), new Operand(method_return_type.getPtrType(), "retval"));
    print_util.branchCondOp(out, new Operand(new OpType(OpTypeId.INT1), String.valueOf(predicate_block.register - 1)), "if.then" + String.valueOf(curr_if_bb_counter), "if.else" + String.valueOf(curr_if_bb_counter));

    // recur on th
    out.println("\nif.then" + String.valueOf(curr_if_bb_counter) + ":");
    then_block = NodeVisit(out, expr.ifbody, new Tracker(predicate_block.register, predicate_block.last_instruction, "%if.then" + String.valueOf(curr_if_bb_counter)));

    // print_util.storeOp(out, new Operand(method_return_type, String.valueOf(x - 1)), new Operand(method_return_type.getPtrType(), "retval"));

    print_util.branchUncondOp(out , "if.end" + String.valueOf(curr_if_bb_counter));

    // recur on else
    out.println("\nif.else" + String.valueOf(curr_if_bb_counter) + ":");
    else_block = NodeVisit(out, expr.elsebody, new Tracker(then_block.register, then_block.last_instruction, "%if.else" + String.valueOf(curr_if_bb_counter)));

    // print_util.storeOp(out, new Operand(method_return_type, String.valueOf(x - 1)), new Operand(method_return_type.getPtrType(), "retval"));

    print_util.branchUncondOp(out , "if.end" + String.valueOf(curr_if_bb_counter));

    //add exit basicblock
    out.println("\nif.end" + String.valueOf(curr_if_bb_counter) + ":");
    OpType cond_type = else_block.last_instruction;

    if (cond_type.getName().equals("void")) {
      return new Tracker(else_block.register, cond_type, "%if.end" + String.valueOf(curr_if_bb_counter));
    }
    String phi_node_1, phi_node_2;
    phi_node_1 = " [ %" + (then_block.register - 1) + " , " + then_block.last_basic_block + " ]";
    phi_node_2 = " [ %" + (else_block.register - 1) + " , " + else_block.last_basic_block + " ]";

    out.println("  %" + else_block.register + " = phi " + cond_type.getName() + phi_node_1 + " , " + phi_node_2);
    attempt_assign_retval(out, else_block.last_instruction, else_block.register);
    return new Tracker(else_block.register + 1, cond_type, "%if.end" + String.valueOf(curr_if_bb_counter));
  }

// print new loop basic blocks and add conditional/unconditional branches
// as and when necessary
  public Tracker loop_capture(PrintWriter out, AST.loop expr, Tracker counter) {
    // adding current basic block to stack for taking car of nested if
    int curr_loop_counter = loop_basic_block_counter;
    loop_basic_block_counter++;

    // attempt_assign_retval(out, counter.last_instruction, counter.register - 1);

    print_util.branchUncondOp(out , "for.cond" + String.valueOf(curr_loop_counter));
    out.println("\nfor.cond" + String.valueOf(curr_loop_counter) + ":");

    Tracker x = NodeVisit(out, expr.predicate, new Tracker(counter.register, counter.last_instruction, "%for.cond" + String.valueOf(curr_loop_counter)));

    print_util.branchCondOp(out, new Operand(new OpType(OpTypeId.INT1), String.valueOf(x.register - 1)), "for.body" + String.valueOf(curr_loop_counter), "for.end" + String.valueOf(curr_loop_counter));

    // recur on loop body
    out.println("\nfor.body" + String.valueOf(curr_loop_counter) + ":");
    x = NodeVisit(out, expr.body, new Tracker(x.register, bool_type, "%for.body" + String.valueOf(curr_loop_counter)));
    OpType loop_type = x.last_instruction;

    print_util.branchUncondOp(out , "for.cond" + String.valueOf(curr_loop_counter));

    //add exit basicblock
    out.println("\nfor.end" + String.valueOf(curr_loop_counter) + ":");

    return new Tracker(x.register , loop_type, "%for.end" + String.valueOf(curr_loop_counter));
  }

  public Tracker arith_capture(PrintWriter out, AST.expression expr, Tracker counter) {
    // First op is MUL
    if (expr instanceof AST.mul) {
      // Get the expressions separately
      Tracker ops1 = NodeVisit(out, ((AST.mul)expr).e1, counter);
      Tracker ops2 = NodeVisit(out, ((AST.mul)expr).e2, ops1);
      print_util.arithOp(out, "mul", new Operand(int_type, String.valueOf(ops1.register - 1)), new Operand(int_type, String.valueOf(ops2.register - 1)), new Operand(int_type, String.valueOf(ops2.register)));
      return new Tracker(ops2.register + 1, int_type, ops2.last_basic_block);

    } else if (expr instanceof AST.divide) {
      // Get the expressions separately
      Tracker ops1 = NodeVisit(out, ((AST.divide)expr).e1, counter);
      Tracker ops2 = NodeVisit(out, ((AST.divide)expr).e2, ops1);

      // Performing div by 0 check
      String ops2_regis = String.valueOf(ops2.register - 1);
      print_util.compareOp(out, "EQ", new Operand(int_type, ops2_regis), (Operand)new IntValue(0), new Operand(bool_type, "comp_" + String.valueOf(ops2.register - 1) + "_0"));
      print_util.branchCondOp(out, new Operand(bool_type, "comp_" + ops2_regis + "_0"), "func_div_by_zero_abort" , "proceed_" + ops2_regis + "_0");
      out.println("proceed_" + ops2_regis + "_0:");
      print_util.arithOp(out, "udiv", new Operand(int_type, String.valueOf(ops1.register - 1)), new Operand(int_type, String.valueOf(ops2.register - 1)), new Operand(int_type, String.valueOf(ops2.register)));

      return new Tracker(ops2.register + 1, int_type, "proceed_" + ops2_regis + "_0:");

    } else if (expr instanceof AST.plus) {
      // Get the expressions separately
      Tracker ops1 = NodeVisit(out, ((AST.plus)expr).e1, counter);
      Tracker ops2 = NodeVisit(out, ((AST.plus)expr).e2, ops1);
      print_util.arithOp(out, "add", new Operand(int_type, String.valueOf(ops1.register - 1)), new Operand(int_type, String.valueOf(ops2.register - 1)), new Operand(int_type, String.valueOf(ops2.register)));
      return new Tracker(ops2.register + 1, int_type, ops2.last_basic_block);

    } else if (expr instanceof AST.sub) {
      // Get the expressions separately
      Tracker ops1 = NodeVisit(out, ((AST.sub)expr).e1, counter);
      Tracker ops2 = NodeVisit(out, ((AST.sub)expr).e2, ops1);
      print_util.arithOp(out, "sub", new Operand(int_type, String.valueOf(ops1.register - 1)), new Operand(int_type, String.valueOf(ops2.register - 1)), new Operand(int_type, String.valueOf(ops2.register)));
      return new Tracker(ops2.register + 1, int_type, ops2.last_basic_block);

    } else if (expr instanceof AST.leq) {
      // Get the expressions separately
      Tracker ops1 = NodeVisit(out, ((AST.leq)expr).e1, counter);
      Tracker ops2 = NodeVisit(out, ((AST.leq)expr).e2, ops1);
      print_util.compareOp(out, "LE", new Operand(int_type, String.valueOf(ops1.register - 1)), new Operand(int_type, String.valueOf(ops2.register - 1)), new Operand(bool_type, String.valueOf(ops2.register)));
      return new Tracker(ops2.register + 1, bool_type, ops2.last_basic_block);

    } else if (expr instanceof AST.lt) {
      // Get the expressions separately
      Tracker ops1 = NodeVisit(out, ((AST.lt)expr).e1, counter);
      Tracker ops2 = NodeVisit(out, ((AST.lt)expr).e2, ops1);
      print_util.compareOp(out, "LT", new Operand(int_type, String.valueOf(ops1.register - 1)), new Operand(int_type, String.valueOf(ops2.register - 1)), new Operand(bool_type, String.valueOf(ops2.register)));
      return new Tracker(ops2.register + 1, bool_type, ops2.last_basic_block);

    } else if (expr instanceof AST.eq) {
      // Get the expressions separately
      Tracker ops1 = NodeVisit(out, ((AST.eq)expr).e1, counter);
      Tracker ops2 = NodeVisit(out, ((AST.eq)expr).e2, ops1);

      if (ops1.last_instruction.getName().equals(int_type.getName()))
        print_util.compareOp(out, "EQ", new Operand(int_type, String.valueOf(ops1.register - 1)), new Operand(int_type, String.valueOf(ops2.register - 1)), new Operand(bool_type, String.valueOf(ops2.register)));

      else if (ops1.last_instruction.getName().equals(bool_type.getName())) {
        print_util.compareOp(out, "EQ", new Operand(bool_type, String.valueOf(ops1.register - 1)), new Operand(bool_type, String.valueOf(ops2.register - 1)), new Operand(bool_type, String.valueOf(ops2.register)));
      }

      else if (ops1.last_instruction.getName().equals(string_type.getName())) {
        Operand return_val = new Operand(bool_type, String.valueOf(ops2.register));
        List<Operand> arguments = new ArrayList<Operand>();
        arguments.add(new Operand(string_type, String.valueOf(ops1.register - 1)));
        arguments.add(new Operand(string_type, String.valueOf(ops2.register - 1)));
        print_util.callOp(out, new ArrayList<OpType>(), "String_strcmp", true, arguments, return_val);
      }

      return new Tracker(ops2.register + 1, bool_type, ops2.last_basic_block);
    } else if (expr instanceof AST.comp) {
      Tracker ops1 = NodeVisit(out, ((AST.comp)expr).e1, counter);
      print_util.arithOp(out, "xor", new Operand(bool_type, String.valueOf(ops1.register - 1)), (Operand)new BoolValue(true), new Operand(bool_type, String.valueOf(ops1.register)));
      return new Tracker(ops1.register + 1, bool_type, ops1.last_basic_block);
    }

    else if (expr instanceof AST.neg) {
      Tracker ops1 = NodeVisit(out, ((AST.neg)expr).e1, counter);
      print_util.arithOp(out, "mul", new Operand(int_type, String.valueOf(ops1.register - 1)), (Operand)new IntValue(-1), new Operand(int_type, String.valueOf(ops1.register)));
      return new Tracker(ops1.register + 1, int_type, ops1.last_basic_block);
    }

    return counter;
  }

}