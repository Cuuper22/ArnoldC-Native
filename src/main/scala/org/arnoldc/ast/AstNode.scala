package org.arnoldc.ast

import org.objectweb.asm.{MethodVisitor, ClassWriter}
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

/**
 * Abstract Syntax Tree Node Definitions
 * "THIS IS WHAT I AM" - The AST structure for ArnoldC programs
 *
 * IMPORTANT: This codebase contains ~194 AST node classes, but only ~46 have
 * corresponding parser rules in ArnoldParser/ArnoldParserExtended.
 *
 * The remaining ~150 nodes are PLANNED EXTENSIONS and are not yet parseable.
 * They exist to support future language features but will fail at parse time.
 *
 * Planned/Unparseable Node Categories (see individual files for full list):
 * - Advanced bitwise operations (BitSet, BitClear, BitToggle, BitMask, etc.)
 * - CPU-specific operations (CPUID, RDTSC, RDMSR, WRMSR, CR register access)
 * - Advanced control flow (ElseIf chains, infinite loops, panic)
 * - Function attributes (inline, noreturn, aligned, static, section, ISR)
 * - Advanced preprocessor (elif, conditional compilation, line directives)
 * - Advanced assembly (invlpg, memory fences, page table operations)
 * - Pointer comparisons (PointerEqual, PointerNotEqual)
 * - Type system extensions (typedefs, bitfields, packed structs, anonymous structs)
 * - Advanced arrays (dynamic arrays, slices, nested initializers, extern arrays)
 * - Callback system (register/invoke callbacks, function pointer arrays)
 * - Documentation comments (DocCommentNode)
 * - Runtime assertions (AssertNode, PanicNode, IsNullNode)
 * - Bit manipulation (RotateLeft/Right, PopCount, CountLeadingZeros, FindFirstSet)
 * - String arrays (StringArrayNode)
 *
 * If you need these features, parser rules must be added to ArnoldParserExtended.
 * For the current set of working features, see the README.md keywords table.
 */
abstract class AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable)
}

abstract class StatementNode extends AstNode

abstract class OperandNode extends AstNode

abstract class ExpressionNode extends AstNode
