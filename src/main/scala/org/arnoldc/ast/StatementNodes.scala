package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable
import org.parboiled.errors.ParsingException

case class DeclareIntNode(variable: String, value: OperandNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    symbolTable.putVariable(variable)
    value.generate(mv, symbolTable)
    if (value.isInstanceOf[NumberNode] || value.isInstanceOf[VariableNode]) {
      mv.visitVarInsn(ISTORE, symbolTable.getVariableAddress(variable))
    } else throw new ParsingException("CANNOT INITIALIZE INT WITH BOOLEAN VALUE")
  }
}

case class PrintNode(operand: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
    operand.generate(mv, symbolTable)
    if (operand.isInstanceOf[StringNode]) {
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V")
    } else {
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V")
    }
  }
}

case class AssignVariableNode(variable: String, expression: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    expression.generate(mv, symbolTable)
    mv.visitVarInsn(ISTORE, symbolTable.getVariableAddress(variable))
  }
}

case class ConditionNode(condition: AstNode, ifStatements: List[StatementNode], elseStatements: List[StatementNode]) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    val elseLabel = new org.objectweb.asm.Label()
    val endLabel = new org.objectweb.asm.Label()
    
    condition.generate(mv, symbolTable)
    mv.visitJumpInsn(IFEQ, elseLabel)
    ifStatements.foreach(_.generate(mv, symbolTable))
    mv.visitJumpInsn(GOTO, endLabel)
    mv.visitLabel(elseLabel)
    elseStatements.foreach(_.generate(mv, symbolTable))
    mv.visitLabel(endLabel)
  }
}

case class WhileNode(condition: AstNode, statements: List[StatementNode]) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    val startLabel = new org.objectweb.asm.Label()
    val endLabel = new org.objectweb.asm.Label()
    
    mv.visitLabel(startLabel)
    condition.generate(mv, symbolTable)
    mv.visitJumpInsn(IFEQ, endLabel)
    statements.foreach(_.generate(mv, symbolTable))
    mv.visitJumpInsn(GOTO, startLabel)
    mv.visitLabel(endLabel)
  }
}

case class CallMethodNode(assignTo: String, methodName: String, arguments: List[OperandNode]) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    arguments.foreach(_.generate(mv, symbolTable))
    mv.visitMethodInsn(INVOKESTATIC, symbolTable.getFileName(), methodName, 
      symbolTable.getMethodDescription(methodName))
    if (assignTo.nonEmpty) {
      mv.visitVarInsn(ISTORE, symbolTable.getVariableAddress(assignTo))
    } else if (symbolTable.getMethodInformation(methodName).returnsValue) {
      mv.visitInsn(POP)
    }
  }
}

case class CallReadMethodNode(assignTo: String) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    mv.visitTypeInsn(NEW, "java/util/Scanner")
    mv.visitInsn(DUP)
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;")
    mv.visitMethodInsn(INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V")
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I")
    if (assignTo.nonEmpty) {
      mv.visitVarInsn(ISTORE, symbolTable.getVariableAddress(assignTo))
    }
  }
}

case class ReturnNode(value: Option[OperandNode]) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    value match {
      case Some(v) =>
        v.generate(mv, symbolTable)
        mv.visitInsn(IRETURN)
      case None =>
        mv.visitInsn(RETURN)
    }
  }
}
