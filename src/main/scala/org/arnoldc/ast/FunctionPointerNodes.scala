package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.arnoldc.SymbolTable

/**
 * Function Pointer AST Nodes for ArnoldC Native
 * "REMEMBER THIS MOVE" - Callbacks and indirect calls for kernel programming
 */

// Function pointer declaration - "REMEMBER THIS MOVE"
case class FunctionPointerDeclareNode(
  variable: String,
  returnType: TypeSpec,
  paramTypes: List[TypeSpec]
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Function pointer assignment - "LEARN THE MOVE"
case class FunctionPointerAssignNode(
  fptrVar: String,
  functionName: String
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Function pointer call - "USE THAT MOVE"
case class FunctionPointerCallNode(
  fptrVar: String,
  arguments: List[AstNode],
  assignTo: Option[String] = None
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Function pointer as expression (for passing as argument)
case class FunctionPointerExprNode(functionName: String) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Null function pointer check
case class IsFunctionNullNode(fptrVar: String) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Array of function pointers (e.g., for interrupt vector table)
case class FunctionPointerArrayNode(
  variable: String,
  returnType: TypeSpec,
  paramTypes: List[TypeSpec],
  size: Int
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Typedef for function pointer type
case class FunctionPointerTypedefNode(
  typeName: String,
  returnType: TypeSpec,
  paramTypes: List[TypeSpec]
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Callback registration (common pattern in kernel code)
case class RegisterCallbackNode(
  callbackTable: String,
  index: AstNode,
  functionName: String
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Call through callback table
case class InvokeCallbackNode(
  callbackTable: String,
  index: AstNode,
  arguments: List[AstNode],
  assignTo: Option[String] = None
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// External function declaration (for linking)
case class ExternFunctionNode(
  functionName: String,
  returnType: TypeSpec,
  paramTypes: List[TypeSpec]
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Static function (file-local visibility)
case class StaticFunctionAttributeNode()

// Inline function hint
case class InlineFunctionAttributeNode()

// No-return function attribute (for panic, etc.)
case class NoReturnAttributeNode()

// Section attribute (for placing code in specific memory regions)
case class SectionAttributeNode(sectionName: String)

// Aligned attribute (for function alignment requirements)
case class AlignedFunctionAttributeNode(alignment: Int)

// Interrupt handler attribute
case class InterruptAttributeNode(irqNum: Int)

// Method with attributes
case class AttributedMethodNode(
  name: String,
  args: List[VariableNode],
  returnType: TypeSpec,
  attributes: List[AstNode],  // List of attribute nodes
  statements: List[StatementNode]
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}
