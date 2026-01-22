package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.arnoldc.SymbolTable

/**
 * Hardware and System AST Nodes for ArnoldC Native
 * "DO IT NOW" - Direct hardware access for kernel programming
 */

// ===== I/O PORT OPERATIONS =====

// Output byte to port - "TALK TO THE PORT"
case class OutbNode(port: AstNode, value: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Input byte from port - "LISTEN TO THE PORT"
case class InbNode(port: AstNode, assignTo: Option[String] = None) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// As expression (for use in assignments)
case class InbExprNode(port: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Output word to port - "TALK BIG TO THE PORT"
case class OutwNode(port: AstNode, value: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Input word from port - "LISTEN BIG TO THE PORT"
case class InwNode(port: AstNode, assignTo: Option[String] = None) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

case class InwExprNode(port: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Output dword to port - "TALK HUGE TO THE PORT"
case class OutlNode(port: AstNode, value: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Input dword from port - "LISTEN HUGE TO THE PORT"
case class InlNode(port: AstNode, assignTo: Option[String] = None) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

case class InlExprNode(port: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// I/O wait (for slow devices)
case class IoWaitNode() extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// ===== INTERRUPT CONTROL =====

// Disable interrupts - "EVERYBODY CHILL"
case class CliNode() extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Enable interrupts - "LET'S PARTY"
case class StiNode() extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Halt CPU - "SLEEP NOW"
case class HltNode() extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Pause instruction - "WAIT A MOMENT"
case class PauseNode() extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// No operation - "TAKE A BREAK"
case class NopNode() extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Load interrupt descriptor table - "SET UP THE HANDLERS"
case class LidtNode(idtPointer: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Load global descriptor table - "SET UP THE SEGMENTS"
case class LgdtNode(gdtPointer: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Software interrupt
case class IntNode(intNum: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// ===== CONTROL REGISTERS =====

// Read control register
case class ReadCrNode(crNum: Int) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Write control register
case class WriteCrNode(crNum: Int, value: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Read model-specific register
case class RdmsrNode(msr: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Write model-specific register
case class WrmsrNode(msr: AstNode, value: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// ===== MEMORY BARRIERS =====

// Memory fence
case class MfenceNode() extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Store fence
case class SfenceNode() extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Load fence
case class LfenceNode() extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// ===== CPUID =====

case class CpuidNode(
  function: AstNode,
  eaxOut: String,
  ebxOut: String,
  ecxOut: String,
  edxOut: String
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// ===== INLINE ASSEMBLY =====

// Inline assembly block - "SPEAK TO THE MACHINE"
case class InlineAsmBlockNode(
  assembly: List[String],
  outputs: List[AsmOperand] = List(),
  inputs: List[AsmOperand] = List(),
  clobbers: List[String] = List()
) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Assembly operand (for input/output constraints)
case class AsmOperand(
  constraint: String,  // e.g., "=r", "r", "m", "a", etc.
  variable: String
)

// Simple inline asm (no operands)
case class SimpleAsmNode(assembly: String) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// ===== INTERRUPT SERVICE ROUTINES =====

// ISR attribute for functions
case class IsrAttributeNode(irqNumber: Int)

// ISR function declaration
case class IsrMethodNode(
  name: String,
  irqNumber: Int,
  body: List[StatementNode],
  hasErrorCode: Boolean = false
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// End of interrupt (send EOI to PIC)
case class EoiNode(picPort: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// ===== PAGING =====

// Invalidate TLB entry
case class InvlpgNode(address: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Read page fault address (CR2)
case class ReadCr2Node() extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Write page directory base (CR3)
case class WriteCr3Node(value: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// ===== TIMESTAMP =====

// Read timestamp counter
case class RdtscNode(lowOut: String, highOut: String) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Read timestamp counter as single 64-bit value
case class RdtscExprNode() extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// ===== STACK OPERATIONS =====

// Get stack pointer
case class GetStackPointerNode() extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Set stack pointer
case class SetStackPointerNode(value: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Get base pointer
case class GetBasePointerNode() extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Set base pointer
case class SetBasePointerNode(value: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}
