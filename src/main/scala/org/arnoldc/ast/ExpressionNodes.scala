package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

// Operand nodes
case class NumberNode(value: Int) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    mv.visitLdcInsn(value)
  }
}

case class VariableNode(variableName: String) extends OperandNode {
  def name = variableName
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    mv.visitVarInsn(ILOAD, symbolTable.getVariableAddress(variableName))
  }
}

case class StringNode(value: String) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    mv.visitLdcInsn(value)
  }
}

// Arithmetic expression nodes
case class PlusExpressionNode(left: AstNode, right: OperandNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitInsn(IADD)
  }
}

case class MinusExpressionNode(left: AstNode, right: OperandNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitInsn(ISUB)
  }
}

case class MultiplicationExpressionNode(left: AstNode, right: OperandNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitInsn(IMUL)
  }
}

case class DivisionExpressionNode(left: AstNode, right: OperandNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitInsn(IDIV)
  }
}

case class ModuloExpressionNode(left: AstNode, right: OperandNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitInsn(IREM)
  }
}

// Logical expression nodes
case class EqualToNode(left: AstNode, right: OperandNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    val trueLabel = new org.objectweb.asm.Label()
    val endLabel = new org.objectweb.asm.Label()
    
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitJumpInsn(IF_ICMPEQ, trueLabel)
    mv.visitInsn(ICONST_0)
    mv.visitJumpInsn(GOTO, endLabel)
    mv.visitLabel(trueLabel)
    mv.visitInsn(ICONST_1)
    mv.visitLabel(endLabel)
  }
}

case class GreaterThanNode(left: AstNode, right: OperandNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    val trueLabel = new org.objectweb.asm.Label()
    val endLabel = new org.objectweb.asm.Label()
    
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitJumpInsn(IF_ICMPGT, trueLabel)
    mv.visitInsn(ICONST_0)
    mv.visitJumpInsn(GOTO, endLabel)
    mv.visitLabel(trueLabel)
    mv.visitInsn(ICONST_1)
    mv.visitLabel(endLabel)
  }
}

case class OrNode(left: AstNode, right: OperandNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitInsn(IOR)
  }
}

case class AndNode(left: AstNode, right: OperandNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitInsn(IAND)
  }
}
