package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

/**
 * Bitwise Operation AST Nodes for ArnoldC Native
 * "CRUSH THEM TOGETHER" - Low-level bit manipulation
 */

// Bitwise AND - "CRUSH THEM TOGETHER"
case class BitwiseAndExprNode(left: AstNode, right: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitInsn(IAND)
  }
}

// Bitwise OR - "JOIN THEM TOGETHER"
case class BitwiseOrExprNode(left: AstNode, right: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitInsn(IOR)
  }
}

// Bitwise XOR - "CONFUSE THEM"
case class BitwiseXorExprNode(left: AstNode, right: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitInsn(IXOR)
  }
}

// Bitwise NOT - "TURN IT AROUND"
case class BitwiseNotExprNode(operand: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    operand.generate(mv, symbolTable)
    mv.visitLdcInsn(-1)
    mv.visitInsn(IXOR)  // ~x == x ^ -1
  }
}

// Bitwise NOT Wrapper - extends OperandNode for parser compatibility
case class BitwiseNotWrapperNode(operand: OperandNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    operand.generate(mv, symbolTable)
    mv.visitLdcInsn(-1)
    mv.visitInsn(IXOR)  // ~x == x ^ -1
  }
}

// Left shift - "PUSH IT LEFT"
case class LeftShiftExprNode(left: AstNode, right: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitInsn(ISHL)
  }
}

// Right shift (arithmetic) - "PUSH IT RIGHT"
case class RightShiftExprNode(left: AstNode, right: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitInsn(ISHR)
  }
}

// Unsigned right shift - "PUSH IT RIGHT UNSIGNED"
case class UnsignedRightShiftExprNode(left: AstNode, right: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    left.generate(mv, symbolTable)
    right.generate(mv, symbolTable)
    mv.visitInsn(IUSHR)
  }
}

// Compound bitwise operations for chained expressions
case class BitwiseChainNode(
  initial: AstNode,
  operations: List[(String, AstNode)]  // ("AND", operand), ("OR", operand), etc.
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    initial.generate(mv, symbolTable)
    operations.foreach { case (op, operand) =>
      operand.generate(mv, symbolTable)
      op match {
        case "AND" => mv.visitInsn(IAND)
        case "OR"  => mv.visitInsn(IOR)
        case "XOR" => mv.visitInsn(IXOR)
        case "SHL" => mv.visitInsn(ISHL)
        case "SHR" => mv.visitInsn(ISHR)
        case "USHR" => mv.visitInsn(IUSHR)
        case _ =>
      }
    }
  }
}

// Bit test - check if bit N is set
case class BitTestNode(value: AstNode, bitPos: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    // result = (value >> bitPos) & 1
    value.generate(mv, symbolTable)
    bitPos.generate(mv, symbolTable)
    mv.visitInsn(ISHR)
    mv.visitLdcInsn(1)
    mv.visitInsn(IAND)
  }
}

// Bit set - set bit N
case class BitSetNode(value: AstNode, bitPos: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    // result = value | (1 << bitPos)
    value.generate(mv, symbolTable)
    mv.visitLdcInsn(1)
    bitPos.generate(mv, symbolTable)
    mv.visitInsn(ISHL)
    mv.visitInsn(IOR)
  }
}

// Bit clear - clear bit N
case class BitClearNode(value: AstNode, bitPos: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    // result = value & ~(1 << bitPos)
    value.generate(mv, symbolTable)
    mv.visitLdcInsn(1)
    bitPos.generate(mv, symbolTable)
    mv.visitInsn(ISHL)
    mv.visitLdcInsn(-1)
    mv.visitInsn(IXOR)  // NOT
    mv.visitInsn(IAND)
  }
}

// Bit toggle - flip bit N
case class BitToggleNode(value: AstNode, bitPos: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    // result = value ^ (1 << bitPos)
    value.generate(mv, symbolTable)
    mv.visitLdcInsn(1)
    bitPos.generate(mv, symbolTable)
    mv.visitInsn(ISHL)
    mv.visitInsn(IXOR)
  }
}

// Mask extraction - extract bits [high:low]
case class BitMaskExtractNode(
  value: AstNode,
  highBit: Int,
  lowBit: Int
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    // result = (value >> lowBit) & ((1 << (highBit - lowBit + 1)) - 1)
    val maskBits = highBit - lowBit + 1
    val mask = (1 << maskBits) - 1
    
    value.generate(mv, symbolTable)
    mv.visitLdcInsn(lowBit)
    mv.visitInsn(ISHR)
    mv.visitLdcInsn(mask)
    mv.visitInsn(IAND)
  }
}

// Rotate left
case class RotateLeftNode(value: AstNode, amount: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    // For 32-bit: (value << amount) | (value >>> (32 - amount))
    // This would need to be done at native level for efficiency
  }
}

// Rotate right
case class RotateRightNode(value: AstNode, amount: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    // For 32-bit: (value >>> amount) | (value << (32 - amount))
  }
}

// Population count (count set bits) - would use native intrinsic
case class PopCountNode(value: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Find first set bit - would use native intrinsic
case class FindFirstSetNode(value: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Count leading zeros - would use native intrinsic
case class CountLeadingZerosNode(value: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Count trailing zeros - would use native intrinsic
case class CountTrailingZerosNode(value: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}
