package org.arnoldc.ast

import org.objectweb.asm.Opcodes._
import org.objectweb.asm.{MethodVisitor, ClassWriter}
import org.arnoldc.{MethodInformation, SymbolTable}
import org.arnoldc.native.NativeGenerator

/**
 * Root AST Node
 * "IT'S SHOWTIME" - The top-level program structure
 *
 * Represents a complete ArnoldC program with global declarations
 * and method definitions. Can generate either JVM bytecode or native code.
 */
case class RootNode(globals: List[AstNode], methods: List[AbstractMethodNode]) extends AstNode {

  // Original JVM bytecode generation
  def generateByteCode(filename: String): Array[Byte] = {
    val globalSymbols = storeMethodSignatures(filename)
    generateClass(filename, globalSymbols).toByteArray
  }

  // NEW: Native C code generation
  def generateNativeC(filename: String): String = {
    val generator = new NativeGenerator()
    generator.generateC(this, filename)
  }

  // NEW: x86 Assembly generation
  def generateNativeAsm(filename: String): String = {
    val generator = new NativeGenerator()
    generator.generateAsm(this, filename)
  }

  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}

  def storeMethodSignatures(filename: String) = {
    def storeTo(symbols: SymbolTable)(s: MethodSignature) = {
      symbols.putMethod(s.name, new MethodInformation(s.returnsValue, s.args.size))
    }
    val globalSymbols = new SymbolTable(None, filename)
    val methodSignatures = methods.map(_.signature)
    methodSignatures.foreach(storeTo(globalSymbols))
    globalSymbols
  }

  def generateClass(className: String, globalSymbols: SymbolTable): ClassWriter = {
    val cw = new ClassWriter(0)
    
    def generateClassHeader() = {
      cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null)
      cw.visitSource("Hello.java", null)
      val mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
      mv.visitVarInsn(ALOAD, 0)
      mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V")
      mv.visitInsn(RETURN)
      mv.visitMaxs(100, 100)
      mv.visitEnd()
      mv
    }
    
    def generateClassBody(methodVisitor: MethodVisitor) = {
      def generateBytecode(method: AbstractMethodNode) {
        method.generate(
          cw.visitMethod(ACC_PUBLIC + ACC_STATIC, method.methodName,
            globalSymbols.getMethodDescription(method.methodName), null, null),
          globalSymbols
        )
      }
      methods.foreach(generateBytecode)
    }
    
    val methodVisitor = generateClassHeader()
    generateClassBody(methodVisitor)
    cw
  }
}
