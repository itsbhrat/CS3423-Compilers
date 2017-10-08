package cool;

import java.io.PrintWriter;
import java.util.*;

public class Codegen{

    public String filename;
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
        type_list.put("Bool", "i32");

        // Int is an integer.
        type_list.put("Int", "i32");

        // String is an set of characters. Each character is i8 (8bits/1byte) in size.
        // According to the COOL Manual, a String's max length is 1024.
        // Refer page 9 for the above statement.
        type_list.put("String", "[1024 x i8]*");

        // Error Messages are in the form of aliases in LLVM-IR. 
        out.println("@_RTERR_div0 = private unnamed_addr constant [27 x i8] c\"RuntimeError: Divide By 0\\0A\\00\", align 1");
        out.println("@_RTERR_voistatdis = private unnamed_addr constant [36 x i8] c\"TypeError: Static Dispatch on Void\\0A\\00\", align 1");
        
        // Memory allocation is inadvertent, hence malloc will always be used.
        out.println("declare i8* @malloc(i64)");

        printString(out);
        printIO(out);
	}

    private void printString(PrintWriter out) {
        // Emitting LLVM-IR for all String functions
        // For this we may require some C functions such as strlen(), strcat() and str(n)cpy()for strings
        // We will first include them
        out.println("declare i64 @strlen(i8*)");
        out.println("declare i8* @strcat(i8*, i8*)");
        out.println("declare i8* @strcpy(i8*, i8*)");
        out.println("declare i8* @strncpy(i8*, i8*, i32)");
        out.println("declare i32 @strcmp(i8*, i8*)\n");

        // String function: Length
        out.println("define i32 @_METH_strlen( [1024 x i8]* %given_str ) {");
        out.println("entry:");
        out.println("    %0 = bitcast [1024 x i8]* %given_str to i8*");
        out.println("    %1 = call i64 @strlen(i8* %0)");
        out.println("    %retval = trunc i64 %1 to i32");
        out.println("ret i32 %retval");
        out.println("}\n");

        // String function: Concatenate
        // Need to return a new string in entirety, not just concatenate to given
        out.println("define [1024 x i8]* @_METH_strcat( [1024 x i8]* %string_1, [1024 x i8]* %string_2 ) {");
        out.println("entry:");
        out.println("    %0 = bitcast [1024 x i8]* %string_1 to i8*");
        out.println("    %1 = bitcast [1024 x i8]* %string_2 to i8*");
        out.println("    %2 = call i8* @malloc(i64 1024)");
        out.println("    %3 = call i8* @strcpy(i8* %2, i8* %0)");
        out.println("    %4 = call i8* @strcat(i8* %3, i8* %1)");
        out.println("    %retval = bitcast i8* %4 to [1024 x i8]*");
        out.println("ret [1024 x i8]* %retval");
        out.println("}\n");

        // String function: Substring
        // Need to return a new string here also. 
        out.println("define [1024 x i8]* @_METH_strsubstr( [1024 x i8]* %string_1, i32 %beg, i32 %n ) {");
        out.println("entry:");
        out.println("    %0 = call i8* @malloc(i64 1024)");
        out.println("    %1 = getelementptr inbounds [1024 x i8], [1024 x i8]* %string_1, i32 0, i32 %beg");
        out.println("    %2 = call i8* @strncpy(i8* %0, i8* %1, i32 %n)");
        out.println("    %retval = bitcast i8* %2 to [1024 x i8]*");
        out.println("ret [1024 x i8]* %retval");
        out.println("}\n");

        // String function: Copy
        // Need to return a new string which is a copy of the original
        out.println("define [1024 x i8]* @_METH_strcopy( [1024 x i8]* %given_str ) {");
        out.println("entry:");
        out.println("    %0 = call i8* @malloc(i64 1024)");
        out.println("    %1 = bitcast [1024 x i8]* %given_str to i8*");
        out.println("    %2 = call i8* @strcpy(i8* %0, i8* %2)");
        out.println("    %retval = bitcast i8* %2 to [1024 x i8]*");
        out.println("ret [1024 x i8]* %retval");
        out.println("}\n");
    }

    private void printIO(PrintWriter out) {
        // Emit LLVM-IR for IO functions
        // in_int, in_string, out_int, out_string
        // Will require C functions like scanf and printf
        out.println("declare i32 @scanf(i8*, ...)");
        out.println("declare i32 @printf(i8*, ...)");
        out.println("@percentD = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1");
        out.println("@percentS = private unnamed_addr constant [3 x i8] c\"%s\\00\", align 1");

        // IO function: In_int
        out.println("define i32 @_METH_ioin_int( %class.IO* %my_IO_class ) {");
        out.println("entry:");
        out.println("    %0 = call i8* @malloc(i64 4)");
        out.println("    %1 = bitcast i8* %0 to i32*");
        out.println("    %2 = call i32 (i8*, ...) @scanf(i8* bitcast ( [3 x i8]* @percentD to i8* ), i32* %1)");
        out.println("    %retval = load i32, i32* %1");
        out.println("ret i32 %retval");
        out.println("}\n");

        // IO function: In_string
        out.println("define [1024 x i8]* @_METH_ioin_string( %class.IO* %my_IO_class ) {");
        out.println("entry:"); 
        out.println("    %0 = call i8* @malloc(i64 1024)");
        out.println("    %1 = bitcast i8* %0 to [1024 x i8]*");
        out.println("    %2 = call i32 (i8*, ...) @scanf(i8* bitcast ( [3 x i8]* @percentS to i8* ), [1024 x i8]* %1)");
        out.println("ret [1024 x i8]* %1");
        out.println("}\n");

        // IO function: Out_int
        // Return value is number of bytes written
        out.println("define i32 @_METH_ioout_int( i32 %cur_number ) {");
        out.println("entry:");
        out.println("    %0 = call i32 (i8*, ...) @printf(i8* bitcast ( [3 x i8]* @percentD to i8* ), i32 %cur_number)");
        out.println("ret i32 %0");
        out.println("}\n");

        // IO function: Out_string
        // Return value is number of bytes written
        out.println("define i32 @_METH_ioout_string( [1024 x i8]* %cur_string) {");
        out.println("entry:");
        out.println("    %0 = call i32 (i8*, ...) @printf(i8* bitcast ( [3 x i8]* @percentS to i8* ), [1024 x i8]* %cur_string)");
        out.println("ret i32 %0");
        out.println("}\n");
    }
}
