package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.arnoldc.SymbolTable

/**
 * Type System AST Nodes for ArnoldC Native
 * "THIS IS WHAT I AM" - Extended type system for systems programming
 */

// Base trait for all type specifications
sealed trait TypeSpec {
  def toCType: String
  def size: Int  // Size in bytes
  def isSigned: Boolean
}

// Primitive types - "THE WARRIORS AND ENEMIES"
case object U8Type extends TypeSpec {
  val toCType = "uint8_t"
  val size = 1
  val isSigned = false
}

case object U16Type extends TypeSpec {
  val toCType = "uint16_t"
  val size = 2
  val isSigned = false
}

case object U32Type extends TypeSpec {
  val toCType = "uint32_t"
  val size = 4
  val isSigned = false
}

case object U64Type extends TypeSpec {
  val toCType = "uint64_t"
  val size = 8
  val isSigned = false
}

case object I8Type extends TypeSpec {
  val toCType = "int8_t"
  val size = 1
  val isSigned = true
}

case object I16Type extends TypeSpec {
  val toCType = "int16_t"
  val size = 2
  val isSigned = true
}

case object I32Type extends TypeSpec {
  val toCType = "int32_t"
  val size = 4
  val isSigned = true
}

case object I64Type extends TypeSpec {
  val toCType = "int64_t"
  val size = 8
  val isSigned = true
}

case object VoidType extends TypeSpec {
  val toCType = "void"
  val size = 0
  val isSigned = false
}

case object CharType extends TypeSpec {
  val toCType = "char"
  val size = 1
  val isSigned = true
}

// Default int type (backward compatibility)
case object IntType extends TypeSpec {
  val toCType = "int"
  val size = 4
  val isSigned = true
}

// Pointer type - "POINT YOUR GUN AT"
case class PointerType(baseType: TypeSpec) extends TypeSpec {
  val toCType = s"${baseType.toCType}*"
  val size = 4  // 32-bit pointers for now
  val isSigned = false
}

// Array type - "LINE THEM UP"
case class ArrayType(elementType: TypeSpec, length: Int) extends TypeSpec {
  val toCType = s"${elementType.toCType}[$length]"
  val size = elementType.size * length
  val isSigned = false
}

// Struct type reference - "CREATE ONE LIKE"
case class StructRefType(structName: String) extends TypeSpec {
  val toCType = s"struct $structName"
  val size = 0  // Determined later from struct definition
  val isSigned = false
}

// Union type reference
case class UnionRefType(unionName: String) extends TypeSpec {
  val toCType = s"union $unionName"
  val size = 0
  val isSigned = false
}

// Enum type reference
case class EnumRefType(enumName: String) extends TypeSpec {
  val toCType = s"enum $enumName"
  val size = 4
  val isSigned = false
}

// Function pointer type - "REMEMBER THIS MOVE"
case class FunctionPointerType(
  returnType: TypeSpec,
  paramTypes: List[TypeSpec]
) extends TypeSpec {
  val toCType = {
    val params = if (paramTypes.isEmpty) "void" 
                 else paramTypes.map(_.toCType).mkString(", ")
    s"${returnType.toCType}(*)($params)"
  }
  val size = 4  // 32-bit function pointers
  val isSigned = false
}

// Type modifiers - "THIS IS ALWAYS THE SAME" / "THIS COULD CHANGE ANYTIME"
case class ConstType(baseType: TypeSpec) extends TypeSpec {
  val toCType = s"const ${baseType.toCType}"
  val size = baseType.size
  val isSigned = baseType.isSigned
}

case class VolatileType(baseType: TypeSpec) extends TypeSpec {
  val toCType = s"volatile ${baseType.toCType}"
  val size = baseType.size
  val isSigned = baseType.isSigned
}

// Type declaration node for variable declarations with explicit types
case class TypedDeclareNode(
  variable: String,
  varType: TypeSpec,
  initialValue: AstNode
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    // JVM generation not supported for typed variables
  }
}

// Type cast expression - "MAKE IT A"
case class TypeCastNode(targetType: TypeSpec, expr: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Sizeof expression - "HOW BIG IS"
case class SizeofTypeNode(targetType: TypeSpec) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Sizeof variable expression - "HOW BIG IS THAT"
case class SizeofExprNode(variable: String) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Hex number literal for low-level programming
case class HexNumberNode(value: Long) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    mv.visitLdcInsn(value.toInt)
  }
}

// Character literal - single quoted
case class CharLiteralNode(value: Char) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    mv.visitLdcInsn(value.toInt)
  }
}
