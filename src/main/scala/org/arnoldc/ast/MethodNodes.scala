package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

abstract class AbstractMethodNode extends AstNode {
  val statements: List[StatementNode]
  val arguments: List[VariableNode]
  val methodName: String
  val returnsValue: Boolean

  def signature = MethodSignature(methodName, arguments, returnsValue)
}

case class MethodSignature(name: String, args: List[VariableNode], returnsValue: Boolean)

case class MainMethodNode(statements: List[StatementNode]) extends AbstractMethodNode {
  val methodName: String = "main"
  val arguments = Nil
  val returnsValue = false

  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    mv.visitCode()
    statements.foreach(_.generate(mv, symbolTable))
    mv.visitInsn(RETURN)
    mv.visitMaxs(100, 100)
    mv.visitEnd()
  }
}

case class MethodNode(
  methodName: String, 
  arguments: List[VariableNode], 
  returnsValue: Boolean, 
  statements: List[StatementNode]
) extends AbstractMethodNode {

  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    val methodSymbolTable = new SymbolTable(Some(symbolTable), methodName)
    mv.visitCode()
    arguments.foreach { a =>
      methodSymbolTable.putVariable(a.variableName)
    }
    statements.foreach(_.generate(mv, methodSymbolTable))
    if (!returnsValue) {
      mv.visitInsn(RETURN)
    } else {
      mv.visitInsn(ICONST_1)
      mv.visitInsn(IRETURN)
    }
    mv.visitMaxs(100, 100)
    mv.visitEnd()
  }
}
