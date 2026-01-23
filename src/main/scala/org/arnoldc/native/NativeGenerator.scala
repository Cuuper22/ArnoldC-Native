package org.arnoldc.native

import org.arnoldc.ast._
import scala.collection.mutable

/**
 * Native Code Generator for ArnoldC Extended
 * "GET YOUR ASS TO MARS" - Generates freestanding C code for bare-metal execution
 * 
 * This extension to ArnoldC enables compiling to native code instead of JVM bytecode.
 * The output is freestanding C that can be compiled with GCC for:
 * - Bare-metal kernels
 * - Embedded systems  
 * - Native executables (with a runtime)
 */
class NativeGenerator {
  
  private val output = new StringBuilder
  private val variables = mutable.Map[String, TypeSpec]()
  private val methods = mutable.Map[String, MethodInfo]()
  private val structs = mutable.Map[String, StructDefNode]()
  private val unions = mutable.Map[String, UnionDefNode]()
  private val enums = mutable.Map[String, EnumDefNode]()
  private val preprocessorDirectives = mutable.ListBuffer[PreprocessorNode]()
  private val globalDecls = new StringBuilder
  private var indentLevel = 0
  
  case class MethodInfo(name: String, returnType: TypeSpec, args: List[(String, TypeSpec)])
  
  private def indent(): String = "    " * indentLevel
  
  private def emit(code: String): Unit = {
    output.append(indent() + code + "\n")
  }
  
  private def emitRaw(code: String): Unit = {
    output.append(code + "\n")
  }
  
  /**
   * Generate C code from ArnoldC AST
   * "IT'S SHOWTIME" - Main entry point
   */
  def generateC(root: RootNode, className: String): String = {
    // Header
    emitRaw("/*")
    emitRaw(" * Generated from ArnoldC Extended - I'LL BE BACK")
    emitRaw(" * Native code output - no JVM required")
    emitRaw(" * \"GET YOUR ASS TO MARS\"")
    emitRaw(" */")
    emitRaw("")
    emitRaw("#include \"arnold_runtime.h\"")
    emitRaw("")

    // Collect global declarations
    collectGlobals(root.globals)
    
    // Generate preprocessor directives (includes, defines, etc.)
    if (preprocessorDirectives.nonEmpty) {
      emitRaw("/* Preprocessor directives - LET ME TELL YOU SOMETHING */")
      preprocessorDirectives.foreach { p =>
        emitRaw(generatePreprocessor(p))
      }
      emitRaw("")
    }
    
    // First pass: collect struct definitions
    root.methods.foreach { method =>
      // For now, collect from statements
    }
    
    // Generate struct definitions
    if (structs.nonEmpty) {
      emitRaw("/* Struct definitions - THIS IS WHAT I AM */")
      structs.values.foreach { s =>
        emitRaw(s.toCType + ";")
        emitRaw("")
      }
    }
    
    // Generate enum definitions
    if (enums.nonEmpty) {
      emitRaw("/* Enum definitions - THESE ARE MY OPTIONS */")
      enums.values.foreach { e =>
        emitRaw(e.toCType + ";")
        emitRaw("")
      }
    }
    
    // First pass: collect method signatures
    root.methods.foreach { method =>
      val sig = method.signature
      val returnType = if (sig.returnsValue) I32Type else VoidType
      val args = sig.args.map(v => (v.name, I32Type: TypeSpec))
      methods(sig.name) = MethodInfo(sig.name, returnType, args)
    }
    
    // Generate forward declarations
    emitRaw("/* Forward declarations - LISTEN TO ME VERY CAREFULLY */")
    methods.values.foreach { m =>
      val retType = typeToC(m.returnType)
      val args = if (m.args.isEmpty) "void" 
                 else m.args.map { case (name, typ) => s"${typeToC(typ)} $name" }.mkString(", ")
      emitRaw(s"$retType ${m.name}($args);")
    }
    emitRaw("")
    
    // Generate global declarations
    if (globalDecls.nonEmpty) {
      emitRaw("/* Global variables */")
      emitRaw(globalDecls.toString())
      emitRaw("")
    }
    
    // Generate each method
    root.methods.foreach(generateMethod)
    
    output.toString()
  }
  
  private def typeToC(t: TypeSpec): String = t match {
    case U8Type => "uint8_t"
    case U16Type => "uint16_t"
    case U32Type => "uint32_t"
    case U64Type => "uint64_t"
    case I8Type => "int8_t"
    case I16Type => "int16_t"
    case I32Type => "int32_t"
    case I64Type => "int64_t"
    case VoidType => "void"
    case CharType => "char"
    case IntType => "int"
    case PointerType(base) => s"${typeToC(base)}*"
    case ArrayType(elem, len) => s"${typeToC(elem)}[$len]"
    case StructRefType(name) => s"struct $name"
    case UnionRefType(name) => s"union $name"
    case EnumRefType(name) => s"enum $name"
    case FunctionPointerType(ret, params) =>
      val paramsStr = if (params.isEmpty) "void" else params.map(typeToC).mkString(", ")
      s"${typeToC(ret)}(*)($paramsStr)"
    case ConstType(base) => s"const ${typeToC(base)}"
    case VolatileType(base) => s"volatile ${typeToC(base)}"
  }
  
  private def generatePreprocessor(p: PreprocessorNode): String = p match {
    case DefineNode(name, value) =>
      value match {
        case Some(v) => s"#define $name $v"
        case None => s"#define $name"
      }
    case IncludeNode(filename, isSystem) =>
      if (isSystem) s"#include <$filename>"
      else s"""#include "$filename""""
    case IfDefNode(cond, thenBlock, elseBlock) =>
      val thenCode = thenBlock.map {
        case pp: PreprocessorNode => generatePreprocessor(pp)
        case _ => "/* statement */"
      }.mkString("\n")
      val elseCode = if (elseBlock.nonEmpty) {
        "#else\n" + elseBlock.map {
          case pp: PreprocessorNode => generatePreprocessor(pp)
          case _ => "/* statement */"
        }.mkString("\n") + "\n"
      } else ""
      s"#ifdef $cond\n$thenCode\n$elseCode#endif /* $cond */"
    case IfNotDefNode(cond, thenBlock, elseBlock) =>
      val thenCode = thenBlock.map {
        case pp: PreprocessorNode => generatePreprocessor(pp)
        case _ => "/* statement */"
      }.mkString("\n")
      val elseCode = if (elseBlock.nonEmpty) {
        "#else\n" + elseBlock.map {
          case pp: PreprocessorNode => generatePreprocessor(pp)
          case _ => "/* statement */"
        }.mkString("\n") + "\n"
      } else ""
      s"#ifndef $cond\n$thenCode\n$elseCode#endif /* $cond */"
    case PragmaOnceNode() =>
      "#pragma once"
    case PragmaNode(directive) =>
      s"#pragma $directive"
    case PreprocessorErrorNode(msg) =>
      s"""#error "$msg""""
    case PreprocessorWarningNode(msg) =>
      s"""#warning "$msg""""
    case _ =>
      s"/* unknown preprocessor directive: ${p.getClass.getSimpleName} */"
  }
  
  private def generateMethod(method: AbstractMethodNode): Unit = method match {
    case MainMethodNode(statements) => generateMainMethod(statements)
    case MethodNode(name, args, returnsValue, statements) => 
      generateUserMethod(name, args, returnsValue, statements)
  }
  
  private def generateMainMethod(statements: List[StatementNode]): Unit = {
    emitRaw("/* IT'S SHOWTIME - Main entry point */")
    emitRaw("void arnold_main(void) {")
    indentLevel += 1
    
    statements.foreach(generateStatement)
    
    indentLevel -= 1
    emitRaw("}")
    emitRaw("/* YOU HAVE BEEN TERMINATED */")
    emitRaw("")
  }
  
  private def generateUserMethod(
    name: String,
    args: List[VariableNode],
    returnsValue: Boolean,
    statements: List[StatementNode]
  ): Unit = {
    val retType = if (returnsValue) "int" else "void"
    val argsStr = if (args.isEmpty) "void" else args.map(a => s"int ${a.name}").mkString(", ")
    
    emitRaw(s"/* LISTEN TO ME VERY CAREFULLY - $name */")
    emitRaw(s"$retType $name($argsStr) {")
    indentLevel += 1
    
    // Add args to known variables
    args.foreach(a => variables(a.name) = I32Type)
    
    statements.foreach(generateStatement)
    
    indentLevel -= 1
    emitRaw("}")
    emitRaw("/* HASTA LA VISTA, BABY */")
    emitRaw("")
  }

  private def collectGlobals(globals: List[AstNode]): Unit = {
    globals.foreach {
      case d: DefineNode =>
        preprocessorDirectives += d

      case s: StructDefNode =>
        structs(s.structName) = s

      case e: EnumDefNode =>
        enums(e.enumName) = e

      case a: ArrayDeclareNode =>
        val typeStr = typeToC(a.elementType)
        a.initialValues match {
          case Some(values) =>
            val valsStr = values.map(generateExpression).mkString(", ")
            globalDecls.append(s"$typeStr ${a.variable}[${a.size}] = {$valsStr};\n")
          case None =>
            globalDecls.append(s"$typeStr ${a.variable}[${a.size}];\n")
        }

      case t: TypedDeclareNode =>
        val typeStr = typeToC(t.varType)
        val valCode = generateExpression(t.initialValue)
        globalDecls.append(s"$typeStr ${t.variable} = $valCode;\n")

      case d: DeclareIntNode =>
        val valCode = generateExpression(d.value)
        globalDecls.append(s"int ${d.variable} = $valCode;\n")

      case CommentNode(text) =>
        globalDecls.append(s"/* $text */\n")

      case MultiLineCommentNode(lines) =>
        globalDecls.append("/*\n")
        lines.foreach { line =>
          globalDecls.append(s" * $line\n")
        }
        globalDecls.append(" */\n")

      case _ =>
    }
  }
  
  private def generateStatement(stmt: StatementNode): Unit = stmt match {
    // ===== VARIABLE DECLARATIONS =====
    
    case DeclareIntNode(name, value) =>
      variables(name) = I32Type
      val valueCode = generateExpression(value)
      emit(s"int $name = $valueCode;  /* HEY CHRISTMAS TREE */")
      
    case TypedDeclareNode(name, varType, value) =>
      variables(name) = varType
      val typeStr = typeToC(varType)
      val valueCode = generateExpression(value)
      emit(s"$typeStr $name = $valueCode;  /* HEY CHRISTMAS TREE */")
      
    case ArrayDeclareNode(name, elemType, size, initValues) =>
      variables(name) = ArrayType(elemType, size)
      val typeStr = typeToC(elemType)
      initValues match {
        case Some(values) =>
          val valsStr = values.map(generateExpression).mkString(", ")
          emit(s"$typeStr $name[$size] = {$valsStr};  /* LINE THEM UP */")
        case None =>
          emit(s"$typeStr $name[$size];  /* LINE THEM UP */")
      }
      
    case StructDeclareNode(varName, structName, initValues) =>
      variables(varName) = StructRefType(structName)
      emit(s"struct $structName $varName;  /* CREATE ONE LIKE */")
      
    // ===== PRINT =====
      
    case PrintNode(content) =>
      content match {
        case StringNode(s) =>
          val escaped = s.replace("\\", "\\\\").replace("\"", "\\\"")
          emit(s"""arnold_print("$escaped");  /* TALK TO THE HAND */""")
          emit("""arnold_print("\n");""")
        case operand: OperandNode =>
          val code = generateExpression(operand)
          emit(s"arnold_print_int($code);  /* TALK TO THE HAND */")
          emit("""arnold_print("\n");""")
        case expr =>
          val code = generateExpression(expr)
          emit(s"arnold_print_int($code);  /* TALK TO THE HAND */")
          emit("""arnold_print("\n");""")
      }
      
    // ===== ASSIGNMENTS =====
      
    case AssignVariableNode(name, expr) =>
      val exprCode = generateExpression(expr)
      emit(s"$name = $exprCode;  /* GET TO THE CHOPPER */")
      
    case ArrayAssignNode(arrayName, index, value) =>
      val idxCode = generateExpression(index)
      val valCode = generateExpression(value)
      emit(s"$arrayName[$idxCode] = $valCode;  /* WHICH ONE */")
      
    case StructMemberAssignNode(structVar, memberName, value) =>
      val valCode = generateExpression(value)
      emit(s"$structVar.$memberName = $valCode;  /* WHAT'S YOUR */")
      
    case StructPointerAssignNode(ptrVar, memberName, value) =>
      val valCode = generateExpression(value)
      emit(s"$ptrVar->$memberName = $valCode;  /* SHOW ME THE */")
      
    case PointerWriteNode(pointer, value) =>
      val ptrCode = generateExpression(pointer)
      val valCode = generateExpression(value)
      emit(s"*($ptrCode) = $valCode;  /* FIRE AT */")
      
    // ===== CONTROL FLOW =====
      
    case ConditionNode(condition, ifBody, elseBody) =>
      val condCode = generateExpression(condition)
      emit(s"if ($condCode) {  /* BECAUSE I'M GOING TO SAY PLEASE */")
      indentLevel += 1
      ifBody.foreach(generateStatement)
      indentLevel -= 1
      
      if (elseBody.nonEmpty) {
        emit("} else {  /* BULLSHIT */")
        indentLevel += 1
        elseBody.foreach(generateStatement)
        indentLevel -= 1
      }
      emit("}  /* YOU HAVE NO RESPECT FOR LOGIC */")
      
    case WhileNode(condition, body) =>
      val condCode = generateExpression(condition)
      emit(s"while ($condCode) {  /* STICK AROUND */")
      indentLevel += 1
      body.foreach(generateStatement)
      indentLevel -= 1
      emit("}  /* CHILL */")
      
    case DoWhileNode(condition, body) =>
      emit("do {  /* DO THIS FIRST */")
      indentLevel += 1
      body.foreach(generateStatement)
      indentLevel -= 1
      val condCode = generateExpression(condition)
      emit(s"} while ($condCode);  /* THEN STICK AROUND */")
      
    case ForLoopNode(loopVar, start, endCond, step, body) =>
      variables(loopVar) = I32Type
      val startCode = generateExpression(start)
      val endCode = generateExpression(endCond)
      val stepCode = generateExpression(step)
      emit(s"for (int $loopVar = $startCode; $loopVar < $endCode; $loopVar += $stepCode) {  /* I'LL COUNT FROM */")
      indentLevel += 1
      body.foreach(generateStatement)
      indentLevel -= 1
      emit("}  /* STOP COUNTING */")
      
    case SwitchNode(variable, cases, defaultCase) =>
      emit(s"switch ($variable) {  /* WHAT'S THE CHOICE */")
      indentLevel += 1
      cases.foreach { c =>
        val caseVal = generateExpression(c.value)
        emit(s"case $caseVal:  /* WHEN IT'S */")
        indentLevel += 1
        c.statements.foreach(generateStatement)
        if (!c.fallthrough) emit("break;")
        indentLevel -= 1
      }
      defaultCase.foreach { stmts =>
        emit("default:  /* OTHERWISE */")
        indentLevel += 1
        stmts.foreach(generateStatement)
        indentLevel -= 1
      }
      indentLevel -= 1
      emit("}  /* NO MORE CHOICES */")
      
    case BreakNode() =>
      emit("break;  /* GET OUT */")
      
    case ContinueNode() =>
      emit("continue;  /* DO IT AGAIN */")
      
    case GotoNode(label) =>
      emit(s"goto $label;  /* GO TO */")
      
    case LabelNode(label) =>
      // Labels need to be at indent level 0 relative to function
      val savedIndent = indentLevel
      indentLevel = 0
      emit(s"$label:  /* YOU ARE HERE */")
      indentLevel = savedIndent
      
    // ===== METHOD CALLS =====
      
    case CallMethodNode(assignTo, methodName, args) =>
      val argsCode = args.map(generateExpression).mkString(", ")
      val call = s"$methodName($argsCode)"
      
      if (assignTo.nonEmpty) {
        emit(s"$assignTo = $call;  /* GET YOUR ASS TO MARS */")
      } else {
        emit(s"$call;  /* DO IT NOW */")
      }
      
    case ReturnNode(value) =>
      value match {
        case Some(v) =>
          val code = generateExpression(v)
          emit(s"return $code;  /* I'LL BE BACK */")
        case None =>
          emit("return;  /* I'LL BE BACK */")
      }
      
    case CallReadMethodNode(assignTo) =>
      if (assignTo.nonEmpty) {
        emit(s"$assignTo = arnold_read_int();  /* I WANT TO ASK YOU... */")
      }
      
    // ===== I/O PORT OPERATIONS =====
      
    case OutbNode(port, value) =>
      val portCode = generateExpression(port)
      val valCode = generateExpression(value)
      emit(s"outb($portCode, $valCode);  /* TALK TO THE PORT */")
      
    case InbNode(port, assignTo) =>
      val portCode = generateExpression(port)
      assignTo match {
        case Some(varName) =>
          emit(s"$varName = inb($portCode);  /* LISTEN TO THE PORT */")
        case None =>
          emit(s"inb($portCode);  /* LISTEN TO THE PORT */")
      }
      
    case OutwNode(port, value) =>
      val portCode = generateExpression(port)
      val valCode = generateExpression(value)
      emit(s"outw($portCode, $valCode);  /* TALK BIG TO THE PORT */")
      
    case InwNode(port, assignTo) =>
      val portCode = generateExpression(port)
      assignTo match {
        case Some(varName) =>
          emit(s"$varName = inw($portCode);  /* LISTEN BIG TO THE PORT */")
        case None =>
          emit(s"inw($portCode);  /* LISTEN BIG TO THE PORT */")
      }
      
    case OutlNode(port, value) =>
      val portCode = generateExpression(port)
      val valCode = generateExpression(value)
      emit(s"outl($portCode, $valCode);  /* TALK HUGE TO THE PORT */")
      
    case InlNode(port, assignTo) =>
      val portCode = generateExpression(port)
      assignTo match {
        case Some(varName) =>
          emit(s"$varName = inl($portCode);  /* LISTEN HUGE TO THE PORT */")
        case None =>
          emit(s"inl($portCode);  /* LISTEN HUGE TO THE PORT */")
      }
      
    // ===== INTERRUPT CONTROL =====
      
    case CliNode() =>
      emit("__asm__ volatile (\"cli\");  /* EVERYBODY CHILL */")
      
    case StiNode() =>
      emit("__asm__ volatile (\"sti\");  /* LET'S PARTY */")
      
    case HltNode() =>
      emit("__asm__ volatile (\"hlt\");  /* SLEEP NOW */")
      
    case PauseNode() =>
      emit("__asm__ volatile (\"pause\");  /* WAIT A MOMENT */")
      
    case NopNode() =>
      emit("__asm__ volatile (\"nop\");  /* TAKE A BREAK */")
      
    // ===== MEMORY OPERATIONS =====
      
    case FreeNode(ptr) =>
      emit(s"arnold_free($ptr);  /* YOU'RE LUGGAGE */")
      
    case MemsetNode(dest, value, count) =>
      val destCode = generateExpression(dest)
      val valCode = generateExpression(value)
      val countCode = generateExpression(count)
      emit(s"arnold_memset($destCode, $valCode, $countCode);  /* FILL WITH */")
      
    case MemcpyNode(dest, src, count) =>
      val destCode = generateExpression(dest)
      val srcCode = generateExpression(src)
      val countCode = generateExpression(count)
      emit(s"arnold_memcpy($destCode, $srcCode, $countCode);  /* COPY FROM TO */")
      
    case MemoryWriteNode(address, writeType, value) =>
      val addrCode = generateExpression(address)
      val typeStr = typeToC(writeType)
      val valCode = generateExpression(value)
      emit(s"*($typeStr*)($addrCode) = $valCode;  /* WRITE TO */")
      
    // ===== INLINE ASSEMBLY =====
      
    case SimpleAsmNode(assembly) =>
      val escaped = assembly.trim.replace("\"", "\\\"").replace("\n", "\\n\"\n\"")
      emit(s"""__asm__ volatile ("$escaped");  /* SPEAK TO THE MACHINE */""")
      
    case InlineAsmBlockNode(lines, outputs, inputs, clobbers) =>
      val asmStr = lines.mkString("\\n")
      val outputStr = outputs.map(o => s""""${o.constraint}"(${o.variable})""").mkString(", ")
      val inputStr = inputs.map(i => s""""${i.constraint}"(${i.variable})""").mkString(", ")
      val clobberStr = clobbers.map(c => s""""$c"""").mkString(", ")
      
      emit("__asm__ volatile (")
      indentLevel += 1
      emit(s""""$asmStr"""")
      if (outputs.nonEmpty || inputs.nonEmpty || clobbers.nonEmpty) {
        emit(s": $outputStr")
      }
      if (inputs.nonEmpty || clobbers.nonEmpty) {
        emit(s": $inputStr")
      }
      if (clobbers.nonEmpty) {
        emit(s": $clobberStr")
      }
      indentLevel -= 1
      emit(");  /* THE MACHINE SAYS */")
      
    // ===== FUNCTION POINTERS =====
      
    case FunctionPointerDeclareNode(variable, returnType, paramTypes) =>
      val retStr = typeToC(returnType)
      val paramsStr = if (paramTypes.isEmpty) "void" else paramTypes.map(typeToC).mkString(", ")
      emit(s"$retStr (*$variable)($paramsStr);  /* REMEMBER THIS MOVE */")
      
    case FunctionPointerAssignNode(fptrVar, functionName) =>
      emit(s"$fptrVar = &$functionName;  /* LEARN THE MOVE */")
      
    case FunctionPointerCallNode(fptrVar, args, assignTo) =>
      val argsCode = args.map(generateExpression).mkString(", ")
      val call = s"(*$fptrVar)($argsCode)"
      assignTo match {
        case Some(varName) =>
          emit(s"$varName = $call;  /* USE THAT MOVE */")
        case None =>
          emit(s"$call;  /* USE THAT MOVE */")
      }
      
    // ===== COMMENTS =====
      
    case CommentNode(text) =>
      emit(s"/* $text */  /* TALK TO YOURSELF */")
      
    case MultiLineCommentNode(lines) =>
      emit("/*")
      lines.foreach { line =>
        emit(s" * $line")
      }
      emit(" */")
      
    case _ =>
      emit(s"/* Unhandled statement: ${stmt.getClass.getSimpleName} */")
  }
  
  private def generateExpression(expr: AstNode): String = expr match {
    // ===== OPERANDS =====
    
    case NumberNode(value) => value.toString
    case HexNumberNode(value) => s"0x${value.toHexString}"
    case VariableNode(name) => name
    case StringNode(value) => s""""$value""""
    case CharLiteralNode(value) => s"'$value'"
    
    // ===== ARITHMETIC =====
    
    case PlusExpressionNode(left, right) =>
      s"(${generateExpression(left)} + ${generateExpression(right)})"
      
    case MinusExpressionNode(left, right) =>
      s"(${generateExpression(left)} - ${generateExpression(right)})"
      
    case MultiplicationExpressionNode(left, right) =>
      s"(${generateExpression(left)} * ${generateExpression(right)})"
      
    case DivisionExpressionNode(left, right) =>
      s"(${generateExpression(left)} / ${generateExpression(right)})"
      
    case ModuloExpressionNode(left, right) =>
      s"(${generateExpression(left)} % ${generateExpression(right)})"
      
    // ===== COMPARISON =====
      
    case EqualToNode(left, right) =>
      s"(${generateExpression(left)} == ${generateExpression(right)})"
      
    case NotEqualNode(left, right) =>
      s"(${generateExpression(left)} != ${generateExpression(right)})"
      
    case GreaterThanNode(left, right) =>
      s"(${generateExpression(left)} > ${generateExpression(right)})"
      
    case LessThanNode(left, right) =>
      s"(${generateExpression(left)} < ${generateExpression(right)})"
      
    case GreaterThanOrEqualNode(left, right) =>
      s"(${generateExpression(left)} >= ${generateExpression(right)})"
      
    case LessThanOrEqualNode(left, right) =>
      s"(${generateExpression(left)} <= ${generateExpression(right)})"
      
    // ===== LOGICAL =====
      
    case OrNode(left, right) =>
      s"(${generateExpression(left)} || ${generateExpression(right)})"
      
    case AndNode(left, right) =>
      s"(${generateExpression(left)} && ${generateExpression(right)})"
      
    case LogicalNotNode(operand) =>
      s"(!${generateExpression(operand)})"
    
    case LogicalNotWrapperNode(operand) =>
      s"(!${generateExpression(operand)})"
      
    // ===== BITWISE =====
      
    case BitwiseAndExprNode(left, right) =>
      s"(${generateExpression(left)} & ${generateExpression(right)})"
      
    case BitwiseOrExprNode(left, right) =>
      s"(${generateExpression(left)} | ${generateExpression(right)})"
      
    case BitwiseXorExprNode(left, right) =>
      s"(${generateExpression(left)} ^ ${generateExpression(right)})"
      
    case BitwiseNotExprNode(operand) =>
      s"(~${generateExpression(operand)})"
      
    case BitwiseNotWrapperNode(operand) =>
      s"(~${generateExpression(operand)})"
      
    case LeftShiftExprNode(left, right) =>
      s"(${generateExpression(left)} << ${generateExpression(right)})"
      
    case RightShiftExprNode(left, right) =>
      s"(${generateExpression(left)} >> ${generateExpression(right)})"
      
    case UnsignedRightShiftExprNode(left, right) =>
      s"((unsigned)${generateExpression(left)} >> ${generateExpression(right)})"
      
    // ===== POINTERS =====
      
    case AddressOfNode(variable) =>
      s"(&$variable)"
      
    case DereferenceNode(pointer) =>
      s"(*${generateExpression(pointer)})"
      
    case PointerAddNode(pointer, offset) =>
      s"(${generateExpression(pointer)} + ${generateExpression(offset)})"
      
    case PointerSubNode(pointer, offset) =>
      s"(${generateExpression(pointer)} - ${generateExpression(offset)})"
      
    // ===== ARRAYS =====
      
    case ArrayAccessNode(arrayVar, index) =>
      s"$arrayVar[${generateExpression(index)}]"
      
    case MultiArrayAccessNode(arrayVar, indices) =>
      val idxStr = indices.map(i => s"[${generateExpression(i)}]").mkString
      s"$arrayVar$idxStr"
      
    case ArrayLengthNode(arrayVar) =>
      s"(sizeof($arrayVar) / sizeof($arrayVar[0]))"
      
    case ArrayPointerNode(arrayVar) =>
      s"(&$arrayVar[0])"
      
    // ===== STRUCTS =====
      
    case StructMemberAccessNode(structVar, memberName) =>
      s"$structVar.$memberName"
      
    case StructPointerAccessNode(ptrVar, memberName) =>
      s"$ptrVar->$memberName"
      
    // ===== TYPE OPERATIONS =====
      
    case TypeCastNode(targetType, expr) =>
      s"((${typeToC(targetType)})${generateExpression(expr)})"
      
    case SizeofTypeNode(targetType) =>
      s"sizeof(${typeToC(targetType)})"
      
    case SizeofExprNode(variable) =>
      s"sizeof($variable)"
      
    // ===== MEMORY =====
      
    case AllocNode(size) =>
      s"arnold_alloc(${generateExpression(size)})"
      
    case MemoryReadNode(address, readType) =>
      val addrCode = generateExpression(address)
      s"(*(${typeToC(readType)}*)($addrCode))"
      
    // ===== I/O =====
      
    case InbExprNode(port) =>
      s"inb(${generateExpression(port)})"
      
    case InwExprNode(port) =>
      s"inw(${generateExpression(port)})"
      
    case InlExprNode(port) =>
      s"inl(${generateExpression(port)})"
      
    // ===== TERNARY =====
      
    case TernaryNode(condition, thenExpr, elseExpr) =>
      s"(${generateExpression(condition)} ? ${generateExpression(thenExpr)} : ${generateExpression(elseExpr)})"
      
    // ===== ENUMS =====
      
    case EnumValueAccessNode(enumName, valueName) =>
      s"$valueName"  // Enum values are just identifiers in C
      
    case _ => s"0 /* unknown expression: ${expr.getClass.getSimpleName} */"
  }
  
  /**
   * Generate x86 assembly from ArnoldC AST
   * "DO IT NOW" - Direct assembly output
   */
  def generateAsm(root: RootNode, className: String): String = {
    val generator = new AsmGenerator()
    generator.generate(root)
  }
}
