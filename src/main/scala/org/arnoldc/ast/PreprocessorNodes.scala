package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.arnoldc.SymbolTable

/**
 * Preprocessor AST Nodes for ArnoldC Native
 * "LET ME TELL YOU SOMETHING" - Compile-time directives
 */

// Base trait for preprocessor directives
sealed trait PreprocessorNode extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// #define equivalent - "LET ME TELL YOU SOMETHING"
case class DefineNode(
  name: String,
  value: Option[String] = None  // Can be empty for flag defines
) extends PreprocessorNode {
  def toCDirective: String = value match {
    case Some(v) => s"#define $name $v"
    case None => s"#define $name"
  }
}

// #define with parameters (macro) - "LET ME TELL YOU SOMETHING WITH"
case class MacroDefineNode(
  name: String,
  parameters: List[String],
  body: String
) extends PreprocessorNode {
  def toCDirective: String = {
    val params = parameters.mkString(", ")
    s"#define $name($params) $body"
  }
}

// #undef equivalent - "FORGET ABOUT"
case class UndefNode(name: String) extends PreprocessorNode {
  def toCDirective: String = s"#undef $name"
}

// #include equivalent - "BRING IN"
case class IncludeNode(
  filename: String,
  isSystemHeader: Boolean = false  // <file> vs "file"
) extends PreprocessorNode {
  def toCDirective: String = {
    if (isSystemHeader) s"#include <$filename>"
    else s"#include \"$filename\""
  }
}

// #ifdef equivalent - "IF YOU KNOW"
case class IfDefNode(
  condition: String,
  thenBlock: List[AstNode],
  elseBlock: List[AstNode] = List()
) extends PreprocessorNode {
  def toCDirective: String = s"#ifdef $condition"
}

// #ifndef equivalent - "IF YOU DON'T KNOW"
case class IfNotDefNode(
  condition: String,
  thenBlock: List[AstNode],
  elseBlock: List[AstNode] = List()
) extends PreprocessorNode {
  def toCDirective: String = s"#ifndef $condition"
}

// #if equivalent - "IF I TELL YOU"
case class IfExprNode(
  expression: String,
  thenBlock: List[AstNode],
  elseBlock: List[AstNode] = List()
) extends PreprocessorNode {
  def toCDirective: String = s"#if $expression"
}

// #else - "OTHERWISE I TELL YOU"
case class PreprocessorElseNode() extends PreprocessorNode {
  def toCDirective: String = "#else"
}

// #elif - "OR IF I TELL YOU"
case class PreprocessorElifNode(expression: String) extends PreprocessorNode {
  def toCDirective: String = s"#elif $expression"
}

// #endif - "THAT'S ALL I KNOW"
case class EndIfDefNode() extends PreprocessorNode {
  def toCDirective: String = "#endif"
}

// #pragma equivalent - "COMPILER LISTEN"
case class PragmaNode(directive: String) extends PreprocessorNode {
  def toCDirective: String = s"#pragma $directive"
}

// #pragma once - "ONLY ONCE"
case class PragmaOnceNode() extends PreprocessorNode {
  def toCDirective: String = "#pragma once"
}

// #error equivalent - "STOP EVERYTHING"
case class PreprocessorErrorNode(message: String) extends PreprocessorNode {
  def toCDirective: String = s"""#error "$message""""
}

// #warning equivalent - "I'M WARNING YOU"
case class PreprocessorWarningNode(message: String) extends PreprocessorNode {
  def toCDirective: String = s"""#warning "$message""""
}

// #line equivalent - "THIS IS LINE"
case class LineDirectiveNode(lineNum: Int, filename: Option[String] = None) extends PreprocessorNode {
  def toCDirective: String = filename match {
    case Some(f) => s"""#line $lineNum "$f""""
    case None => s"#line $lineNum"
  }
}

// Predefined macro access (__FILE__, __LINE__, etc.)
case class PredefinedMacroNode(name: String) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Common predefined macros
object PredefinedMacros {
  val FILE = "__FILE__"
  val LINE = "__LINE__"
  val DATE = "__DATE__"
  val TIME = "__TIME__"
  val FUNC = "__func__"
  val COUNTER = "__COUNTER__"
}

// Conditional compilation block
case class ConditionalCompilationNode(
  condition: PreprocessorNode,  // IfDefNode, IfNotDefNode, or IfExprNode
  thenStatements: List[AstNode],
  elseStatements: List[AstNode] = List()
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}

// Include guard helper (combines #ifndef, #define, content, #endif)
case class IncludeGuardNode(
  guardName: String,
  content: List[AstNode]
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
  
  def toCDirective: String = {
    s"#ifndef $guardName\n#define $guardName\n/* content */\n#endif /* $guardName */"
  }
}
