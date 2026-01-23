package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.arnoldc.SymbolTable

/**
 * Enum AST Nodes for ArnoldC Native
 * "THESE ARE MY OPTIONS" - Enumeration support for systems programming
 */

// Enum value with optional explicit value
case class EnumValue(name: String, value: Option[Int] = None)

// Enum definition - "THESE ARE MY OPTIONS"
case class EnumDefNode(
  enumName: String,
  values: List[EnumValue],
  baseType: TypeSpec = U32Type  // Default to uint32_t
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
  
  def toCType: String = {
    val valuesDef = values.zipWithIndex.map { case (v, idx) =>
      v.value match {
        case Some(explicit) => s"    ${v.name} = $explicit"
        case None => s"    ${v.name}"
      }
    }.mkString(",\n")
    s"enum $enumName {\n$valuesDef\n}"
  }
  
  // Calculate values (handling explicit and auto-increment)
  def resolvedValues: Map[String, Int] = {
    var currentValue = 0
    values.map { v =>
      val resolved = v.value.getOrElse(currentValue)
      currentValue = resolved + 1
      v.name -> resolved
    }.toMap
  }
}

// Enum variable declaration
case class EnumDeclareNode(
  variable: String,
  enumName: String,
  initialValue: Option[String] = None  // Enum value name
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Enum value access - getting the integer value of an enum constant
case class EnumValueAccessNode(
  enumName: String,
  valueName: String
) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Bitfield enum (flags) - for things like page table flags
case class FlagsEnumDefNode(
  enumName: String,
  values: List[EnumValue]  // Values should be powers of 2
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
  
  def toCType: String = {
    val valuesDef = values.map { v =>
      v.value match {
        case Some(explicit) => s"    ${v.name} = 0x${explicit.toHexString.toUpperCase}"
        case None => s"    ${v.name}"
      }
    }.mkString(",\n")
    s"enum $enumName {\n$valuesDef\n}"
  }
}
