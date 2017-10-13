package cool;

enum OpTypeId {
  EMPTY, VOID, INT1, INT1_PTR, INT1_PPTR, INT8, INT8_PTR, INT8_PPTR,
  INT32, INT32_PTR, INT32_PPTR, VAR_ARG,
  OBJ, OBJ_PTR, OBJ_PPTR
}


/* Mapping Data Types & Data Type Names */
class OpType {

  protected OpTypeId id;
  protected String  name;

  OpType() {
    id = OpTypeId.EMPTY;
    name = "";
  }
  OpType(OpTypeId i) {
    id = i;
    switch (id) {
    case EMPTY:
      name = "";
      break;
    case VOID:
      name = "void";
      break;
    case INT1:
      name = "i1";
      break;
    case INT8:
      name = "i8";
      break;
    case INT32:
      name = "i32";
      break;
    case INT1_PTR:
      name = "i1*";
      break;
    case INT8_PTR:
      name = "i8*";
      break;
    case INT32_PTR:
      name = "i32*";
      break;
    case INT1_PPTR:
      name = "i1**";
      break;
    case INT8_PPTR:
      name = "i8**";
      break;
    case INT32_PPTR:
      name = "i32**";
      break;
    case VAR_ARG:
      name = "...";
      break;
    case OBJ:
    case OBJ_PTR:
    case OBJ_PPTR:
      break;
    default:
      assert false : "Variable type not implemented";
    }
  }
  //user defined type id are of type OBJ
  OpType(String n) {
    id = OpTypeId.OBJ;
    name = "%" + n;
  }
  //user defined types and single/double pointers
  OpType(String n, int ptr_level) {
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
      assert false : "Invalid pointer level";
  }
  
  OpTypeId getId() {
    return id;
  }
  void setId(OpTypeId i) {
    id = i;
  }
  String getName() {
    return name;
  }
  boolean isPtr() {
    return ((id == OpTypeId.INT1_PTR) || (id == OpTypeId.INT8_PTR) ||
            (id == OpTypeId.INT32_PTR) || (id == OpTypeId.OBJ_PTR));
  }
  OpType getPtrType() {
    OpTypeId ptrId = OpTypeId.EMPTY;
    switch (id) {
    case INT1:
      ptrId = OpTypeId.INT1_PTR;
      break;
    case INT8:
      ptrId = OpTypeId.INT8_PTR;
      break;
    case INT32:
      ptrId = OpTypeId.INT32_PTR;
      break;
    case INT1_PTR:
      ptrId = OpTypeId.INT1_PPTR;
      break;
    case INT8_PTR:
      ptrId = OpTypeId.INT8_PPTR;
      break;
    case INT32_PTR:
      ptrId = OpTypeId.INT32_PPTR;
      break;
    case OBJ:
      ptrId = OpTypeId.OBJ_PTR;
      break;
    case OBJ_PTR:
      ptrId = OpTypeId.OBJ_PPTR;
      break;
    default:
      assert false : "getPtrType(): Type unsupported";
    }
    if (ptrId == OpTypeId.OBJ_PTR || ptrId == OpTypeId.OBJ_PPTR) {
      OpType newType = new OpType(name.substring(1), 1);  //remove the % from the name
      newType.setId(ptrId);
      return newType;
    } else {
      return (new OpType(ptrId));
    }
  }

  OpType getDerefPtrType() {
    OpTypeId derefId = OpTypeId.EMPTY;
    switch (id) {
    case INT1_PTR:
      derefId = OpTypeId.INT1;
      break;
    case INT8_PTR:
      derefId = OpTypeId.INT8;
      break;
    case INT32_PTR:
      derefId = OpTypeId.INT32;
      break;
    case INT1_PPTR:
      derefId = OpTypeId.INT1_PTR;
      break;
    case INT8_PPTR:
      derefId = OpTypeId.INT8_PTR;
      break;
    case INT32_PPTR:
      derefId = OpTypeId.INT32_PTR;
      break;
    case OBJ_PTR:
      derefId = OpTypeId.OBJ;
      break;
    case OBJ_PPTR:
      derefId = OpTypeId.OBJ_PTR;
      break;
    default:
      assert false : "get_deref_type(): Cannot get type after dereferencing";
    }
    if (derefId == OpTypeId.OBJ || derefId == OpTypeId.OBJ_PTR) {
      OpType newType = new OpType(name.substring(1, name.length() - 1));  //remove % and last * from name
      newType.setId(derefId);
      return newType;
    } else {
      return (new OpType(derefId));
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
  protected String name;
  Operand() {
    type = new OpType(OpTypeId.EMPTY);
    name = "";
  }
  Operand(OpType t, String n) {
    type = t;
    name = "%" + n;
  }
  OpType getType() {
    return type;
  }
  void setType(OpType t) {
    type = t;
  }
  String getTypename() {
    return type.getName();
  }
  String getOriginalName() {
    return name.substring(1);   
  }
  String getName() {
    return name;
  }
}

class GlobalValue extends Operand {
  private Operand value;
  GlobalValue(OpType t, String n, Operand v) {
    type = t;
    name = "@" + n;
    value = v;
  }
  GlobalValue(OpType t, String n) {
    type = t;
    name = "@" + n;
  }
  Operand getValue() {
    return value;
  }
}
class ConstValue extends Operand {
  protected String value;
  ConstValue(OpType t, String val) {
    value = val;
    type = t;
    name = val;
  }
  String getValue() {
    return value;
  }
}
class IntValue extends ConstValue {
  private int iValue;
  IntValue(int i) {
    super(new OpType(OpTypeId.INT32), String.valueOf(i));
    iValue = i;
  }
  int getIntValue() {
    return iValue;
  }
}
class BoolValue extends ConstValue {
  private boolean bValue;
  BoolValue(boolean b) {
    super(new OpType(OpTypeId.INT1), "");
    bValue = b;
    if(b)
      value = "true";
    else
      value = "false";
    name = value;
  }
  boolean getBoolValue() {
    return bValue;
  }
}
