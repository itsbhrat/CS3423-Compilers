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

class Tracker {
  public int register;
  public int if_counter;
  OpType last_instruction;
  Tracker() {
    register = 0;
    if_counter = 0;
  }

  Tracker(int reg , int cnt) {
    register = reg;
    if_counter = cnt;
  }
}

public class Codegen {

  String filename;
  HashMap<String, ClassNode> classList = new HashMap<String, ClassNode>();
  Printer print_util = new Printer();

  OpType string_type = get_optype("String", true, 0);
  OpType int_type = get_optype("Int", true, 0);
  OpType bool_type = get_optype("Bool", true, 0);

  Integer loop_basic_block_counter = 0;
  String CLASS_NAME = null;
  OpType method_return_type;

  public Codegen(AST.program program, PrintWriter out) {
    // Write Code generator code here
    out.print("; I am a comment in LLVM-IR. Feel free to remove me.\n");

    // Assuming that the code runs on linux machines with 64 bit
    // Also taking into fact that all classes are in one file
    out.println("source_filename = \"" + program.classes.get(0).filename + "\"");
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
    out.println("@strfmt = private unnamed_addr constant [3 x i8] c\"%d\\00\"");
    out.println("@intfmt = private unnamed_addr constant [3 x i8] c\"%s\\00\"");
    out.println("@.str.empty = private unnamed_addr constant [1 x i8] c\"\\00\"");

    for (AST.class_ cl : program.classes) {
      filename = cl.filename;
      insert_class(cl);

      if (cl.name.equals("Main")) {

        // Define the main function here
        print_util.define(out, int_type, "main", new ArrayList<Operand>());
        print_util.allocaOp(out, get_optype("Main", true, 0), new Operand(get_optype("Main", true, 1), "obj"));
        List<Operand> op_list = new ArrayList<Operand>();
        op_list.add(new Operand(get_optype("Main", true, 1), "obj"));
        print_util.callOp(out, new ArrayList<OpType>(), "Main_Cons_Main", true, op_list, new Operand(new OpType(OpTypeId.VOID), "null"));
        print_util.retOp(out, (Operand)new IntValue(0));
      }

      // If the class is one of the predefined classes in COOL, we only require the functions
      // and not the class itself
      if (cl.name.equals("Int") || cl.name.equals("String") || cl.name.equals("Bool") || cl.name.equals("Object")) {
        if (cl.name.equals("String")) {
          pre_def_string(out, "length");
          pre_def_string(out, "cat");
          pre_def_string(out, "substr");
          pre_def_string(out, "copy");
          pre_def_string(out, "strcmp");
        } else if (cl.name.equals("Object")) {
          pre_def_object(out, "copy");            // To be implemented
          pre_def_object(out, "type_name");       // To be implemented
          pre_def_object(out, "abort");           // To be implemented
        }
        continue;
      }

      // Similarly for IO, just as above
      else if (cl.name.equals("IO")) {
        pre_def_io(out, "in_int");
        pre_def_io(out, "in_string");
        pre_def_io(out, "out_int");
        pre_def_io(out, "out_string");
      }

      // Taking the attributes of the class first and generating code for it
      List<OpType> attribute_types = new ArrayList<OpType>();
      for (AST.attr attribute : classList.get(cl.name).attributes) {
        attribute_types.add(get_optype(attribute.typeid, true, 0));
        if (attribute.typeid.equals("String") && attribute.value instanceof AST.string_const) { // Getting existing string constants
          string_capture(out, attribute.value);
        }
      }
      print_util.typeDefine(out, cl.name, attribute_types); // Emits the code

      /* Now we need to build a constructor for the class to initialize the values of the
      var_name : type_name <- assignment of type_name
      */
      build_constructor(out, cl.name);

      if (cl.name.equals("IO")) {
        continue;
      }

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
        for (AST.formal f : mtd.formals) {
          Operand cur_arg = new Operand(get_optype(f.typeid, true, 0), f.name);
          arguments.add(cur_arg);
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
        counter.register = 0;
        counter.if_counter = 0;

        CLASS_NAME = cl.name;
        // Required to do here: Build expressions
        counter = NodeVisit(out, mtd.body, counter);

        if (mtd.typeid.equals("Object")) {
          print_util.retOp(out, new Operand(new OpType(OpTypeId.VOID), "null"));
        } else {
          print_util.loadOp(out, method_return_type, new Operand(method_return_type.getPtrType(), "retval"), new Operand(method_return_type, String.valueOf(counter.register)));
          print_util.retOp(out, new Operand(method_return_type, String.valueOf(counter.register)));
          counter.register++;
        }
        // Placeholder completion added
      }
    }
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
    classList.put(cur_class.name, new ClassNode(cur_class.name, cur_class_attributes, cur_class_methods));
  }

  public OpType get_optype(String typeid, boolean isClass, int depth) {
    if (typeid.equals("String") && isClass == true) {
      return new OpType(OpTypeId.INT8_PTR);
    } else if (typeid.equals("Int") && isClass == true) {
      return new OpType(OpTypeId.INT32);
    } else if (typeid.equals("Bool") && isClass == true) {
      return new OpType(OpTypeId.INT1);
    } else if (isClass == true) {
      return new OpType("class." + typeid, depth);
    } else {
      return new OpType(typeid, depth);
    }
  }
  /*
    public void get_default_value(Optype type, String name, boolean isAllocaNeeded) {
      if (typeid.equals("String") && isClass == true) {
        if(isAllocaNeeded)
          our.println("%" + name + "")
        out.println("store i8* getelementptr inbounds ([6 x i8], [6 x i8]* @.str.4, i32 0, i32 0), i8** %j")
        return new OpType(OpTypeId.INT8_PTR);
      }
      else if (typeid.equals("Int")) {
          if(isAllocaNeeded)
            out.println("%" + name + " = alloca i32 ");
          out.println("store i32 0, i32* %i" + name);
      }
      else if (typeid.equals("Bool")) {
          if(isAllocaNeeded)
            out.println("%" + name + " = alloca i1 ");
          out.println("store i32 0, i32* %i" + name);
      }
      else if (isClass == true) {
        return new OpType("class." + typeid, depth);
      } else {
        return new OpType(typeid, depth);
      }
    }*/
  // Function to generate the constructor of a given class
  public void build_constructor(PrintWriter out, String class_name) {

    // Name of constructor (mangled)
    String method_name = class_name + "_Cons_" + class_name;

    // List of Operand for attributes
    List<Operand> cons_arg_list = new ArrayList<Operand>();
    cons_arg_list.add(new Operand(get_optype(class_name, true, 1), "this"));

    // Define the constructor and establish pointer information
    print_util.define(out, new OpType(OpTypeId.VOID), method_name, cons_arg_list);
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
        if (cur_attr.value instanceof AST.no_expr) {
          print_util.storeOp(out, (Operand)new IntValue(0), new Operand(ptr, cur_attr.name));
        } else {
          print_util.storeOp(out, (Operand)new IntValue(((AST.int_const)cur_attr.value).value), new Operand(ptr, cur_attr.name));
        }
      }

      // String attribute codegen
      else if (cur_attr.typeid.equals("String")) {
        operand_list.add((Operand)new IntValue(0));
        operand_list.add((Operand)new IntValue(i));
        print_util.getElementPtr(out, get_optype(class_name, true, 0), operand_list, result, true);     // That func is here
        String length_string = null;
        if (cur_attr.value instanceof AST.no_expr) {
          length_string = "[" + 1 + " x i8]";
          out.print("\tstore i8* getelementptr inbounds (" + length_string + ", " + length_string + "* @.str.empty");
        } else {
          length_string = "[" + String.valueOf(((AST.string_const)cur_attr.value).value.length() + 1) + " x i8]";
          out.print("\tstore i8* getelementptr inbounds (" + length_string + ", " + length_string + "* @.str." + cur_attr.lineNo);
        }
        out.println(", i32 0, i32 0), i8** %" + cur_attr.name);
      }

      // Bool attribute codegen
      else if (cur_attr.typeid.equals("Bool")) {
        operand_list.add((Operand)new IntValue(0));
        operand_list.add((Operand)new IntValue(i));
        print_util.getElementPtr(out, get_optype(class_name, true, 0), operand_list, result, true);    // That func is here
        OpType ptr = new OpType(OpTypeId.INT1_PTR);
        if (cur_attr.value instanceof AST.no_expr) {
          print_util.storeOp(out, (Operand)new BoolValue(false), new Operand(ptr, cur_attr.name));
        } else {
          print_util.storeOp(out, (Operand)new BoolValue(((AST.bool_const)cur_attr.value).value), new Operand(ptr, cur_attr.name));
        }
      }
    }
    print_util.retOp(out, new Operand(new OpType(OpTypeId.VOID), "retval"));
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
      List<Operand> operand_list = new ArrayList<Operand>();
      Operand result = new Operand(int_type, cur_class.attributes.get(i).name);
      operand_list.add(new Operand(get_optype(class_name, true, 1), "this1"));
      operand_list.add((Operand)new IntValue(0));
      operand_list.add((Operand)new IntValue(i));
      print_util.getElementPtr(out, get_optype(class_name, true, 0), operand_list, result, true);     // That func is here
    }
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
    else if (f_name.equals("cat")) {
      return_val = new Operand(string_type, "retval");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "this"));
      arguments.add(new Operand(string_type, "that"));
      print_util.define(out, return_val.getType(), new_method_name, arguments);
      print_util.callOp(out, new ArrayList<OpType>(), "strcat", true, arguments, return_val);
      print_util.retOp(out, return_val);
    }

    // Emitting code for substr
    // This needs to be checked
    /*
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
      arguments.add((Operand)new IntValue(0));
      arguments.add(new Operand(int_type, "start"));
      print_util.getElementPtr(out, string_type, arguments, return_val, true);

      return_val = new Operand(string_type, "retval");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "0"));
      arguments.add(new Operand(string_type, "1"));
      arguments.add(new Operand(int_type, "len"));
      print_util.callOp(out, new ArrayList<OpType>(), "strncpy", true, arguments, return_val);
      print_util.retOp(out, return_val);
    }*/

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

    // Emitting code for copy
    else if (f_name.equals("copy")) {
      return_val = new Operand(string_type, "retval");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "this"));
      print_util.define(out, return_val.getType(), new_method_name, arguments);

      return_val = new Operand(string_type, "0");
      arguments = new ArrayList<Operand>();
      arguments.add((Operand)new IntValue(1024));
      print_util.callOp(out, new ArrayList<OpType>(), "malloc", true, arguments, return_val);

      return_val = new Operand(string_type, "retval");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "0"));
      arguments.add(new Operand(string_type, "this"));
      print_util.callOp(out, new ArrayList<OpType>(), "strcpy", true, arguments, return_val);
      print_util.retOp(out, return_val);
    }
  }

  public void pre_def_object(PrintWriter out, String f_name) {
    // Do Something
  }

  public void pre_def_io(PrintWriter out, String f_name) {
    String new_method_name = "IO_" + f_name;
    Operand return_val = null;
    List<Operand> arguments = null;

    // Method for generating the out_string method
    if (f_name.equals("out_string")) {
      return_val = new Operand(get_optype("IO", true, 1), "this");
      arguments = new ArrayList<Operand>();
      arguments.add(return_val);
      arguments.add(new Operand(string_type, "given"));
      print_util.define(out, return_val.getType(), new_method_name, arguments);

      out.println("\t%0 = bitcast [3 x i8]* @strfmt to i8*");

      return_val = new Operand(int_type, "1");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "0"));
      arguments.add(new Operand(string_type, "given"));
      List<OpType> argTypes = new ArrayList<OpType>();
      argTypes.add(string_type);
      argTypes.add(new OpType(OpTypeId.VAR_ARG));
      print_util.callOp(out, argTypes, "printf", true, arguments, return_val);

      return_val = new Operand(get_optype("IO", true, 1), "this");
      print_util.retOp(out, return_val);
    }

    // Method for generating the out_int method
    else if (f_name.equals("out_int")) {
      return_val = new Operand(get_optype("IO", true, 1), "this");
      arguments = new ArrayList<Operand>();
      arguments.add(return_val);
      arguments.add(new Operand(int_type, "given"));
      print_util.define(out, return_val.getType(), new_method_name, arguments);

      out.println("\t%0 = bitcast [3 x i8]* @intfmt to i8*");

      return_val = new Operand(int_type, "1");
      arguments = new ArrayList<Operand>();
      arguments.add(new Operand(string_type, "0"));
      arguments.add(new Operand(int_type, "given"));
      List<OpType> argTypes = new ArrayList<OpType>();
      argTypes.add(string_type);
      argTypes.add(new OpType(OpTypeId.VAR_ARG));
      print_util.callOp(out, argTypes, "printf", true, arguments, return_val);

      return_val = new Operand(get_optype("IO", true, 1), "this");
      print_util.retOp(out, return_val);
    }

    // Method for generating the in_string method
    else if (f_name.equals("in_string")) {
      return_val = new Operand(string_type, "given");
      arguments = new ArrayList<Operand>();
      arguments.add(return_val);
      print_util.define(out, return_val.getType(), new_method_name, arguments);

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
      return_val = new Operand(int_type, "given");
      arguments = new ArrayList<Operand>();
      arguments.add(return_val);
      print_util.define(out, return_val.getType(), new_method_name, arguments);

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
      out.print("@.str." + expr.lineNo + " = private unnamed_addr constant [" + String.valueOf(cap_string.length() + 1) + " x i8] c\"");
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
    }
    return ;
  }

  public Tracker NodeVisit(PrintWriter out, AST.expression expr, Tracker counter) {
    if (expr instanceof AST.assign) {
      AST.assign cur = (AST.assign)expr;
      if (cur.e1 instanceof AST.mul || cur.e1 instanceof AST.divide || cur.e1 instanceof AST.plus || cur.e1 instanceof AST.sub ||
          cur.e1 instanceof AST.leq || cur.e1 instanceof AST.lt ) {
        return arith_capture(out, ((AST.assign)expr).e1, counter);
      }
    }

    if (expr instanceof AST.cond) {
      return cond_capture(out, (AST.cond)expr, counter);
    }

    if (expr instanceof AST.loop) {
      return loop_capture(out, (AST.loop)expr, counter);
    }

    if (expr instanceof AST.block) {
      AST.block the_block = (AST.block)expr;
      for (AST.expression cur_expr : the_block.l1) {
        counter = NodeVisit(out, cur_expr, counter);
        if (cur_expr instanceof AST.assign) {
          if (cur_expr.type.equals("Int")) {
            boolean flag = check_attribute(((AST.assign)cur_expr).name);

            // If attribute
            if (flag == true) {
              // If it is just like this: i <- 1;
              if (((AST.assign)cur_expr).e1 instanceof AST.int_const) {
                int e1_val = ((AST.int_const)((AST.assign)cur_expr).e1).value;
                print_util.storeOp(out, (Operand)new IntValue(e1_val), new Operand(new OpType(OpTypeId.INT32_PTR), ((AST.assign)cur_expr).name));
              }

              // If it like this: i <- weird operations
              else {
                print_util.storeOp(out, new Operand(int_type, String.valueOf(counter.register - 1)), new Operand(new OpType(OpTypeId.INT32_PTR), ((AST.assign)cur_expr).name));
              }
            }

            // If not attribute
            else {

              // Same case as above
              if (((AST.assign)cur_expr).e1 instanceof AST.int_const) {
                int e1_val = ((AST.int_const)((AST.assign)cur_expr).e1).value;
                print_util.storeOp(out, (Operand)new IntValue(e1_val), new Operand(new OpType(OpTypeId.INT32_PTR), ((AST.assign)cur_expr).name));
              }

              // Samce case as above
              else {
                print_util.storeOp(out, new Operand(int_type, String.valueOf(counter.register - 1)), new Operand(new OpType(OpTypeId.INT32_PTR), ((AST.assign)cur_expr).name + ".addr"));
              }
            }
          } else if (cur_expr.type.equals("String")) {
            boolean flag = check_attribute(((AST.assign)cur_expr).name);
            if (flag == true) {
              // If it is just like this: j <- "Hello";
              if (((AST.assign)cur_expr).e1 instanceof AST.string_const) {
                AST.assign cur_assign = (AST.assign)cur_expr;
                String length_string = "[" + String.valueOf(((AST.string_const)cur_assign.e1).value.length() + 1) + " x i8]";
                out.print("\tstore i8* getelementptr inbounds (" + length_string + ", " + length_string + "* @.str." + cur_assign.lineNo);
                out.println(", i32 0, i32 0), i8** %" + cur_assign.name);
                print_util.loadOp(out, new OpType(OpTypeId.INT8_PTR), new Operand(new OpType(OpTypeId.INT8_PPTR), cur_assign.name), new Operand(new OpType(OpTypeId.INT8_PTR), String.valueOf(counter.register)));
                counter.register++;
              }

            } else {
              // If it is just like this: j <- "Hello";
              if (((AST.assign)cur_expr).e1 instanceof AST.string_const) {
                AST.assign cur_assign = (AST.assign)cur_expr;
                String length_string = "[" + String.valueOf(((AST.string_const)cur_assign.e1).value.length() + 1) + " x i8]";
                out.print("\tstore i8* getelementptr inbounds (" + length_string + ", " + length_string + "* @.str." + cur_assign.lineNo);
                out.println(", i32 0, i32 0), i8** %" + cur_assign.name + ".addr");
                print_util.loadOp(out, new OpType(OpTypeId.INT8_PTR), new Operand(new OpType(OpTypeId.INT8_PPTR), cur_assign.name + ".addr"), new Operand(new OpType(OpTypeId.INT8_PTR), String.valueOf(counter.register)));
                counter.register++;
              }

            }
          }
        }
      }
      if (counter.register > 0)
        print_util.storeOp(out, new Operand(method_return_type, String.valueOf(counter.register - 1)), new Operand(method_return_type.getPtrType(), "retval"));
      return counter;
    }

    if (expr instanceof AST.mul || expr instanceof AST.divide || expr instanceof AST.plus || expr instanceof AST.sub ||
        expr instanceof AST.leq || expr instanceof AST.lt ) {
      return arith_capture(out, expr, counter);
    }

    if (expr instanceof AST.eq ) {
      return equality_capture(out, ((AST.eq)expr).e1, ((AST.eq)expr).e2, counter);
    }

    if (expr instanceof AST.object) {
      AST.object obj = (AST.object)expr;
      if (method_return_type.getName().equals(get_optype(obj.type, true, 0).getName())) {
        OpType op = get_optype(obj.type, true, 0);
        Operand non_cons = new Operand(op, String.valueOf(counter.register));
        boolean flag = check_attribute(obj.name);
        if (flag == true) {
          print_util.loadOp(out, op, new Operand(op.getPtrType(), obj.name), non_cons);
        } else {
          print_util.loadOp(out, op, new Operand(op.getPtrType(), obj.name + ".addr"), non_cons);
        }
        print_util.storeOp(out, new Operand(method_return_type, String.valueOf(counter.register)), new Operand(method_return_type.getPtrType(), "retval"));
        return new Tracker(counter.register + 1, counter.if_counter);
      }
      return counter;
    }
    return counter;
  }

  public Tracker cond_capture(PrintWriter out, AST.cond expr, Tracker counter) {
    // adding current basic block to stack for taking car of nested if
    //
    int curr_if_bb_counter = counter.if_counter;

    Tracker predicate_block, then_block , else_block;
    predicate_block = NodeVisit(out, expr.predicate, new Tracker(counter.register, counter.if_counter + 1));
    // print_util.storeOp(out, new Operand(method_return_type, String.valueOf(x - 1)), new Operand(method_return_type.getPtrType(), "retval"));
    print_util.branchCondOp(out, new Operand(new OpType(OpTypeId.INT1), String.valueOf(predicate_block.register - 1)), "if.then" + String.valueOf(curr_if_bb_counter), "if.else" + String.valueOf(curr_if_bb_counter));

    // recur on th
    out.println("\nif.then" + String.valueOf(curr_if_bb_counter) + ":");
    then_block = NodeVisit(out, expr.ifbody, predicate_block);
    // print_util.storeOp(out, new Operand(method_return_type, String.valueOf(x - 1)), new Operand(method_return_type.getPtrType(), "retval"));

    print_util.branchUncondOp(out , "if.end" + String.valueOf(curr_if_bb_counter));

    // recur on else
    out.println("\nif.else" + String.valueOf(curr_if_bb_counter) + ":");
    else_block = NodeVisit(out, expr.elsebody, then_block);

//    print_util.storeOp(out, new Operand(method_return_type, String.valueOf(x - 1)), new Operand(method_return_type.getPtrType(), "retval"));

    print_util.branchUncondOp(out , "if.end" + String.valueOf(curr_if_bb_counter));

    //add exit basicblock
    out.println("\nif.end" + String.valueOf(curr_if_bb_counter) + ":");
    OpType cond_type = get_optype(expr.type, true, 0);

    String phi_node_1, phi_node_2;
    if (then_block.if_counter == predicate_block.if_counter) {
      phi_node_1 = " [ %" + (then_block.register - 1) + " , %if.then" + (predicate_block.if_counter - 1) + " ]";
    } else {
      phi_node_1 = " [ %" + (then_block.register - 1) + " , %if.end" + (then_block.if_counter - 1) + " ]";
    }

    if (else_block.if_counter == then_block.if_counter) {
      phi_node_2 = " [ %" + (else_block.register - 1) + " , %if.else" + (then_block.if_counter - 1) + " ]";
      // out.println("  %" + else_block.register + " = phi " + cond_type.getName() + ] [ %" + (else_block-1) + " , %if.else" + curr_if_bb_counter + "]");
    } else {
      phi_node_2 = " [ %" + (else_block.register - 1) + " , %if.end" + (else_block.if_counter - 1) + " ]";
    }
    out.println("  %" + else_block.register + " = phi " + cond_type.getName() + phi_node_1 + " , " + phi_node_2);
    return new Tracker(else_block.register + 1, else_block.if_counter);
  }

  // print new loop basic blocks and add conditional/unconditional branches
  // as and when necessary
  public Tracker loop_capture(PrintWriter out, AST.loop expr, Tracker counter) 2{
    // adding current basic block to stack for taking car of nested if
    int curr_loop_counter = loop_basic_block_counter;
    loop_basic_block_counter++;

    print_util.branchUncondOp(out , "for.cond" + String.valueOf(curr_loop_counter));
    out.println("\nfor.cond" + String.valueOf(curr_loop_counter) + ":");

    Tracker x = NodeVisit(out, expr.predicate, counter);

    print_util.branchCondOp(out, new Operand(new OpType(OpTypeId.INT1), String.valueOf(x.register - 1)), "for.body" + String.valueOf(curr_loop_counter), "for.end" + String.valueOf(curr_loop_counter));

    // recur on loop body
    out.println("\nfor.body" + String.valueOf(curr_loop_counter) + ":");
    x = NodeVisit(out, expr.body, x);
    print_util.branchUncondOp(out , "for.cond" + String.valueOf(curr_loop_counter));

    //add exit basicblock
    out.println("\nfor.end" + String.valueOf(curr_loop_counter) + ":");
    out.println("  %" + x.register + " = phi " + get_optype(expr.type, true, 0).getName() + " [ %" + (x.register - 1) + " , %for.cond" + (curr_loop_counter) + " ]");

    if (method_return_type.getName().equals(get_optype(expr.type, true, 0)))
      print_util.storeOp(out, new Operand(method_return_type, String.valueOf(x.register)), new Operand(method_return_type.getPtrType(), "retval"));

    return new Tracker(x.register + 1, x.if_counter);
  }

  public Tracker arith_capture(PrintWriter out, AST.expression expr, Tracker counter) {
    // Operations are four kinds: mul, divide, plus, sub
    // The idea is for every operation faced, we increment the "ops" and return it

    // First op is MUL
    if (expr instanceof AST.mul) {
      // Get the expressions separately
      AST.expression e1 = ((AST.mul)expr).e1;
      AST.expression e2 = ((AST.mul)expr).e2;
      return arith_impl_capture(out, e1, e2, "mul", true, counter);

    } else if (expr instanceof AST.divide) {
      // Get the expressions separately
      AST.expression e1 = ((AST.divide)expr).e1;
      AST.expression e2 = ((AST.divide)expr).e2;
      return arith_impl_capture(out, e1, e2, "udiv", true, counter);

    } else if (expr instanceof AST.plus) {
      // Get the expressions separately
      AST.expression e1 = ((AST.plus)expr).e1;
      AST.expression e2 = ((AST.plus)expr).e2;
      return arith_impl_capture(out, e1, e2, "add", true, counter);

    } else if (expr instanceof AST.sub) {
      // Get the expressions separately
      AST.expression e1 = ((AST.sub)expr).e1;
      AST.expression e2 = ((AST.sub)expr).e2;
      return arith_impl_capture(out, e1, e2, "sub", true, counter);
    } else if (expr instanceof AST.leq) {
      // Get the expressions separately
      AST.expression e1 = ((AST.leq)expr).e1;
      AST.expression e2 = ((AST.leq)expr).e2;
      return arith_impl_capture(out, e1, e2, "LE", false, counter);
    } else if (expr instanceof AST.lt) {
      // Get the expressions separately
      AST.expression e1 = ((AST.lt)expr).e1;
      AST.expression e2 = ((AST.lt)expr).e2;
      return arith_impl_capture(out, e1, e2, "LT", false, counter);
    }
    return counter;
  }

  public Tracker arith_impl_capture(PrintWriter out, AST.expression e1, AST.expression e2, String operation, boolean isArith,  Tracker counter) {
    // Test for the kinds of expressions obtained. This has to be done pairwise
    // First case, if both the operands are int constants
    if (e1 instanceof AST.int_const && e2 instanceof AST.int_const) {
      int e1_val = ((AST.int_const)e1).value;
      int e2_val = ((AST.int_const)e2).value;

      if (isArith)
        print_util.arithOp(out, operation, (Operand)new IntValue(e1_val), (Operand)new IntValue(e2_val), new Operand(int_type, String.valueOf(counter.register)));
      else
        print_util.compareOp(out, operation, (Operand)new IntValue(e1_val), (Operand)new IntValue(e2_val), new Operand(bool_type, String.valueOf(counter.register)));

      return new Tracker(counter.register + 1, counter.if_counter);
    }

    // Second and Third cases are analogous except for the placement of the object
    else if (e1 instanceof AST.int_const && e2 instanceof AST.object) {
      int e1_val = ((AST.int_const)e1).value;
      AST.object e2_obj = (AST.object)e2;
      Operand non_cons = new Operand(int_type, String.valueOf(counter.register));
      boolean flag = check_attribute(e2_obj.name);
      if (flag == true) {
        print_util.loadOp(out, int_type, new Operand(new OpType(OpTypeId.INT32_PTR), e2_obj.name), non_cons);
      } else {
        print_util.loadOp(out, int_type, new Operand(new OpType(OpTypeId.INT32_PTR), e2_obj.name + ".addr"), non_cons);
      }
      if (isArith)
        print_util.arithOp(out, operation, (Operand)new IntValue(e1_val), non_cons, new Operand(int_type, String.valueOf(counter.register + 1)));
      else
        print_util.compareOp(out, operation, (Operand)new IntValue(e1_val), non_cons, new Operand(bool_type, String.valueOf(counter.register + 1)));

      return new Tracker(counter.register + 2, counter.if_counter);
    }

    else if (e1 instanceof AST.object && e2 instanceof AST.int_const) {
      int e2_val = ((AST.int_const)e2).value;
      AST.object e1_obj = (AST.object)e1;
      Operand non_cons = new Operand(int_type, String.valueOf(counter.register));
      boolean flag = check_attribute(e1_obj.name);
      if (flag == true) {
        print_util.loadOp(out, int_type, new Operand(new OpType(OpTypeId.INT32_PTR), e1_obj.name), non_cons);
      } else {
        print_util.loadOp(out, int_type, new Operand(new OpType(OpTypeId.INT32_PTR), e1_obj.name + ".addr"), non_cons);
      }
      if (isArith)
        print_util.arithOp(out, operation, (Operand)new IntValue(e2_val), non_cons, new Operand(int_type, String.valueOf(counter.register + 1)));
      else
        print_util.compareOp(out, operation, (Operand)new IntValue(e2_val), non_cons, new Operand(bool_type, String.valueOf(counter.register + 1)));

      return new Tracker(counter.register + 2, counter.if_counter);
    }

    // Last case is when both the operands are objects
    else if (e1 instanceof AST.object && e2 instanceof AST.object) {
      AST.object e1_obj = (AST.object)e1;
      AST.object e2_obj = (AST.object)e2;
      Operand non_cons_1 = new Operand(int_type, String.valueOf(counter.register));
      Operand non_cons_2 = new Operand(int_type, String.valueOf(counter.register + 1));
      boolean flag1 = check_attribute(e1_obj.name);
      boolean flag2 = check_attribute(e2_obj.name);
      if (flag1 == true) {
        print_util.loadOp(out, int_type, new Operand(new OpType(OpTypeId.INT32_PTR), e1_obj.name), non_cons_1);
      } else {
        print_util.loadOp(out, int_type, new Operand(new OpType(OpTypeId.INT32_PTR), e1_obj.name + ".addr"), non_cons_1);
      }
      if (flag2 == true) {
        print_util.loadOp(out, int_type, new Operand(new OpType(OpTypeId.INT32_PTR), e2_obj.name), non_cons_2);
      } else {
        print_util.loadOp(out, int_type, new Operand(new OpType(OpTypeId.INT32_PTR), e2_obj.name + ".addr"), non_cons_2);
      }
      if (isArith)
        print_util.arithOp(out, operation, non_cons_1, non_cons_2, new Operand(int_type, String.valueOf(counter.register + 2)));
      else
        print_util.compareOp(out, operation, non_cons_1, non_cons_2, new Operand(bool_type, String.valueOf(counter.register + 2)));

      return new Tracker(counter.register + 3, counter.if_counter);
    }

    else {
      if (e1 instanceof AST.object) {
        AST.object e1_obj = (AST.object)e1;
        Tracker cur_ops = arith_capture(out, e2, counter);
        Operand non_cons = new Operand(int_type, String.valueOf(cur_ops.register));
        boolean flag = check_attribute(e1_obj.name);
        if (flag == true) {
          print_util.loadOp(out, int_type, new Operand(new OpType(OpTypeId.INT32_PTR), e1_obj.name), non_cons);
        } else {
          print_util.loadOp(out, int_type, new Operand(new OpType(OpTypeId.INT32_PTR), e1_obj.name + ".addr"), non_cons);
        }
        if (isArith)
          print_util.arithOp(out, operation, non_cons, new Operand(int_type, String.valueOf(cur_ops.register - 1)), new Operand(int_type, String.valueOf(cur_ops.register + 1)));
        else
          print_util.compareOp(out, operation, non_cons, new Operand(int_type, String.valueOf(cur_ops.register - 1)), new Operand(bool_type, String.valueOf(cur_ops.register + 1)));

        return new Tracker(cur_ops.register + 2, cur_ops.if_counter);
      }

      if (e2 instanceof AST.object) {
        AST.object e2_obj = (AST.object)e2;
        Tracker cur_ops = arith_capture(out, e1, counter);
        Operand non_cons = new Operand(int_type, String.valueOf(cur_ops.register));
        boolean flag = check_attribute(e2_obj.name);
        if (flag == true) {
          print_util.loadOp(out, int_type, new Operand(new OpType(OpTypeId.INT32_PTR), e2_obj.name), non_cons);
        } else {
          print_util.loadOp(out, int_type, new Operand(new OpType(OpTypeId.INT32_PTR), e2_obj.name + ".addr"), non_cons);
        }
        if (isArith)
          print_util.arithOp(out, operation, non_cons, new Operand(int_type, String.valueOf(cur_ops.register - 1)), new Operand(int_type, String.valueOf(cur_ops.register + 1)));
        else
          print_util.compareOp(out, operation, non_cons, new Operand(int_type, String.valueOf(cur_ops.register - 1)), new Operand(bool_type, String.valueOf(cur_ops.register + 1)));
        return new Tracker(cur_ops.register + 2, cur_ops.if_counter);
      }

      if (e1 instanceof AST.int_const) {
        Tracker cur_ops = arith_capture(out, e2, counter);
        int e1_val = ((AST.int_const)e1).value;
        if (isArith)
          print_util.arithOp(out, operation, (Operand)new IntValue(e1_val), new Operand(int_type, String.valueOf(cur_ops.register - 1)), new Operand(int_type, String.valueOf(cur_ops.register)));
        else
          print_util.compareOp(out, operation, (Operand)new IntValue(e1_val), new Operand(int_type, String.valueOf(cur_ops.register - 1)), new Operand(bool_type, String.valueOf(cur_ops.register)));
        return new Tracker(cur_ops.register + 1, cur_ops.if_counter);
      }

      if (e2 instanceof AST.int_const) {
        Tracker cur_ops = arith_capture(out, e1, counter);
        int e2_val = ((AST.int_const)e2).value;
        if (isArith)
          print_util.arithOp(out, operation, (Operand)new IntValue(e2_val), new Operand(int_type, String.valueOf(cur_ops.register - 1)), new Operand(int_type, String.valueOf(cur_ops.register)));
        else
          print_util.compareOp(out, operation, (Operand)new IntValue(e2_val), new Operand(int_type, String.valueOf(cur_ops.register - 1)), new Operand(bool_type, String.valueOf(cur_ops.register)));
        return new Tracker(cur_ops.register + 1, cur_ops.if_counter);
      }
      return counter;
    }
  }

  public boolean check_attribute(String objname) {
    for (AST.attr check_attr : classList.get(CLASS_NAME).attributes) {
      if (objname.equals(check_attr.name)) {
        return true;
      }
    }
    return false;
  }

  public Tracker equality_capture(PrintWriter out, AST.expression e1, AST.expression e2, Tracker counter) {
    // Test for the kinds of expressions obtained. This has to be done pairwise
    // First case, if both the operands are int constants

    /* checking for int constants */

    if (e1.type.equals("Int"))
      return arith_impl_capture(out, e1, e2, "EQ", false, counter);

    /* checking for bool constants */

    /* both boolean constants */
    if (e1.type.equals("Bool")) {

      if (e1 instanceof AST.bool_const && e2 instanceof AST.bool_const) {
        boolean e1_val = ((AST.bool_const)e1).value;
        boolean e2_val = ((AST.bool_const)e2).value;
        print_util.compareOp(out, "EQ", (Operand)new BoolValue(e1_val), (Operand)new BoolValue(e2_val), new Operand(bool_type, String.valueOf(counter.register)));
        return new Tracker(counter.register + 1, counter.if_counter);
      }

      // Second and Third cases are analogous except for the placement of the object
      if (e1 instanceof AST.bool_const && e2 instanceof AST.object) {
        boolean e1_val = ((AST.bool_const)e1).value;
        AST.object e2_obj = (AST.object)e2;
        Operand non_cons = new Operand(bool_type, String.valueOf(counter.register));
        boolean flag = check_attribute(e2_obj.name);

        if (flag == true) {
          print_util.loadOp(out, bool_type, new Operand(new OpType(OpTypeId.INT1_PTR), e2_obj.name), non_cons);
        } else {
          print_util.loadOp(out, bool_type, new Operand(new OpType(OpTypeId.INT1_PTR), e2_obj.name + ".addr"), non_cons);
        }
        print_util.compareOp(out, "EQ", (Operand)new BoolValue(e1_val), non_cons, new Operand(bool_type, String.valueOf(counter.register + 1)));

        return new Tracker(counter.register + 2, counter.if_counter);
      }

      if (e1 instanceof AST.object && e2 instanceof AST.bool_const) {
        boolean e2_val = ((AST.bool_const)e2).value;
        AST.object e1_obj = (AST.object)e1;
        Operand non_cons = new Operand(bool_type, String.valueOf(counter.register));
        boolean flag = check_attribute(e1_obj.name);
        if (flag == true) {
          print_util.loadOp(out, bool_type, new Operand(new OpType(OpTypeId.INT1_PTR), e1_obj.name), non_cons);
        } else {
          print_util.loadOp(out, bool_type, new Operand(new OpType(OpTypeId.INT1_PTR), e1_obj.name + ".addr"), non_cons);
        }
        print_util.compareOp(out, "EQ", (Operand)new BoolValue(e2_val), non_cons, new Operand(bool_type, String.valueOf(counter.register + 1)));

        return new Tracker(counter.register + 2, counter.if_counter);
      }

      // Last case is when both the operands are objects
      if (e1 instanceof AST.object && e2 instanceof AST.object) {
        AST.object e1_obj = (AST.object)e1;
        AST.object e2_obj = (AST.object)e2;
        Operand non_cons_1 = new Operand(bool_type, String.valueOf(counter.register));
        Operand non_cons_2 = new Operand(bool_type, String.valueOf(counter.register + 1));
        boolean flag1 = check_attribute(e1_obj.name);
        boolean flag2 = check_attribute(e2_obj.name);
        if (flag1 == true) {
          print_util.loadOp(out, bool_type, new Operand(new OpType(OpTypeId.INT1_PTR), e1_obj.name), non_cons_1);
        } else {
          print_util.loadOp(out, bool_type, new Operand(new OpType(OpTypeId.INT1_PTR), e1_obj.name + ".addr"), non_cons_1);
        }
        if (flag2 == true) {
          print_util.loadOp(out, bool_type, new Operand(new OpType(OpTypeId.INT1_PTR), e2_obj.name), non_cons_2);
        } else {
          print_util.loadOp(out, bool_type, new Operand(new OpType(OpTypeId.INT1_PTR), e2_obj.name + ".addr"), non_cons_2);
        }
        print_util.compareOp(out, "EQ", non_cons_1, non_cons_2, new Operand(bool_type, String.valueOf(counter.register + 2)));

        return new Tracker(counter.register + 3, counter.if_counter);
      }
      return counter;
    }

    if (e1.type.equals("String")) {

      if (e1 instanceof AST.string_const && e2 instanceof AST.string_const) {
        String e1_val = ((AST.string_const)e1).value;
        String e2_val = ((AST.string_const)e2).value;

        out.println("\t%" + counter.register + " = bitcast [" + String.valueOf(e1_val.length() + 1) + " x i8]* " + "@.str." + e1.lineNo + " to i8*");
        out.println("\t%" + (counter.register + 1) + " = bitcast [" + String.valueOf(e2_val.length() + 1) + " x i8]* " + "@.str." + e2.lineNo + " to i8*");

        Operand return_val = new Operand(bool_type, String.valueOf(counter.register + 2));
        List<Operand> arguments = new ArrayList<Operand>();
        arguments.add(new Operand(string_type, String.valueOf(counter.register)));
        arguments.add(new Operand(string_type, String.valueOf(counter.register + 1)));
        print_util.callOp(out, new ArrayList<OpType>(), "String_strcmp", true, arguments, return_val);
        return new Tracker(counter.register + 3, counter.if_counter);
      }

      // Second and Third cases are analogous except for the placement of the object
      if (e1 instanceof AST.string_const && e2 instanceof AST.object) {
        String e1_val = ((AST.string_const)e1).value;
        out.println("\t%" + counter.register + " = bitcast [" + String.valueOf(e1_val.length() + 1) + " x i8]* " + "@.str." + e1.lineNo + " to i8*");

        AST.object e2_obj = (AST.object)e2;
        Operand non_cons = new Operand(string_type, String.valueOf(counter.register + 1));
        boolean flag = check_attribute(e2_obj.name);

        if (flag == true) {
          print_util.loadOp(out, string_type, new Operand(new OpType(OpTypeId.INT8_PPTR), e2_obj.name), non_cons);
        } else {
          print_util.loadOp(out, string_type, new Operand(new OpType(OpTypeId.INT8_PPTR), e2_obj.name + ".addr"), non_cons);
        }

        Operand return_val = new Operand(bool_type, String.valueOf(counter.register + 2));
        List<Operand> arguments = new ArrayList<Operand>();
        arguments.add(new Operand(string_type, String.valueOf(counter.register)));
        arguments.add(new Operand(string_type, String.valueOf(counter.register + 1)));

        print_util.callOp(out, new ArrayList<OpType>(), "String_strcmp", true, arguments, return_val);

        return new Tracker(counter.register + 3, counter.if_counter);
      }

      if (e1 instanceof AST.object && e2 instanceof AST.string_const) {
        String e2_val = ((AST.string_const)e2).value;
        out.println("\t%" + counter.register + " = bitcast [" + String.valueOf(e2_val.length() + 1) + " x i8]* " + "@.str." + e2.lineNo + " to i8*");

        AST.object e1_obj = (AST.object)e1;
        Operand non_cons = new Operand(string_type, String.valueOf(counter.register + 1));
        boolean flag = check_attribute(e1_obj.name);

        if (flag == true) {
          print_util.loadOp(out, string_type, new Operand(new OpType(OpTypeId.INT8_PPTR), e1_obj.name), non_cons);
        } else {
          print_util.loadOp(out, string_type, new Operand(new OpType(OpTypeId.INT8_PPTR), e1_obj.name + ".addr"), non_cons);
        }

        Operand return_val = new Operand(bool_type, String.valueOf(counter.register + 2));
        List<Operand> arguments = new ArrayList<Operand>();
        arguments.add(new Operand(string_type, String.valueOf(counter.register)));
        arguments.add(new Operand(string_type, String.valueOf(counter.register + 1)));

        print_util.callOp(out, new ArrayList<OpType>(), "String_strcmp", true, arguments, return_val);

        return new Tracker(counter.register + 3, counter.if_counter);
      }

      // Last case is when both the operands are objects
      if (e1 instanceof AST.object && e2 instanceof AST.object) {
        AST.object e1_obj = (AST.object)e1;
        AST.object e2_obj = (AST.object)e2;
        Operand non_cons_1 = new Operand(bool_type, String.valueOf(counter.register));
        Operand non_cons_2 = new Operand(bool_type, String.valueOf(counter.register + 1));
        boolean flag1 = check_attribute(e1_obj.name);
        boolean flag2 = check_attribute(e2_obj.name);
        if (flag1 == true) {
          print_util.loadOp(out, bool_type, new Operand(new OpType(OpTypeId.INT8_PPTR), e1_obj.name), non_cons_1);
        } else {
          print_util.loadOp(out, bool_type, new Operand(new OpType(OpTypeId.INT8_PPTR), e1_obj.name + ".addr"), non_cons_1);
        }
        if (flag2 == true) {
          print_util.loadOp(out, bool_type, new Operand(new OpType(OpTypeId.INT8_PPTR), e2_obj.name), non_cons_2);
        } else {
          print_util.loadOp(out, bool_type, new Operand(new OpType(OpTypeId.INT8_PPTR), e2_obj.name + ".addr"), non_cons_2);
        }

        Operand return_val = new Operand(bool_type, String.valueOf(counter.register + 2));
        List<Operand> arguments = new ArrayList<Operand>();
        arguments.add(non_cons_1);
        arguments.add(non_cons_2);

        print_util.callOp(out, new ArrayList<OpType>(), "String_strcmp", true, arguments, return_val);

        return new Tracker(counter.register + 3, counter.if_counter);
      }
      return counter;
    }

    return counter;
  }
}
