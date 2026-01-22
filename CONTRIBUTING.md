# Contributing to ArnoldC-Native

"Come with me if you want to contribute." - The Contributor

## Getting Started

### Prerequisites

- JDK 11 or later
- SBT (Scala Build Tool)
- Git

### Setup

```bash
# Clone the repo
git clone https://github.com/YOUR_USERNAME/ArnoldC-Native.git
cd ArnoldC-Native

# Compile
sbt compile

# Run tests
sbt test

# Build JAR
sbt assembly
```

## How to Contribute

### 1. Adding New Arnold Quotes

Want to add a new Arnold quote as a keyword? Here's how:

#### Step 1: Add the Keyword

In `ArnoldParserExtended.scala`, add your keyword:

```scala
// In the keywords section
val MyNewKeyword = "HASTA LA VISTA"
```

#### Step 2: Create AST Node

Create a new case class in the appropriate `*Nodes.scala` file:

```scala
case class MyNewNode(value: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {}
}
```

#### Step 3: Add Parser Rule

In `ArnoldParserExtended.scala`:

```scala
def MyNewStatement: Rule1[MyNewNode] = rule {
  MyNewKeyword ~ WhiteSpace ~ Operand ~ EOL ~~> MyNewNode
}
```

Don't forget to add it to the `Statement` rule!

#### Step 4: Add Code Generation

In `NativeGenerator.scala`:

```scala
case MyNewNode(value) =>
  val code = generateExpression(value)
  emit(s"my_c_function($code);  /* HASTA LA VISTA */")
```

#### Step 5: Add Tests

In `ArnoldParserExtendedSpec.scala`:

```scala
it should "parse my new statement" in {
  val code = """IT'S SHOWTIME
    |HASTA LA VISTA 42
    |YOU HAVE BEEN TERMINATED
    |""".stripMargin
    
  val result = parser.parse(code)
  // assertions...
}
```

And in `NativeGeneratorSpec.scala`:

```scala
it should "generate my new C code" in {
  val code = """...""".stripMargin
  val cCode = compile(code)
  cCode should include ("my_c_function")
}
```

#### Step 6: Update Documentation

- Add to `docs/LANGUAGE_SPEC.md`
- Update `README.md` with the new keyword

### 2. Bug Fixes

1. Create an issue describing the bug
2. Fork the repo
3. Create a branch: `git checkout -b fix/issue-number`
4. Write a failing test that demonstrates the bug
5. Fix the bug
6. Ensure all tests pass: `sbt test`
7. Submit a PR

### 3. Feature Requests

1. Open an issue with the `enhancement` label
2. Describe the feature and its use case
3. Include example ArnoldC code if applicable
4. Bonus points for Arnold quote suggestions!

## Code Style

### Scala

- Use 2-space indentation
- Follow Scala style guide
- Add comments for complex logic
- Use meaningful variable names (Arnold quotes optional)

### Commit Messages

Use conventional commits with Arnold flair:

```
feat: Add HASTA LA VISTA keyword for process termination
fix: Parser now handles nested STICK AROUND loops
docs: Update README with new examples
test: Add tests for bitwise operations
```

## Testing

### Running Tests

```bash
# All tests
sbt test

# Specific test class
sbt "testOnly org.arnoldc.ArnoldParserExtendedSpec"

# With coverage
sbt coverage test coverageReport
```

### Writing Tests

- Each new feature needs parser AND generator tests
- Test edge cases
- Include Arnold-appropriate test names

Example:
```scala
it should "terminate when asked nicely" in {
  // test code
}
```

## Pull Request Process

1. Update documentation
2. Add/update tests
3. Ensure CI passes
4. Request review
5. Address feedback
6. Merge with "I'LL BE BACK"

## Questions?

Open an issue or reach out. We don't bite (unless you're a bug).

---

"GET TO THE CHOPPER!" - When you see a merge conflict

"CONSIDER THAT A DIVORCE" - When closing a stale PR

"I'LL BE BACK" - Standard PR merge message
