(*
  class Some_1 inherits Some_2 is represented in a AST node as follows

  _class
    class_name
    inherited_class_name_1
    inherited_class_name_2
    ...

    _attr

    _method

*)


class Some_1 inherits Some_2 {
-- demonstrates case-of-esac syntax verification
(*

         a : Some_3 <- case self of is represented in a AST node as follows

        _typcase       (non terminal of switch condition)
            _object    (terminal of self (variable of Cool) => variable of switch case)
              object_name
            _branch    (non terminal of first case of the switch case)
              object_name
              type
              expr (non terminal telling case body of 1st case)
            _branch    (non terminal of other cases of the switch case)

*)

     a : Some_3 <- case tRue of
		      n : Some_3 => (new Some_4);
		      n : Some_1 => (new Some_3);
		      n : Some_4 => n;
   	         esac;

-- demonstrates assign and dispatch (both from a different object and self itself)
     b : Int <- a.doh() + g.doh() + doh() + printh();
     doh() : Int { (let i : Int <- h in { h <- h + 2; i; } ) };
};

class Some_4 inherits Some_3 {
     c : Int <- doh();
     d : Object <- printh();
};


class Some_3 inherits Some_1 {
     e : Some_4 <- case trUe of
		  n : Some_3 => (new Some_4);
		  n : Some_4 => n;
		esac;

-- demonstrates dynamic dispatch
     f : Int <- a@Some_2.doh() + g.doh() + e.doh() + doh() + printh();
};

class Some_2 inherits IO {

-- demonstrates declare and assign objects
     h : Int <- 1;
     g : Some_1  <- case trUe of
		     	n : Some_2 => (new Some_1);
		     	n : Some_3 => (new Some_4);
			n : Some_1  => (new Some_3);
			n : Some_4 => n;
		  esac;

     i : Object <- printh();
     printh() : Int { { out_int(h); 0; } };
-- demonstrates let and nested/block expressions
     doh() : Int { (let i: Int <- h in { h <- h + 1; i; } ) };
};

class Main {
(*
  Attributes  & Methods of Classes are represented in a AST node as follows
  _class
    _attr
      attr_name
      attr_type
      attr_assignment (in this case _new => object_type)

*)
  a : Some_2 <- new Some_2;
  b : Some_1 <- new Some_1;
  c : Some_3 <- new Some_3;
  d : Some_4 <- new Some_4;

  main(): String { "do nothing" };
};
