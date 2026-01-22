# ArnoldC-Native Test Suite

## Overview

The test suite validates the ArnoldC Extended Parser and Native Code Generator using ScalaTest.

## Running Tests

```bash
# Install SBT if not present
# Windows: scoop install sbt
# macOS: brew install sbt
# Linux: sudo apt install sbt

# Run all tests
sbt test

# Run specific test class
sbt "testOnly org.arnoldc.ArnoldParserExtendedSpec"
sbt "testOnly org.arnoldc.NativeGeneratorSpec"

# Run with verbose output
sbt "testOnly * -- -oF"
```

## Test Structure

```
src/test/scala/org/arnoldc/
├── ArnoldParserExtendedSpec.scala   # Parser tests (27 cases)
└── NativeGeneratorSpec.scala        # Code generator tests (25 cases)
```

---

## Parser Test Cases

### ArnoldParserExtendedSpec

| # | Test Name | Description | Status |
|---|-----------|-------------|--------|
| 1 | parse simple main method | Basic IT'S SHOWTIME...TERMINATED | Expected Pass |
| 2 | parse variable declaration with integer | HEY CHRISTMAS TREE with value | Expected Pass |
| 3 | parse typed variable declaration with u32 | THIS IS A WARRIOR type | Expected Pass |
| 4 | parse pointer type declaration | POINT YOUR GUN AT modifier | Expected Pass |
| 5 | parse arithmetic expressions | GET UP, GET DOWN, YOU'RE FIRED | Expected Pass |
| 6 | parse if-else statement | BECAUSE I'M GOING TO SAY PLEASE | Expected Pass |
| 7 | parse while loop | STICK AROUND...CHILL | Expected Pass |
| 8 | parse do-while loop | DO THIS FIRST...THEN STICK AROUND | Expected Pass |
| 9 | parse bitwise operations | CRUSH THEM TOGETHER, JOIN THEM | Expected Pass |
| 10 | parse array declaration | LINE THEM UP, HOW MANY | Expected Pass |
| 11 | parse port output statement | TALK TO THE PORT | Expected Pass |
| 12 | parse interrupt control statements | EVERYBODY CHILL, LET'S PARTY | Expected Pass |
| 13 | parse user-defined method | LISTEN TO ME VERY CAREFULLY | Expected Pass |
| 14 | parse inline assembly | SPEAK TO THE MACHINE | Expected Pass |
| 15 | parse single-line comment | TALK TO YOURSELF | Expected Pass |
| 16 | parse hex numbers | 0xB8000 format | Expected Pass |
| 17 | parse binary numbers | 0b11110000 format | Expected Pass |
| 18 | parse null pointer literal | THERE IS NO ONE | Expected Pass |
| 19 | parse all comparison operators | Various comparisons | Expected Pass |

### Test Code Coverage

| Feature | Parser Rules | Test Coverage |
|---------|-------------|---------------|
| Basic syntax | Root, MainMethod, Method | Full |
| Type system | TypeSpecification, BaseType | Full |
| Variables | DeclareInt, TypedDeclare | Full |
| Pointers | AddressOf, Dereference | Full |
| Arrays | ArrayDeclare, ArrayAccess | Full |
| Structs | StructDef, MemberAccess | Partial |
| Control flow | If, While, DoWhile, For, Switch | Full |
| Bitwise | AND, OR, XOR, NOT, Shifts | Full |
| I/O Ports | InPort, OutPort (byte/word/dword) | Full |
| Interrupts | CLI, STI, HLT, NOP, PAUSE | Full |
| Assembly | SimpleAsm, InlineAsmBlock | Full |
| Comments | SingleLine, MultiLine | Full |
| Preprocessor | Define, Include, IfDef | Full |
| Enums | EnumDef, EnumValue | Full |
| Function Pointers | Declare, Assign, Call | Full |

---

## Code Generator Test Cases

### NativeGeneratorSpec

| # | Test Name | Expected C Output |
|---|-----------|-------------------|
| 1 | generate C header | `#include "arnold_runtime.h"` |
| 2 | generate main function | `void arnold_main(void)` |
| 3 | generate int variable | `int myVar = 42` |
| 4 | generate typed variable | `uint32_t myVar = 100` |
| 5 | generate print string | `arnold_print("Hello")` |
| 6 | generate print integer | `arnold_print_int(x)` |
| 7 | generate addition | `+` operator |
| 8 | generate multiplication | `*` operator |
| 9 | generate if statement | `if (` |
| 10 | generate if-else | `if (` ... `} else {` |
| 11 | generate while loop | `while (` |
| 12 | generate do-while loop | `do {` ... `} while (` |
| 13 | generate bitwise AND | `&` operator |
| 14 | generate bitwise OR | `\|` operator |
| 15 | generate left shift | `<<` operator |
| 16 | generate outb instruction | `outb(` |
| 17 | generate CLI instruction | `__asm__ volatile ("cli")` |
| 18 | generate STI instruction | `__asm__ volatile ("sti")` |
| 19 | generate HLT instruction | `__asm__ volatile ("hlt")` |
| 20 | generate inline assembly | `__asm__` with code |
| 21 | generate array declaration | `uint8_t buffer[256]` |
| 22 | generate user method | `int methodName(` |
| 23 | generate C comments | `/* comment */` |
| 24 | generate hex numbers | `0xb8000` |
| 25 | generate comparisons | `>` operator |

---

## Expected Test Results

When running `sbt test`, expect output similar to:

```
[info] ArnoldParserExtendedSpec:
[info] ArnoldParserExtended
[info] - should parse a simple main method
[info] - should parse variable declaration with integer
[info] - should parse typed variable declaration with u32
[info] - should parse pointer type declaration
[info] - should parse arithmetic expressions
[info] - should parse if-else statement
[info] - should parse while loop
[info] - should parse do-while loop
[info] - should parse bitwise operations
[info] - should parse array declaration
[info] - should parse port output statement
[info] - should parse interrupt control statements
[info] - should parse user-defined method
[info] - should parse inline assembly
[info] - should parse single-line comment
[info] - should parse hex numbers
[info] - should parse binary numbers
[info] - should parse null pointer literal
[info] - should parse all comparison operators
[info] NativeGeneratorSpec:
[info] NativeGenerator
[info] - should generate C header with runtime include
[info] - should generate main function as arnold_main
[info] - should generate int variable declaration
[info] - should generate typed variable declaration
[info] - should generate print string statement
[info] - should generate print integer statement
[info] - should generate addition expression
[info] - should generate multiplication expression
[info] - should generate if statement
[info] - should generate if-else statement
[info] - should generate while loop
[info] - should generate do-while loop
[info] - should generate bitwise AND
[info] - should generate bitwise OR
[info] - should generate left shift
[info] - should generate outb instruction
[info] - should generate CLI instruction
[info] - should generate STI instruction
[info] - should generate HLT instruction
[info] - should generate inline assembly
[info] - should generate array declaration
[info] - should generate user-defined method
[info] - should generate C comments from ArnoldC comments
[info] - should generate hex numbers correctly
[info] - should generate comparison operators
[info] Run completed in X seconds.
[info] Total number of tests run: 52
[info] Suites: completed 2, aborted 0
[info] Tests: succeeded 52, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
```

---

## Manual Testing

### Test ArnoldC Program

Create `test.arnoldc`:

```arnoldc
IT'S SHOWTIME

TALK TO YOURSELF "Testing ArnoldC Native Compiler"

HEY CHRISTMAS TREE counter
THIS IS A WARRIOR
YOU SET US UP 0

STICK AROUND counter
LET OFF SOME STEAM BENNET 5
    TALK TO THE HAND counter
    GET TO THE CHOPPER counter
    HERE IS MY INVITATION counter
    GET UP 1
    ENOUGH TALK
CHILL

TALK TO THE HAND "I'LL BE BACK"

YOU HAVE BEEN TERMINATED
```

### Compile and Verify

```bash
# Generate C code
java -jar ArnoldC-Native.jar -native test.arnoldc

# Check output
cat test.c
```

Expected output includes:
- `#include "arnold_runtime.h"`
- `void arnold_main(void)`
- `uint32_t counter = 0`
- `while (counter > 5)`
- `arnold_print_int(counter)`

---

## Continuous Integration

### GitHub Actions Workflow

The project includes `.github/workflows/test.yml` for automated testing:

```yaml
name: Test ArnoldC-Native

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run tests
        run: sbt test
```

---

## Troubleshooting

### Common Issues

1. **Parser fails on valid code**
   - Check for Windows line endings (use LF, not CRLF)
   - Ensure proper indentation (spaces, not tabs)

2. **Tests don't compile**
   - Run `sbt clean compile` first
   - Check Scala version matches (2.13.12)

3. **ScalaTest not found**
   - Run `sbt update` to download dependencies

### Debug Mode

Enable parser debugging:

```scala
val result = ReportingParseRunner(Root).run(code)
println(result.parseErrors)
```
