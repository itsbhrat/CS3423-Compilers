; I am a comment in LLVM-IR. Feel free to remove me.
%class.Main = type { i32, i8*, i1 }
define i8* @Main_foo( %class.Main* %this, i32 %a, i1 %b, i8* %c) {
}
define i32 @Main_main( %class.Main* %this) {
}
define %class.Object* @Main_bar( %class.Main* %this, i8* %e, i8* %f, i1 %g, i32 %h, i32 %l, i1 %m) {
}
%class.Object = type {  }
define %class.Object* @Object_abort( %class.Object* %this) {
}
define i8* @Object_type_name( %class.Object* %this) {
}
define %class.Object* @Object_copy( %class.Object* %this) {
}
%class.IO = type {  }
define %class.IO* @IO_out_string( %class.IO* %this, i8* %x) {
}
define %class.IO* @IO_out_int( %class.IO* %this, i32 %x) {
}
define i8* @IO_in_string( %class.IO* %this) {
}
define i32 @IO_in_int( %class.IO* %this) {
}
%class.Int = type {  }
%class.String = type {  }
define i32 @String_length( %class.String* %this) {
}
define i8* @String_concat( %class.String* %this, i8* %s) {
}
define i8* @String_substr( %class.String* %this, i32 %i, i32 %l) {
}
%class.Bool = type {  }
