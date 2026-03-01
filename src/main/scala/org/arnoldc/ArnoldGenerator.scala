package org.arnoldc

import org.arnoldc.ast.RootNode

/**
 * JVM Bytecode Generator
 * "GET TO THE CHOPPER" - Original ArnoldC compiler backend
 *
 * Generates JVM bytecode (.class files) from the ArnoldC AST.
 * This is the original code generation path that outputs Java bytecode
 * using the ASM library.
 */
class ArnoldGenerator extends ClassLoader {

  def generate(arnoldCode: String, filename: String): (Array[Byte], RootNode) = {
    val parser = new ArnoldParser
    val rootNode = parser.parse(arnoldCode)
    (rootNode.generateByteCode(filename), rootNode)
  }
}
