# MicroJava Compiler

## Project Overview  
This project implements a **compiler** for the **MicroJava programming language**, which translates syntactically and semantically correct MicroJava programs into **MicroJava bytecode** executable on the **MicroJava Virtual Machine (VM)**.

The compiler consists of four main components:  
- **Lexical Analysis** – Identifies language tokens from the source code and generates a set of tokens for further processing. Reports lexical errors when detected.  
- **Syntax Analysis** – Parses tokens to verify grammatical correctness and ensures that they form valid program structures. If syntax errors occur, the parser provides error messages and recovery mechanisms.  
- **Semantic Analysis** – Constructs an **Abstract Syntax Tree (AST)** and performs semantic checks to ensure correct program logic. If errors are found, appropriate error messages are displayed.  
- **Code Generation** – Translates syntactically and semantically correct MicroJava programs into executable bytecode for the MicroJava VM.
