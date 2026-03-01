package org.arnoldc

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.arnoldc.ast._
import org.arnoldc.native.NativeGenerator

/**
 * Unit tests for NativeGenerator
 * "GET YOUR ASS TO MARS" - Testing C code generation
 */
class NativeGeneratorSpec extends AnyFlatSpec with Matchers {

  val parser = new ArnoldParserExtended()

  def compile(code: String): String = {
    val ast = parser.parse(code)
    // Create a new generator instance for each test to avoid state leakage
    val generator = new NativeGenerator()
    generator.generateC(ast, "Test")
  }

  // ===== BASIC CODE GENERATION =====

  "NativeGenerator" should "generate C header with runtime include" in {
    val code =
      """IT'S SHOWTIME
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("#include \"arnold_runtime.h\"")
  }

  it should "generate main function as arnold_main" in {
    val code =
      """IT'S SHOWTIME
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("void arnold_main(void)")
  }

  // ===== VARIABLE DECLARATIONS =====

  it should "generate int variable declaration" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE myVar
        |YOU SET US UP 42
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("int myVar = 42")
  }

  it should "generate typed variable declaration" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE myVar
        |THIS IS A WARRIOR
        |YOU SET US UP 100
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("uint32_t myVar = 100")
  }

  // ===== PRINT STATEMENTS =====

  it should "generate print string statement" in {
    val code =
      """IT'S SHOWTIME
        |TALK TO THE HAND "Hello"
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("arnold_print")
    cCode should include ("Hello")
  }

  it should "generate print integer statement" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE x
        |YOU SET US UP 42
        |TALK TO THE HAND x
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("arnold_print_int")
  }

  // ===== ARITHMETIC OPERATIONS =====

  it should "generate addition expression" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE x
        |YOU SET US UP 5
        |GET TO THE CHOPPER x
        |HERE IS MY INVITATION x
        |GET UP 10
        |ENOUGH TALK
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("+")
  }

  it should "generate multiplication expression" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE x
        |YOU SET US UP 5
        |GET TO THE CHOPPER x
        |HERE IS MY INVITATION x
        |YOU'RE FIRED 3
        |ENOUGH TALK
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("*")
  }

  // ===== CONTROL FLOW =====

  it should "generate if statement" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE flag
        |YOU SET US UP @NO PROBLEMO
        |BECAUSE I'M GOING TO SAY PLEASE flag
        |TALK TO THE HAND "Yes"
        |YOU HAVE NO RESPECT FOR LOGIC
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("if (")
  }

  it should "generate if-else statement" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE flag
        |YOU SET US UP @I LIED
        |BECAUSE I'M GOING TO SAY PLEASE flag
        |TALK TO THE HAND "Yes"
        |BULLSHIT
        |TALK TO THE HAND "No"
        |YOU HAVE NO RESPECT FOR LOGIC
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("if (")
    cCode should include ("} else {")
  }

  it should "generate while loop" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE x
        |YOU SET US UP 5
        |STICK AROUND x
        |GET TO THE CHOPPER x
        |HERE IS MY INVITATION x
        |GET DOWN 1
        |ENOUGH TALK
        |CHILL
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("while (")
  }

  it should "generate do-while loop" in {
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

    val cCode = compile(code)
    cCode should include ("do {")
    cCode should include ("} while (")
  }

  // ===== BITWISE OPERATIONS =====

  it should "generate bitwise AND" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE x
        |YOU SET US UP 0xFF
        |GET TO THE CHOPPER x
        |HERE IS MY INVITATION x
        |CRUSH THEM TOGETHER 0x0F
        |ENOUGH TALK
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("&")
  }

  it should "generate bitwise OR" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE x
        |YOU SET US UP 0x0F
        |GET TO THE CHOPPER x
        |HERE IS MY INVITATION x
        |JOIN THEM TOGETHER 0xF0
        |ENOUGH TALK
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("|")
  }

  it should "generate left shift" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE x
        |YOU SET US UP 1
        |GET TO THE CHOPPER x
        |HERE IS MY INVITATION x
        |PUSH IT LEFT 4
        |ENOUGH TALK
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("<<")
  }

  // ===== PORT I/O =====

  it should "generate outb instruction" in {
    val code =
      """IT'S SHOWTIME
        |TALK TO THE PORT 0x3F8 0x41
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("outb(")
  }

  // ===== INTERRUPT CONTROL =====

  it should "generate CLI instruction" in {
    val code =
      """IT'S SHOWTIME
        |EVERYBODY CHILL
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("cli")
  }

  it should "generate STI instruction" in {
    val code =
      """IT'S SHOWTIME
        |LET'S PARTY
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("sti")
  }

  it should "generate HLT instruction" in {
    val code =
      """IT'S SHOWTIME
        |SLEEP NOW
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("hlt")
  }

  // ===== INLINE ASSEMBLY =====

  it should "generate inline assembly" in {
    val code =
      """IT'S SHOWTIME
        |SPEAK TO THE MACHINE
        |mov eax, 0
        |THE MACHINE SAYS
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("__asm__")
    cCode should include ("mov eax, 0")
  }

  // ===== ARRAYS =====

  it should "generate array declaration" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE buffer
        |LINE THEM UP
        |THIS IS A TINY WARRIOR
        |HOW MANY 256
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("uint8_t buffer[256]")
  }

  // ===== USER METHODS =====

  it should "generate user-defined method" in {
    val code =
      """LISTEN TO ME VERY CAREFULLY double
        |I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE x
        |GIVE THESE PEOPLE AIR
        |HEY CHRISTMAS TREE result
        |YOU SET US UP 0
        |GET TO THE CHOPPER result
        |HERE IS MY INVITATION x
        |YOU'RE FIRED 2
        |ENOUGH TALK
        |I'LL BE BACK result
        |HASTA LA VISTA, BABY
        |
        |IT'S SHOWTIME
        |HEY CHRISTMAS TREE y
        |YOU SET US UP 0
        |GET YOUR ASS TO MARS y
        |DO IT NOW double 5
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("int double(")
    cCode should include ("return ")
  }

  // ===== COMMENTS =====

  it should "generate C comments from ArnoldC comments" in {
    val code =
      """IT'S SHOWTIME
        |TALK TO YOURSELF "This is a test"
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("/* This is a test */")
  }

  // ===== HEX NUMBERS =====

  it should "generate hex numbers correctly" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE addr
        |YOU SET US UP 0xB8000
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include ("0xb8000")
  }

  // ===== COMPARISON OPERATORS =====

  it should "generate comparison operators" in {
    val code =
      """IT'S SHOWTIME
        |HEY CHRISTMAS TREE x
        |YOU SET US UP 5
        |HEY CHRISTMAS TREE result
        |YOU SET US UP 0
        |GET TO THE CHOPPER result
        |HERE IS MY INVITATION x
        |LET OFF SOME STEAM BENNET 3
        |ENOUGH TALK
        |YOU HAVE BEEN TERMINATED
        |""".stripMargin

    val cCode = compile(code)
    cCode should include (">")
  }
}
