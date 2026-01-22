package org.arnoldc

import org.parboiled.scala._
import org.parboiled.errors.{ErrorUtils, ParsingException}
import org.arnoldc.ast._

/**
 * Extended ArnoldC Parser for Systems Programming
 * "GET TO THE CHOPPER" - Full-featured parser for bare-metal programming
 * 
 * This extends the original ArnoldC parser with:
 * - Type system (u8, u16, u32, u64, i8, i16, i32, i64, void, char)
 * - Pointers and memory operations
 * - Struct and union definitions
 * - Array operations
 * - Bitwise operators
 * - Extended control flow (for, switch, goto, break, continue)
 * - Inline assembly
 * - Function pointers
 * - Hardware access (I/O ports, interrupts)
 */
class ArnoldParserExtended extends Parser {

  // ===== ERROR MESSAGES - Arnold style =====
  val ParseError = "WHAT THE FUCK DID I DO WRONG"
  
  // ===== ORIGINAL ARNOLDC KEYWORDS =====
  val DeclareInt = "HEY CHRISTMAS TREE"
  val SetInitialValue = "YOU SET US UP"
  val BeginMain = "IT'S SHOWTIME"
  val PlusOperator = "GET UP"
  val MinusOperator = "GET DOWN"
  val MultiplicationOperator = "YOU'RE FIRED"
  val DivisionOperator = "HE HAD TO SPLIT"
  val EndMain = "YOU HAVE BEEN TERMINATED"
  val Print = "TALK TO THE HAND"
  val Read = "I WANT TO ASK YOU A BUNCH OF QUESTIONS AND I WANT TO HAVE THEM ANSWERED IMMEDIATELY"
  val AssignVariable = "GET TO THE CHOPPER"
  val SetValue = "HERE IS MY INVITATION"
  val EndAssignVariable = "ENOUGH TALK"
  val False = "I LIED"
  val True = "NO PROBLEMO"
  val EqualTo = "YOU ARE NOT YOU YOU ARE ME"
  val GreaterThan = "LET OFF SOME STEAM BENNET"
  val Or = "CONSIDER THAT A DIVORCE"
  val And = "KNOCK KNOCK"
  val If = "BECAUSE I'M GOING TO SAY PLEASE"
  val Else = "BULLSHIT"
  val EndIf = "YOU HAVE NO RESPECT FOR LOGIC"
  val While = "STICK AROUND"
  val EndWhile = "CHILL"
  val DeclareMethod = "LISTEN TO ME VERY CAREFULLY"
  val MethodArguments = "I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE"
  val Return = "I'LL BE BACK"
  val EndMethodDeclaration = "HASTA LA VISTA, BABY"
  val CallMethod = "DO IT NOW"
  val NonVoidMethod = "GIVE THESE PEOPLE AIR"
  val AssignVariableFromMethodCall = "GET YOUR ASS TO MARS"
  val Modulo = "I LET HIM GO"

  // ===== TYPE SYSTEM KEYWORDS =====
  val TypeU8 = "THIS IS A TINY WARRIOR"
  val TypeU16 = "THIS IS A SMALL WARRIOR"
  val TypeU32 = "THIS IS A WARRIOR"
  val TypeU64 = "THIS IS A BIG WARRIOR"
  val TypeI8 = "THIS IS A TINY ENEMY"
  val TypeI16 = "THIS IS A SMALL ENEMY"
  val TypeI32 = "THIS IS AN ENEMY"
  val TypeI64 = "THIS IS A BIG ENEMY"
  val TypeVoid = "THIS IS NOTHING"
  val TypeChar = "THIS IS A LETTER"
  val TypeConst = "THIS IS ALWAYS THE SAME"
  val TypeVolatile = "THIS COULD CHANGE ANYTIME"

  // ===== POINTER KEYWORDS =====
  val PointerTypeKw = "POINT YOUR GUN AT"  // Renamed to avoid collision with PointerType case class
  val AddressOf = "WHERE ARE YOU"
  val Dereference = "SHOW ME WHAT YOU GOT"
  val PointerAssign = "AIM AT"
  val PointerWrite = "FIRE AT"

  // ===== STRUCT/UNION KEYWORDS =====
  val StructBegin = "THIS IS WHAT I AM"
  val StructMember = "THIS IS WHAT I'M MADE OF"
  val StructEnd = "I'M DONE DESCRIBING MYSELF"
  val UnionBegin = "THIS IS MY DISGUISE"
  val UnionMember = "I CAN LOOK LIKE"
  val UnionEnd = "DISGUISE COMPLETE"
  val CreateStruct = "CREATE ONE LIKE"
  val MemberAccess = "WHAT'S YOUR"
  val PointerMemberAccess = "SHOW ME THE"

  // ===== ARRAY KEYWORDS =====
  val ArrayDeclare = "LINE THEM UP"
  val ArraySize = "HOW MANY"
  val ArrayAccess = "WHICH ONE"
  val ArrayInit = "PUT THEM IN LINE"

  // ===== BITWISE KEYWORDS =====
  val BitwiseAnd = "CRUSH THEM TOGETHER"
  val BitwiseOr = "JOIN THEM TOGETHER"
  val BitwiseXor = "CONFUSE THEM"
  val BitwiseNot = "TURN IT AROUND"
  val ShiftLeft = "PUSH IT LEFT"
  val ShiftRight = "PUSH IT RIGHT"

  // ===== CONTROL FLOW KEYWORDS =====
  val ForBegin = "I'LL COUNT FROM"
  val ForTo = "TO"
  val ForStep = "COUNT BY"
  val ForEnd = "STOP COUNTING"
  val SwitchBegin = "WHAT'S THE CHOICE"
  val SwitchCase = "WHEN IT'S"
  val SwitchDefault = "OTHERWISE"
  val SwitchEnd = "NO MORE CHOICES"
  val Break = "GET OUT"
  val Continue = "DO IT AGAIN"
  val Goto = "GO TO"
  val Label = "YOU ARE HERE"
  val DoWhileBegin = "DO THIS FIRST"
  val DoWhileEnd = "THEN STICK AROUND"
  
  // ===== COMPARISON KEYWORDS =====
  val LessThan = "YOU'RE NOT BIG ENOUGH"
  val LessThanOrEqual = "YOU'RE NOT BIGGER THAN"
  val GreaterThanOrEqual = "YOU'RE AT LEAST AS BIG AS"
  val NotEqual = "YOU ARE NOT ME"
  val LogicalNot = "THAT'S A LIE"
  
  // ===== ENUM KEYWORDS =====
  val EnumBegin = "THESE ARE MY OPTIONS"
  val EnumValueKw = "OPTION"
  val EnumEnd = "NO MORE OPTIONS"
  
  // ===== PREPROCESSOR KEYWORDS =====
  val Define = "LET ME TELL YOU SOMETHING"
  val Include = "BRING IN"
  val IfDef = "IF YOU KNOW"
  val IfNotDef = "IF YOU DON'T KNOW"
  val EndIfDef = "THAT'S ALL I KNOW"
  
  // ===== STORAGE CLASS KEYWORDS =====
  val Static = "THIS STAYS HERE"
  val Extern = "SOMEWHERE ELSE"
  val Packed = "SQUEEZE THEM TOGETHER"
  
  // ===== NULL KEYWORD =====
  val NullValue = "THERE IS NO ONE"

  // ===== MEMORY KEYWORDS =====
  val Alloc = "I NEED YOUR MEMORY"
  val Free = "YOU'RE LUGGAGE"
  val MemRead = "LOOK AT"
  val MemWrite = "WRITE TO"
  val MemCopy = "COPY FROM"
  val MemCopyTo = "TO"
  val MemSet = "FILL WITH"
  val Sizeof = "HOW BIG IS"
  val SizeofExpr = "HOW BIG IS THAT"
  val Cast = "MAKE IT A"

  // ===== I/O PORT KEYWORDS =====
  val OutPort = "TALK TO THE PORT"
  val InPort = "LISTEN TO THE PORT"
  val OutPortWord = "TALK BIG TO THE PORT"
  val InPortWord = "LISTEN BIG TO THE PORT"
  val OutPortDword = "TALK HUGE TO THE PORT"
  val InPortDword = "LISTEN HUGE TO THE PORT"

  // ===== INTERRUPT KEYWORDS =====
  val DisableInterrupts = "EVERYBODY CHILL"
  val EnableInterrupts = "LET'S PARTY"
  val Halt = "SLEEP NOW"
  val Pause = "WAIT A MOMENT"
  val Nop = "TAKE A BREAK"

  // ===== INLINE ASSEMBLY KEYWORDS =====
  val AsmBegin = "SPEAK TO THE MACHINE"
  val AsmEnd = "THE MACHINE SAYS"
  val AsmOutput = "MACHINE OUTPUT"
  val AsmInput = "MACHINE INPUT"
  val AsmClobber = "MACHINE DESTROYS"

  // ===== FUNCTION POINTER KEYWORDS =====
  val FuncPtrDeclare = "REMEMBER THIS MOVE"
  val FuncPtrSignature = "LOOKS LIKE"
  val FuncPtrAssign = "LEARN THE MOVE"
  val FuncPtrCall = "USE THAT MOVE"

  // ===== COMMENT KEYWORDS =====
  val Comment = "TALK TO YOURSELF"
  val MultiLineCommentBegin = "LET ME THINK ABOUT THIS"
  val MultiLineCommentEnd = "I'VE THOUGHT ABOUT IT"

  // ===== WHITESPACE AND STRUCTURE =====
  val EOL = zeroOrMore("\t" | "\r" | " ") ~ "\n" ~ zeroOrMore("\t" | "\r" | " " | "\n")
  val OptEOL = zeroOrMore("\t" | "\r" | " " | "\n")
  val WhiteSpace = oneOrMore(" " | "\t")
  val OptWhiteSpace = zeroOrMore(" " | "\t")

  // ===== ROOT GRAMMAR =====
  
  def Root: Rule1[RootNode] = rule {
    OptEOL ~ zeroOrMore(GlobalDeclaration) ~ oneOrMore(AbstractMethod) ~ EOI ~~> { 
      (globals: List[AstNode], methods: List[AbstractMethodNode]) => 
        RootNode(methods)  // TODO: handle globals properly
    }
  }

  def GlobalDeclaration: Rule1[AstNode] = rule {
    PreprocessorDirective | StructDefinition | UnionDefinition | EnumDefinition | GlobalVariable
  }

  def GlobalVariable: Rule1[AstNode] = rule {
    DeclareInt ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~ 
    optional(TypeSpecification ~ EOL) ~
    SetInitialValue ~ WhiteSpace ~ Operand ~~> { 
      (name: String, typeOpt: Option[TypeSpec], value: OperandNode) =>
        typeOpt match {
          case Some(t) => TypedDeclareNode(name, t, value)
          case None => DeclareIntNode(name, value)
        }
    } ~ EOL
  }

  // ===== TYPE SPECIFICATION =====

  def TypeSpecification: Rule1[TypeSpec] = rule {
    TypeModifier | BaseType
  }

  def TypeModifier: Rule1[TypeSpec] = rule {
    TypeConst ~ EOL ~ TypeSpecification ~~> ((t: TypeSpec) => ConstType(t)) |
    TypeVolatile ~ EOL ~ TypeSpecification ~~> ((t: TypeSpec) => VolatileType(t)) |
    PointerTypeKw ~ EOL ~ TypeSpecification ~~> ((t: TypeSpec) => PointerType(t))
  }

  def BaseType: Rule1[TypeSpec] = rule {
    TypeU8 ~> (_ => U8Type) |
    TypeU16 ~> (_ => U16Type) |
    TypeU32 ~> (_ => U32Type) |
    TypeU64 ~> (_ => U64Type) |
    TypeI8 ~> (_ => I8Type) |
    TypeI16 ~> (_ => I16Type) |
    TypeI32 ~> (_ => I32Type) |
    TypeI64 ~> (_ => I64Type) |
    TypeVoid ~> (_ => VoidType) |
    TypeChar ~> (_ => CharType) |
    CreateStruct ~ WhiteSpace ~ VariableName ~> (name => StructRefType(name)) |
    "CHOOSE FROM" ~ WhiteSpace ~ VariableName ~> (name => EnumRefType(name))
  }

  // ===== STRUCT DEFINITIONS =====

  def StructDefinition: Rule1[StructDefNode] = rule {
    StructBegin ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
    zeroOrMore(StructMemberDef) ~
    StructEnd ~ EOL ~~> StructDefNode
  }

  def StructMemberDef: Rule1[StructMember] = rule {
    StructMember ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
    TypeSpecification ~ EOL ~~> { (name: String, typeSpec: TypeSpec) =>
      StructMember(name, typeSpec)
    }
  }

  def UnionDefinition: Rule1[UnionDefNode] = rule {
    UnionBegin ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
    zeroOrMore(UnionMemberDef) ~
    UnionEnd ~ EOL ~~> UnionDefNode
  }

  def UnionMemberDef: Rule1[StructMember] = rule {
    UnionMember ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
    TypeSpecification ~ EOL ~~> { (name: String, typeSpec: TypeSpec) =>
      StructMember(name, typeSpec)
    }
  }

  // ===== ENUM DEFINITIONS =====

  def EnumDefinition: Rule1[EnumDefNode] = rule {
    EnumBegin ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
    oneOrMore(EnumValueDef) ~
    EnumEnd ~ EOL ~~> { (name: String, values: List[EnumValue]) =>
      EnumDefNode(name, values)
    }
  }

  def EnumValueDef: Rule1[EnumValue] = rule {
    EnumValueKw ~ WhiteSpace ~ VariableName ~> (s => s) ~ 
    optional(WhiteSpace ~ Number) ~ EOL ~~> { (name: String, value: Option[NumberNode]) =>
      EnumValue(name, value.map(_.value))
    }
  }

  // ===== PREPROCESSOR DIRECTIVES =====

  def PreprocessorDirective: Rule1[PreprocessorNode] = rule {
    DefineDirective |
    IncludeDirective |
    IfDefDirective |
    IfNotDefDirective |
    PragmaOnceDirective
  }

  // LET ME TELL YOU SOMETHING DEBUG 1
  def DefineDirective: Rule1[DefineNode] = rule {
    Define ~ WhiteSpace ~ VariableName ~> (s => s) ~
    optional(WhiteSpace ~ PreprocessorValue) ~ EOL ~~> { (name: String, value: Option[String]) =>
      DefineNode(name, value)
    }
  }

  def PreprocessorValue: Rule1[String] = rule {
    oneOrMore(!EOL ~ ANY) ~> (s => s)
  }

  // BRING IN "filename.h"
  // BRING IN <system.h>
  def IncludeDirective: Rule1[IncludeNode] = rule {
    Include ~ WhiteSpace ~ (
      "\"" ~ zeroOrMore(!anyOf("\"") ~ ANY) ~> (s => s) ~ "\"" ~~> { (filename: String) =>
        IncludeNode(filename, isSystemHeader = false)
      } |
      "<" ~ zeroOrMore(!anyOf(">") ~ ANY) ~> (s => s) ~ ">" ~~> { (filename: String) =>
        IncludeNode(filename, isSystemHeader = true)
      }
    ) ~ EOL
  }

  // IF YOU KNOW DEBUG
  // ... statements ...
  // THAT'S ALL I KNOW
  def IfDefDirective: Rule1[IfDefNode] = rule {
    IfDef ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
    zeroOrMore(PreprocessorBodyStatement) ~
    optional(Else ~ EOL ~ zeroOrMore(PreprocessorBodyStatement)) ~
    EndIfDef ~ EOL ~~> { (cond: String, thenStmts: List[AstNode], elseOpt: Option[List[AstNode]]) =>
      IfDefNode(cond, thenStmts, elseOpt.getOrElse(List()))
    }
  }

  // IF YOU DON'T KNOW DEBUG
  def IfNotDefDirective: Rule1[IfNotDefNode] = rule {
    IfNotDef ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
    zeroOrMore(PreprocessorBodyStatement) ~
    optional(Else ~ EOL ~ zeroOrMore(PreprocessorBodyStatement)) ~
    EndIfDef ~ EOL ~~> { (cond: String, thenStmts: List[AstNode], elseOpt: Option[List[AstNode]]) =>
      IfNotDefNode(cond, thenStmts, elseOpt.getOrElse(List()))
    }
  }

  // ONLY ONCE (pragma once)
  def PragmaOnceDirective: Rule1[PragmaOnceNode] = rule {
    "ONLY ONCE" ~ EOL ~> (_ => PragmaOnceNode())
  }

  // Statements that can appear inside preprocessor conditionals
  def PreprocessorBodyStatement: Rule1[AstNode] = rule {
    DefineDirective | Statement
  }

  // ===== METHOD DEFINITIONS =====

  def AbstractMethod: Rule1[AbstractMethodNode] = rule {
    (MainMethod | Method) ~ optional(EOL)
  }

  def MainMethod: Rule1[AbstractMethodNode] = rule {
    BeginMain ~ EOL ~ zeroOrMore(Statement) ~ EndMain ~~> MainMethodNode
  }

  def Method: Rule1[AbstractMethodNode] = rule {
    DeclareMethod ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
    zeroOrMore(MethodParam) ~
    optional(MethodReturnType) ~> ((m: Option[Boolean]) => m.getOrElse(false)) ~
    zeroOrMore(Statement) ~ EndMethodDeclaration ~~> MethodNode
  }

  def MethodParam: Rule1[VariableNode] = rule {
    MethodArguments ~ WhiteSpace ~ Variable ~ optional(EOL ~ TypeSpecification) ~ EOL
  }

  def MethodReturnType: Rule1[Boolean] = rule {
    NonVoidMethod ~ optional(EOL ~ TypeSpecification) ~ EOL ~> (_ => true)
  }

  // ===== STATEMENTS =====

  def Statement: Rule1[StatementNode] = rule {
    TypedDeclareStatement |
    DeclareIntStatement | 
    ArrayDeclareStatement |
    StructDeclareStatement |
    FunctionPointerStatement |
    PrintStatement |
    AssignVariableStatement |
    ArrayAssignStatement |
    StructMemberAssignStatement |
    PointerWriteStatement |
    ConditionStatement |
    WhileStatement |
    DoWhileStatement |
    ForStatement |
    SwitchStatement |
    CallMethodStatement | 
    ReturnStatement | 
    CallReadMethodStatement |
    BreakStatement |
    ContinueStatement |
    GotoStatement |
    LabelStatement |
    PortOutStatement |
    PortInStatement |
    InterruptStatement |
    MemoryStatement |
    InlineAsmStatement |
    CommentStatement
  }

  // ===== VARIABLE DECLARATIONS =====

  def DeclareIntStatement: Rule1[DeclareIntNode] = rule {
    DeclareInt ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~ 
    SetInitialValue ~ WhiteSpace ~ Operand ~~> DeclareIntNode ~ EOL
  }

  def TypedDeclareStatement: Rule1[TypedDeclareNode] = rule {
    DeclareInt ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
    TypeSpecification ~ EOL ~
    SetInitialValue ~ WhiteSpace ~ Expression ~~> TypedDeclareNode ~ EOL
  }

  def ArrayDeclareStatement: Rule1[ArrayDeclareNode] = rule {
    ArrayDeclareWithInit | ArrayDeclareNoInit
  }

  def ArrayDeclareNoInit: Rule1[ArrayDeclareNode] = rule {
    DeclareInt ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
    ArrayDeclare ~ EOL ~
    TypeSpecification ~ EOL ~
    ArraySize ~ WhiteSpace ~ Number ~~> { 
      (name: String, elemType: TypeSpec, size: NumberNode) =>
        ArrayDeclareNode(name, elemType, size.value, None)
    } ~ EOL
  }

  def ArrayDeclareWithInit: Rule1[ArrayDeclareNode] = rule {
    DeclareInt ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
    ArrayDeclare ~ EOL ~
    TypeSpecification ~ EOL ~
    ArraySize ~ WhiteSpace ~ Number ~ EOL ~
    ArrayInit ~ EOL ~
    ArrayInitValues ~~> { 
      (name: String, elemType: TypeSpec, size: NumberNode, values: List[OperandNode]) =>
        ArrayDeclareNode(name, elemType, size.value, Some(values))
    } ~ EOL
  }

  def ArrayInitValues: Rule1[List[OperandNode]] = rule {
    Operand ~ zeroOrMore(WhiteSpace ~ Operand) ~~> { (first: OperandNode, rest: List[OperandNode]) =>
      first :: rest
    }
  }

  def StructDeclareStatement: Rule1[StructDeclareNode] = rule {
    DeclareInt ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
    CreateStruct ~ WhiteSpace ~ VariableName ~> (s => s) ~~> {
      (varName: String, structName: String) =>
        StructDeclareNode(varName, structName, None)
    } ~ EOL
  }

  // ===== ASSIGNMENT STATEMENTS =====

  def AssignVariableStatement: Rule1[AssignVariableNode] = rule {
    AssignVariable ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~ 
    Expression ~ EndAssignVariable ~ EOL ~~> AssignVariableNode
  }

  def ArrayAssignStatement: Rule1[ArrayAssignNode] = rule {
    AssignVariable ~ WhiteSpace ~ ArrayAccess ~ WhiteSpace ~ 
    VariableName ~> (s => s) ~ WhiteSpace ~ Operand ~ EOL ~
    Expression ~ EndAssignVariable ~~> {
      (arrayName: String, index: OperandNode, value: AstNode) =>
        ArrayAssignNode(arrayName, index, value)
    } ~ EOL
  }

  def StructMemberAssignStatement: Rule1[StructMemberAssignNode] = rule {
    AssignVariable ~ WhiteSpace ~ MemberAccess ~ WhiteSpace ~
    VariableName ~> (s => s) ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
    Expression ~ EndAssignVariable ~~> {
      (structVar: String, memberName: String, value: AstNode) =>
        StructMemberAssignNode(structVar, memberName, value)
    } ~ EOL
  }

  def PointerWriteStatement: Rule1[PointerWriteNode] = rule {
    PointerWrite ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
    Expression ~ EndAssignVariable ~~> {
      (ptrVar: String, value: AstNode) =>
        PointerWriteNode(VariableNode(ptrVar), value)
    } ~ EOL
  }

  // ===== EXPRESSIONS =====

  def Expression: Rule1[AstNode] = rule {
    SetValueExpression ~ zeroOrMore(
      ArithmeticOperation | 
      LogicalOperation | 
      BitwiseOperation
    )
  }

  def SetValueExpression: Rule1[OperandNode] = rule {
    SetValue ~ WhiteSpace ~ ExtendedOperand ~ EOL
  }

  def ExtendedOperand: Rule1[OperandNode] = rule {
    LogicalNotExpr |
    BitwiseNotExpr |
    AddressOfExpr |
    DereferenceExpr |
    ArrayAccessExpr |
    StructMemberExpr |
    PointerMemberExpr |
    CastExpr |
    SizeofExpr |
    PortInExpr |
    AllocExpr |
    Operand
  }
  
  def LogicalNotExpr: Rule1[OperandNode] = rule {
    LogicalNot ~ WhiteSpace ~ Operand ~~> { (op: OperandNode) =>
      LogicalNotWrapperNode(op)
    }
  }
  
  def BitwiseNotExpr: Rule1[OperandNode] = rule {
    BitwiseNot ~ WhiteSpace ~ Operand ~~> { (op: OperandNode) =>
      BitwiseNotWrapperNode(op)
    }
  }
  
  def PointerMemberExpr: Rule1[StructPointerAccessNode] = rule {
    PointerMemberAccess ~ WhiteSpace ~ VariableName ~> (s => s) ~ WhiteSpace ~ 
    VariableName ~> (s => s) ~~> StructPointerAccessNode
  }

  def AddressOfExpr: Rule1[AddressOfNode] = rule {
    AddressOf ~ WhiteSpace ~ VariableName ~> (v => AddressOfNode(v))
  }

  def DereferenceExpr: Rule1[DereferenceNode] = rule {
    Dereference ~ WhiteSpace ~ VariableName ~> (v => DereferenceNode(VariableNode(v)))
  }

  def ArrayAccessExpr: Rule1[ArrayAccessNode] = rule {
    ArrayAccess ~ WhiteSpace ~ VariableName ~> (s => s) ~ WhiteSpace ~ Operand ~~> ArrayAccessNode
  }

  def StructMemberExpr: Rule1[StructMemberAccessNode] = rule {
    MemberAccess ~ WhiteSpace ~ VariableName ~> (s => s) ~ WhiteSpace ~ 
    VariableName ~> (s => s) ~~> StructMemberAccessNode
  }

  def CastExpr: Rule1[TypeCastNode] = rule {
    Cast ~ WhiteSpace ~ TypeSpecification ~ WhiteSpace ~ Operand ~~> {
      (targetType: TypeSpec, expr: OperandNode) =>
        TypeCastNode(targetType, expr)
    }
  }

  def SizeofExpr: Rule1[OperandNode] = rule {
    Sizeof ~ WhiteSpace ~ TypeSpecification ~~> SizeofTypeNode |
    SizeofExpr ~ WhiteSpace ~ VariableName ~> (v => SizeofExprNode(v))
  }

  def AllocExpr: Rule1[AllocNode] = rule {
    Alloc ~ WhiteSpace ~ Operand ~~> AllocNode
  }

  def PortInExpr: Rule1[OperandNode] = rule {
    PortInDwordExpr |
    PortInWordExpr |
    PortInByteExpr
  }

  def PortInByteExpr: Rule1[InbExprNode] = rule {
    InPort ~ WhiteSpace ~ Operand ~~> InbExprNode
  }

  def PortInWordExpr: Rule1[InwExprNode] = rule {
    InPortWord ~ WhiteSpace ~ Operand ~~> InwExprNode
  }

  def PortInDwordExpr: Rule1[InlExprNode] = rule {
    InPortDword ~ WhiteSpace ~ Operand ~~> InlExprNode
  }

  // ===== ARITHMETIC OPERATIONS =====

  def ArithmeticOperation: ReductionRule1[AstNode, AstNode] = rule {
    PlusExpression ~~> PlusExpressionNode |
    MinusExpression ~~> MinusExpressionNode |
    MultiplicationExpression ~~> MultiplicationExpressionNode |
    DivisionExpression ~~> DivisionExpressionNode |
    ModuloExpression ~~> ModuloExpressionNode
  }

  def PlusExpression: Rule1[AstNode] = rule { PlusOperator ~ WhiteSpace ~ Operand ~ EOL }
  def MinusExpression: Rule1[AstNode] = rule { MinusOperator ~ WhiteSpace ~ Operand ~ EOL }
  def MultiplicationExpression: Rule1[AstNode] = rule { MultiplicationOperator ~ WhiteSpace ~ Operand ~ EOL }
  def DivisionExpression: Rule1[AstNode] = rule { DivisionOperator ~ WhiteSpace ~ Operand ~ EOL }
  def ModuloExpression: Rule1[AstNode] = rule { Modulo ~ WhiteSpace ~ Operand ~ EOL }

  // ===== LOGICAL OPERATIONS =====

  def LogicalOperation: ReductionRule1[AstNode, AstNode] = rule {
    Or ~ WhiteSpace ~ Operand ~ EOL ~~> OrNode |
    And ~ WhiteSpace ~ Operand ~ EOL ~~> AndNode |
    EqualTo ~ WhiteSpace ~ Operand ~ EOL ~~> EqualToNode |
    GreaterThan ~ WhiteSpace ~ Operand ~ EOL ~~> GreaterThanNode |
    GreaterThanOrEqual ~ WhiteSpace ~ Operand ~ EOL ~~> GreaterThanOrEqualNode |
    LessThan ~ WhiteSpace ~ Operand ~ EOL ~~> LessThanNode |
    LessThanOrEqual ~ WhiteSpace ~ Operand ~ EOL ~~> LessThanOrEqualNode |
    NotEqual ~ WhiteSpace ~ Operand ~ EOL ~~> NotEqualNode
  }

  // ===== BITWISE OPERATIONS =====

  def BitwiseOperation: ReductionRule1[AstNode, AstNode] = rule {
    BitwiseAnd ~ WhiteSpace ~ Operand ~ EOL ~~> BitwiseAndExprNode |
    BitwiseOr ~ WhiteSpace ~ Operand ~ EOL ~~> BitwiseOrExprNode |
    BitwiseXor ~ WhiteSpace ~ Operand ~ EOL ~~> BitwiseXorExprNode |
    ShiftLeft ~ WhiteSpace ~ Operand ~ EOL ~~> LeftShiftExprNode |
    ShiftRight ~ WhiteSpace ~ Operand ~ EOL ~~> RightShiftExprNode
  }

  // ===== CONTROL FLOW =====

  def ConditionStatement: Rule1[ConditionNode] = rule {
    If ~ WhiteSpace ~ Operand ~ EOL ~ zeroOrMore(Statement) ~
    (Else ~ EOL ~ zeroOrMore(Statement) ~~> ConditionNode
      | zeroOrMore(Statement) ~~> ConditionNode) ~ EndIf ~ EOL
  }

  def WhileStatement: Rule1[WhileNode] = rule {
    While ~ WhiteSpace ~ Operand ~ EOL ~ zeroOrMore(Statement) ~ EndWhile ~ EOL ~~> WhileNode
  }
  
  def DoWhileStatement: Rule1[DoWhileNode] = rule {
    DoWhileBegin ~ EOL ~ zeroOrMore(Statement) ~ DoWhileEnd ~ WhiteSpace ~ Operand ~ EOL ~~> {
      (body: List[StatementNode], condition: OperandNode) => DoWhileNode(condition, body)
    }
  }

  def ForStatement: Rule1[ForLoopNode] = rule {
    ForBegin ~ WhiteSpace ~ VariableName ~> (s => s) ~ WhiteSpace ~
    Operand ~ WhiteSpace ~ ForTo ~ WhiteSpace ~ Operand ~ WhiteSpace ~
    ForStep ~ WhiteSpace ~ Operand ~ EOL ~
    zeroOrMore(Statement) ~
    ForEnd ~ EOL ~~> ForLoopNode
  }

  def SwitchStatement: Rule1[SwitchNode] = rule {
    SwitchBegin ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
    zeroOrMore(CaseClause) ~
    optional(DefaultClause) ~
    SwitchEnd ~ EOL ~~> SwitchNode
  }

  def CaseClause: Rule1[CaseNode] = rule {
    SwitchCase ~ WhiteSpace ~ Operand ~ EOL ~
    zeroOrMore(Statement) ~~> { (value: OperandNode, stmts: List[StatementNode]) =>
      CaseNode(value, stmts, false)
    }
  }

  def DefaultClause: Rule1[List[StatementNode]] = rule {
    SwitchDefault ~ EOL ~ zeroOrMore(Statement)
  }

  def BreakStatement: Rule1[BreakNode] = rule {
    Break ~ EOL ~> (_ => BreakNode())
  }

  def ContinueStatement: Rule1[ContinueNode] = rule {
    Continue ~ EOL ~> (_ => ContinueNode())
  }

  def GotoStatement: Rule1[GotoNode] = rule {
    Goto ~ WhiteSpace ~ VariableName ~> (label => GotoNode(label)) ~ EOL
  }

  def LabelStatement: Rule1[LabelNode] = rule {
    Label ~ WhiteSpace ~ VariableName ~> (label => LabelNode(label)) ~ EOL
  }

  // ===== I/O STATEMENTS =====

  def PrintStatement: Rule1[PrintNode] = rule {
    Print ~ WhiteSpace ~ (Operand ~~> PrintNode | "\"" ~ String ~ "\"" ~~> PrintNode) ~ EOL
  }

  // ===== METHOD CALLS =====

  def CallMethodStatement: Rule1[StatementNode] = rule {
    (AssignVariableFromMethodCall ~ WhiteSpace ~ VariableName ~> (v => v) ~ EOL | "" ~> (v => v)) ~
    CallMethod ~ WhiteSpace ~ VariableName ~> (v => v) ~
    zeroOrMore(WhiteSpace ~ Operand) ~ EOL ~~> CallMethodNode
  }

  def CallReadMethodStatement: Rule1[StatementNode] = rule {
    (AssignVariableFromMethodCall ~ WhiteSpace ~ VariableName ~> (v => v) ~ EOL | "" ~> (v => v)) ~
    CallMethod ~ EOL ~ Read ~ EOL ~~> CallReadMethodNode
  }

  def ReturnStatement: Rule1[StatementNode] = rule {
    Return ~ ((WhiteSpace ~ Operand ~~> (o => ReturnNode(Some(o)))) | "" ~> (s => ReturnNode(None))) ~ EOL
  }

  // ===== PORT I/O STATEMENTS =====

  def PortOutStatement: Rule1[StatementNode] = rule {
    PortOutDwordStatement |
    PortOutWordStatement |
    PortOutByteStatement
  }

  def PortOutByteStatement: Rule1[OutbNode] = rule {
    OutPort ~ WhiteSpace ~ Operand ~ WhiteSpace ~ Operand ~ EOL ~~> OutbNode
  }

  def PortOutWordStatement: Rule1[OutwNode] = rule {
    OutPortWord ~ WhiteSpace ~ Operand ~ WhiteSpace ~ Operand ~ EOL ~~> OutwNode
  }

  def PortOutDwordStatement: Rule1[OutlNode] = rule {
    OutPortDword ~ WhiteSpace ~ Operand ~ WhiteSpace ~ Operand ~ EOL ~~> OutlNode
  }

  def PortInStatement: Rule1[StatementNode] = rule {
    PortInDwordStatement |
    PortInWordStatement |
    PortInByteStatement
  }

  def PortInByteStatement: Rule1[InbNode] = rule {
    (AssignVariableFromMethodCall ~ WhiteSpace ~ VariableName ~> (v => Some(v)) ~ EOL | 
     "" ~> (_ => None: Option[String])) ~
    InPort ~ WhiteSpace ~ Operand ~~> {
      (assignTo: Option[String], port: OperandNode) =>
        InbNode(port, assignTo)
    } ~ EOL
  }

  def PortInWordStatement: Rule1[InwNode] = rule {
    (AssignVariableFromMethodCall ~ WhiteSpace ~ VariableName ~> (v => Some(v)) ~ EOL | 
     "" ~> (_ => None: Option[String])) ~
    InPortWord ~ WhiteSpace ~ Operand ~~> {
      (assignTo: Option[String], port: OperandNode) =>
        InwNode(port, assignTo)
    } ~ EOL
  }

  def PortInDwordStatement: Rule1[InlNode] = rule {
    (AssignVariableFromMethodCall ~ WhiteSpace ~ VariableName ~> (v => Some(v)) ~ EOL | 
     "" ~> (_ => None: Option[String])) ~
    InPortDword ~ WhiteSpace ~ Operand ~~> {
      (assignTo: Option[String], port: OperandNode) =>
        InlNode(port, assignTo)
    } ~ EOL
  }

  // ===== INTERRUPT STATEMENTS =====

  def InterruptStatement: Rule1[StatementNode] = rule {
    DisableInterrupts ~ EOL ~> (_ => CliNode()) |
    EnableInterrupts ~ EOL ~> (_ => StiNode()) |
    Halt ~ EOL ~> (_ => HltNode()) |
    Pause ~ EOL ~> (_ => PauseNode()) |
    Nop ~ EOL ~> (_ => NopNode())
  }

  // ===== MEMORY STATEMENTS =====

  def MemoryStatement: Rule1[StatementNode] = rule {
    FreeStatement |
    MemsetStatement |
    MemcpyStatement |
    MemWriteStatement
  }

  def FreeStatement: Rule1[FreeNode] = rule {
    Free ~ WhiteSpace ~ VariableName ~> (v => FreeNode(v)) ~ EOL
  }

  def MemsetStatement: Rule1[MemsetNode] = rule {
    MemSet ~ WhiteSpace ~ VariableName ~> (v => VariableNode(v)) ~ 
    WhiteSpace ~ Operand ~ WhiteSpace ~ Operand ~ EOL ~~> MemsetNode
  }

  def MemcpyStatement: Rule1[MemcpyNode] = rule {
    MemCopy ~ WhiteSpace ~ VariableName ~> (v => VariableNode(v)) ~
    WhiteSpace ~ MemCopyTo ~ WhiteSpace ~ VariableName ~> (v => VariableNode(v)) ~
    WhiteSpace ~ Operand ~ EOL ~~> MemcpyNode
  }

  def MemWriteStatement: Rule1[MemoryWriteNode] = rule {
    MemWrite ~ WhiteSpace ~ Operand ~ EOL ~
    TypeSpecification ~ EOL ~
    SetValue ~ WhiteSpace ~ Operand ~ EndAssignVariable ~ EOL ~~> MemoryWriteNode
  }

  // ===== FUNCTION POINTERS =====

  def FunctionPointerStatement: Rule1[StatementNode] = rule {
    FunctionPointerDeclareStatement |
    FunctionPointerAssignStatement |
    FunctionPointerCallStatement
  }

  // REMEMBER THIS MOVE handler_ptr
  // LOOKS LIKE THIS IS NOTHING
  // I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE arg1
  // THIS IS A WARRIOR
  def FunctionPointerDeclareStatement: Rule1[FunctionPointerDeclareNode] = rule {
    FuncPtrDeclare ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
    FuncPtrSignature ~ WhiteSpace ~ TypeSpecification ~ EOL ~
    zeroOrMore(FuncPtrParamType) ~~> {
      (varName: String, retType: TypeSpec, paramTypes: List[TypeSpec]) =>
        FunctionPointerDeclareNode(varName, retType, paramTypes)
    }
  }

  def FuncPtrParamType: Rule1[TypeSpec] = rule {
    MethodArguments ~ EOL ~ TypeSpecification ~ EOL
  }

  // LEARN THE MOVE handler_ptr FROM my_handler
  def FunctionPointerAssignStatement: Rule1[FunctionPointerAssignNode] = rule {
    FuncPtrAssign ~ WhiteSpace ~ VariableName ~> (s => s) ~ WhiteSpace ~
    "FROM" ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~~> FunctionPointerAssignNode
  }

  // USE THAT MOVE handler_ptr arg1 arg2
  // or with assignment:
  // GET YOUR ASS TO MARS result
  // USE THAT MOVE handler_ptr arg1 arg2
  def FunctionPointerCallStatement: Rule1[FunctionPointerCallNode] = rule {
    (AssignVariableFromMethodCall ~ WhiteSpace ~ VariableName ~> (v => Some(v)) ~ EOL |
     "" ~> (_ => None: Option[String])) ~
    FuncPtrCall ~ WhiteSpace ~ VariableName ~> (s => s) ~
    zeroOrMore(WhiteSpace ~ Operand) ~ EOL ~~> {
      (assignTo: Option[String], fptrVar: String, args: List[OperandNode]) =>
        FunctionPointerCallNode(fptrVar, args, assignTo)
    }
  }

  // ===== INLINE ASSEMBLY =====

  def InlineAsmStatement: Rule1[SimpleAsmNode] = rule {
    AsmBegin ~ EOL ~
    zeroOrMore(!AsmEnd ~ ANY) ~> (s => s) ~
    AsmEnd ~ EOL ~~> SimpleAsmNode
  }

  // ===== COMMENTS =====

  def CommentStatement: Rule1[StatementNode] = rule {
    MultiLineComment | SingleLineComment
  }

  // TALK TO YOURSELF "This is a comment"
  def SingleLineComment: Rule1[CommentNode] = rule {
    Comment ~ WhiteSpace ~ "\"" ~ zeroOrMore(!anyOf("\"") ~ ANY) ~> (s => s) ~ "\"" ~ EOL ~~> CommentNode
  }

  // LET ME THINK ABOUT THIS
  // Line 1
  // Line 2
  // I'VE THOUGHT ABOUT IT
  def MultiLineComment: Rule1[MultiLineCommentNode] = rule {
    MultiLineCommentBegin ~ EOL ~
    zeroOrMore(CommentLine) ~
    MultiLineCommentEnd ~ EOL ~~> MultiLineCommentNode
  }

  def CommentLine: Rule1[String] = rule {
    !MultiLineCommentEnd ~ zeroOrMore(!EOL ~ ANY) ~> (s => s) ~ EOL
  }

  // ===== OPERANDS =====

  def Operand: Rule1[OperandNode] = rule {
    NullLiteral | HexNumber | BinaryNumber | Number | Variable | Boolean | CharLiteral
  }

  def Variable: Rule1[VariableNode] = rule { VariableName ~> VariableNode }
  def VariableName: Rule0 = rule { rule("A" - "Z" | "a" - "z" | "_") ~ zeroOrMore("A" - "Z" | "a" - "z" | "0" - "9" | "_") }

  def Number: Rule1[NumberNode] = rule {
    oneOrMore("0" - "9") ~> ((matched: String) => NumberNode(matched.toInt)) |
    "-" ~ oneOrMore("0" - "9") ~> ((matched: String) => NumberNode(-matched.toInt))
  }

  def HexNumber: Rule1[HexNumberNode] = rule {
    "0x" ~ oneOrMore("0" - "9" | "a" - "f" | "A" - "F") ~> { (matched: String) =>
      HexNumberNode(java.lang.Long.parseLong(matched, 16))
    }
  }
  
  def BinaryNumber: Rule1[HexNumberNode] = rule {
    "0b" ~ oneOrMore("0" | "1") ~> { (matched: String) =>
      HexNumberNode(java.lang.Long.parseLong(matched, 2))
    }
  }
  
  def NullLiteral: Rule1[HexNumberNode] = rule {
    NullValue ~> (_ => HexNumberNode(0L))
  }

  def Boolean: Rule1[NumberNode] = rule {
    "@" ~ True ~> (_ => NumberNode(1)) |
    "@" ~ False ~> (_ => NumberNode(0))
  }

  def CharLiteral: Rule1[CharLiteralNode] = rule {
    "'" ~ ANY ~> ((c: Any) => CharLiteralNode(c.toString.charAt(0))) ~ "'"
  }

  def String: Rule1[StringNode] = rule {
    zeroOrMore(rule { !anyOf("\"\\") ~ ANY }) ~> StringNode
  }

  // ===== ENTRY POINT =====

  def parse(expression: String): RootNode = {
    val parsingResult = ReportingParseRunner(Root).run(expression)
    parsingResult.result match {
      case Some(root) => root
      case None => throw new ParsingException(ParseError + ":\n" +
        ErrorUtils.printParseErrors(parsingResult))
    }
  }
}
