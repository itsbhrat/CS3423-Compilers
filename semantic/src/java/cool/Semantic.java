package cool;

import java.util.*;
import cool.AST.class_;

class ClassNode {
	public String name;
	public String parent;
	public int height;
	public HashMap<String, AST.attr> attributes;
	public HashMap<String, AST.method> methods;

	ClassNode(String class_name, String class_parent, int class_height, HashMap<String, AST.attr> class_atributes, HashMap<String, AST.method> class_methods) {
		name = class_name;
		parent = class_parent;
		height = class_height;
		attributes.putAll(class_atributes);
		methods.putAll(class_methods);
	}
}


public class Semantic {
	private boolean errorFlag = false;

	public void reportError(String filename, int lineNo, String error) {
		errorFlag = true;
		System.err.println(filename + ":" + lineNo + ": " + error);
	}
	public boolean getErrorFlag() {
		return errorFlag;
	}

	/*
		Don't change code above this line
	*/

	private ScopeTable<AST.attr> scp_tbl	= new ScopeTable<AST.attr>();

	public HashMap<String, ClassNode> classList;
	private String filename;

	public Semantic(AST.program program) {
		//Write Semantic analyzer code here
		define_built_in_classes();
	}

	private void define_built_in_classes() {
		//consider line number to be 0 for all built in classes
		//for functions returning SELF_TYPE , return the same class name a typeid


		/****** adding object class members ******/
		HashMap <String, AST.method> Object_methods = new HashMap <String, AST.method>();

		Object_methods.put("abort", new AST.method("abort", new ArrayList<AST.formal>(), "Object", new AST.no_expr(0), 0));
		Object_methods.put("type_name", new AST.method("type_name", new ArrayList<AST.formal>(), "String", new AST.no_expr(0), 0));
		Object_methods.put("copy", new AST.method("copy", new ArrayList<AST.formal>(), "Object", new AST.no_expr(0), 0));

		classList.put("Object", new ClassNode("Object", null, 0, new HashMap <String, AST.attr>(), Object_methods));

		/****** adding IO class members ******/

		HashMap <String, AST.method> IO_methods = new HashMap <String, AST.method>();

		List<AST.formal> out_string_formal = new ArrayList<AST.formal>();
		out_string_formal.add(new AST.formal("x", "String", 0));
		IO_methods.put("out_string", new AST.method("out_string", out_string_formal, "IO", new AST.no_expr(0), 0));

		List<AST.formal> out_int_formal = new ArrayList<AST.formal>();
		out_int_formal.add(new AST.formal("x", "Int", 0));
		IO_methods.put("out_int", new AST.method("out_int", out_int_formal, "IO", new AST.no_expr(0), 0));

		IO_methods.put("in_string", new AST.method("in_string", new ArrayList<AST.formal>(), "String", new AST.no_expr(0), 0));
		IO_methods.put("in_int", new AST.method("in_int", new ArrayList<AST.formal>(), "Int", new AST.no_expr(0), 0));

		IO_methods.putAll(Object_methods);		//inherit all object methods
		classList.put("IO", new ClassNode("IO", "Object", 1, new HashMap <String, AST.attr>(), IO_methods));

		/****** adding Int class members ******/

		HashMap <String, AST.method> Int_methods = new HashMap <String, AST.method>();
		Int_methods.putAll(Object_methods);		//inherit all object methods
		classList.put("Int", new ClassNode("Int", "Object", 1, new HashMap <String, AST.attr>(), Int_methods));

		/****** adding Bool class members ******/

		HashMap <String, AST.method> Bool_methods = new HashMap <String, AST.method>();
		Bool_methods.putAll(Object_methods);		//inherit all object methods
		classList.put("Bool", new ClassNode("Bool", "Object", 1, new HashMap <String, AST.attr>(), Bool_methods));

		/****** adding String class members ******/

		HashMap <String, AST.method> String_methods = new HashMap <String, AST.method>();

		String_methods.put("length", new AST.method("length", new ArrayList<AST.formal>(), "Int", new AST.no_expr(0), 0));

		List<AST.formal> concat_formal = new ArrayList<AST.formal>();
		concat_formal.add(new AST.formal("s", "String", 0));
		String_methods.put("concat", new AST.method("concat", concat_formal, "String", new AST.no_expr(0), 0));

		List<AST.formal> substr_formal = new ArrayList<AST.formal>();
		substr_formal.add(new AST.formal("i", "Int", 0));
		substr_formal.add(new AST.formal("l", "Int", 0));
		String_methods.put("substr", new AST.method("substr", substr_formal, "String", new AST.no_expr(0), 0));

		String_methods.putAll(Object_methods);		//inherit all object methods
		classList.put("String", new ClassNode("String", "Object", 1, new HashMap <String, AST.attr>(), String_methods));
	}

	private void insert_class(AST.class_ user) {

		String parent = user.parent;

		HashMap <String, AST.attr> user_attributes = new HashMap<String, AST.attr>();
		HashMap <String, AST.method> user_method = new HashMap <String, AST.method>();

		HashMap <String, AST.attr> parent_attributes = classList.get(parent).attributes;
		HashMap <String, AST.method> parent_method = classList.get(parent).methods;

		/* inheriting parent class method & attributes */

		ClassNode user_node = new ClassNode(user.name, parent, classList.get(parent).height + 1, parent_attributes, parent_method);	// adding the parents attribute list and method list

		// TODO doubt if parent attr fails should we add or not 

		for (AST.feature e : user.features) {

			if (e.getClass() == AST.attr.class) {	//	if class feature is an atribute
				AST.attr ae = (AST.attr) e;

				if (user_attributes.containsKey(ae.name)) {
					reportError(user.filename, ae.lineNo, "Attribute " + ae.name + " is defined multiply in class " + user.name + ".");
				} else if(parent_attributes.containsKey(ae.name)) {
					reportError(user.filename, ae.lineNo, "Attribute " + ae.name + " is an attribute of inherited class " + parent + ".");
				} else {
					user_attributes.put(ae.name, ae);
				}
			}
			else if (e.getClass() == AST.method.class) {	//	if class feature is an method
				AST.method me = (AST.method) e;

				if (user_method.containsKey(me.name)) {
					reportError(user.filename, me.lineNo, "Method " + me.name + " is multiply defined.");
				}
				else {
					user_method.put(me.name, me);
				}
			}
		}
	}


	public void NodeVisit(AST.expression expr) {
		if (expr instanceof AST.bool_const) {
			NodeVisit((AST.bool_const)expr);
		} else if (expr instanceof AST.str_const) {
			NodeVisit((AST.str_const)expr);
		} else if (expr instanceof AST.int_const) {
			NodeVisit((AST.int_const)expr);
		} else if (expr instanceof AST.object) {
			NodeVisit((AST.object)expr);
		} else if (expr instanceof AST.comp) {
			NodeVisit((AST.comp)expr);
		} else if (expr instanceof AST.eq) {
			NodeVisit((AST.eq)expr);
		} else if (expr instanceof AST.leq) {
			NodeVisit((AST.leq)expr);
		} else if (expr instanceof AST.lt) {
			NodeVisit((AST.lt)expr);
		} else if (expr instanceof AST.neg) {
			NodeVisit((AST.neg)expr);
		} else if (expr instanceof AST.divide) {
			NodeVisit((AST.divide)expr);
		} else if (expr instanceof AST.mul) {
			NodeVisit((AST.mul)expr);
		} else if (expr instanceof AST.sub) {
			NodeVisit((AST.sub)expr);
		} else if (expr instanceof AST.plus) {
			NodeVisit((AST.plus)expr);
		} else if (expr instanceof AST.isvoid) {
			NodeVisit((AST.isvoid)expr);
		} else if (expr instanceof AST.new_) {
			NodeVisit((AST.new_)expr);
		} else if (expr instanceof AST.block) {
			NodeVisit((AST.block)expr);
		} else if (expr instanceof AST.loop) {
			NodeVisit((AST.loop)expr);
		} else if (expr instanceof AST.cond) {
			NodeVisit((AST.cond)expr);
		}
	}

	public void NodeVisit(AST.bool_const bool) {
		bool.type	= "boolean";
	}

	public void NodeVisit(AST.str_const string) {
		string.type	= "string";
	}

	public void NodeVisit(AST.int_const integer) {
		integer.type	= "integer";
	}

	public void NodeVisit(AST.object obj) {
		return_val	= scp_tbl.lookUpGlobal(obj.name);
		if (return_val == null) {
		reportError(filename, obj.lineNo, "Identifier " + obj.name + " not present in scope");
			obj.type	= "Object";
		} else {
			obj.type	= return_val.typeid;
		}
	}

	public void NodeVisit(AST.comp complement) {
		NodeVisit(complement.e1);
		String e1_type	= complement.e1.type;
		if (e1_type.equals("boolean") == false) {
			reportError(filename, complement.lineNo, "Incompatible type for complement");
		}
		complement.type	= "boolean";
	}

	public void NodeVisit(AST.eq equality) {
		NodeVisit(equality.e1);
		NodeVisit(equality.e2);
		String e1_type	= equality.e1.type;
		String e2_type	= equality.e2.type;
		if ((e1_type.equals("boolean") || e1_type.equals("integer") || e1_type.equals("string"))
			||
			(e2_type.equals("boolean") || e2_type.equals("integer") || e2_type.equals("string"))) {
			if (e1_type.equals(e2_type) == false) {
				reportError(filename, equality.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for equality");
			}
		}
		equality.type	= "boolean";
	}

	public void NodeVisit(AST.leq less_equal) {
		NodeVisit(less_equal.e1);
		NodeVisit(less_equal.e2);
		String e1_type	= less_equal.e1.type;
		String e2_type	= less_equal.e2.type;
		if ((e1_type.equals("integer") == false || e2_type.equals("integer") == false)) {
			reportError(filename, less_equal.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for comparison");
		}
		less_equal.type	= "boolean";
	}

	public void NodeVisit(AST.lt less_than) {
		NodeVisit(less_than.e1);
		NodeVisit(less_than.e2);
		String e1_type	= less_than.e1.type;
		String e2_type	= less_than.e2.type;
		if ((e1_type.equals("integer") == false || e2_type.equals("integer") == false)) {
			reportError(filename, less_than.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for comparison");
		}
		less_equal.type	= "boolean";
	}

	public void NodeVisit(AST.neg negation) {
		NodeVisit(negation.e1);
		String e1_type	= negation.e1.type;
		if (e1_type.equals("integer") == false) {
			reportError(filename, negation.lineNo, "Incompatible type " + e1_type + " for negation");
		}
		negation.type	= "integer";
	}

	public void NodeVisit(AST.divide division) {
		NodeVisit(division.e1);
		NodeVisit(division.e2);
		String e1_type	= division.e1.type;
		String e2_type	= division.e2.type;
		if (e1_type.equals("integer") == false || e2_type.equals("integer") == false) {
			reportError(filename, division.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for division");
		}
		division.type	= "integer";
	}

	public void NodeVisit(AST.mul multipication) {
		NodeVisit(multiplication.e1);
		NodeVisit(multiplication.e2);
		String e1_type	= multiplication.e1.type;
		String e2_type	= multiplication.e2.type;
		if (e1_type.equals("integer") == false || e2_type.equals("integer") == false) {
			reportError(filename, multiplication.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for multiplication");
		}
		multiplication.type	= "integer";
	}

	public void NodeVisit(AST.sub subtraction) {
		NodeVisit(subtraction.e1);
		NodeVisit(subtraction.e2);
		String e1_type	= subtraction.e1.type;
		String e2_type	= subtraction.e2.type;
		if (e1_type.equals("integer") == false || e2_type.equals("integer") == false) {
			reportError(filename, subtraction.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for subtraction");
		}
		subtraction.type	= "integer";
	}

	public void NodeVisit(AST.plus addition) {
		NodeVisit(addition.e1);
		NodeVisit(addition.e2);
		String e1_type	= addition.e1.type;
		String e2_type	= addition.e2.type;
		if (e1_type.equals("integer") == false || e2_type.equals("integer") == false) {
			reportError(filename, addition.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for addition");
		}
		addition.type	= "integer";
	}

	public void NodeVisit(AST.isvoid voidcheck) {
		voidcheck.type	= "boolean";
	}

	public void NodeVisit(AST.new_ new_object) {
		// FLAG
		// new can be used only for pre-existing classes. Hence we will need to check if the new_ typeid exists
		// need to define a function check_class
		return_val	= check_class(new_.typeid);
		if (return_val == null) {
		reportError(filename, new_object.lineNo, new_.typeid + " does not exist");
			new_object.type	= "object";
		} else {
			new_object.type	= new_object.typeid;
		}
	}

	public void NodeVisit(AST.block block_expr) {
		List<AST.expression> expr_list	= ((AST.block)expr).l1;
		AST.expression sing_expr	= new AST.expression();
		for (AST.expression sing_expr : expr_list) {
			NodeVisit(sing_expr);
		}
		block.type	= sing_expr.type;	// Last iterate's type
	}

	public void NodeVisit(AST.loop loop_structure) {
		NodeVisit(loop_structure.predicate);
		NodeVisit(loop_structure.body);
		pred_type	= loop_structure.predicate.type;
		if (pred_type.equals("boolean") == false) {
			reportError(filename, loop_structure.lineNo, "Non-boolean predicate for loop");
		}
		loop.type	= "object";
	}

	public void NodeVisit(AST.cond condition) {
		NodeVisit(condition.predicate);
		NodeVisit(condition.ifbody);
		NodeVisit(condition.elsebody);
		pred_type	= loop_structure.predicate.type;
		if (pred_type.equals("boolean") == false) {
			reportError(filename, condition.lineNo, "Non-boolean predicate for if clause");
		}
		condition.type = lca(condition.ifbody.type, condition.elsebody.type);
	}
}
