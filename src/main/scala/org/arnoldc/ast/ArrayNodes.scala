package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.arnoldc.SymbolTable

/**
 * Array Operation AST Nodes for ArnoldC Native
 * "LINE THEM UP" - Array manipulation for systems programming
 */

// Array declaration - "LINE THEM UP"
case class ArrayDeclareNode(
  variable: String,
  elementType: TypeSpec,
  size: Int,
  initialValues: Option[List[AstNode]] = None
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Array declaration with computed size
case class DynamicArrayDeclareNode(
  variable: String,
  elementType: TypeSpec,
  sizeExpr: AstNode
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Array element access - "WHICH ONE"
case class ArrayAccessNode(
  arrayVar: String,
  index: AstNode
) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Multi-dimensional array access
case class MultiArrayAccessNode(
  arrayVar: String,
  indices: List[AstNode]
) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Array element assignment
case class ArrayAssignNode(
  arrayVar: String,
  index: AstNode,
  value: AstNode
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Multi-dimensional array assignment
case class MultiArrayAssignNode(
  arrayVar: String,
  indices: List[AstNode],
  value: AstNode
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Array initializer - "PUT THEM IN LINE"
case class ArrayInitializerNode(values: List[AstNode]) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Nested array initializer for multi-dimensional arrays
case class NestedArrayInitializerNode(
  values: List[ArrayInitializerNode]
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// String literal as char array
case class StringArrayNode(value: String) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
  
  def toCharArray: List[Int] = value.map(_.toInt).toList :+ 0  // null-terminated
}

// Array length expression
case class ArrayLengthNode(arrayVar: String) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Array pointer decay - array name as pointer to first element
case class ArrayPointerNode(arrayVar: String) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Array slice (for future extension)
case class ArraySliceNode(
  arrayVar: String,
  start: AstNode,
  end: AstNode
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Array of structs declaration
case class StructArrayDeclareNode(
  variable: String,
  structName: String,
  size: Int
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Static array (global/file scope)
case class StaticArrayDeclareNode(
  variable: String,
  elementType: TypeSpec,
  size: Int,
  initialValues: Option[List[AstNode]] = None
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Extern array declaration
case class ExternArrayDeclareNode(
  variable: String,
  elementType: TypeSpec
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}
