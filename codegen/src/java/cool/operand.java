package cool;

enum OpTypeId {
	EMPTY, VOID, INT1, INT1_PTR, INT1_PPTR, INT8, INT8_PTR, INT8_PPTR,
	INT32, INT32_PTR, INT32_PPTR, VAR_ARG,
	OBJ, OBJ_PTR, OBJ_PPTR
}


/* Mapping Data Types & Data Type Names */
class OpType {

	protected OpTypeId id;
	protected string  name;

	OpType() {
		id = OpTypeId.EMPTY;
		name = "";
	}
	OpType(OpTypeId i) {
		id = i;
		switch (id) {
		case OpTypeId.EMPTY:
			name = "";
			break;
		case OpTypeId.VOID:
			name = "void";
			break;
		case OpTypeId.INT1:
			name = "i1";
			break;
		case OpTypeId.INT8:
			name = "i8";
			break;
		case OpTypeId.INT32:
			name = "i32";
			break;
		case OpTypeId.INT1_PTR:
			name = "i1*";
			break;
		case OpTypeId.INT8_PTR:
			name = "i8*";
			break;
		case OpTypeId.INT32_PTR:
			name = "i32*";
			break;
		case OpTypeId.INT1_PPTR:
			name = "i1**";
			break;
		case OpTypeId.INT8_PPTR:
			name = "i8**";
			break;
		case OpTypeId.INT32_PPTR:
			name = "i32**";
			break;
		case OpTypeId.VAR_ARG:
			name = "...";
			break;
		case OpTypeId.OBJ:
		case OpTypeId.OBJ_PTR:
		case OpTypeId.OBJ_PPTR:
			break;
		default:
			assert 0 : "Variable type not implemented";
		}
	}
	//user defined type id are of type OBJ
	OpType(string n) {
		id = OpTypeId.OBJ;
		name = "%" + n;
	}
	//user defined types and single/double pointers
	OpType(string n, int ptr_level) {
		name = "%" + n;
		id = OpTypeId.OBJ;
		// Pointer to an object
		if (ptr_level == 1) {
			name += "*";
			id = OpTypeId.OBJ_PTR;
		}
		// Pointer to a pointer to an object;
		if (ptr_level == 2) {
			id = OpTypeId.OBJ_PPTR;
			name += "**";
		}
		if (ptr_level > 2 || ptr_level < 0)
			assert 0 : "Invalid pointer level";
	}
	OpTypeId getId() {
		return id;
	}
	OpTypeId setId(OpTypeId i) {
		id = i;
	}
	string getName() {
		return name;
	}
	boolean isPtr() {
		return ((id == OpTypeId.INT1_PTR) || (id == OpTypeId.INT8_PTR) ||
		        (id == OpTypeId.INT32_PTR) || (id == OpTypeId.OBJ_PTR));
	}
	OpType getPtrType() {
		OpTypeId ptrId;
		switch (id) {
		case OpTypeId.INT1:
			ptr_id = OpTypeId.INT1_PTR;
			break;
		case OpTypeId.INT8:
			ptr_id = OpTypeId.INT8_PTR;
			break;
		case OpTypeId.INT32:
			ptr_id = OpTypeId.INT32_PTR;
			break;
		case OpTypeId.INT1_PTR:
			ptr_id = OpTypeId.INT1_PPTR;
			break;
		case OpTypeId.INT8_PTR:
			ptr_id = OpTypeId.INT8_PPTR;
			break;
		case OpTypeId.INT32_PTR:
			ptr_id = OpTypeId.INT32_PPTR;
			break;
		case OpTypeId.OBJ:
			ptr_id = OpTypeId.OBJ_PTR;
			break;
		case OpTypeId.OBJ_PTR:
			ptr_id = OpTypeId.OBJ_PPTR;
			break;
		default:
			assert 0 : "getPtrType(): Type unsupported";
		}
		if (ptrId == OpTypeId.OBJ_PTR || ptrId == OpTypeId.OBJ_PPTR) {
			OpType newType = new OpType(name.substring(1), 1);	//remove the % from the name
			newType.setId(ptrId);
			return newType;
		} else {
			return OpType(ptrId);
		}
	}

	OpType getDerefPtrType() {
		OpTypeId derefId;
		switch (id) {
		case OpTypeId.INT1_PTR:
			derefId = OpTypeId.INT1;
			break;
		case OpTypeId.INT8_PTR:
			derefId = OpTypeId.INT8;
			break;
		case OpTypeId.INT32_PTR:
			derefId = OpTypeId.INT32;
			break;
		case OpTypeId.INT1_PPTR:
			derefId = OpTypeId.INT1_PTR;
			break;
		case OpTypeId.INT8_PPTR:
			derefId = OpTypeId.INT8_PTR;
			break;
		case OpTypeId.INT32_PPTR:
			derefId = OpTypeId.INT32_PTR;
			break;
		case OpTypeId.OBJ_PTR:
			derefId = OpTypeId.OBJ;
			break;
		case OpTypeId.OBJ_PPTR:
			derefId = OpTypeId.OBJ_PTR;
			break;
		default:
			assert 0 : "get_deref_type(): Cannot get type after dereferencing";
		}
		if (derefId == OpTypeId.OBJ || derefId == OpTypeId.OBJ_PTR) {
			opType newType = new OpType(name.substring(1, name.length() - 1));	//remove % and last * from name
			newType.setId(derefId);
			return newType;
		} else {
			return OpType(derefId);
		}
	}
	boolean isPptr() {
		return ((id == OpTypeId.INT1_PPTR) || (id == OpTypeId.INT8_PPTR) ||
		        (id == OpTypeId.INT32_PPTR) || (id == OpTypeId.OBJ_PPTR));
	}
	boolean isIntObject() {
		return ((id == OpTypeId.OBJ_PTR) && name.equals("%Int*"));
	}
	boolean isBoolObject() {
		return ((id == OpTypeId.OBJ_PTR) && name.equals("%Bool*"));
	}
	boolean isStringObject() {
		return ((id == OpTypeId.OBJ_PTR) && name.equals("%String*"));
	}
	boolean isSameWith(OpType t) {
		return name.equals(t.getName());
	}
}


class Operand {
	protected OpType type;
	protected string name;
	Operand() {
		type = OpTypeId.EMPTY;
		name = "";
	}
	Operand(OpType t, string n) {
		type = t;
		name = "%" + n;
	}
	OpType getType() {
		return type;
	}
	void setType(OpType t) {
		type = t;
	}
	string getTypeName() {
		return OpType.getName();
	}
	string getName() {
		return name;
	}
}

class GlobalValue extends Operand {
	private Operand value;
	GlobalValue(OpType t, string n, Operand v) {
		type = t;
		name = "@" + n;
		value = v;
	}
	GlobalValue(OpType t, string n) {
		type = t;
		name = "@" + n;
	}
	Operand getValue() {
		return value;
	}
}
class ConstValue extends Operand {
	protected string value;
	ConstValue(OpType t, string val) {
		value = val;
		type = t;
		name = val;
	}
	string getValue() {
		return value;
	}
}
class CasedtValue extends ConstValue {
	protected OpType precastType;
	CasedtValue(OpType t, string val, OpType precast) {
		super(t, "bitcast " + precast.getName() + " " + val + "to " + t.getName());
		precastType = precast;
	}
	string getPrecastType() {
		return precastType;
	}	
}
class IntValue extends ConstValue {
	private int iValue;
	IntValue(int i) {
		super(OpType(OpTypeId.INT32), String.valueOf(i));
		iValue = i;
	}
	int getIntValue() {
		return iValue;
	}
}
class BoolValue extends ConstValue {
	private boolean bValue;
	BoolValue(boolean b) {
		super(OpType(OpTypeId.INT1), "");
		bValue = b;
		if(b)
			value = "true";
		else
			value = "false";
		name = value;
	}
	int getBoolValue() {
		return bValue;
	}
}

class NullValue extends ConstValue {
	NullValue(OpType t) {
		super(t, "null");
	}
}
