class Some_1 inherits Some_2 {
-- demonstrates case-of-esac syntax verification
     a : Some_3 <- case self of
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
     e : Some_4 <- case self of
		  n : Some_3 => (new Some_4);
		  n : Some_4 => n;
		esac;

-- demonstrates dynamic dispatch
     f : Int <- a@Some_2.doh() + g.doh() + e.doh() + doh() + printh();
};

class Some_2 inherits IO {

-- demonstrates declare and assign objects
     h : Int <- 1;
     g : Some_1  <- case self of
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
  a : Some_2 <- new Some_2;
  b : Some_1 <- new Some_1;
  c : Some_3 <- new Some_3;
  d : Some_4 <- new Some_4;

  main(): String { "do nothing" };
};
