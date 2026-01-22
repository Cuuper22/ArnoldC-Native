package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.arnoldc.SymbolTable

/**
 * Comment AST Nodes for ArnoldC Native
 * "TALK TO YOURSELF" - Source-level comments
 */

// Single-line comment - "TALK TO YOURSELF"
case class CommentNode(text: String) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    // Comments don't generate bytecode, they're just for documentation
  }
  
  def toCComment: String = s"/* $text */"
}

// Multi-line comment block - "LET ME THINK ABOUT THIS" ... "I'VE THOUGHT ABOUT IT"
case class MultiLineCommentNode(lines: List[String]) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
  
  def toCComment: String = {
    val content = lines.map(l => s" * $l").mkString("\n")
    s"/*\n$content\n */"
  }
}

// Documentation comment (for generating docs)
case class DocCommentNode(
  brief: String,
  params: List[(String, String)] = List(),  // (paramName, description)
  returns: Option[String] = None,
  notes: List[String] = List()
) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
  
  def toDoxygenComment: String = {
    val sb = new StringBuilder
    sb.append("/**\n")
    sb.append(s" * @brief $brief\n")
    params.foreach { case (name, desc) =>
      sb.append(s" * @param $name $desc\n")
    }
    returns.foreach { ret =>
      sb.append(s" * @return $ret\n")
    }
    notes.foreach { note =>
      sb.append(s" * @note $note\n")
    }
    sb.append(" */")
    sb.toString()
  }
}
