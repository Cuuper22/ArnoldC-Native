package org.arnoldc.ast

import org.objectweb.asm.{MethodVisitor, ClassWriter}
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

abstract class AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable)
}

abstract class StatementNode extends AstNode

abstract class OperandNode extends AstNode

abstract class ExpressionNode extends AstNode
