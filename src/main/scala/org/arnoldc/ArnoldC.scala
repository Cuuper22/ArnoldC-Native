package org.arnoldc

import java.io.{FileOutputStream, PrintWriter}
import org.arnoldc.ast.RootNode
import org.arnoldc.native.NativeGenerator

/**
 * ArnoldC Compiler - Extended with Native Code Support
 * "I'LL BE BACK" - Now with bare-metal capabilities
 * 
 * Usage:
 *   ArnoldC [-run|-declaim|-native|-asm|-kernel] <source.arnoldc>
 * 
 * Options:
 *   -run      Run the program after compilation (JVM)
 *   -declaim  Generate audio output (JVM)
 *   -native   Generate freestanding C code
 *   -asm      Generate x86 assembly
 *   -kernel   Generate kernel-ready C code with runtime
 */
object ArnoldC {
  
  val VERSION = "1.0.0-NATIVE"
  
  def main(args: Array[String]) {
    if (args.contains("-version") || args.contains("--version")) {
      println(s"ArnoldC Compiler v$VERSION")
      println("\"I'LL BE BACK\"")
      return
    }

    if (args.length < 1 || args.contains("-help") || args.contains("--help")) {
      printUsage()
      return
    }
    
    val command = getCommandFromArgs(args)
    val filename = getFileNameFromArgs(args)

    // Parse source code with proper resource cleanup and error handling
    val sourceCode = try {
      val source = scala.io.Source.fromFile(filename)
      try {
        source.mkString
      } finally {
        source.close()
      }
    } catch {
      case e: java.io.FileNotFoundException =>
        println(s"ERROR: File not found: $filename")
        println("WHAT THE FUCK DID I DO WRONG!")
        return
      case e: java.io.IOException =>
        println(s"ERROR: Failed to read file: $filename")
        println(s"Details: ${e.getMessage}")
        println("YOU SHOULD NOT DRINK AND BAKE!")
        return
    }

    val parser = new ArnoldParserExtended()
    val root = parser.parse(sourceCode)
    
    val baseName = if (filename.contains('.')) {
      filename.replaceAll("\\.[^.]*$", "")
    } else {
      filename
    }
    
    command match {
      case "-native" => generateNative(root, baseName)
      case "-asm" => generateAsm(root, baseName)
      case "-kernel" => generateKernel(root, baseName)
      case _ => generateJVM(args, root, baseName, sourceCode, command)
    }
  }
  
  /**
   * Generate freestanding C code
   * "GET YOUR ASS TO MARS" - Native code output
   */
  def generateNative(root: RootNode, baseName: String): Unit = {
    println(s"[NATIVE] Generating C code: $baseName.c")
    println("         GET YOUR ASS TO MARS")
    
    val generator = new NativeGenerator()
    val cCode = generator.generateC(root, baseName)
    
    val writer = new PrintWriter(baseName + ".c")
    writer.write(cCode)
    writer.close()
    
    println(s"[OK] Generated: $baseName.c")
    println("     Compile with: gcc -ffreestanding -c $baseName.c")
    println("     I'LL BE BACK")
  }
  
  /**
   * Generate x86 assembly
   * "DO IT NOW" - Direct to metal
   */
  def generateAsm(root: RootNode, baseName: String): Unit = {
    println(s"[ASM] Generating assembly: $baseName.asm")
    println("      DO IT NOW")
    
    val generator = new NativeGenerator()
    val asmCode = generator.generateAsm(root, baseName)
    
    val writer = new PrintWriter(baseName + ".asm")
    writer.write(asmCode)
    writer.close()
    
    println(s"[OK] Generated: $baseName.asm")
    println("     Assemble with: nasm -f elf32 $baseName.asm")
    println("     I'LL BE BACK")
  }
  
  /**
   * Generate kernel-ready package
   * "IT'S SHOWTIME" - Full kernel support
   */
  def generateKernel(root: RootNode, baseName: String): Unit = {
    println("==========================================")
    println("   ARNOLDC KERNEL GENERATOR")
    println("   IT'S SHOWTIME")
    println("==========================================")
    println()
    
    // Generate C code
    val generator = new NativeGenerator()
    val cCode = generator.generateC(root, baseName)
    
    val writer = new PrintWriter(baseName + "_kernel.c")
    writer.write(cCode)
    writer.close()
    println(s"[OK] Generated: ${baseName}_kernel.c")
    
    // Generate runtime header
    generateRuntimeHeader(baseName)
    println(s"[OK] Generated: arnold_runtime.h")
    
    // Generate Makefile
    generateKernelMakefile(baseName)
    println(s"[OK] Generated: Makefile")
    
    println()
    println("To build your kernel:")
    println("  1. Install i686-elf-gcc cross-compiler")
    println("  2. Run: make")
    println("  3. Run: make run  (to test in QEMU)")
    println()
    println("HASTA LA VISTA, BABY")
  }
  
  /**
   * Original JVM bytecode generation
   */
  def generateJVM(args: Array[String], root: RootNode, baseName: String, 
                  sourceCode: String, command: String): Unit = {
    val generator = new ArnoldGenerator()
    val (bytecode, _) = generator.generate(sourceCode, baseName)
    
    val out = new FileOutputStream(baseName + ".class")
    out.write(bytecode)
    out.close()
    
    println(s"[OK] Generated: $baseName.class")
    
    command match {
      case "-run" => Executor.execute(baseName)
      case "-declaim" => Declaimer.declaim(root, baseName)
      case _ =>
    }
  }
  
  def generateRuntimeHeader(baseName: String): Unit = {
    val header = """/*
 * ArnoldC Native Runtime
 * "GET TO THE CHOPPER" - Bare-metal support functions
 */

#ifndef ARNOLD_RUNTIME_H
#define ARNOLD_RUNTIME_H

/* For kernel mode */
#ifdef ARNOLD_KERNEL_MODE
  /* VGA text output */
  void arnold_print(const char* str);
  void arnold_print_int(int value);
  int arnold_read_int(void);
#else
  /* Standard library wrappers */
  #include <stdio.h>
  
  static inline void arnold_print(const char* str) {
    printf("%s", str);
  }
  
  static inline void arnold_print_int(int value) {
    printf("%d", value);
  }
  
  static inline int arnold_read_int(void) {
    int value;
    scanf("%d", &value);
    return value;
  }
#endif

/* Entry point - called by kernel or main */
void arnold_main(void);

#endif /* ARNOLD_RUNTIME_H */
"""
    val writer = new PrintWriter("arnold_runtime.h")
    writer.write(header)
    writer.close()
  }
  
  def generateKernelMakefile(baseName: String): Unit = {
    val makefile = s"""# ArnoldC Kernel Makefile
# "GET YOUR ASS TO MARS"

CC = i686-elf-gcc
AS = nasm
LD = i686-elf-ld

CFLAGS = -ffreestanding -O2 -Wall -nostdlib -DARNOLD_KERNEL_MODE
ASFLAGS = -f elf32

all: kernel.bin

${baseName}_kernel.o: ${baseName}_kernel.c arnold_runtime.h
\t$$(CC) $$(CFLAGS) -c $$< -o $$@

kernel.bin: ${baseName}_kernel.o
\t$$(LD) -T linker.ld -o $$@ $$^

run: kernel.bin
\tqemu-system-i386 -kernel kernel.bin

clean:
\trm -f *.o kernel.bin

.PHONY: all run clean
"""
    val writer = new PrintWriter("Makefile")
    writer.write(makefile)
    writer.close()
  }
  
  def getFileNameFromArgs(args: Array[String]): String = args.length match {
    case 1 => args(0)
    case 2 => args(1)
    case _ => throw new RuntimeException("WHAT THE FUCK DID I DO WRONG!")
  }
  
  def getCommandFromArgs(args: Array[String]): String = args.length match {
    case 2 => args(0)
    case 1 => ""
    case _ => throw new RuntimeException("WHAT THE FUCK DID I DO WRONG!")
  }
  
  def printUsage(): Unit = {
    println(s"""
ArnoldC Compiler v$VERSION - "I'LL BE BACK"
Extended with Native Code Support

Usage: arnoldc [options] <source.arnoldc>

Options:
  (none)     Generate JVM bytecode (.class)
  -run       Run after compilation (JVM)
  -declaim   Generate audio output (JVM)
  -native    Generate freestanding C code
  -asm       Generate x86 assembly  
  -kernel    Generate complete kernel package

Examples:
  arnoldc hello.arnoldc              # Generate hello.class
  arnoldc -run hello.arnoldc         # Compile and run on JVM
  arnoldc -native hello.arnoldc      # Generate hello.c
  arnoldc -kernel mykernel.arnoldc   # Generate kernel package

"HASTA LA VISTA, BABY"
""")
  }
}
