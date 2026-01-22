package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.arnoldc.SymbolTable

/**
 * Struct and Union AST Nodes for ArnoldC Native
 * "THIS IS WHAT I AM" - Data structure definitions
 */

// Struct member definition
case class StructMember(name: String, memberType: TypeSpec)

// Struct definition - "THIS IS WHAT I AM"
case class StructDefNode(
  structName: String,
  members: List[StructMember]
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
  
  def toCType: String = {
    val memberDefs = members.map { m =>
      s"    ${m.memberType.toCType} ${m.name};"
    }.mkString("\n")
    s"struct $structName {\n$memberDefs\n}"
  }
  
  def size: Int = members.map(_.memberType.size).sum
}

// Union definition - "THIS IS MY DISGUISE"
case class UnionDefNode(
  unionName: String,
  members: List[StructMember]
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
  
  def toCType: String = {
    val memberDefs = members.map { m =>
      s"    ${m.memberType.toCType} ${m.name};"
    }.mkString("\n")
    s"union $unionName {\n$memberDefs\n}"
  }
  
  def size: Int = members.map(_.memberType.size).max
}

// Struct variable declaration - "CREATE ONE LIKE"
case class StructDeclareNode(
  variable: String,
  structName: String,
  initialValues: Option[Map[String, AstNode]] = None
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Union variable declaration
case class UnionDeclareNode(
  variable: String,
  unionName: String
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Struct member access - "WHAT'S YOUR"
case class StructMemberAccessNode(
  structVar: String,
  memberName: String
) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Struct member access via pointer - "SHOW ME THE"
case class StructPointerAccessNode(
  pointerVar: String,
  memberName: String
) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Struct member assignment - direct
case class StructMemberAssignNode(
  structVar: String,
  memberName: String,
  value: AstNode
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Struct member assignment via pointer
case class StructPointerAssignNode(
  pointerVar: String,
  memberName: String,
  value: AstNode
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Nested struct access - chained member access
case class NestedMemberAccessNode(
  baseVar: String,
  memberPath: List[String]  // e.g., ["outer", "inner", "field"]
) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Anonymous struct inline definition
case class AnonymousStructNode(
  members: List[StructMember]
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Packed struct attribute (for precise memory layout)
case class PackedStructDefNode(
  structName: String,
  members: List[StructMember]
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
  
  def toCType: String = {
    val memberDefs = members.map { m =>
      s"    ${m.memberType.toCType} ${m.name};"
    }.mkString("\n")
    s"struct __attribute__((packed)) $structName {\n$memberDefs\n}"
  }
}

// Bitfield member for structs
case class BitfieldMember(name: String, baseType: TypeSpec, bits: Int)

case class BitfieldStructDefNode(
  structName: String,
  members: List[Either[StructMember, BitfieldMember]]
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}
