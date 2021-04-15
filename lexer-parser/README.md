# lexer-parser-interpreter
An interpreter that can read and evaluate inputs

Part 1: Lexer.

A lexer is a tool that takes a String as input and emits a list of Tokens. (I've provided the Token class for you.)

A Token has a type and a value. Our language has the following Tokens:

Integers: At least one digit in a row.
Floats: One or more digits, followed by ., followed by more digits.
Identifiers. A letter, followed by letters or numbers.
Operators: +, -, *, /
Assignment: =
Expression assignment: #
We also have an EOF token, to denote the end of file.
Part 2: Expression Tree. The expression tree is a data structure that stores a computation. Leaves contain numbers or variables, and parent nodes contain operators. We can compute the result by doing an inorder traversal.

Part 3: Interpreter: The interpreter is the engine that runs everything:

prompt the user for input, read in a string, run it through the lexer, then parse it and display the result.
Read a series of sentences from a file and sequentially parse them and display their results.
Part 4: Expression assignment. A cool feature of expression trees is the fact tht they're both code and data. Store the expression tree in the Symbol Table so that when that identifier is evaluated, the corresponding expression tree is fetched from the symbol table and evaluated.

