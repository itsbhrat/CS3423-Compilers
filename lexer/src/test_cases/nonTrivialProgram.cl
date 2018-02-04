(*
 *  This program takes an expression as string from the user
 *  and evaluates it taking precedence into account
 *  In this 2 stacks valu , operator are implemented using strings 
 *  In this input error correction is not done
 *  It is assumed that the input given is valid
*)

(*
 * P.S. For converting Int to String logic is taken from examples arith.cl   
*)
(* Class To check whether a String is a digit or not*)
class IsDigit 
{
    isDigit(param : String) : Bool 
    {
        {
            if param = "0" then true else 
            if param = "1" then true else 
            if param = "2" then true else 
            if param = "3" then true else 
            if param = "4" then true else 
            if param = "5" then true else 
            if param = "6" then true else 
            if param = "7" then true else 
            if param = "8" then true else 
            if param = "9" then true else 
            {false;}
            fi fi fi fi fi fi fi fi fi fi;
        }
    };
};

(* Class To change 1 character to Int *)
class ToDigit 
{
    toDigit(param : String) : Int 
    {
        {
            if param = "0" then 0 else 
            if param = "1" then 1 else 
            if param = "2" then 2 else 
            if param = "3" then 3 else 
            if param = "4" then 4 else 
            if param = "5" then 5 else 
            if param = "6" then 6 else 
            if param = "7" then 7 else 
            if param = "8" then 8 else 
            if param = "9" then 9 else 
            { abort(); 0; }
            fi fi fi fi fi fi fi fi fi fi;
        }
    };
};

(* For changing String to Int power of 10 needs to be done
 * Hence , power function is made which finds base^exponent 
  *)
class Power 
{
    z : Int <- 1;
    math_pow(base : Int , exponent : Int) : Int 
    {
        {
            z <- 1;
            while 0 < exponent  --loop to find power by multyplying base*base exponent times
                loop
                    {
                        z <- z * base;
                        exponent <- exponent - 1;
                    }
                pool;
            z;
        }
    };
};

(* For changing String to Int 
    It inherits from Power as 10^i needs to be done
  *)
class Atoi inherits Power
{
    answer : Int <- 0;
    char_to_digit : ToDigit <- new ToDigit;
    atoi(param : String) : Int
    {
        (let len : Int <- param.length() , track : Int <- 0 ,  char_string : String <- "" , temp : Int in {
            answer <- 0;
            while 0 < len
                loop 
                    {
                        char_string <- param.substr(len - 1 , 1);   --extracting character from beginninh to end
                        if(char_string = "-") then {answer <- ~answer;}     --for negative
                            else {
                                temp <- char_to_digit.toDigit(char_string); --finding Int equivalent
                                answer <- answer + temp * (math_pow(10 , track));   --raising 10 to track and adding to final answer
                            }
                        fi;                      
                        track <- track + 1;
                        len <- len - 1;        
                    }    
                pool;
                answer;
        })
    };
};

(* For taking 2 operands and after performing the operation in them
    return the answer transformed to String
  *)
class ApplyOperation inherits IO
{

    int_to_String(param : Int) : String {
	if param = 0 then "0" else 
        if 0 < param then int_to_String_recursive(param) else
          "-".concat(int_to_String_recursive(param * ~1)) 
        fi fi
    };
	
    int_to_String_recursive(param : Int) : String {
        if param = 0 then "" else 
	    (let next : Int <- param / 10 in
		int_to_String_recursive(next).concat(digit_to_char(param - next * 10))
	    )
        fi
    };
    
    (* Finding String equivalent to Int *)
    digit_to_char(param : Int) : String {
	if param = 0 then "0" else
	if param = 1 then "1" else
	if param = 2 then "2" else
	if param = 3 then "3" else
	if param = 4 then "4" else
	if param = 5 then "5" else
	if param = 6 then "6" else
	if param = 7 then "7" else
	if param = 8 then "8" else
	if param = 9 then "9" else
	{ abort(); ""; }
        fi fi fi fi fi fi fi fi fi fi
     };
     
    apply_operation(operand2 : Int , operand1 : Int , operator : String ) : String
    {
        {
            --check the operation and return the string form
            if operator = "+" then { int_to_String(operand1 + operand2); } else 
            if operator = "-" then { int_to_String(operand1 - operand2); } else 
            if operator = "*" then { int_to_String(operand1 * operand2); } else 
            if operator = "/" then { if operand2 = 0 then {out_string("\nERROR : Divide By 0 exception\n"); abort(); "0";} else { int_to_String(operand1 / operand2); } fi; } else
            { abort(); "0";}
             fi fi fi fi;
        }   
    };
};


(*function to return boolean for the precedene*)
class Precedence
{
    checkPrecedence(operator1 : String , operator2 : String) : Bool
    {
        {
            if operator2 = "(" then { false; } else
            if operator2 = ")" then { false; } else
            if operator1 = "*" then { if operator2 = "+" then {false;} else if operator2 = "-" then {false;} else {true;} fi fi;} else
            if operator1 = "/" then { if operator2 = "+" then {false;} else if operator2 = "-" then {false;} else {true;} fi fi;} else
            { true;}
             fi fi fi fi;
        }   
    };
};

(*
implement stack using string
using $ as delimiter
*)

class Stack inherits IO
{
    stack : String <- "";
    temporaryString : String <- "";
    
    isEmpty() : Bool
    {
        if stack = "" 
            then true
            else false
        fi
    };
        
    push(param : String) : SELF_TYPE
    {
        {
            param <- param.concat("$");
            stack <- param.concat(stack);
            self;
        }        
    };
    
    --return the head element but don't pop it
    top() : String
    {
            (let len : Int <- stack.length() , temp : String <- "" , answer : String <- ""  , loop_variable : Int <- 0 in {
                while loop_variable < len   --traversing until we find first $
                loop 
                    {
                        temp <- stack.substr(loop_variable , 1);
                        if(temp = "$")  --if dollar break the loop
                            then { loop_variable <- len; }
                            else { answer <- answer.concat(temp);} --if there is not dollar yet , it is a digit
                        fi;
                        
                        loop_variable <- loop_variable + 1;        
                    }    
                pool;
                answer;
            })   
    };
        --return the head element and pop it
    pop() : String
    {
            (let len : Int <- stack.length() , temp : String <- "" , answer : String <- ""  , loop_variable : Int <- 0 in {
                while loop_variable < len   --traversing until we find first $
                loop 
                    {
                        temp <- stack.substr(loop_variable , 1);
                        if(temp = "$")  --if dollar , update the stack string and break the loop
                            then {                                
                                if(loop_variable = len-1)
                                    then {stack <- "";}
                                    else {stack <- stack.substr(loop_variable+1 , len - loop_variable - 1);}
                                fi;
                                loop_variable <- len; 
                                }
                            else { answer <- answer.concat(temp); }--if there is not dollar yet , it is a digit}
                        fi;
                        loop_variable <- loop_variable + 1;        
                    }    
                pool;
                answer;
            })   
    };

};


class Main inherits IO
{
    --since only single inheritance is allowed , creating objects for the classes
    input : String;
    char_is_digit : IsDigit <- new IsDigit;
    string_atoi : Atoi <- new Atoi;
    value_stack : Stack <- new Stack;
    operator_stack : Stack <- new Stack;
    applyOperation : ApplyOperation <- new ApplyOperation;
    precedence : Precedence <- new Precedence;
    
    main() : Object
    { 
        {
            out_string("1. Enter Only Positive Integers\n");
            out_string("2. Do not enter unnecessary charactes like spaces\n");
            out_string("3. Enter numbers , ( , )  , +,-,*,/\n");
            
            
            out_string("Enter The Expression : ");
            input <- in_string();
            --adding ( ) to expression for simplification
            input <- "(".concat(input);
            input <- input.concat(")");
            
            (let len : Int <- input.length() , char : String <- "" , temp : String <- ""  , temp_int : Int <- 0 , extractNumber : Bool <- true , loop_variable : Int <- 0 in {
                while loop_variable < len
                loop 
                    {
                        char <- input.substr(loop_variable , 1);
                        
                        if(char_is_digit.isDigit(char) = true)  --if char is digit , loop it to find full number
                            then {  
                                temp <- "";    
                                extractNumber <- true;                        
                                while (extractNumber)   --loop will continue until a non digit is found
                                loop
                                    {
                                        if loop_variable < len
                                            then {
                                                --if digit append it
                                                char <- input.substr(loop_variable , 1);                                             
                                                if(char_is_digit.isDigit(char) = true)
                                                    then{ temp <- temp.concat(char); loop_variable <- loop_variable + 1;}
                                                else  {extractNumber <- false;}
                                                fi;
                                            }
                                            else {extractNumber <- false;}
                                            fi;
                                    }
                                pool;
                                
                                loop_variable <- loop_variable - 1;
                                value_stack.push(temp); --push number to value stack
                                }
                            else if char = "(" then { operator_stack.push("(");}    --push ( to operator stack
                                else if char = ")"  --if is there keep onsolving until ( is found
                                        then {

                                            while(not (operator_stack.top() = "("))
loop{ value_stack.push(applyOperation.apply_operation( string_atoi.atoi(value_stack.pop()) , string_atoi.atoi(value_stack.pop()) , operator_stack.pop()));  }pool;
                                            operator_stack.pop();
                                        } else {

                                            extractNumber <- true;     
                                            while extractNumber
                                            loop
                                                {
                                                    if(operator_stack.isEmpty() = true)
                                                        then {extractNumber <- false;}
                                                        else if(precedence.checkPrecedence(char , operator_stack.top()) = true)
                                                                then{
     value_stack.push(applyOperation.apply_operation( string_atoi.atoi(value_stack.pop()) , string_atoi.atoi(value_stack.pop()) , operator_stack.pop())); 
                                                                }
                                                                else{extractNumber <- false;}
                                                        fi fi;
                                                }
                                            pool;
                                            operator_stack.push(char);
                                                }
                                                fi fi fi; 
                                             loop_variable <- loop_variable + 1;                                         
                        }    
                pool;

            });
            
            out_string("Answer : ");
            out_string(value_stack.top());
            out_string("\n");
        }
    };
};












