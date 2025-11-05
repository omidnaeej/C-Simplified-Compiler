# 🧩 C-Simplified Compiler

A **three-phase compiler** for a simplified subset of the C programming language — developed as part of a *Compiler Design* undergraduate course.  
The project gradually builds a full front-end pipeline: from parsing and AST generation to semantic analysis, optimizations, and type checking.

---

## 📚 Project Overview

|  Phase  | Focus | Key Concepts |
|:--------|:-------|:--------------|
| **Phase 1** | Abstract Syntax Tree (AST) construction | ANTLR grammar design, AST node hierarchy, statement counting, and expression types recognition |
| **Phase 2** | Name Analysis & Code Optimization | Symbol tables, undeclared variable/function detection, unreachable/dead code elimination, and static optimizations |
| **Phase 3** | Type Checking & Vulnerability Detection | Type compatibility checking, function signature validation, and basic security analysis (memory leaks, uninitialized vars) |

---

## Features

### **Phase 1: AST Construction**
- ANTLR-based grammar for simplified C syntax (`SimpleLang`).
- AST node definitions for statements, expressions, and control flow.
- Computes:
  - Number of statements in each scope.
  - Expression types recognition.

### **Phase 2: Name Analysis & Optimizations**
- Symbol table construction (variables, functions, scopes).
- Detects:
  - Undeclared variable usage.
  - Undeclared function calls.
- Performs optimizations:
  - Removes unused variables/parameters.
  - Eliminates dead code after `return`.
  - Simplifies multiple assignments.
  - Substitutes `typedef` and constant values.
  - Removes unreachable code (from `main` reachability analysis).

### **Phase 3: Type & Vulnerability Analysis**
- Detects type-checking errors:
  - Argument–parameter type mismatches.
  - Inconsistent operand types in expressions.
  - Return type mismatches.
- Security checks (for Computer Engineering extension):
  - Memory leaks (allocated but not freed memory).
  - Use of uninitialized variables.

---

## Technologies & Tools

- **ANTLR** – grammar parsing and AST generation  
- **Java** – core implementation language  
- **Visitor Pattern** – for traversing and analyzing AST  
- **Symbol Table / Static Analysis** – for name and type checks  

---

## Repository Structure

```

C-Simplified-Compiler/
├── Phase1/   # AST construction and CPY converter
├── Phase2/   # Name analysis and optimization
├── Phase3/   # Type checking and vulnerability detection
└── README.md

```

Each phase can be built and tested independently.  
Sample inputs and outputs are provided in each directory for validation.

---

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/omidnaeej/C-Simplified-Compiler.git
   cd C-Simplified-Compiler
   ```

2. Navigate to a phase directory (e.g., `Phase1`):

   ```bash
   cd Phase1
   ```
   
3. Run:

   ```bash
   javac phase-1/src/*.java
   java -cp phase-1/src SimpleLang input.c
   ```
   Replace input.c with your test source file written in the simplified C syntax.
   
5. Check the output files or console logs for results (AST printout, analysis reports, etc.).

---

## Example Outputs

**Statement counting and expression recognition (Phase 1):**
#### program: 
```c
// Function to reverse a number
int reverseNumber(int num) {
    int reversedNum = 0;
    while (num > 0) {
        reversedNum = (reversedNum * 10) + (num % 10);
        num /= 10;
    }
    return reversedNum;
}

int main() {
    int number;

    // Get input from the user
    printf("Enter an integer: ");
    scanf("%d", &number);

    // Handle negative numbers (not palindromes)
    if (number < 0) {
        printf("%d is not a palindrome.\n", number);
        return 0;
    }

    // Check if the number is a palindrome
    int reversed = reverseNumber(number);
    if (number == reversed) {
        printf("%d is a palindrome.\n", number);
    } else {
        printf("%d is not a palindrome.\n", number);
    }

    return 0;
}
```

#### output: 
```
Line 2: Stmt function reverseNumber = 3
Line 3: Expr 0
Line 4: Expr >
Line 4: Stmt while = 2
Line 5: Expr =
Line 6: Expr /=
Line 8: Expr reversedNum
Line 11: Stmt function main = 7
Line 15: Expr printf
Line 16: Expr scanf
Line 19: Expr <
Line 19: Stmt selection = 2
Line 20: Expr printf
Line 21: Expr 0
Line 25: Expr reverseNumber
Line 26: Expr ==
Line 26: Stmt selection = 1
Line 28: Stmt selection = 1
Line 27: Expr printf
Line 29: Expr printf
Line 32: Expr 0
```

**Undeclared variable detection (Phase 2):**

```
Line:12 -> x not declared
```

**Type mismatch (Phase 3):**

```
Line:24 -> ReturnTypeMismatch: expected int, found float
```

---

## 🧑‍💻 Author

**Omid Naeej**
📍 *Compiler Design Undergraduate Project, Spring 2025*
🔗 [GitHub Profile](https://github.com/omidnaeej)

---

## License

This project is released for educational purposes.
All rights reserved © 2025 Omid Naeej.

---
