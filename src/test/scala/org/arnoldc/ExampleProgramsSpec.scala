package org.arnoldc

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.io.Source
import java.io.File

/**
 * Tests for Example Programs
 * "COME WITH ME IF YOU WANT TO LIVE" - Verify examples parse correctly
 *
 * These tests ensure all example programs in the examples/ directory
 * can be parsed successfully without errors.
 */
class ExampleProgramsSpec extends AnyFlatSpec with Matchers {

  val parser = new ArnoldParserExtended()

  def parseFile(filename: String): Unit = {
    val file = new File(s"examples/$filename")
    if (file.exists()) {
      val source = Source.fromFile(file)
      try {
        val code = source.mkString
        parser.parse(code) // Should not throw
      } finally {
        source.close()
      }
    } else {
      // If examples directory doesn't exist in test context, skip gracefully
      pending
    }
  }

  "ArnoldC Parser" should "parse vga_hello.arnoldc without errors" in {
    parseFile("vga_hello.arnoldc")
  }

  it should "parse real_kernel.arnoldc without errors" in {
    parseFile("real_kernel.arnoldc")
  }

  it should "parse gdt_setup.arnoldc without errors" in {
    parseFile("gdt_setup.arnoldc")
  }

  it should "parse keyboard_driver.arnoldc without errors" in {
    parseFile("keyboard_driver.arnoldc")
  }
}
