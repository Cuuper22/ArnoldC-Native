package org.arnoldc.native

import org.arnoldc.ast._
import scala.collection.mutable

/**
 * x86 Assembly Generator
 * "DO IT NOW" - Direct assembly code generation for bare metal
 *
 * Generates x86 assembly (NASM syntax) from the ArnoldC AST.
 * Outputs .asm files that can be assembled with NASM for
 * freestanding/kernel applications.
 */
class AsmGenerator {

  private val asm = new StringBuilder
  private var labelCounter = 0

  private val defines = mutable.LinkedHashMap[String, String]()
  private val globalVars = mutable.LinkedHashMap[String, GlobalVar]()
  private val globalArrays = mutable.LinkedHashMap[String, GlobalArray]()
  private val globalComments = mutable.ListBuffer[String]()

  private case class GlobalVar(varType: TypeSpec, init: Option[AstNode])
  private case class GlobalArray(elemType: TypeSpec, length: Int, init: Option[List[AstNode]])

  private case class LocalVarInfo(offset: Int, varType: TypeSpec)
  private case class LocalArrayInfo(offset: Int, elemType: TypeSpec, length: Int)
  private case class ParamInfo(offset: Int, varType: TypeSpec)
  private case class LocalLayout(
    locals: Map[String, LocalVarInfo],
    arrays: Map[String, LocalArrayInfo],
    totalSize: Int
  )

  private case class MethodContext(
    name: String,
    returnsValue: Boolean,
    locals: Map[String, LocalVarInfo],
    localArrays: Map[String, LocalArrayInfo],
    params: Map[String, ParamInfo],
    endLabel: String,
    breakLabels: mutable.Stack[String],
    continueLabels: mutable.Stack[String]
  )

  def generate(root: RootNode): String = {
    collectGlobals(root.globals)
    emitHeader()
    emitDefines()
    emitDataSections()
    emitTextSection(root.methods)
    asm.toString()
  }

  private def emitHeader(): Unit = {
    emitRaw("; ArnoldC Native Assembly")
    emitRaw("; Generated from ArnoldC-Native")
    emitRaw("; \"GET YOUR ASS TO MARS\"")
    emitRaw("BITS 32")
    emitRaw("")
  }

  private def emitDefines(): Unit = {
    if (globalComments.nonEmpty) {
      globalComments.foreach(emitRaw)
      emitRaw("")
    }

    if (defines.nonEmpty) {
      defines.foreach { case (name, value) =>
        emitRaw(s"$name equ $value")
      }
      emitRaw("")
    }
  }

  private def emitDataSections(): Unit = {
    val dataLines = new mutable.ListBuffer[String]()
    val bssLines = new mutable.ListBuffer[String]()

    globalVars.foreach { case (name, info) =>
      val baseType = unwrapType(info.varType)
      info.init match {
        case Some(init) =>
          dataLines += s"$name: ${dataDirective(baseType)} ${constValue(init)}"
        case None =>
          bssLines += s"$name: ${bssDirective(baseType)} 1"
      }
    }

    globalArrays.foreach { case (name, info) =>
      val baseType = unwrapType(info.elemType)
      info.init match {
        case Some(values) =>
          val padded = values.map(constValue).padTo(info.length, "0")
          dataLines ++= formatDataArray(name, dataDirective(baseType), padded)
        case None =>
          bssLines += s"$name: ${bssDirective(baseType)} ${info.length}"
      }
    }

    if (dataLines.nonEmpty) {
      emitRaw("section .data")
      dataLines.foreach(emitRaw)
      emitRaw("")
    }

    if (bssLines.nonEmpty) {
      emitRaw("section .bss")
      bssLines.foreach(emitRaw)
      emitRaw("")
    }
  }

  private def emitTextSection(methods: List[AbstractMethodNode]): Unit = {
    emitRaw("section .text")
    emitRaw("global arnold_main")
    emitRaw("")

    methods.foreach { method =>
      generateMethod(method)
    }
  }

  private def collectGlobals(globals: List[AstNode]): Unit = {
    globals.foreach {
      case DefineNode(name, value) =>
        val defineValue = value.map(_.trim).filter(_.nonEmpty).getOrElse("1")
        defines(name) = defineValue

      case ArrayDeclareNode(name, elemType, size, initValues) =>
        globalArrays(name) = GlobalArray(elemType, size, initValues)

      case TypedDeclareNode(name, varType, initialValue) =>
        globalVars(name) = GlobalVar(varType, Some(initialValue))

      case DeclareIntNode(name, value) =>
        globalVars(name) = GlobalVar(I32Type, Some(value))

      case CommentNode(text) =>
        globalComments += s"; $text"

      case MultiLineCommentNode(lines) =>
        globalComments += ";"
        lines.foreach(line => globalComments += s"; $line")
        globalComments += ";"

      case _ =>
    }
  }

  private def generateMethod(method: AbstractMethodNode): Unit = {
    val methodName = method match {
      case _: MainMethodNode => "arnold_main"
      case other => other.methodName
    }

    val locals = mutable.LinkedHashMap[String, TypeSpec]()
    val localArrays = mutable.LinkedHashMap[String, LocalArrayInfo]()
    collectLocals(method.statements, locals, localArrays)

    val paramInfos = method.arguments.zipWithIndex.map { case (arg, index) =>
      val offset = 8 + (index * 4)
      arg.variableName -> ParamInfo(offset, I32Type)
    }.toMap

    val layout = allocateLocals(locals.toMap, localArrays.toMap)

    val endLabel = newLabel(s"${methodName}_end")
    val ctx = MethodContext(
      methodName,
      method.returnsValue,
      layout.locals,
      layout.arrays,
      paramInfos,
      endLabel,
      mutable.Stack[String](),
      mutable.Stack[String]()
    )

    emitRaw(s"; Method: $methodName")
    emitRaw(s"$methodName:")
    emitRaw("    push ebp")
    emitRaw("    mov ebp, esp")
    if (layout.totalSize > 0) {
      emitRaw(s"    sub esp, ${layout.totalSize}")
    }

    method.statements.foreach(stmt => emitStatement(stmt, ctx))

    emitRaw(s"${ctx.endLabel}:")
    emitRaw("    mov esp, ebp")
    emitRaw("    pop ebp")
    emitRaw("    ret")
    emitRaw("")
  }

  private def collectLocals(
    statements: List[StatementNode],
    locals: mutable.Map[String, TypeSpec],
    arrays: mutable.Map[String, LocalArrayInfo]
  ): Unit = {
    statements.foreach {
      case DeclareIntNode(name, _) =>
        locals.getOrElseUpdate(name, I32Type)

      case TypedDeclareNode(name, varType, _) =>
        locals.getOrElseUpdate(name, varType)

      case ArrayDeclareNode(name, elemType, size, _) =>
        if (!arrays.contains(name)) {
          arrays(name) = LocalArrayInfo(0, elemType, size)
        }

      case ForLoopNode(loopVar, _, _, _, body) =>
        locals.getOrElseUpdate(loopVar, I32Type)
        collectLocals(body, locals, arrays)

      case ConditionNode(_, ifBody, elseBody) =>
        collectLocals(ifBody, locals, arrays)
        collectLocals(elseBody, locals, arrays)

      case WhileNode(_, body) =>
        collectLocals(body, locals, arrays)

      case DoWhileNode(_, body) =>
        collectLocals(body, locals, arrays)

      case SwitchNode(_, cases, defaultCase) =>
        cases.foreach(c => collectLocals(c.statements, locals, arrays))
        defaultCase.foreach(stmts => collectLocals(stmts, locals, arrays))

      case _ =>
    }
  }

  private def allocateLocals(
    locals: Map[String, TypeSpec],
    arrays: Map[String, LocalArrayInfo]
  ): LocalLayout = {
    val localInfos = mutable.LinkedHashMap[String, LocalVarInfo]()
    val arrayInfos = mutable.LinkedHashMap[String, LocalArrayInfo]()
    var offset = 0

    locals.foreach { case (name, varType) =>
      val size = alignSize(typeSize(varType))
      offset += size
      localInfos(name) = LocalVarInfo(offset, varType)
    }

    arrays.foreach { case (name, info) =>
      val size = alignSize(typeSize(info.elemType) * info.length)
      offset += size
      arrayInfos(name) = LocalArrayInfo(offset, info.elemType, info.length)
    }

    LocalLayout(localInfos.toMap, arrayInfos.toMap, alignSize(offset))
  }

  private def emitStatement(stmt: StatementNode, ctx: MethodContext): Unit = stmt match {
    case DeclareIntNode(name, value) =>
      emitExpr(value, ctx)
      storeVariable(name, ctx, I32Type)

    case TypedDeclareNode(name, varType, value) =>
      emitExpr(value, ctx)
      storeVariable(name, ctx, varType)

    case AssignVariableNode(name, expr) =>
      emitExpr(expr, ctx)
      val varType = resolveVariableType(name, ctx).getOrElse(I32Type)
      storeVariable(name, ctx, varType)

    case ArrayAssignNode(arrayName, index, value) =>
      storeArrayValue(arrayName, index, value, ctx)

    case PointerWriteNode(pointer, value) =>
      storePointerValue(pointer, value, ctx, U32Type)

    case MemoryWriteNode(address, writeType, value) =>
      storePointerValue(address, value, ctx, writeType)

    case ConditionNode(condition, ifBody, elseBody) =>
      val elseLabel = newLabel("else")
      val endLabel = newLabel("endif")
      emitExpr(condition, ctx)
      emit("cmp eax, 0")
      emit(s"je $elseLabel")
      ifBody.foreach(stmt => emitStatement(stmt, ctx))
      emit(s"jmp $endLabel")
      emitRaw(s"$elseLabel:")
      elseBody.foreach(stmt => emitStatement(stmt, ctx))
      emitRaw(s"$endLabel:")

    case WhileNode(condition, body) =>
      val startLabel = newLabel("while_start")
      val endLabel = newLabel("while_end")
      ctx.breakLabels.push(endLabel)
      ctx.continueLabels.push(startLabel)
      emitRaw(s"$startLabel:")
      emitExpr(condition, ctx)
      emit("cmp eax, 0")
      emit(s"je $endLabel")
      body.foreach(stmt => emitStatement(stmt, ctx))
      emit(s"jmp $startLabel")
      emitRaw(s"$endLabel:")
      ctx.breakLabels.pop()
      ctx.continueLabels.pop()

    case DoWhileNode(condition, body) =>
      val startLabel = newLabel("do_start")
      val endLabel = newLabel("do_end")
      ctx.breakLabels.push(endLabel)
      ctx.continueLabels.push(startLabel)
      emitRaw(s"$startLabel:")
      body.foreach(stmt => emitStatement(stmt, ctx))
      emitExpr(condition, ctx)
      emit("cmp eax, 0")
      emit(s"jne $startLabel")
      emitRaw(s"$endLabel:")
      ctx.breakLabels.pop()
      ctx.continueLabels.pop()

    case ForLoopNode(loopVar, start, endCond, step, body) =>
      val startLabel = newLabel("for_start")
      val endLabel = newLabel("for_end")
      val continueLabel = newLabel("for_continue")
      ctx.breakLabels.push(endLabel)
      ctx.continueLabels.push(continueLabel)
      emitExpr(start, ctx)
      storeVariable(loopVar, ctx, I32Type)
      emitRaw(s"$startLabel:")
      loadVariable(loopVar, ctx, I32Type)
      emit("push eax")
      emitExpr(endCond, ctx)
      emit("pop ecx")
      emit("cmp ecx, eax")
      emit(s"jge $endLabel")
      body.foreach(stmt => emitStatement(stmt, ctx))
      emitRaw(s"$continueLabel:")
      loadVariable(loopVar, ctx, I32Type)
      emit("push eax")
      emitExpr(step, ctx)
      emit("pop ecx")
      emit("add eax, ecx")
      storeVariable(loopVar, ctx, I32Type)
      emit(s"jmp $startLabel")
      emitRaw(s"$endLabel:")
      ctx.breakLabels.pop()
      ctx.continueLabels.pop()

    case SwitchNode(variable, cases, defaultCase) =>
      val endLabel = newLabel("switch_end")
      val defaultLabel = defaultCase.map(_ => newLabel("switch_default")).getOrElse(endLabel)
      val caseLabels = cases.map(_ => newLabel("case"))
      ctx.breakLabels.push(endLabel)

      val switchType = resolveVariableType(variable, ctx).getOrElse(I32Type)
      cases.zip(caseLabels).foreach { case (c, caseLabel) =>
        loadVariable(variable, ctx, switchType)
        emit("push eax")
        emitExpr(c.value, ctx)
        emit("pop ecx")
        emit("cmp ecx, eax")
        emit(s"je $caseLabel")
      }
      emit(s"jmp $defaultLabel")

      cases.zip(caseLabels).foreach { case (c, caseLabel) =>
        emitRaw(s"$caseLabel:")
        c.statements.foreach(stmt => emitStatement(stmt, ctx))
        if (!c.fallthrough) {
          emit(s"jmp $endLabel")
        }
      }

      defaultCase.foreach { stmts =>
        emitRaw(s"$defaultLabel:")
        stmts.foreach(stmt => emitStatement(stmt, ctx))
      }

      emitRaw(s"$endLabel:")
      ctx.breakLabels.pop()

    case BreakNode() =>
      ctx.breakLabels.headOption.foreach(label => emit(s"jmp $label"))

    case ContinueNode() =>
      ctx.continueLabels.headOption.foreach(label => emit(s"jmp $label"))

    case CallMethodNode(assignTo, methodName, args) =>
      args.reverse.foreach { arg =>
        emitExpr(arg, ctx)
        emit("push eax")
      }

      val target = if (methodName == "main") "arnold_main" else methodName
      emit(s"call $target")
      if (args.nonEmpty) {
        emit(s"add esp, ${args.size * 4}")
      }

      if (assignTo.nonEmpty) {
        val varType = resolveVariableType(assignTo, ctx).getOrElse(I32Type)
        storeVariable(assignTo, ctx, varType)
      }

    case ReturnNode(value) =>
      value.foreach(v => emitExpr(v, ctx))
      emit(s"jmp ${ctx.endLabel}")

    case OutbNode(port, value) =>
      emitExpr(port, ctx)
      emitExpr(value, ctx)
      emit("push eax")
      emitExpr(port, ctx)
      emit("mov dx, ax")
      emit("pop eax")
      emit("out dx, al")

    case InbNode(port, assignTo) =>
      emitExpr(port, ctx)
      emit("mov dx, ax")
      emit("in al, dx")
      emit("movzx eax, al")
      assignTo.foreach { name =>
        val varType = resolveVariableType(name, ctx).getOrElse(U8Type)
        storeVariable(name, ctx, varType)
      }

    case OutwNode(port, value) =>
      emitExpr(value, ctx)
      emit("push eax")
      emitExpr(port, ctx)
      emit("mov dx, ax")
      emit("pop eax")
      emit("out dx, ax")

    case InwNode(port, assignTo) =>
      emitExpr(port, ctx)
      emit("mov dx, ax")
      emit("in ax, dx")
      emit("movzx eax, ax")
      assignTo.foreach { name =>
        val varType = resolveVariableType(name, ctx).getOrElse(U16Type)
        storeVariable(name, ctx, varType)
      }

    case OutlNode(port, value) =>
      emitExpr(value, ctx)
      emit("push eax")
      emitExpr(port, ctx)
      emit("mov dx, ax")
      emit("pop eax")
      emit("out dx, eax")

    case InlNode(port, assignTo) =>
      emitExpr(port, ctx)
      emit("mov dx, ax")
      emit("in eax, dx")
      assignTo.foreach { name =>
        val varType = resolveVariableType(name, ctx).getOrElse(I32Type)
        storeVariable(name, ctx, varType)
      }

    case CliNode() =>
      emit("cli")

    case StiNode() =>
      emit("sti")

    case HltNode() =>
      emit("hlt")

    case PauseNode() =>
      emit("pause")

    case NopNode() =>
      emit("nop")

    case CommentNode(text) =>
      emitRaw(s"; $text")

    case MultiLineCommentNode(lines) =>
      emitRaw(";")
      lines.foreach(line => emitRaw(s"; $line"))
      emitRaw(";")

    case _ =>
      emitRaw(s"; Unhandled statement: ${stmt.getClass.getSimpleName}")
  }

  private def emitExpr(expr: AstNode, ctx: MethodContext): Unit = expr match {
    case NumberNode(value) =>
      emit(s"mov eax, $value")

    case HexNumberNode(value) =>
      emit(s"mov eax, 0x${value.toHexString}")

    case CharLiteralNode(value) =>
      emit(s"mov eax, ${value.toInt}")

    case VariableNode(name) =>
      loadVariable(name, ctx, resolveVariableType(name, ctx).getOrElse(I32Type))

    case ArrayAccessNode(arrayVar, index) =>
      loadArrayValue(arrayVar, index, ctx)

    case PlusExpressionNode(left, right) =>
      emitExpr(left, ctx)
      emit("push eax")
      emitExpr(right, ctx)
      emit("pop ecx")
      emit("add eax, ecx")

    case MinusExpressionNode(left, right) =>
      emitExpr(left, ctx)
      emit("push eax")
      emitExpr(right, ctx)
      emit("pop ecx")
      emit("sub ecx, eax")
      emit("mov eax, ecx")

    case MultiplicationExpressionNode(left, right) =>
      emitExpr(left, ctx)
      emit("push eax")
      emitExpr(right, ctx)
      emit("pop ecx")
      emit("imul eax, ecx")

    case DivisionExpressionNode(left, right) =>
      emitExpr(left, ctx)
      emit("push eax")
      emitExpr(right, ctx)
      emit("mov ecx, eax")
      emit("pop eax")
      emit("cdq")
      emit("idiv ecx")

    case ModuloExpressionNode(left, right) =>
      emitExpr(left, ctx)
      emit("push eax")
      emitExpr(right, ctx)
      emit("mov ecx, eax")
      emit("pop eax")
      emit("cdq")
      emit("idiv ecx")
      emit("mov eax, edx")

    case EqualToNode(left, right) =>
      emitCompare(left, right, "sete", ctx)

    case NotEqualNode(left, right) =>
      emitCompare(left, right, "setne", ctx)

    case GreaterThanNode(left, right) =>
      emitCompare(left, right, "setg", ctx)

    case GreaterThanOrEqualNode(left, right) =>
      emitCompare(left, right, "setge", ctx)

    case LessThanNode(left, right) =>
      emitCompare(left, right, "setl", ctx)

    case LessThanOrEqualNode(left, right) =>
      emitCompare(left, right, "setle", ctx)

    case OrNode(left, right) =>
      emitExpr(left, ctx)
      emit("push eax")
      emitExpr(right, ctx)
      emit("pop ecx")
      emit("or eax, ecx")

    case AndNode(left, right) =>
      emitExpr(left, ctx)
      emit("push eax")
      emitExpr(right, ctx)
      emit("pop ecx")
      emit("and eax, ecx")

    case LogicalNotNode(operand) =>
      emitExpr(operand, ctx)
      emit("cmp eax, 0")
      emit("sete al")
      emit("movzx eax, al")

    case LogicalNotWrapperNode(operand) =>
      emitExpr(operand, ctx)
      emit("cmp eax, 0")
      emit("sete al")
      emit("movzx eax, al")

    case BitwiseAndExprNode(left, right) =>
      emitExpr(left, ctx)
      emit("push eax")
      emitExpr(right, ctx)
      emit("pop ecx")
      emit("and eax, ecx")

    case BitwiseOrExprNode(left, right) =>
      emitExpr(left, ctx)
      emit("push eax")
      emitExpr(right, ctx)
      emit("pop ecx")
      emit("or eax, ecx")

    case BitwiseXorExprNode(left, right) =>
      emitExpr(left, ctx)
      emit("push eax")
      emitExpr(right, ctx)
      emit("pop ecx")
      emit("xor eax, ecx")

    case BitwiseNotExprNode(operand) =>
      emitExpr(operand, ctx)
      emit("not eax")

    case BitwiseNotWrapperNode(operand) =>
      emitExpr(operand, ctx)
      emit("not eax")

    case LeftShiftExprNode(left, right) =>
      emitExpr(left, ctx)
      emit("push eax")
      emitExpr(right, ctx)
      emit("mov ecx, eax")
      emit("pop eax")
      emit("shl eax, cl")

    case RightShiftExprNode(left, right) =>
      emitExpr(left, ctx)
      emit("push eax")
      emitExpr(right, ctx)
      emit("mov ecx, eax")
      emit("pop eax")
      emit("shr eax, cl")

    case UnsignedRightShiftExprNode(left, right) =>
      emitExpr(left, ctx)
      emit("push eax")
      emitExpr(right, ctx)
      emit("mov ecx, eax")
      emit("pop eax")
      emit("shr eax, cl")

    case AddressOfNode(variable) =>
      emitAddressOf(variable, ctx)

    case DereferenceNode(pointer) =>
      emitExpr(pointer, ctx)
      emit("mov eax, [eax]")

    case PointerAddNode(pointer, offset) =>
      emitExpr(pointer, ctx)
      emit("push eax")
      emitExpr(offset, ctx)
      emit("pop ecx")
      emit("add eax, ecx")

    case PointerSubNode(pointer, offset) =>
      emitExpr(pointer, ctx)
      emit("push eax")
      emitExpr(offset, ctx)
      emit("pop ecx")
      emit("sub ecx, eax")
      emit("mov eax, ecx")

    case TypeCastNode(_, expr) =>
      emitExpr(expr, ctx)

    case SizeofTypeNode(targetType) =>
      emit(s"mov eax, ${typeSize(targetType)}")

    case SizeofExprNode(variable) =>
      val size = resolveVariableType(variable, ctx).map(typeSize).getOrElse(4)
      emit(s"mov eax, $size")

    case MemoryReadNode(address, readType) =>
      emitExpr(address, ctx)
      loadFromPointer(readType)

    case InbExprNode(port) =>
      emitExpr(port, ctx)
      emit("mov dx, ax")
      emit("in al, dx")
      emit("movzx eax, al")

    case InwExprNode(port) =>
      emitExpr(port, ctx)
      emit("mov dx, ax")
      emit("in ax, dx")
      emit("movzx eax, ax")

    case InlExprNode(port) =>
      emitExpr(port, ctx)
      emit("mov dx, ax")
      emit("in eax, dx")

    case TernaryNode(condition, thenExpr, elseExpr) =>
      val elseLabel = newLabel("ternary_else")
      val endLabel = newLabel("ternary_end")
      emitExpr(condition, ctx)
      emit("cmp eax, 0")
      emit(s"je $elseLabel")
      emitExpr(thenExpr, ctx)
      emit(s"jmp $endLabel")
      emitRaw(s"$elseLabel:")
      emitExpr(elseExpr, ctx)
      emitRaw(s"$endLabel:")

    case _ =>
      emit("mov eax, 0")
  }

  private def loadVariable(name: String, ctx: MethodContext, varType: TypeSpec): Unit = {
    val baseType = unwrapType(varType)
    ctx.locals.get(name) match {
      case Some(info) =>
        val offset = info.offset
        loadFromStack(offset, baseType)
      case None =>
        ctx.localArrays.get(name) match {
          case Some(info) =>
            emit(s"lea eax, [ebp-${info.offset}]")
          case None =>
            ctx.params.get(name) match {
              case Some(param) =>
                loadFromParam(param.offset, baseType)
              case None =>
                globalVars.get(name) match {
                  case Some(global) =>
                    loadFromGlobal(name, unwrapType(global.varType))
                  case None =>
                    globalArrays.get(name) match {
                      case Some(_) =>
                        emit(s"lea eax, [$name]")
                      case None =>
                        emit(s"mov eax, $name")
                    }
                }
            }
        }
    }
  }

  private def storeVariable(name: String, ctx: MethodContext, varType: TypeSpec): Unit = {
    val baseType = unwrapType(varType)
    ctx.locals.get(name) match {
      case Some(info) =>
        val offset = info.offset
        storeToStack(offset, baseType)
      case None =>
        ctx.params.get(name) match {
          case Some(param) =>
            storeToParam(param.offset, baseType)
          case None =>
            if (globalVars.contains(name)) {
              storeToGlobal(name, baseType)
            }
        }
    }
  }

  private def loadArrayValue(arrayName: String, index: AstNode, ctx: MethodContext): Unit = {
    emitExpr(index, ctx)
    emit("mov ecx, eax")

    ctx.localArrays.get(arrayName) match {
      case Some(info) =>
        emitLeaScaled(s"ebp-${info.offset}", info.elemType, "ecx")
        loadFromPointer(info.elemType)
      case None =>
        globalArrays.get(arrayName) match {
          case Some(info) =>
            emitLeaScaled(arrayName, info.elemType, "ecx")
            loadFromPointer(info.elemType)
          case None =>
            emit("mov eax, 0")
        }
    }
  }

  private def storeArrayValue(arrayName: String, index: AstNode, value: AstNode, ctx: MethodContext): Unit = {
    emitExpr(index, ctx)
    emit("mov ecx, eax")

    ctx.localArrays.get(arrayName) match {
      case Some(info) =>
        emitLeaScaled(s"ebp-${info.offset}", info.elemType, "ecx")
        emit("push eax")
        emitExpr(value, ctx)
        emit("pop ecx")
        storeToPointer(info.elemType)
      case None =>
        globalArrays.get(arrayName) match {
          case Some(info) =>
            emitLeaScaled(arrayName, info.elemType, "ecx")
            emit("push eax")
            emitExpr(value, ctx)
            emit("pop ecx")
            storeToPointer(info.elemType)
          case None =>
        }
    }
  }

  private def emitAddressOf(variable: String, ctx: MethodContext): Unit = {
    ctx.locals.get(variable) match {
      case Some(info) => emit(s"lea eax, [ebp-${info.offset}]")
      case None =>
        ctx.params.get(variable) match {
          case Some(param) => emit(s"lea eax, [ebp+${param.offset}]")
          case None => emit(s"lea eax, [$variable]")
        }
    }
  }

  private def loadFromStack(offset: Int, varType: TypeSpec): Unit = {
    val addr = s"[ebp-$offset]"
    loadFromAddress(addr, varType)
  }

  private def loadFromParam(offset: Int, varType: TypeSpec): Unit = {
    val addr = s"[ebp+$offset]"
    loadFromAddress(addr, varType)
  }

  private def loadFromGlobal(name: String, varType: TypeSpec): Unit = {
    val addr = s"[$name]"
    loadFromAddress(addr, varType)
  }

  private def loadFromAddress(addr: String, varType: TypeSpec): Unit = {
    typeSize(varType) match {
      case 1 => emit(s"movzx eax, byte $addr")
      case 2 => emit(s"movzx eax, word $addr")
      case _ => emit(s"mov eax, dword $addr")
    }
  }

  private def storeToStack(offset: Int, varType: TypeSpec): Unit = {
    val addr = s"[ebp-$offset]"
    storeToAddress(addr, varType)
  }

  private def storeToParam(offset: Int, varType: TypeSpec): Unit = {
    val addr = s"[ebp+$offset]"
    storeToAddress(addr, varType)
  }

  private def storeToGlobal(name: String, varType: TypeSpec): Unit = {
    val addr = s"[$name]"
    storeToAddress(addr, varType)
  }

  private def storeToAddress(addr: String, varType: TypeSpec): Unit = {
    typeSize(varType) match {
      case 1 => emit(s"mov byte $addr, al")
      case 2 => emit(s"mov word $addr, ax")
      case _ => emit(s"mov dword $addr, eax")
    }
  }

  private def storePointerValue(address: AstNode, value: AstNode, ctx: MethodContext, writeType: TypeSpec): Unit = {
    emitExpr(address, ctx)
    emit("push eax")
    emitExpr(value, ctx)
    emit("pop ecx")
    storeToPointer(writeType)
  }

  private def loadFromPointer(varType: TypeSpec): Unit = {
    typeSize(varType) match {
      case 1 => emit("movzx eax, byte [eax]")
      case 2 => emit("movzx eax, word [eax]")
      case _ => emit("mov eax, dword [eax]")
    }
  }

  private def storeToPointer(varType: TypeSpec): Unit = {
    typeSize(varType) match {
      case 1 => emit("mov byte [ecx], al")
      case 2 => emit("mov word [ecx], ax")
      case _ => emit("mov dword [ecx], eax")
    }
  }

  private def emitLeaScaled(base: String, elemType: TypeSpec, indexReg: String): Unit = {
    val scale = typeSize(elemType) match {
      case 1 => ""
      case 2 => "*2"
      case 4 => "*4"
      case other => s"*$other"
    }

    val scaledIndex = if (scale.isEmpty) indexReg else s"$indexReg$scale"
    emit(s"lea eax, [$base + $scaledIndex]")
  }

  private def resolveVariableType(name: String, ctx: MethodContext): Option[TypeSpec] = {
    ctx.locals.get(name).map(_.varType)
      .orElse(ctx.localArrays.get(name).map(info => ArrayType(info.elemType, info.length)))
      .orElse(ctx.params.get(name).map(_.varType))
      .orElse(globalVars.get(name).map(_.varType))
      .orElse(globalArrays.get(name).map(info => ArrayType(info.elemType, info.length)))
  }

  private def emitCompare(left: AstNode, right: AstNode, setInstr: String, ctx: MethodContext): Unit = {
    emitExpr(left, ctx)
    emit("push eax")
    emitExpr(right, ctx)
    emit("pop ecx")
    emit("cmp ecx, eax")
    emit(s"$setInstr al")
    emit("movzx eax, al")
  }

  private def constValue(node: AstNode): String = node match {
    case NumberNode(value) => value.toString
    case HexNumberNode(value) => s"0x${value.toHexString}"
    case CharLiteralNode(value) => value.toInt.toString
    case VariableNode(name) => name
    case _ => "0"
  }

  private def formatDataArray(label: String, directive: String, values: List[String]): List[String] = {
    val chunks = values.grouped(16).toList
    chunks.zipWithIndex.map { case (chunk, index) =>
      val prefix = if (index == 0) s"$label: $directive" else s"    $directive"
      prefix + " " + chunk.mkString(", ")
    }
  }

  private def dataDirective(varType: TypeSpec): String = typeSize(varType) match {
    case 1 => "db"
    case 2 => "dw"
    case 4 => "dd"
    case _ => "dd"
  }

  private def bssDirective(varType: TypeSpec): String = typeSize(varType) match {
    case 1 => "resb"
    case 2 => "resw"
    case 4 => "resd"
    case _ => "resd"
  }

  private def unwrapType(varType: TypeSpec): TypeSpec = varType match {
    case ConstType(inner) => unwrapType(inner)
    case VolatileType(inner) => unwrapType(inner)
    case other => other
  }

  private def typeSize(varType: TypeSpec): Int = unwrapType(varType).size match {
    case 0 => 4
    case other => other
  }

  private def alignSize(size: Int): Int = {
    val align = 4
    ((size + align - 1) / align) * align
  }

  private def emit(line: String): Unit = {
    asm.append("    ").append(line).append("\n")
  }

  private def emitRaw(line: String): Unit = {
    asm.append(line).append("\n")
  }

  private def newLabel(prefix: String): String = {
    labelCounter += 1
    s"${prefix}_$labelCounter"
  }

}
