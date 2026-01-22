# ArnoldC Systems Programming Language Specification

## "I'LL BE BACK" - Extended for Bare-Metal Programming

This document specifies the extended ArnoldC language designed for systems programming,
including operating system kernels, embedded systems, and bare-metal applications.

---

## Table of Contents

1. [Original ArnoldC Keywords](#1-original-arnoldc-keywords)
2. [Type System](#2-type-system)
3. [Pointer Operations](#3-pointer-operations)
4. [Struct and Union Definitions](#4-struct-and-union-definitions)
5. [Array Operations](#5-array-operations)
6. [Bitwise Operations](#6-bitwise-operations)
7. [Control Flow Extensions](#7-control-flow-extensions)
8. [Memory Operations](#8-memory-operations)
9. [I/O Port Operations](#9-io-port-operations)
10. [Interrupt Control](#10-interrupt-control)
11. [Inline Assembly](#11-inline-assembly)
12. [Function Pointers](#12-function-pointers)
13. [Type Casting and Sizeof](#13-type-casting-and-sizeof)
14. [Complete Grammar](#14-complete-grammar)
15. [Examples](#15-examples)

---

## 0. Comments and General Syntax

### Comments

Comments in ArnoldC are added automatically in generated C code (like `/* GET TO THE CHOPPER */`) for debugging.

For source-level comments (future enhancement), the syntax will be:
```arnoldc
TALK TO YOURSELF "This is a comment - ignored by compiler"
```

### Numeric Literals

| Format | Example | Description |
|--------|---------|-------------|
| Decimal | `42`, `-17` | Standard integers |
| Hexadecimal | `0xB8000`, `0xFF` | Prefixed with `0x` |
| Binary | `0b10101010`, `0b1111` | Prefixed with `0b` |
| Character | `'A'`, `'0'` | Single character in quotes |

### Special Values

| Keyword | Value | Description |
|---------|-------|-------------|
| `@NO PROBLEMO` | `1` (true) | Boolean true |
| `@I LIED` | `0` (false) | Boolean false |
| `THERE IS NO ONE` | `NULL` / `0` | Null pointer |

---

## 1. Original ArnoldC Keywords

### Program Structure
| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `IT'S SHOWTIME` | Begin main function | `int main() {` |
| `YOU HAVE BEEN TERMINATED` | End main function | `}` |
| `LISTEN TO ME VERY CAREFULLY` | Declare method | `void func_name(` |
| `I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE` | Method parameter | `int param` |
| `GIVE THESE PEOPLE AIR` | Non-void return type | Return type is `int` |
| `HASTA LA VISTA, BABY` | End method declaration | `}` |

### Variable Declaration
| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `HEY CHRISTMAS TREE` | Declare variable | `int var_name` |
| `YOU SET US UP` | Set initial value | `= value;` |

### Variable Assignment
| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `GET TO THE CHOPPER` | Begin assignment | `var_name = ` |
| `HERE IS MY INVITATION` | Set value in assignment | `value` |
| `ENOUGH TALK` | End assignment | `;` |

### Arithmetic Operations (inside assignment)
| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `GET UP` | Addition | `+` |
| `GET DOWN` | Subtraction | `-` |
| `YOU'RE FIRED` | Multiplication | `*` |
| `HE HAD TO SPLIT` | Division | `/` |
| `I LET HIM GO` | Modulo | `%` |

### Logical/Comparison Operations
| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `YOU ARE NOT YOU YOU ARE ME` | Equality | `==` |
| `YOU ARE NOT ME` | Not equal | `!=` |
| `LET OFF SOME STEAM BENNET` | Greater than | `>` |
| `YOU'RE AT LEAST AS BIG AS` | Greater than or equal | `>=` |
| `YOU'RE NOT BIG ENOUGH` | Less than | `<` |
| `YOU'RE NOT BIGGER THAN` | Less than or equal | `<=` |
| `CONSIDER THAT A DIVORCE` | Logical OR | `\|\|` |
| `KNOCK KNOCK` | Logical AND | `&&` |
| `THAT'S A LIE` | Logical NOT | `!` |

### Boolean Values and Special Literals
| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `@NO PROBLEMO` | True | `1` |
| `@I LIED` | False | `0` |
| `THERE IS NO ONE` | Null pointer | `NULL` / `0` |
| `0x...` | Hexadecimal literal | `0x...` |
| `0b...` | Binary literal | `0b...` |
| `'c'` | Character literal | `'c'` |

### Control Flow
| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `BECAUSE I'M GOING TO SAY PLEASE` | If statement | `if (condition) {` |
| `BULLSHIT` | Else branch | `} else {` |
| `YOU HAVE NO RESPECT FOR LOGIC` | End if | `}` |
| `STICK AROUND` | While loop | `while (condition) {` |
| `CHILL` | End while | `}` |
| `DO THIS FIRST` | Do-while loop start | `do {` |
| `THEN STICK AROUND` | Do-while condition | `} while (cond);` |

### I/O Operations
| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `TALK TO THE HAND` | Print output | `printf(...)` |
| `I WANT TO ASK YOU A BUNCH OF QUESTIONS AND I WANT TO HAVE THEM ANSWERED IMMEDIATELY` | Read input | `scanf(...)` |

### Method Calls
| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `DO IT NOW` | Call method | `func_name(args)` |
| `GET YOUR ASS TO MARS` | Assign from method call | `var = func()` |
| `I'LL BE BACK` | Return statement | `return value;` |

---

## 2. Type System

### Type Declaration Keywords

| Keyword | Type | Size | C Equivalent |
|---------|------|------|--------------|
| `THIS IS A TINY WARRIOR` | u8 | 8-bit unsigned | `uint8_t` |
| `THIS IS A SMALL WARRIOR` | u16 | 16-bit unsigned | `uint16_t` |
| `THIS IS A WARRIOR` | u32 | 32-bit unsigned | `uint32_t` |
| `THIS IS A BIG WARRIOR` | u64 | 64-bit unsigned | `uint64_t` |
| `THIS IS A TINY ENEMY` | i8 | 8-bit signed | `int8_t` |
| `THIS IS A SMALL ENEMY` | i16 | 16-bit signed | `int16_t` |
| `THIS IS AN ENEMY` | i32 | 32-bit signed | `int32_t` |
| `THIS IS A BIG ENEMY` | i64 | 64-bit signed | `int64_t` |
| `THIS IS NOTHING` | void | void type | `void` |
| `THIS IS A LETTER` | char | 8-bit char | `char` |
| `THIS IS ALWAYS THE SAME` | const | constant modifier | `const` |
| `THIS COULD CHANGE ANYTIME` | volatile | volatile modifier | `volatile` |

### Syntax

```arnoldc
HEY CHRISTMAS TREE myByte
THIS IS A TINY WARRIOR
YOU SET US UP 255

HEY CHRISTMAS TREE myInt
THIS IS A WARRIOR
YOU SET US UP 42

HEY CHRISTMAS TREE myConstant
THIS IS ALWAYS THE SAME
THIS IS A WARRIOR
YOU SET US UP 100
```

### Generated C
```c
uint8_t myByte = 255;
uint32_t myInt = 42;
const uint32_t myConstant = 100;
```

---

## 3. Pointer Operations

### Pointer Keywords

| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `POINT YOUR GUN AT` | Declare pointer type | `type *` |
| `WHERE ARE YOU` | Address-of operator | `&var` |
| `SHOW ME WHAT YOU GOT` | Dereference operator | `*ptr` |
| `AIM AT` | Assign pointer | `ptr = &var` |
| `FIRE AT` | Write through pointer | `*ptr = value` |

### Syntax

```arnoldc
HEY CHRISTMAS TREE value
THIS IS A WARRIOR
YOU SET US UP 42

HEY CHRISTMAS TREE myPointer
POINT YOUR GUN AT
THIS IS A WARRIOR
AIM AT WHERE ARE YOU value

GET TO THE CHOPPER result
HERE IS MY INVITATION SHOW ME WHAT YOU GOT myPointer
ENOUGH TALK

FIRE AT myPointer
HERE IS MY INVITATION 100
ENOUGH TALK
```

### Generated C
```c
uint32_t value = 42;
uint32_t *myPointer = &value;
result = *myPointer;
*myPointer = 100;
```

---

## 4. Struct and Union Definitions

### Struct Keywords

| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `THIS IS WHAT I AM` | Begin struct definition | `struct name {` |
| `THIS IS WHAT I'M MADE OF` | Struct member | `type member;` |
| `I'M DONE DESCRIBING MYSELF` | End struct definition | `};` |
| `CREATE ONE LIKE` | Declare struct variable | `struct_type var;` |
| `WHAT'S YOUR` | Access struct member | `var.member` |
| `SHOW ME THE` | Access via pointer | `ptr->member` |

### Union Keywords

| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `THIS IS MY DISGUISE` | Begin union definition | `union name {` |
| `I CAN LOOK LIKE` | Union member | `type member;` |
| `DISGUISE COMPLETE` | End union definition | `};` |

### Syntax

```arnoldc
THIS IS WHAT I AM GDTEntry
THIS IS WHAT I'M MADE OF limitLow
    THIS IS A SMALL WARRIOR
THIS IS WHAT I'M MADE OF baseLow
    THIS IS A SMALL WARRIOR
THIS IS WHAT I'M MADE OF baseMiddle
    THIS IS A TINY WARRIOR
THIS IS WHAT I'M MADE OF access
    THIS IS A TINY WARRIOR
THIS IS WHAT I'M MADE OF granularity
    THIS IS A TINY WARRIOR
THIS IS WHAT I'M MADE OF baseHigh
    THIS IS A TINY WARRIOR
I'M DONE DESCRIBING MYSELF

HEY CHRISTMAS TREE myEntry
CREATE ONE LIKE GDTEntry

GET TO THE CHOPPER WHAT'S YOUR myEntry limitLow
HERE IS MY INVITATION 0xFFFF
ENOUGH TALK
```

### Generated C
```c
struct GDTEntry {
    uint16_t limitLow;
    uint16_t baseLow;
    uint8_t baseMiddle;
    uint8_t access;
    uint8_t granularity;
    uint8_t baseHigh;
};

struct GDTEntry myEntry;
myEntry.limitLow = 0xFFFF;
```

---

## 5. Array Operations

### Array Keywords

| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `LINE THEM UP` | Declare array | `type arr[size]` |
| `HOW MANY` | Array size | `[size]` |
| `WHICH ONE` | Array index access | `arr[index]` |
| `PUT THEM IN LINE` | Array initializer | `= {values}` |

### Syntax

```arnoldc
HEY CHRISTMAS TREE buffer
LINE THEM UP
THIS IS A TINY WARRIOR
HOW MANY 256

HEY CHRISTMAS TREE gdt
LINE THEM UP
CREATE ONE LIKE GDTEntry
HOW MANY 5

GET TO THE CHOPPER WHICH ONE buffer 0
HERE IS MY INVITATION 0x41
ENOUGH TALK

HEY CHRISTMAS TREE lookup
LINE THEM UP
THIS IS A WARRIOR
HOW MANY 5
PUT THEM IN LINE
10 20 30 40 50
```

### Generated C
```c
uint8_t buffer[256];
struct GDTEntry gdt[5];
buffer[0] = 0x41;
uint32_t lookup[5] = {10, 20, 30, 40, 50};
```

---

## 6. Bitwise Operations

### Bitwise Keywords

| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `CRUSH THEM TOGETHER` | Bitwise AND | `&` |
| `JOIN THEM TOGETHER` | Bitwise OR | `\|` |
| `CONFUSE THEM` | Bitwise XOR | `^` |
| `TURN IT AROUND` | Bitwise NOT | `~` |
| `PUSH IT LEFT` | Left shift | `<<` |
| `PUSH IT RIGHT` | Right shift | `>>` |

### Syntax

```arnoldc
GET TO THE CHOPPER flags
HERE IS MY INVITATION baseFlags
CRUSH THEM TOGETHER 0x0F
JOIN THEM TOGETHER 0x80
PUSH IT LEFT 4
ENOUGH TALK

GET TO THE CHOPPER inverted
HERE IS MY INVITATION value
TURN IT AROUND
ENOUGH TALK
```

### Generated C
```c
flags = ((baseFlags & 0x0F) | 0x80) << 4;
inverted = ~value;
```

---

## 7. Control Flow Extensions

### For Loop Keywords

| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `I'LL COUNT FROM` | For loop start | `for (i = start;` |
| `TO` | For loop end condition | `i < end;` |
| `COUNT BY` | For loop increment | `i += step) {` |
| `STOP COUNTING` | End for loop | `}` |

### Switch/Case Keywords

| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `WHAT'S THE CHOICE` | Switch statement | `switch (var) {` |
| `WHEN IT'S` | Case label | `case value:` |
| `OTHERWISE` | Default case | `default:` |
| `GET OUT` | Break statement | `break;` |
| `NO MORE CHOICES` | End switch | `}` |

### Other Control Flow

| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `GET OUT` | Break | `break;` |
| `DO IT AGAIN` | Continue | `continue;` |
| `GO TO` | Goto | `goto label;` |
| `YOU ARE HERE` | Label | `label:` |

### Syntax

```arnoldc
I'LL COUNT FROM i 0 TO 10 COUNT BY 1
    TALK TO THE HAND i
STOP COUNTING

WHAT'S THE CHOICE value
WHEN IT'S 1
    TALK TO THE HAND "One"
    GET OUT
WHEN IT'S 2
    TALK TO THE HAND "Two"
    GET OUT
OTHERWISE
    TALK TO THE HAND "Other"
NO MORE CHOICES
```

### Generated C
```c
for (int i = 0; i < 10; i += 1) {
    printf("%d\n", i);
}

switch (value) {
    case 1:
        printf("One\n");
        break;
    case 2:
        printf("Two\n");
        break;
    default:
        printf("Other\n");
}
```

---

## 8. Memory Operations

### Memory Keywords

| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `I NEED YOUR MEMORY` | Allocate memory | `malloc(size)` or custom allocator |
| `YOU'RE LUGGAGE` | Free memory | `free(ptr)` |
| `LOOK AT` | Read from address | `*(type*)addr` |
| `WRITE TO` | Write to address | `*(type*)addr = val` |
| `COPY FROM TO` | Memory copy | `memcpy(dst, src, n)` |
| `FILL WITH` | Memory set | `memset(ptr, val, n)` |

### Syntax

```arnoldc
HEY CHRISTMAS TREE buffer
POINT YOUR GUN AT
THIS IS A TINY WARRIOR
I NEED YOUR MEMORY 1024

FILL WITH buffer 0 1024

YOU'RE LUGGAGE buffer

LOOK AT 0xB8000
THIS IS A SMALL WARRIOR
GET TO THE CHOPPER vgaChar
HERE IS MY INVITATION LOOK AT 0xB8000
ENOUGH TALK

WRITE TO 0xB8000
THIS IS A SMALL WARRIOR
HERE IS MY INVITATION 0x0F41
ENOUGH TALK
```

### Generated C
```c
uint8_t *buffer = arnold_alloc(1024);
arnold_memset(buffer, 0, 1024);
arnold_free(buffer);
uint16_t vgaChar = *(uint16_t*)0xB8000;
*(uint16_t*)0xB8000 = 0x0F41;
```

---

## 9. I/O Port Operations

### I/O Port Keywords

| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `TALK TO THE PORT` | Output to port | `outb(port, value)` |
| `LISTEN TO THE PORT` | Input from port | `inb(port)` |
| `TALK BIG TO THE PORT` | Output word to port | `outw(port, value)` |
| `LISTEN BIG TO THE PORT` | Input word from port | `inw(port)` |
| `TALK HUGE TO THE PORT` | Output dword to port | `outl(port, value)` |
| `LISTEN HUGE TO THE PORT` | Input dword from port | `inl(port)` |

### Syntax

```arnoldc
TALK TO THE PORT 0x3F8 0x41

GET YOUR ASS TO MARS status
LISTEN TO THE PORT 0x3FD
DO IT NOW

STICK AROUND status
    CRUSH THEM TOGETHER 0x20
    YOU ARE NOT YOU YOU ARE ME 0
    GET YOUR ASS TO MARS status
    LISTEN TO THE PORT 0x3FD
    DO IT NOW
CHILL
```

### Generated C
```c
outb(0x3F8, 0x41);
status = inb(0x3FD);
while ((status & 0x20) == 0) {
    status = inb(0x3FD);
}
```

---

## 10. Interrupt Control

### Interrupt Keywords

| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `EVERYBODY CHILL` | Disable interrupts | `cli` / `__asm__("cli")` |
| `LET'S PARTY` | Enable interrupts | `sti` / `__asm__("sti")` |
| `SLEEP NOW` | Halt CPU | `hlt` / `__asm__("hlt")` |
| `WAIT A MOMENT` | Pause instruction | `pause` / `__asm__("pause")` |
| `WHAT'S THE INTERRUPT` | Get interrupt flag | Read EFLAGS |
| `HANDLE THIS` | Define ISR | ISR function |

### Syntax

```arnoldc
EVERYBODY CHILL

TALK TO THE HAND "Interrupts disabled"

LET'S PARTY

STICK AROUND @NO PROBLEMO
    SLEEP NOW
CHILL
```

### Generated C
```c
__asm__ volatile ("cli");
printf("Interrupts disabled\n");
__asm__ volatile ("sti");
while (1) {
    __asm__ volatile ("hlt");
}
```

---

## 11. Inline Assembly

### Inline Assembly Keywords

| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `SPEAK TO THE MACHINE` | Begin inline assembly | `__asm__ volatile (` |
| `THE MACHINE SAYS` | End inline assembly | `);` |
| `MACHINE INPUT` | Input operand | `: "r"(var)` |
| `MACHINE OUTPUT` | Output operand | `: "=r"(var)` |
| `MACHINE DESTROYS` | Clobber list | `: "memory"` |

### Syntax

```arnoldc
HEY CHRISTMAS TREE cr0Value
THIS IS A WARRIOR
YOU SET US UP 0

SPEAK TO THE MACHINE
    mov eax, cr0
    or eax, 1
    mov cr0, eax
MACHINE OUTPUT cr0Value eax
THE MACHINE SAYS

SPEAK TO THE MACHINE
    lgdt [gdt_descriptor]
    jmp 0x08:flush
    flush:
THE MACHINE SAYS
```

### Generated C
```c
uint32_t cr0Value = 0;
__asm__ volatile (
    "mov %%cr0, %%eax\n\t"
    "or $1, %%eax\n\t"
    "mov %%eax, %%cr0"
    : "=a"(cr0Value)
);

__asm__ volatile (
    "lgdt (%0)\n\t"
    "jmp $0x08, $flush\n\t"
    "flush:"
    :
    : "r"(&gdt_descriptor)
);
```

---

## 12. Function Pointers

### Function Pointer Keywords

| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `REMEMBER THIS MOVE` | Declare function pointer | `ret (*name)(args)` |
| `LOOKS LIKE` | Function pointer type | Function signature |
| `USE THAT MOVE` | Call via function pointer | `(*fptr)(args)` |
| `LEARN THE MOVE` | Assign function to pointer | `fptr = &func` |

### Syntax

#### Declaring a Function Pointer
```arnoldc
REMEMBER THIS MOVE handler
LOOKS LIKE THIS IS NOTHING
I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE
THIS IS A WARRIOR
```

This declares: `void (*handler)(uint32_t);`

For a function pointer with multiple parameters:
```arnoldc
REMEMBER THIS MOVE callback
LOOKS LIKE THIS IS A WARRIOR
I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE
THIS IS A WARRIOR
I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE
POINT YOUR GUN AT
THIS IS A TINY WARRIOR
```

This declares: `uint32_t (*callback)(uint32_t, uint8_t*);`

#### Assigning a Function to a Pointer
```arnoldc
LEARN THE MOVE handler FROM myInterruptHandler
```

This assigns: `handler = &myInterruptHandler;`

#### Calling Through a Function Pointer
```arnoldc
USE THAT MOVE handler 0x21

GET YOUR ASS TO MARS result
USE THAT MOVE callback 42 buffer
```

### Generated C
```c
void (*handler)(uint32_t);
handler = &myInterruptHandler;
(*handler)(0x21);

uint32_t (*callback)(uint32_t, uint8_t*);
result = (*callback)(42, buffer);
```

---

## 13. Type Casting and Sizeof

### Cast and Sizeof Keywords

| Keyword | Meaning | C Equivalent |
|---------|---------|--------------|
| `MAKE IT A` | Type cast | `(type)` |
| `HOW BIG IS` | Sizeof operator | `sizeof(type)` |
| `HOW BIG IS THAT` | Sizeof expression | `sizeof(expr)` |

### Syntax

```arnoldc
GET TO THE CHOPPER bytePtr
HERE IS MY INVITATION MAKE IT A POINT YOUR GUN AT THIS IS A TINY WARRIOR address
ENOUGH TALK

HEY CHRISTMAS TREE structSize
THIS IS A WARRIOR
YOU SET US UP HOW BIG IS GDTEntry

HEY CHRISTMAS TREE arraySize
THIS IS A WARRIOR
YOU SET US UP HOW BIG IS THAT myArray
```

### Generated C
```c
bytePtr = (uint8_t*)address;
uint32_t structSize = sizeof(struct GDTEntry);
uint32_t arraySize = sizeof(myArray);
```

---

## 14. Complete Grammar

### EBNF Grammar

```ebnf
Program         ::= (StructDef | UnionDef | GlobalDecl)* MainMethod Method*

MainMethod      ::= "IT'S SHOWTIME" EOL Statement* "YOU HAVE BEEN TERMINATED"

Method          ::= "LISTEN TO ME VERY CAREFULLY" MethodName EOL
                    MethodParams?
                    ReturnType?
                    Statement*
                    "HASTA LA VISTA, BABY"

MethodParams    ::= ("I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE" 
                    TypeSpec Variable EOL)*

ReturnType      ::= "GIVE THESE PEOPLE AIR" TypeSpec? EOL

TypeSpec        ::= PrimitiveType | PointerType | ArrayType | StructType | Modifier TypeSpec

PrimitiveType   ::= "THIS IS A TINY WARRIOR"    (* u8 *)
                  | "THIS IS A SMALL WARRIOR"   (* u16 *)
                  | "THIS IS A WARRIOR"         (* u32 *)
                  | "THIS IS A BIG WARRIOR"     (* u64 *)
                  | "THIS IS A TINY ENEMY"      (* i8 *)
                  | "THIS IS A SMALL ENEMY"     (* i16 *)
                  | "THIS IS AN ENEMY"          (* i32 *)
                  | "THIS IS A BIG ENEMY"       (* i64 *)
                  | "THIS IS NOTHING"           (* void *)
                  | "THIS IS A LETTER"          (* char *)

Modifier        ::= "THIS IS ALWAYS THE SAME"   (* const *)
                  | "THIS COULD CHANGE ANYTIME" (* volatile *)

PointerType     ::= "POINT YOUR GUN AT" TypeSpec

ArrayType       ::= "LINE THEM UP" TypeSpec "HOW MANY" Integer

StructType      ::= "CREATE ONE LIKE" TypeName

StructDef       ::= "THIS IS WHAT I AM" TypeName EOL
                    StructMember*
                    "I'M DONE DESCRIBING MYSELF"

StructMember    ::= "THIS IS WHAT I'M MADE OF" MemberName EOL
                    TypeSpec EOL

UnionDef        ::= "THIS IS MY DISGUISE" TypeName EOL
                    UnionMember*
                    "DISGUISE COMPLETE"

UnionMember     ::= "I CAN LOOK LIKE" MemberName EOL
                    TypeSpec EOL

Statement       ::= DeclareStmt | AssignStmt | PrintStmt | IfStmt | WhileStmt
                  | ForStmt | SwitchStmt | CallStmt | ReturnStmt | MemoryStmt
                  | PortStmt | InterruptStmt | AsmStmt | BreakStmt | ContinueStmt
                  | GotoStmt | LabelStmt

DeclareStmt     ::= "HEY CHRISTMAS TREE" Variable EOL
                    TypeSpec EOL
                    "YOU SET US UP" Expression EOL

AssignStmt      ::= "GET TO THE CHOPPER" LValue EOL
                    "HERE IS MY INVITATION" Expression
                    (ArithOp Expression | BitwiseOp Expression | LogicalOp Expression)*
                    "ENOUGH TALK" EOL

LValue          ::= Variable
                  | "WHICH ONE" Variable Expression    (* array[index] *)
                  | "WHAT'S YOUR" Variable MemberName  (* struct.member *)
                  | "SHOW ME THE" Variable MemberName  (* ptr->member *)
                  | "SHOW ME WHAT YOU GOT" Variable    (* *ptr *)

ArithOp         ::= "GET UP"           (* + *)
                  | "GET DOWN"         (* - *)
                  | "YOU'RE FIRED"     (* * *)
                  | "HE HAD TO SPLIT"  (* / *)
                  | "I LET HIM GO"     (* % *)

BitwiseOp       ::= "CRUSH THEM TOGETHER"  (* & *)
                  | "JOIN THEM TOGETHER"   (* | *)
                  | "CONFUSE THEM"         (* ^ *)
                  | "PUSH IT LEFT"         (* << *)
                  | "PUSH IT RIGHT"        (* >> *)

LogicalOp       ::= "YOU ARE NOT YOU YOU ARE ME"  (* == *)
                  | "LET OFF SOME STEAM BENNET"   (* > *)
                  | "CONSIDER THAT A DIVORCE"     (* || *)
                  | "KNOCK KNOCK"                 (* && *)

Expression      ::= Operand
                  | "WHERE ARE YOU" Variable           (* &var *)
                  | "SHOW ME WHAT YOU GOT" Variable    (* *ptr *)
                  | "TURN IT AROUND" Expression        (* ~expr *)
                  | "MAKE IT A" TypeSpec Expression    (* (type)expr *)
                  | "HOW BIG IS" TypeSpec              (* sizeof(type) *)
                  | "HOW BIG IS THAT" Variable         (* sizeof(var) *)
                  | "WHICH ONE" Variable Expression    (* arr[i] *)
                  | "WHAT'S YOUR" Variable MemberName  (* s.member *)

Operand         ::= Integer | HexInteger | Variable | Boolean | String

Boolean         ::= "@NO PROBLEMO" | "@I LIED"

IfStmt          ::= "BECAUSE I'M GOING TO SAY PLEASE" Expression EOL
                    Statement*
                    ("BULLSHIT" EOL Statement*)?
                    "YOU HAVE NO RESPECT FOR LOGIC" EOL

WhileStmt       ::= "STICK AROUND" Expression EOL
                    Statement*
                    "CHILL" EOL

ForStmt         ::= "I'LL COUNT FROM" Variable Expression 
                    "TO" Expression 
                    "COUNT BY" Expression EOL
                    Statement*
                    "STOP COUNTING" EOL

SwitchStmt      ::= "WHAT'S THE CHOICE" Variable EOL
                    CaseClause*
                    DefaultClause?
                    "NO MORE CHOICES" EOL

CaseClause      ::= "WHEN IT'S" Operand EOL Statement*

DefaultClause   ::= "OTHERWISE" EOL Statement*

BreakStmt       ::= "GET OUT" EOL

ContinueStmt    ::= "DO IT AGAIN" EOL

GotoStmt        ::= "GO TO" Label EOL

LabelStmt       ::= "YOU ARE HERE" Label EOL

CallStmt        ::= ("GET YOUR ASS TO MARS" Variable EOL)?
                    "DO IT NOW" MethodName Expression* EOL

ReturnStmt      ::= "I'LL BE BACK" Expression? EOL

MemoryStmt      ::= "I NEED YOUR MEMORY" Expression EOL                (* alloc *)
                  | "YOU'RE LUGGAGE" Variable EOL                      (* free *)
                  | "FILL WITH" Variable Expression Expression EOL     (* memset *)
                  | "COPY FROM" Variable "TO" Variable Expression EOL  (* memcpy *)
                  | "LOOK AT" Expression EOL TypeSpec EOL              (* read mem *)
                  | "WRITE TO" Expression EOL TypeSpec EOL 
                    "HERE IS MY INVITATION" Expression EOL             (* write mem *)

PortStmt        ::= "TALK TO THE PORT" Expression Expression EOL       (* outb *)
                  | "LISTEN TO THE PORT" Expression EOL                (* inb *)
                  | "TALK BIG TO THE PORT" Expression Expression EOL   (* outw *)
                  | "LISTEN BIG TO THE PORT" Expression EOL            (* inw *)

InterruptStmt   ::= "EVERYBODY CHILL" EOL       (* cli *)
                  | "LET'S PARTY" EOL           (* sti *)
                  | "SLEEP NOW" EOL             (* hlt *)
                  | "WAIT A MOMENT" EOL         (* pause *)

AsmStmt         ::= "SPEAK TO THE MACHINE" EOL
                    AsmLine*
                    ("MACHINE OUTPUT" Variable Register EOL)*
                    ("MACHINE INPUT" Variable Register EOL)*
                    ("MACHINE DESTROYS" Register+ EOL)*
                    "THE MACHINE SAYS" EOL

AsmLine         ::= .* EOL  (* Raw assembly text *)

FunctionPtr     ::= "HEY CHRISTMAS TREE" Variable EOL
                    "REMEMBER THIS MOVE" EOL
                    ReturnTypeSpec EOL
                    "LOOKS LIKE" "(" ParamTypes ")" EOL

ParamTypes      ::= TypeSpec ("," TypeSpec)*

FuncPtrAssign   ::= "LEARN THE MOVE" Variable MethodName EOL

FuncPtrCall     ::= ("GET YOUR ASS TO MARS" Variable EOL)?
                    "USE THAT MOVE" Variable EOL
                    "DO IT NOW" Expression* EOL
```

---

## 15. Examples

### Example 1: VGA Text Mode Hello World

```arnoldc
IT'S SHOWTIME

HEY CHRISTMAS TREE vga
POINT YOUR GUN AT
THIS IS A SMALL WARRIOR
YOU SET US UP MAKE IT A POINT YOUR GUN AT THIS IS A SMALL WARRIOR 0xB8000

HEY CHRISTMAS TREE i
THIS IS A WARRIOR
YOU SET US UP 0

HEY CHRISTMAS TREE message
LINE THEM UP
THIS IS A LETTER
PUT THEM IN LINE 'H' 'E' 'L' 'L' 'O' ' ' 'W' 'O' 'R' 'L' 'D' 0

STICK AROUND WHICH ONE message i
    YOU ARE NOT YOU YOU ARE ME 0
    
    GET TO THE CHOPPER WHICH ONE vga i
    HERE IS MY INVITATION WHICH ONE message i
    JOIN THEM TOGETHER 0x0F00
    ENOUGH TALK
    
    GET TO THE CHOPPER i
    HERE IS MY INVITATION i
    GET UP 1
    ENOUGH TALK
CHILL

YOU HAVE BEEN TERMINATED
```

### Example 2: GDT Setup

```arnoldc
THIS IS WHAT I AM GDTEntry
THIS IS WHAT I'M MADE OF limitLow
    THIS IS A SMALL WARRIOR
THIS IS WHAT I'M MADE OF baseLow
    THIS IS A SMALL WARRIOR
THIS IS WHAT I'M MADE OF baseMiddle
    THIS IS A TINY WARRIOR
THIS IS WHAT I'M MADE OF access
    THIS IS A TINY WARRIOR
THIS IS WHAT I'M MADE OF granularity
    THIS IS A TINY WARRIOR
THIS IS WHAT I'M MADE OF baseHigh
    THIS IS A TINY WARRIOR
I'M DONE DESCRIBING MYSELF

THIS IS WHAT I AM GDTPointer
THIS IS WHAT I'M MADE OF limit
    THIS IS A SMALL WARRIOR
THIS IS WHAT I'M MADE OF base
    THIS IS A WARRIOR
I'M DONE DESCRIBING MYSELF

HEY CHRISTMAS TREE gdt
LINE THEM UP
CREATE ONE LIKE GDTEntry
HOW MANY 3

HEY CHRISTMAS TREE gdtPtr
CREATE ONE LIKE GDTPointer

LISTEN TO ME VERY CAREFULLY setupGDT
    GET TO THE CHOPPER WHAT'S YOUR WHICH ONE gdt 1 limitLow
    HERE IS MY INVITATION 0xFFFF
    ENOUGH TALK
    
    GET TO THE CHOPPER WHAT'S YOUR WHICH ONE gdt 1 access
    HERE IS MY INVITATION 0x9A
    ENOUGH TALK
    
    GET TO THE CHOPPER WHAT'S YOUR WHICH ONE gdt 1 granularity
    HERE IS MY INVITATION 0xCF
    ENOUGH TALK
    
    GET TO THE CHOPPER WHAT'S YOUR gdtPtr limit
    HERE IS MY INVITATION HOW BIG IS THAT gdt
    GET DOWN 1
    ENOUGH TALK
    
    GET TO THE CHOPPER WHAT'S YOUR gdtPtr base
    HERE IS MY INVITATION MAKE IT A THIS IS A WARRIOR WHERE ARE YOU gdt
    ENOUGH TALK
    
    SPEAK TO THE MACHINE
        lgdt [gdtPtr]
        jmp 0x08:.reload_cs
        .reload_cs:
        mov ax, 0x10
        mov ds, ax
        mov es, ax
        mov fs, ax
        mov gs, ax
        mov ss, ax
    THE MACHINE SAYS
HASTA LA VISTA, BABY
```

### Example 3: Simple Interrupt Handler

```arnoldc
LISTEN TO ME VERY CAREFULLY keyboardHandler
I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE intNum
    THIS IS A WARRIOR
    
    HEY CHRISTMAS TREE scancode
    THIS IS A TINY WARRIOR
    YOU SET US UP 0
    
    GET YOUR ASS TO MARS scancode
    LISTEN TO THE PORT 0x60
    DO IT NOW
    
    TALK TO THE HAND scancode
    
    TALK TO THE PORT 0x20 0x20
HASTA LA VISTA, BABY

IT'S SHOWTIME

EVERYBODY CHILL

TALK TO THE HAND "Setting up keyboard interrupt..."

HEY CHRISTMAS TREE handler
REMEMBER THIS MOVE
THIS IS NOTHING
LOOKS LIKE (THIS IS A WARRIOR)

LEARN THE MOVE handler keyboardHandler

LET'S PARTY

STICK AROUND @NO PROBLEMO
    SLEEP NOW
CHILL

YOU HAVE BEEN TERMINATED
```

### Example 4: Memory Management

```arnoldc
LISTEN TO ME VERY CAREFULLY memcpy
I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE dest
    POINT YOUR GUN AT THIS IS A TINY WARRIOR
I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE src
    POINT YOUR GUN AT THIS IS A TINY WARRIOR
I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE count
    THIS IS A WARRIOR
    
    HEY CHRISTMAS TREE i
    THIS IS A WARRIOR
    YOU SET US UP 0
    
    STICK AROUND i
        LET OFF SOME STEAM BENNET count
        
        FIRE AT WHICH ONE dest i
        HERE IS MY INVITATION SHOW ME WHAT YOU GOT WHICH ONE src i
        ENOUGH TALK
        
        GET TO THE CHOPPER i
        HERE IS MY INVITATION i
        GET UP 1
        ENOUGH TALK
    CHILL
HASTA LA VISTA, BABY

LISTEN TO ME VERY CAREFULLY memset
I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE ptr
    POINT YOUR GUN AT THIS IS A TINY WARRIOR
I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE value
    THIS IS A TINY WARRIOR
I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE count
    THIS IS A WARRIOR
    
    HEY CHRISTMAS TREE i
    THIS IS A WARRIOR
    YOU SET US UP 0
    
    STICK AROUND i
        LET OFF SOME STEAM BENNET count
        
        FIRE AT WHICH ONE ptr i
        HERE IS MY INVITATION value
        ENOUGH TALK
        
        GET TO THE CHOPPER i
        HERE IS MY INVITATION i
        GET UP 1
        ENOUGH TALK
    CHILL
HASTA LA VISTA, BABY
```

---

## Appendix A: Quick Reference Card

### Types
| Arnold | C | Size |
|--------|---|------|
| `THIS IS A TINY WARRIOR` | `uint8_t` | 8-bit |
| `THIS IS A SMALL WARRIOR` | `uint16_t` | 16-bit |
| `THIS IS A WARRIOR` | `uint32_t` | 32-bit |
| `THIS IS A BIG WARRIOR` | `uint64_t` | 64-bit |
| `THIS IS A TINY ENEMY` | `int8_t` | 8-bit |
| `THIS IS A SMALL ENEMY` | `int16_t` | 16-bit |
| `THIS IS AN ENEMY` | `int32_t` | 32-bit |
| `THIS IS A BIG ENEMY` | `int64_t` | 64-bit |

### Pointers
| Arnold | C |
|--------|---|
| `POINT YOUR GUN AT` | `*` (pointer type) |
| `WHERE ARE YOU` | `&` (address-of) |
| `SHOW ME WHAT YOU GOT` | `*` (dereference) |
| `FIRE AT` | `*ptr =` (write through ptr) |

### Bitwise
| Arnold | C |
|--------|---|
| `CRUSH THEM TOGETHER` | `&` |
| `JOIN THEM TOGETHER` | `\|` |
| `CONFUSE THEM` | `^` |
| `TURN IT AROUND` | `~` |
| `PUSH IT LEFT` | `<<` |
| `PUSH IT RIGHT` | `>>` |

### Hardware Access
| Arnold | C |
|--------|---|
| `TALK TO THE PORT` | `outb()` |
| `LISTEN TO THE PORT` | `inb()` |
| `EVERYBODY CHILL` | `cli` |
| `LET'S PARTY` | `sti` |
| `SLEEP NOW` | `hlt` |

---

## Appendix B: Error Messages

All error messages are delivered in true Arnold fashion:

| Error | Arnold Message |
|-------|----------------|
| Syntax error | `WHAT THE FUCK DID I DO WRONG` |
| Unknown variable | `WHO IS YOUR DADDY AND WHAT DOES HE DO` |
| Type mismatch | `YOU'RE ONE UGLY MOTHERFUCKER` |
| Division by zero | `YOU SHOULD NOT DRINK AND BAKE` |
| Null pointer | `THERE IS NO BATHROOM` |
| Stack overflow | `GET TO THE CHOPPER... BUT IT'S TOO FAR` |
| Out of memory | `I NEED A VACATION` |
| Invalid address | `WRONG COORDINATES` |

---

*"I'll be back... with more features."*
