class TypeIdCaseInsEnsItIVeNaMe inherits IO {
    -- in this type Id , name starts with upper case
    a : Int <- ~0045;
    a : Bool <- true;   -- type Id starts with upper case
    a : Bool <- True;
    a : Bool <- NOT True;
	main():IO {
	    -- object Id starts with lower case
		out_string("Hello world");
	};
	objectIdCaseInsEnsItIVeNaMe():typeId {
	    -- object Id starts with lower case
	};
};

