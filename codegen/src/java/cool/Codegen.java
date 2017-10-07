package cool;

import java.io.PrintWriter;
import java.util.HashMap;

String filename;

public class Codegen{

    // HashMap to map the types in COOL to types in LLVM-IR
    public HashMap<String, String> type_list = new HashMap<String, String>();
	public Codegen(AST.program program, PrintWriter out){
		// Write Code generator code here
        // out.println("; I am a comment in LLVM-IR. Feel free to remove me.");

        // Printing the trivial code in LLVM-IR
        // This includes the filename, datalayout, triple. This information was
        // obtained using the LLVM-IR emitted by clang on a simple program
        out.println("source_filename = " + "\"" + filename + "\"");
        out.println("target datalayout = " + "\"e-m:e-i64:64-f80:128-n8:16:32:64-S128\"");
        out.println("target triple = " + "\"x86_64-unknown-linux-gnu\"");

        // Mapping the types in COOL to types in LLVM-IR
        // Bool is an integer for convenience, which can only take values 0 or 1.
        type_list.insert("Bool", "i32");

        // Int is an integer.
        type_list.insert("Int", "i32");

        // String is an set of characters. Each character is i8 (8bits/1byte) in size.
        // According to the COOL Manual, a String's max length is 1024.
        // Refer page 9 for the above statement.
        type_list.insert("String", "[1024 x i8]*");

        // Error Messages are in the form of aliases in LLVM-IR. 
        out.println("@_RTERR_div0 = private unnamed_addr constant [27 x i8] c\"RuntimeError: Divide By 0\\0A\\00\"");
        out.println("@_RTERR_voidstatdis = private unnamed_addr constant [36 x i8] c\"TypeError: Static Dispatch on Void\\0A\\00\"");
        
        // Emitting LLVM-IR for all String and Object functions
        // For this we may require some C functions such as strlen(), strcat(), strncpy() and strcmp()
        // We will first include them
        out.println("declare i64 @strlen(i8*)");
        out.println("declare i8* @strcat(i8*, i8*)");
        out.println("declare i8* @strncpy(i8*, i8*)");
        out.println("declare i32 @strcmp(i8*, i8*)");

        // String function: Length
        out.println("define i32 @_CMETH_len( [1024 x i8]* %given_str ) {");
        out.println("entry:");
        out.println("%0 = bitcast [1024 x i8]* %given_str to i8*");
        out.println("%1 = call i64 @strlen(i8* %0)");
        out.println("ret i32 trunc i64 %1 to %i32");
        out.println("}");

        // String function: Concatenate
        // Need to return a new string in entirety, not just concatenate to given
        out.println("define i8* @_CMETH_cat( [1024 x i8]* %string_1 , [1024 x i8]* %string_2 ) {");
        out.println("entry:");
        out.println("}");

        // String function: Copy to n bytes
        // Need to return a new string here also. 
        out.println("define [1024 x i8]* @_CMETH_ncpy( [1024 x i8]* %given_str, i32 %begin, i32 %n) {");
        out.println("entry:");
        out.println("}");

        // String function: Compare two strings
        // Need to return true or false if the strings are equal
        out.println("define i32 @_CMETH_cmp( [1024 x i8]* %string_1, [1024 x i8]* %string_2) {");
        out.println("entry:");
        out.println("%0 = bitcast [1024 x i8]* %string_1 to i8*");
        out.println("%1 = bitcast [1024 x i8]* %string_2 to i8*");
        out.println("%2 = call i32 @strcmp(i8* %0, i8* %1)");
        out.println("%3 = icmp eq i32 %2, 0");
        out.println("%retval = bitcast i1 %3 to i32");
        out.println("ret i32 retval");
        out.println("}");                
	}
}
