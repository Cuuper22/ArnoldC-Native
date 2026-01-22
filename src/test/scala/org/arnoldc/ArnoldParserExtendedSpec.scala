package org.arnoldc

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.arnoldc.ast._

/**
 * Unit tests for ArnoldC Extended Parser
 * "COME WITH ME IF YOU WANT TO LIVE" - Testing is survival
 */
class ArnoldParserExtendedSpec extends AnyFlatSpec with Matchers {

  val parser = new ArnoldParserExtended()

  // ===== BASIC PROGRAM STRUCTURE =====

  "ArnoldParserExtended" should "parse a simple main method" in {
    val code =
      """IT'S SHOWTIME
        |TALK TO THE HAND "Hello World"
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    result.methods should have length 1
    result.methods.head shouldBe a[MainMethodNode]
  }

  it should "parse variable declaration with integer" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE myVar
        |YOU SET US UP 42
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    val mainMethod = result.methods.head.asInstanceOf[MainMethodNode]
    mainMethod.statements should have length 1
    mainMethod.statements.head shouldBe a[DeclareIntNode]
  }

  // ===== TYPE SYSTEM =====

  it should "parse typed variable declaration with u32" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE myVar
        |THIS IS A WARRIOR
        |YOU SET US UP 42
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    val mainMethod = result.methods.head.asInstanceOf[MainMethodNode]
    mainMethod.statements.head shouldBe a[TypedDeclareNode]
    val decl = mainMethod.statements.head.asInstanceOf[TypedDeclareNode]
    decl.varType shouldBe U32Type
  }

  it should "parse pointer type declaration" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE myPtr
        |POINT YOUR GUN AT
        |THIS IS A WARRIOR
        |YOU SET US UP 0
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    val mainMethod = result.methods.head.asInstanceOf[MainMethodNode]
    val decl = mainMethod.statements.head.asInstanceOf[TypedDeclareNode]
    decl.varType shouldBe a[PointerType]
  }

  // ===== ARITHMETIC =====

  it should "parse arithmetic expressions" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE x
        |YOU SET US UP 10
        |GET TO THE CHOPPER x
        |HERE IS MY INVITATION x
        |GET UP 5
        |GET DOWN 2
        |YOU'RE FIRED 3
        |ENOUGH TALK
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    result.methods should have length 1
  }

  // ===== CONTROL FLOW =====

  it should "parse if-else statement" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE flag
        |YOU SET US UP @NO PROBLEMO
        |BECAUSE I'M GOING TO SAY PLEASE flag
        |TALK TO THE HAND "Yes"
        |BULLSHIT
        |TALK TO THE HAND "No"
        |YOU HAVE NO RESPECT FOR LOGIC
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    val mainMethod = result.methods.head.asInstanceOf[MainMethodNode]
    mainMethod.statements.exists(_.isInstanceOf[ConditionNode]) shouldBe true
  }

  it should "parse while loop" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE counter
        |YOU SET US UP 5
        |STICK AROUND counter
        |TALK TO THE HAND counter
        |GET TO THE CHOPPER counter
        |HERE IS MY INVITATION counter
        |GET DOWN 1
        |ENOUGH TALK
        |CHILL
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    val mainMethod = result.methods.head.asInstanceOf[MainMethodNode]
    mainMethod.statements.exists(_.isInstanceOf[WhileNode]) shouldBe true
  }

  it should "parse do-while loop" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE x
        |YOU SET US UP 0
        |DO THIS FIRST
        |GET TO THE CHOPPER x
        |HERE IS MY INVITATION x
        |GET UP 1
        |ENOUGH TALK
        |THEN STICK AROUND x
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    val mainMethod = result.methods.head.asInstanceOf[MainMethodNode]
    mainMethod.statements.exists(_.isInstanceOf[DoWhileNode]) shouldBe true
  }

  // ===== BITWISE OPERATIONS =====

  it should "parse bitwise operations" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE flags
        |YOU SET US UP 0xFF
        |GET TO THE CHOPPER flags
        |HERE IS MY INVITATION flags
        |CRUSH THEM TOGETHER 0x0F
        |JOIN THEM TOGETHER 0x80
        |PUSH IT LEFT 4
        |ENOUGH TALK
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    result.methods should have length 1
  }

  // ===== ARRAY OPERATIONS =====

  it should "parse array declaration" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE buffer
        |LINE THEM UP
        |THIS IS A TINY WARRIOR
        |HOW MANY 256
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    val mainMethod = result.methods.head.asInstanceOf[MainMethodNode]
    mainMethod.statements.head shouldBe a[ArrayDeclareNode]
    val arr = mainMethod.statements.head.asInstanceOf[ArrayDeclareNode]
    arr.elementType shouldBe U8Type
    arr.size shouldBe 256
  }

  // ===== PORT I/O =====

  it should "parse port output statement" in {
    val code =
      """IT'S SHOWTIME
        |TALK TO THE PORT 0x3F8 0x41
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    val mainMethod = result.methods.head.asInstanceOf[MainMethodNode]
    mainMethod.statements.head shouldBe a[OutbNode]
  }

  // ===== INTERRUPT CONTROL =====

  it should "parse interrupt control statements" in {
    val code =
      """IT'S SHOWTIME
        |EVERYBODY CHILL
        |LET'S PARTY
        |SLEEP NOW
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    val mainMethod = result.methods.head.asInstanceOf[MainMethodNode]
    mainMethod.statements(0) shouldBe a[CliNode]
    mainMethod.statements(1) shouldBe a[StiNode]
    mainMethod.statements(2) shouldBe a[HltNode]
  }

  // ===== USER-DEFINED METHODS =====

  it should "parse user-defined method" in {
    val code =
      """LISTEN TO ME VERY CAREFULLY addNumbers
        |I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE a
        |I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE b
        |GIVE THESE PEOPLE AIR
        |HEY CHRISTMAS TREE result
        |YOU SET US UP 0
        |GET TO THE CHOPPER result
        |HERE IS MY INVITATION a
        |GET UP b
        |ENOUGH TALK
        |I'LL BE BACK result
        |HASTA LA VISTA, BABY
        |
        |IT'S SHOWTIME
        |HEY CHRISTMAS TREE sum
        |YOU SET US UP 0
        |GET YOUR ASS TO MARS sum
        |DO IT NOW addNumbers 5 10
        |TALK TO THE HAND sum
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    result.methods should have length 2
    result.methods.head shouldBe a[MethodNode]
  }

  // ===== INLINE ASSEMBLY =====

  it should "parse inline assembly" in {
    val code =
      """IT'S SHOWTIME
        |SPEAK TO THE MACHINE
        |mov eax, 0
        |THE MACHINE SAYS
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    val mainMethod = result.methods.head.asInstanceOf[MainMethodNode]
    mainMethod.statements.head shouldBe a[SimpleAsmNode]
  }

  // ===== COMMENTS =====

  it should "parse single-line comment" in {
    val code =
      """IT'S SHOWTIME
        |TALK TO YOURSELF "This is a comment"
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    val mainMethod = result.methods.head.asInstanceOf[MainMethodNode]
    mainMethod.statements.head shouldBe a[CommentNode]
  }

  // ===== HEX AND BINARY NUMBERS =====

  it should "parse hex numbers" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE addr
        |YOU SET US UP 0xB8000
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    val mainMethod = result.methods.head.asInstanceOf[MainMethodNode]
    val decl = mainMethod.statements.head.asInstanceOf[DeclareIntNode]
    decl.value shouldBe a[HexNumberNode]
  }

  it should "parse binary numbers" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE mask
        |YOU SET US UP 0b11110000
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    val mainMethod = result.methods.head.asInstanceOf[MainMethodNode]
    val decl = mainMethod.statements.head.asInstanceOf[DeclareIntNode]
    decl.value shouldBe a[HexNumberNode]
    decl.value.asInstanceOf[HexNumberNode].value shouldBe 0xF0
  }

  // ===== NULL POINTER =====

  it should "parse null pointer literal" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE ptr
        |POINT YOUR GUN AT
        |THIS IS A WARRIOR
        |YOU SET US UP THERE IS NO ONE
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    val mainMethod = result.methods.head.asInstanceOf[MainMethodNode]
    val decl = mainMethod.statements.head.asInstanceOf[TypedDeclareNode]
    decl.value shouldBe a[HexNumberNode]
    decl.value.asInstanceOf[HexNumberNode].value shouldBe 0
  }

  // ===== COMPARISON OPERATORS =====

  it should "parse all comparison operators" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE result
        |YOU SET US UP 0
        |GET TO THE CHOPPER result
        |HERE IS MY INVITATION 5
        |LET OFF SOME STEAM BENNET 3
        |ENOUGH TALK
        |GET TO THE CHOPPER result
        |HERE IS MY INVITATION 5
        |YOU'RE AT LEAST AS BIG AS 5
        |ENOUGH TALK
        |GET TO THE CHOPPER result
        |HERE IS MY INVITATION 3
        |YOU'RE NOT BIG ENOUGH 5
        |ENOUGH TALK
        |GET TO THE CHOPPER result
        |HERE IS MY INVITATION 3
        |YOU'RE NOT BIGGER THAN 5
        |ENOUGH TALK
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val result = parser.parse(code)
    result.methods should have length 1
  }
}
