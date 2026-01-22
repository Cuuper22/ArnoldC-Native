package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

/**
 * Extended Control Flow AST Nodes for ArnoldC Native
 * "STICK AROUND" - More ways to control program flow
 */

// For loop - "I'LL COUNT FROM"
case class ForLoopNode(
  loopVar: String,
  start: AstNode,
  endCondition: AstNode,
  step: AstNode,
  body: List[StatementNode]
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    // Traditional for loop generation
    val startLabel = new org.objectweb.asm.Label()
    val endLabel = new org.objectweb.asm.Label()
    
    // Initialize loop variable
    start.generate(mv, symbolTable)
    mv.visitVarInsn(ISTORE, symbolTable.getVariableAddress(loopVar))
    
    mv.visitLabel(startLabel)
    
    // Check condition
    mv.visitVarInsn(ILOAD, symbolTable.getVariableAddress(loopVar))
    endCondition.generate(mv, symbolTable)
    mv.visitJumpInsn(IF_ICMPGE, endLabel)
    
    // Body
    body.foreach(_.generate(mv, symbolTable))
    
    // Increment
    mv.visitVarInsn(ILOAD, symbolTable.getVariableAddress(loopVar))
    step.generate(mv, symbolTable)
    mv.visitInsn(IADD)
    mv.visitVarInsn(ISTORE, symbolTable.getVariableAddress(loopVar))
    
    mv.visitJumpInsn(GOTO, startLabel)
    mv.visitLabel(endLabel)
  }
}

// Switch statement - "WHAT'S THE CHOICE"
case class SwitchNode(
  variable: String,
  cases: List[CaseNode],
  defaultCase: Option[List[StatementNode]]
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    // Would need tableswitch or lookupswitch
  }
}

// Case clause - "WHEN IT'S"
case class CaseNode(
  value: AstNode,
  statements: List[StatementNode],
  fallthrough: Boolean = false  // If true, no implicit break
)

// Break statement - "GET OUT"
case class BreakNode() extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    // Needs context to know which label to jump to
  }
}

// Continue statement - "DO IT AGAIN"
case class ContinueNode() extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    // Needs context to know which label to jump to
  }
}

// Goto statement - "GO TO"
case class GotoNode(label: String) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Label definition - "YOU ARE HERE"
case class LabelNode(label: String) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Do-while loop - "DO THIS THEN STICK AROUND"
case class DoWhileNode(
  condition: AstNode,
  body: List[StatementNode]
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    val startLabel = new org.objectweb.asm.Label()
    
    mv.visitLabel(startLabel)
    body.foreach(_.generate(mv, symbolTable))
    condition.generate(mv, symbolTable)
    mv.visitJumpInsn(IFNE, startLabel)
  }
}

// Infinite loop - "FOREVER"
case class InfiniteLoopNode(
  body: List[StatementNode]
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    val startLabel = new org.objectweb.asm.Label()
    mv.visitLabel(startLabel)
    body.foreach(_.generate(mv, symbolTable))
    mv.visitJumpInsn(GOTO, startLabel)
  }
}

// Less than comparison - "YOU'RE NOT BIG ENOUGH"
case class LessThanNode(left: AstNode, right: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    val trueLabel = new org.objectweb.asm.Label()
    val endLabel = new org.objectweb.asm.Label()
    
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitJumpInsn(IF_ICMPLT, trueLabel)
    mv.visitInsn(ICONST_0)
    mv.visitJumpInsn(GOTO, endLabel)
    mv.visitLabel(trueLabel)
    mv.visitInsn(ICONST_1)
    mv.visitLabel(endLabel)
  }
}

// Less than or equal - "YOU'RE NOT BIGGER"
case class LessThanOrEqualNode(left: AstNode, right: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    val trueLabel = new org.objectweb.asm.Label()
    val endLabel = new org.objectweb.asm.Label()
    
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitJumpInsn(IF_ICMPLE, trueLabel)
    mv.visitInsn(ICONST_0)
    mv.visitJumpInsn(GOTO, endLabel)
    mv.visitLabel(trueLabel)
    mv.visitInsn(ICONST_1)
    mv.visitLabel(endLabel)
  }
}

// Greater than or equal - "YOU'RE BIG ENOUGH"
case class GreaterThanOrEqualNode(left: AstNode, right: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    val trueLabel = new org.objectweb.asm.Label()
    val endLabel = new org.objectweb.asm.Label()
    
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitJumpInsn(IF_ICMPGE, trueLabel)
    mv.visitInsn(ICONST_0)
    mv.visitJumpInsn(GOTO, endLabel)
    mv.visitLabel(trueLabel)
    mv.visitInsn(ICONST_1)
    mv.visitLabel(endLabel)
  }
}

// Not equal - "YOU ARE NOT ME"
case class NotEqualNode(left: AstNode, right: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    val trueLabel = new org.objectweb.asm.Label()
    val endLabel = new org.objectweb.asm.Label()
    
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitJumpInsn(IF_ICMPNE, trueLabel)
    mv.visitInsn(ICONST_0)
    mv.visitJumpInsn(GOTO, endLabel)
    mv.visitLabel(trueLabel)
    mv.visitInsn(ICONST_1)
    mv.visitLabel(endLabel)
  }
}

// Logical NOT - "THAT'S A LIE"
case class LogicalNotNode(operand: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    val trueLabel = new org.objectweb.asm.Label()
    val endLabel = new org.objectweb.asm.Label()
    
    operand.generate(mv, symbolTable)
    mv.visitJumpInsn(IFEQ, trueLabel)
    mv.visitInsn(ICONST_0)
    mv.visitJumpInsn(GOTO, endLabel)
    mv.visitLabel(trueLabel)
    mv.visitInsn(ICONST_1)
    mv.visitLabel(endLabel)
  }
}

// Logical NOT Wrapper - extends OperandNode for parser compatibility
case class LogicalNotWrapperNode(operand: OperandNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    val trueLabel = new org.objectweb.asm.Label()
    val endLabel = new org.objectweb.asm.Label()
    
    operand.generate(mv, symbolTable)
    mv.visitJumpInsn(IFEQ, trueLabel)
    mv.visitInsn(ICONST_0)
    mv.visitJumpInsn(GOTO, endLabel)
    mv.visitLabel(trueLabel)
    mv.visitInsn(ICONST_1)
    mv.visitLabel(endLabel)
  }
}

// Ternary/conditional expression - "IF THIS THEN THAT OTHERWISE OTHER"
case class TernaryNode(
  condition: AstNode,
  thenExpr: AstNode,
  elseExpr: AstNode
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    val elseLabel = new org.objectweb.asm.Label()
    val endLabel = new org.objectweb.asm.Label()
    
    condition.generate(mv, symbolTable)
    mv.visitJumpInsn(IFEQ, elseLabel)
    thenExpr.generate(mv, symbolTable)
    mv.visitJumpInsn(GOTO, endLabel)
    mv.visitLabel(elseLabel)
    elseExpr.generate(mv, symbolTable)
    mv.visitLabel(endLabel)
  }
}

// Else-if chain (more efficient than nested if-else)
case class ElseIfNode(
  condition: AstNode,
  statements: List[StatementNode]
)

case class IfElseIfNode(
  mainCondition: AstNode,
  mainBody: List[StatementNode],
  elseIfClauses: List[ElseIfNode],
  elseBody: Option[List[StatementNode]]
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Assert statement (for kernel debugging)
case class AssertNode(
  condition: AstNode,
  message: Option[String] = None
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Panic/halt statement
case class PanicNode(message: String) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}
