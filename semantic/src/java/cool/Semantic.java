package cool;

public class Semantic{
	private boolean errorFlag		= false;
	private ScopeTable<AST.attr> scp_tbl	= new ScopeTable<AST.attr>(); 
	
	public void reportError(String filename, int lineNo, String error){
		errorFlag = true;
		System.err.println(filename+":"+lineNo+": "+error);
	}
	public boolean getErrorFlag(){
		return errorFlag;
	}

/*
	Don't change code above this line
*/
	public Semantic(AST.program program){
		//Write Semantic analyzer code here
	}
	
	public void low_com_anc(
	
	public void NodeVisit(AST.expression expr) {
		if(expr instanceof AST.bool_const) {
			NodeVisit((AST.bool_const)expr)
		}
		else if(expr instanceof AST.str_const) {
			NodeVisit((AST.str_const)expr)
		}
		else if(expr instanceof AST.int_const) {
			NodeVisit((AST.int_const)expr)
		}
		else if(expr instanceof AST.object) {
			NodeVisit((AST.object)expr)
		}
		else if(expr instanceof AST.comp) {
			NodeVisit((AST.comp)expr)
		}
		else if(expr instanceof AST.eq) {
			NodeVisit((AST.eq)expr)
		}
		else if(expr instanceof AST.leq) {
			NodeVisit((AST.leq)expr)
		}
		else if(expr instanceof AST.lt) {
			NodeVisit((AST.lt)expr) 
		}
		else if(expr instanceof AST.neg) {
			NodeVisit((AST.neg)expr)
		}
		else if(expr instanceof AST.divide) {
			NodeVisit((AST.divide)expr)
		}
		else if(expr instanceof AST.mul) {
			NodeVisit((AST.mul)expr)
		}
		else if(expr instanceof AST.sub) {
			NodeVisit((AST.sub)expr)
		}
		else if(expr instanceof AST.plus) {
			NodeVisit((AST.plus)expr)
		}
		else if(expr instanceof AST.isvoid) {
			NodeVisit((AST.isvoid)expr)
		}
		else if(expr instanceof AST.new_) {
			NodeVisit((AST.new_)expr)
		}
		else if(expr instanceof AST.typcase) {
			NodeVisit((AST.typcase)expr)
		}
		else if(expr instanceof AST.block) {
			NodeVisit((AST.block)expr)
		}
		else if(expr instanceof AST.loop) {
			NodeVisit((AST.loop)expr)
		}
		else if(expr instanceof AST.cond) {
			NodeVisit((AST.cond)expr)
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
		if return_val == null {
			reportError(filename, obj.lineNo, "Identifier " + obj.name + " not present in scope");
			obj.type	= "Object";
		}
		else {
			obj.type	= return_val.typeid;
		}
	}

	public void NodeVisit(AST.comp complement) {
		NodeVisit(complement.e1);
		String e1_type	= complement.e1.type;
		if e1_type.equals("boolean") == false {
			reportError(filename, complement.lineNo, "Incompatible type for complement");
		}
		complement.type	= "boolean";
	}

	public void NodeVisit(AST.eq equality) {
		NodeVisit(equality.e1);
		NodeVisit(equality.e2);
		String e1_type	= equality.e1.type;
		String e2_type	= equality.e2.type;
		if (e1_type.equals("boolean") || e1_type.equals("integer") || e1_type.equals("string")) 
			|| 
		   (e2_type.equals("boolean") || e2_type.equals("integer") || e2_type.equals("string")) {
			if e1_type.equals(e2_type) == false {
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
		if (e1_type.equals("integer") == false || e2_type.equals("integer") == false) {
			reportError(filename, less_equal.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for comparison");
		}
		less_equal.type	= "boolean"
	}

	public void NodeVisit(AST.lt less_than) {
		NodeVisit(less_than.e1);
		NodeVisit(less_than.e2);
		String e1_type	= less_than.e1.type;
		String e2_type	= less_than.e2.type;
		if (e1_type.equals("integer") == false || e2_type.equals("integer") == false) {
			reportError(filename, less_than.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for comparison");
		}
		less_equal.type	= "boolean"
	}

	public void NodeVisit(AST.neg negation) {
		NodeVisit(negation.e1);
		String e1_type	= negation.e1.type;
		if (e1_type.equals("integer") == false) {
			reportError(filename, negation.lineNo, "Incompatible type " + e1_type + " for negation");
		}
		negation.type	= "integer"
	}

	public void NodeVisit(AST.divide division) {
		NodeVisit(division.e1);
		NodeVisit(division.e2);
		String e1_type	= division.e1.type;
		String e2_type	= division.e2.type;
		if (e1_type.equals("integer") == false || e2_type.equals("integer") == false) {
			reportError(filename, division.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for division");
		}
		division.type	= "integer"
	}

	public void NodeVisit(AST.mul multipication) {
		NodeVisit(multiplication.e1);
		NodeVisit(multiplication.e2);
		String e1_type	= multiplication.e1.type;
		String e2_type	= multiplication.e2.type;
		if (e1_type.equals("integer") == false || e2_type.equals("integer") == false) {
			reportError(filename, multiplication.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for multiplication");
		}
		multiplication.type	= "integer"
	}

	public void NodeVisit(AST.sub subtraction) {
		NodeVisit(subtraction.e1);
		NodeVisit(subtraction.e2);
		String e1_type	= subtraction.e1.type;
		String e2_type	= subtraction.e2.type;
		if (e1_type.equals("integer") == false || e2_type.equals("integer") == false) {
			reportError(filename, subtraction.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for subtraction");
		}
		subtraction.type	= "integer"
	}

	public void NodeVisit(AST.plus addition) {
		NodeVisit(addition.e1);
		NodeVisit(addition.e2);
		String e1_type	= addition.e1.type;
		String e2_type	= addition.e2.type;
		if (e1_type.equals("integer") == false || e2_type.equals("integer") == false) {
			reportError(filename, addition.lineNo, "Incompatible types " + e1_type + " & " + e2_type + " for addition");
		}
		addition.type	= "integer"
	}

	public void NodeVisit(AST.isvoid voidcheck) {
		voidcheck.type	= "boolean";
	}

	public void NodeVisit(AST.new_ new_object) {
		// FLAG
		// new can be used only for pre-existing classes. Hence we will need to check if the new_ typeid exists
		// Call a table of classes class_table
		return_val	= class_table.get(new_.typeid)
		if return_val == null {
			reportError(filename, new_object.lineNo, new_.typeid + " does not exist");
			new_object.type	= "object"
		}
		else {
			new_object.type	= new_object.typeid
		}
	}	
	
	public void NodeVisit(AST.typcase cases) {
		NodeVisit(cases.predicate);
		pred_type	= cases.predicate.type;
		if(pred_type.equals("boolean") == true) {
			List<AST.branch> brnch_list	= cases.branches;
			for(AST.branch sing_brnch_1 : brnch_list) {
				for(AST.branch sing_brnch_2 : brnch_list) {
					if(sing_brnch_1.type.equals(sing_brnch_2.type)) {
						reportError(filename, sing_brnch_1.lineNo, "Non-distinct variable types in branches of case statement");
					}
				}
			}
			
			for(AST.branch sng_brnch : brnch_list) {
				scp_tbl.enterScope();
				// FLAG
				// Need to refer to a table consisting of all classes.
				// Call that table class_table
				ret_val		= class_table.get(sng_brnch.type);
				ins_type	= "Object";
				if ret_val == null {
					reportError(filename, sng_brnch.lineNo, "Class " + sng_brnch.type + " in case branch is undefined");
					ins_type	= "Object"
				}
				else {
					ins_type	= sng_brnch.type
				}
				scp_tbl.insert(sing_brnch.name, new AST.attr(sng_brnch.name, ins_type, sng_brnch.value, sng_brnch.lineNo));
				NodeVisit(sng_brnch.value);
				scp_tbl.exitScope();
			}
			
			ret_val		= cases.branches.get(0).type;
			brnch_list	= cases.branches.subList(1, cases.branches.size());
			for(AST.branch sng_brnch : brnch_list ) {
				ret_val	= lca(ret_val, sng_brnch.value.type)
			}
			cases.type	= ret_val
		
		} else {
			reportError(filename, cases.lineNo, "Non-boolean predicate for case statement");
		}
	}
	
	public void NodeVisit(AST.block block_expr) {
		List<AST.expression> expr_list	= ((AST.block)expr).l1;
		AST.expression sing_expr	= new AST.expression;
		for(sing_expr : expr_list) {
			NodeVisit(sing_expr)
		}
		block.type	= sing_expr.type	// Last iterate's type
	}

	public void NodeVisit(AST.loop loop_structure) {
		NodeVisit(loop_structure.predicate);
		NodeVisit(loop_structure.body);
		pred_type	= loop_structure.predicate.type;
		if(pred_type.equals("boolean") == false) {
			reportError(filename, loop_structure.lineNo, "Non-boolean predicate for loop");
		}
		loop.type	= "object";
	}
	
	public void NodeVisit(AST.cond condition) {
		NodeVisit(condition.predicate);
		NodeVisit(condition.ifbody);
		NodeVisit(condition.elsebody);
		pred_type	= loop_structure.predicate.type;
		if pred_type.equals("boolean") == false {
			reportError(filename, condition.lineNo, "Non-boolean predicate for if clause");
		}
		condition.type = lca(condition.ifbody.type, condition.elsebody.type)
	}
}
