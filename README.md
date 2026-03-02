## Why

I wanted to write an operating system in ArnoldC — the language where every keyword is an Arnold Schwarzenegger movie quote. Problem: ArnoldC only compiles to JVM bytecode. You can't boot an OS on the JVM.

So I forked the compiler and added native code generation. Three new backends: freestanding C, x86 assembly, and full kernel packages. Then I added 36 new keywords for the things an OS needs — memory allocation (`I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MEMORY`), interrupt control (`EVERYBODY CHILL`), port I/O (`TALK TO THE PORT`), inline assembly, bitwise operations.

The result: [ToaruOS-Arnold](https://github.com/Cuuper22/ToaruOS-Arnold), a 22,000-line bare-metal desktop OS compiled entirely through this fork. The compiler turned a joke language into something that can drive a TCP/IP stack.

# ArnoldC-Native

Fork of [ArnoldC](https://github.com/lhartikk/ArnoldC) with native x86 code generation. All original programs work unchanged — the new backends and keywords are additive.

Built to compile [ToaruOS-Arnold](https://github.com/Cuuper22/ToaruOS-Arnold).

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## What it adds

```bash
# Original JVM bytecode (unchanged)
arnoldc hello.arnoldc           # → hello.class

# NEW: Generate freestanding C code
arnoldc -native hello.arnoldc   # → hello.c

# NEW: Generate x86 assembly
arnoldc -asm hello.arnoldc      # → hello.asm

# NEW: Generate full kernel package
arnoldc -kernel mykernel.arnoldc
```

## New keywords

36 new Arnold quotes for systems programming:

<details>
<summary>Memory operations</summary>

| Keyword | C Equivalent | Source |
|---------|-------------|--------|
| `I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MEMORY` | `malloc()` | Terminator |
| `YOU'RE LUGGAGE` | `free()` | Commando |
| `WHAT'S WRONG WITH YOUR EYES` | `*(ptr)` read | Total Recall |
| `SEE YOU AT THE PARTY` | `*(ptr) =` write | Total Recall |

</details>

<details>
<summary>I/O, interrupts, bitwise</summary>

| Keyword | C Equivalent |
|---------|-------------|
| `TALK TO THE PORT` | `outb(port, val)` |
| `LISTEN TO THE PORT` | `inb(port)` |
| `EVERYBODY CHILL` | `cli` (disable IRQ) |
| `LET'S PARTY` | `sti` (enable IRQ) |
| `SLEEP NOW` | `hlt` |
| `CRUSH THEM` | `&` (AND) |
| `JOIN THEM` | `\|` (OR) |
| `CONFUSE THEM` | `^` (XOR) |
| `PUSH LEFT` / `PUSH RIGHT` | `<<` / `>>` |

</details>

<details>
<summary>Inline assembly, enums, preprocessor, comparisons</summary>

| Keyword | C Equivalent |
|---------|-------------|
| `SPEAK TO THE MACHINE` | `__asm__("...")` |
| `THESE ARE MY OPTIONS` / `OPTION` | `enum` |
| `LET ME TELL YOU SOMETHING` | `#define` |
| `BRING IN` | `#include` |
| `IF YOU KNOW` / `IF YOU DON'T KNOW` | `#ifdef` / `#ifndef` |
| `LET OFF SOME STEAM BENNET` | `>` |
| `YOU'RE NOT BIG ENOUGH` | `<` |
| `THAT'S A LIE` | `!` (logical NOT) |
| `TURN IT AROUND` | `~` (bitwise NOT) |

</details>

## Example: kernel in ArnoldC

```arnoldc
IT'S SHOWTIME

HEY CHRISTMAS TREE vgaBuffer
YOU SET US UP 753664

HEY CHRISTMAS TREE character
YOU SET US UP 65

EVERYBODY CHILL                        ; disable interrupts
SEE YOU AT THE PARTY vgaBuffer character  ; write 'A' to VGA memory
LET'S PARTY                            ; enable interrupts
SLEEP NOW                              ; halt CPU

YOU HAVE BEEN TERMINATED
```

This compiles to a bootable kernel that writes 'A' to VGA memory at 0xB8000.

## Architecture

```
┌──────────────────────────────────────┐
│          ArnoldC Source              │
│   IT'S SHOWTIME ... TERMINATED      │
└──────────────────────┬───────────────┘
                       │
                       ▼
┌──────────────────────────────────────┐
│      ArnoldParser (Parboiled PEG)    │
└──────────────────────┬───────────────┘
                       │
                       ▼
┌──────────────────────────────────────┐
│         Abstract Syntax Tree         │
│   RootNode → MethodNode → Statement  │
└──────┬───────────┬───────────┬───────┘
       │           │           │
       ▼           ▼           ▼
  ┌─────────┐ ┌─────────┐ ┌─────────┐
  │   JVM   │ │ Native  │ │   ASM   │
  │ (orig)  │ │  (C)    │ │ (x86)   │
  └────┬────┘ └────┬────┘ └────┬────┘
       │           │           │
       ▼           ▼           ▼
    .class       .c          .asm
               (GCC)       (NASM)
                 └─────┬─────┘
                       ▼
              Bootable Kernel
```

## Building

```bash
# Prerequisites: Java 11+, sbt
sbt assembly

# Use the compiler
java -jar target/scala-2.13/ArnoldC-Native.jar -kernel mykernel.arnoldc

# Run tests
sbt test
```

## Known limitations

**JVM backend:** New native-mode keywords (memory ops, port I/O, inline assembly) fail in JVM mode. Use `-native`, `-asm`, or `-kernel` flags for extended features.

**AST coverage:** The codebase defines ~194 AST node classes, but only ~46 have parser rules. The remaining ~150 are planned extensions (advanced bitwise, CPU instructions, type system, etc.) — defined but not yet parseable. See `src/main/scala/org/arnoldc/ast/AstNode.scala` for the full list.

**Kernel mode:** Generated Makefiles require `i686-elf-gcc` cross-compiler and may need customization for your kernel. Linker scripts are not auto-generated.

## Compatibility

All original ArnoldC programs work unchanged. New keywords only activate with `-native`/`-asm`/`-kernel` flags. JVM output is identical to the original compiler.

## Credits

- **Original ArnoldC** — [Lauri Hartikka](https://github.com/lhartikk)
- **Native code generation** — [Cuuper22](https://github.com/Cuuper22)
- **Used by** — [ToaruOS-Arnold](https://github.com/Cuuper22/ToaruOS-Arnold) (22K-line bare-metal OS)

## License

Apache 2.0 (same as original ArnoldC)
