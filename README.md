# 🧩 C-Simplified Compiler

A **three-phase compiler** for a simplified subset of the C programming language — developed as part of a *Compiler Design* undergraduate course.  
The project gradually builds a full front-end pipeline: from parsing and AST generation to semantic analysis, optimizations, and type checking.

---

## 📚 Project Overview

| Phase | Focus | Key Concepts |
|:------|:-------|:--------------|
| **Phase 1** | Abstract Syntax Tree (AST) construction | ANTLR grammar design, AST node hierarchy, statement counting, expression depth, and CPY (Python-like C) conversion |
| **Phase 2** | Name Analysis & Code Optimization | Symbol tables, undeclared variable/function detection, unreachable/dead code elimination, and static optimizations |
| **Phase 3** | Type Checking & Vulnerability Detection | Type compatibility checking, function signature validation, and basic security analysis (memory leaks, uninitialized vars) |

---

## Features

### **Phase 1: AST Construction**
- ANTLR-based grammar for simplified C syntax (`SimpleLang`).
- AST node definitions for statements, expressions, and control flow.
- Computes:
  - Number of statements in each scope.
  - Expression depth.
- Converts C-like syntax to a Python-style version (“CPY”) for readability.

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
3. Build and run:

   ```bash
   javac *.java
   java Main input.c
   ```
4. Check the output files or console logs for results (AST printout, analysis reports, etc.).

---

## 🧾 Example Outputs

**Statement counting and expression depth (Phase 1):**

```
Function -> 5 statements
Expression -> Depth: 3
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

## 🪪 License

This project is released for educational purposes.
All rights reserved © 2025 Omid Naeej.

---
