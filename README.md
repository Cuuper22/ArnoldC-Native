# ArnoldC-Native

```
    _    ____  _   _  ___  _     ____   ____   _   _    _  _____ _____     _______ 
   / \  |  _ \| \ | |/ _ \| |   |  _ \ / ___| | \ | |  / \|_   _|_ _\ \   / / ____|
  / _ \ | |_) |  \| | | | | |   | | | | |     |  \| | / _ \ | |  | | \ \ / /|  _|  
 / ___ \|  _ <| |\  | |_| | |___| |_| | |___  | |\  |/ ___ \| |  | |  \ V / | |___ 
/_/   \_\_| \_\_| \_|\___/|_____|____/ \____| |_| \_/_/   \_\_| |___|  \_/  |_____|
                                                                                   
              FORK WITH NATIVE CODE GENERATION - "I'LL BE BACK"
```

## What Is This?

This is a **fork of ArnoldC** that adds native code generation capabilities. Instead of only outputting JVM bytecode, this version can generate:

- **Freestanding C code** for bare-metal programming
- **x86 Assembly** for direct hardware control
- **Complete kernel packages** ready to boot

## New Compiler Options

```bash
# Original JVM bytecode (unchanged)
arnoldc hello.arnoldc           # → hello.class

# NEW: Generate C code
arnoldc -native hello.arnoldc   # → hello.c

# NEW: Generate x86 assembly  
arnoldc -asm hello.arnoldc      # → hello.asm

# NEW: Generate full kernel package
arnoldc -kernel mykernel.arnoldc
```

## New Keywords for Kernel Programming

We've added Arnold quotes for systems programming:

### Memory Operations
| Keyword | C Equivalent | Arnold Quote Origin |
|---------|--------------|---------------------|
| `I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MEMORY` | `malloc()` | Terminator |
| `YOU'RE LUGGAGE` | `free()` | Commando |
| `WHAT'S WRONG WITH YOUR EYES` | `*(ptr)` read | Total Recall |
| `SEE YOU AT THE PARTY` | `*(ptr) =` write | Total Recall |

### I/O Port Operations
| Keyword | C Equivalent | Arnold Quote Origin |
|---------|--------------|---------------------|
| `TALK TO THE PORT` | `outb(port, val)` | - |
| `LISTEN TO THE PORT` | `inb(port)` | - |

### Interrupt Control
| Keyword | C Equivalent | Arnold Quote Origin |
|---------|--------------|---------------------|
| `EVERYBODY CHILL` | `cli` (disable IRQ) | Batman & Robin |
| `LET'S PARTY` | `sti` (enable IRQ) | - |
| `SLEEP NOW` | `hlt` | - |

### Bitwise Operations
| Keyword | C Equivalent | Arnold Quote Origin |
|---------|--------------|---------------------|
| `CRUSH THEM` | `&` (AND) | - |
| `JOIN THEM` | `\|` (OR) | - |
| `CONFUSE THEM` | `^` (XOR) | - |
| `PUSH LEFT` | `<<` | - |
| `PUSH RIGHT` | `>>` | - |

### Inline Assembly
| Keyword | C Equivalent |
|---------|--------------|
| `SPEAK TO THE MACHINE` | `__asm__("...")` |
| `THE MACHINE SAYS` | End of asm block |

### Enums
| Keyword | C Equivalent |
|---------|--------------|
| `THESE ARE MY OPTIONS` | `enum name {` |
| `OPTION` | Enum value |
| `NO MORE OPTIONS` | `}` |

### Preprocessor
| Keyword | C Equivalent |
|---------|--------------|
| `LET ME TELL YOU SOMETHING` | `#define` |
| `BRING IN` | `#include` |
| `IF YOU KNOW` | `#ifdef` |
| `IF YOU DON'T KNOW` | `#ifndef` |
| `THAT'S ALL I KNOW` | `#endif` |
| `ONLY ONCE` | `#pragma once` |

### Comments
| Keyword | C Equivalent |
|---------|--------------|
| `TALK TO YOURSELF "..."` | `/* ... */` |
| `LET ME THINK ABOUT THIS` | `/*` multiline |
| `I'VE THOUGHT ABOUT IT` | `*/` end |

### Comparisons
| Keyword | C Equivalent |
|---------|--------------|
| `YOU ARE NOT YOU YOU ARE ME` | `==` |
| `YOU ARE NOT ME` | `!=` |
| `LET OFF SOME STEAM BENNET` | `>` |
| `YOU'RE AT LEAST AS BIG AS` | `>=` |
| `YOU'RE NOT BIG ENOUGH` | `<` |
| `YOU'RE NOT BIGGER THAN` | `<=` |
| `THAT'S A LIE` | `!` (logical NOT) |
| `TURN IT AROUND` | `~` (bitwise NOT) |

## Example: Real Kernel in ArnoldC

```arnoldc
IT'S SHOWTIME

TALK TO THE HAND "ArnoldC Kernel Booting..."

HEY CHRISTMAS TREE vgaBuffer
YOU SET US UP 753664

HEY CHRISTMAS TREE character
YOU SET US UP 65

EVERYBODY CHILL

SEE YOU AT THE PARTY vgaBuffer character

LET'S PARTY

TALK TO THE HAND "Kernel initialized!"

SLEEP NOW

YOU HAVE BEEN TERMINATED
```

This compiles to actual kernel code that:
1. Disables interrupts
2. Writes 'A' to VGA memory (0xB8000)
3. Enables interrupts
4. Halts the CPU

## Building

### Prerequisites

```bash
# Ubuntu/Debian
sudo apt install openjdk-11-jdk scala sbt

# Build the compiler
sbt assembly
```

### Running Tests

```bash
# Run the test suite
sbt test
```

Tests cover:
- Parser tests for all syntax features
- Code generator tests for C output
- Type system tests
- Control flow tests
- Bitwise operation tests

### Usage

```bash
# Compile your ArnoldC kernel
java -jar ArnoldC-Native.jar -kernel mykernel.arnoldc

# This generates:
#   - mykernel_kernel.c     (C code)
#   - arnold_runtime.h      (Runtime header)
#   - Makefile              (Build system)

# Build the kernel (requires cross-compiler)
make

# Run in QEMU
make run
```

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    ArnoldC Source                       │
│              IT'S SHOWTIME ... TERMINATED               │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                    ArnoldParser                         │
│                   (Parboiled PEG)                       │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                    Abstract Syntax Tree                 │
│            RootNode → MethodNode → StatementNode        │
└─────────────────────────────────────────────────────────┘
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
┌───────────────────┐ ┌─────────┐ ┌─────────────┐
│  JVM Generator    │ │ Native  │ │ ASM         │
│  (Original ASM)   │ │ Gen (C) │ │ Generator   │
└───────────────────┘ └─────────┘ └─────────────┘
              │            │            │
              ▼            ▼            ▼
         .class         .c          .asm
         (JVM)      (GCC/Clang)    (NASM)
                         │            │
                         └─────┬──────┘
                               ▼
                    ┌─────────────────┐
                    │  Kernel Binary  │
                    │   (Bootable!)   │
                    └─────────────────┘
```

## How It Works

1. **Parsing**: Same Parboiled parser as original ArnoldC
2. **AST**: Extended with new node types for kernel ops
3. **Code Generation**: New `NativeGenerator` class that:
   - Walks the AST
   - Outputs freestanding C code
   - Maps Arnold quotes to C constructs
4. **Compilation**: GCC cross-compiler builds native binary
5. **Linking**: Custom linker script places code at 1MB
6. **Booting**: GRUB or direct QEMU multiboot

## Why Fork Instead of Transpile?

1. **Single tool** - One compiler, multiple backends
2. **Proper AST** - Work with parsed structure, not text
3. **Extensible** - Easy to add new keywords
4. **Maintainable** - Changes to parser benefit all backends
5. **Authentic** - It's still ArnoldC, just enhanced

## Compatibility

- All original ArnoldC programs work unchanged
- New keywords only needed for kernel-specific ops
- JVM output identical to original compiler
- `-native` and `-kernel` are additive features

## Contributing

1. Fork this repo
2. Add your Arnold quotes
3. Implement the AST node
4. Add to NativeGenerator
5. Submit PR with "I'LL BE BACK"

## License

Apache 2.0 (same as original ArnoldC)

---

```
"Come with me if you want to boot." - ArnoldC Kernel

"I'll be back." - Every kernel after a reboot

"Consider that a divorce." - When unmounting filesystems

"Get to the chopper!" - Memory allocation

"You have been terminated." - Process exit
```

## Credits

- **Original ArnoldC**: Lauri Hartikk
- **Native Extension**: Claude Code
- **Inspiration**: The Governator himself
