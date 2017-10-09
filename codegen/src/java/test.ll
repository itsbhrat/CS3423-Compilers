; I am a comment in LLVM-IR. Feel free to remove me.
%class.Main = type { i32, i8*, i1 }
define void @Main_Cons_Main( %class.Main* %this ) {
entry:
	%this.addr = alloca %class.Main*
	store %class.Main* %this, %class.Main** %this.addr
	%this1 = load %class.Main*, %class.Main** %this.addr
	%i = getelementptr inbounds %class.Main, %class.Main* %this1, i32 0, i32 0
	store i32 0, i32* %i
}
define i8* @Main_foo( %class.Main* %this, i32 %a, i1 %b, i8* %c ) {
entry:
}
define i32 @Main_main( %class.Main* %this ) {
entry:
}
define %class.Object @Main_bar( %class.Main* %this, i8* %e, i8* %f, i1 %g, i32 %h, i32 %l, i1 %m ) {
entry:
}
%class.Object = type {  }
define void @Object_Cons_Object( %class.Object* %this ) {
entry:
	%this.addr = alloca %class.Object*
	store %class.Object* %this, %class.Object** %this.addr
	%this1 = load %class.Object*, %class.Object** %this.addr
}
define %class.Object @Object_abort( %class.Object* %this ) {
entry:
}
define i8* @Object_type_name( %class.Object* %this ) {
entry:
}
define %class.Object @Object_copy( %class.Object* %this ) {
entry:
}
%class.IO = type {  }
define void @IO_Cons_IO( %class.IO* %this ) {
entry:
	%this.addr = alloca %class.IO*
	store %class.IO* %this, %class.IO** %this.addr
	%this1 = load %class.IO*, %class.IO** %this.addr
}
define %class.IO @IO_out_string( %class.IO* %this, i8* %x ) {
entry:
}
define %class.IO @IO_out_int( %class.IO* %this, i32 %x ) {
entry:
}
define i8* @IO_in_string( %class.IO* %this ) {
entry:
}
define i32 @IO_in_int( %class.IO* %this ) {
entry:
}
%class.Int = type {  }
define void @Int_Cons_Int( %class.Int* %this ) {
entry:
	%this.addr = alloca %class.Int*
	store %class.Int* %this, %class.Int** %this.addr
	%this1 = load %class.Int*, %class.Int** %this.addr
}
%class.String = type {  }
define void @String_Cons_String( %class.String* %this ) {
entry:
	%this.addr = alloca %class.String*
	store %class.String* %this, %class.String** %this.addr
	%this1 = load %class.String*, %class.String** %this.addr
}
define i32 @String_length( %class.String* %this ) {
entry:
}
define i8* @String_concat( %class.String* %this, i8* %s ) {
entry:
}
define i8* @String_substr( %class.String* %this, i32 %i, i32 %l ) {
entry:
}
%class.Bool = type {  }
define void @Bool_Cons_Bool( %class.Bool* %this ) {
entry:
	%this.addr = alloca %class.Bool*
	store %class.Bool* %this, %class.Bool** %this.addr
	%this1 = load %class.Bool*, %class.Bool** %this.addr
}
