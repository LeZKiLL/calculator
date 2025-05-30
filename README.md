# Java Multi-Mode Calculator

A Swing-based desktop calculator application offering Simple, Scientific (Numerical), and Algebraic (Symbolic) modes.

## Overview

This project is a Java application that provides users with three distinct calculator functionalities accessible through a main menu:

1.  **Simple Calculator:** For basic arithmetic operations (+, -, \*, /).
2.  **Scientific Calculator (Numerical):** For evaluating complex numerical expressions with support for standard scientific functions, parentheses, operator precedence, and a toggleable mode for prioritizing fraction or decimal results.
3.  **Algebraic Calculator (Symbolic):** For basic symbolic algebra, including polynomial expansion (FOIL-like operations for binomials) and solving linear and quadratic equations for a single variable 'x'.

The application features a custom rounded button UI for a more modern look and feel.

## Features

* **Main Menu:** Easy navigation to select the desired calculator mode.
* **Custom UI:** Uses `RoundedButton` for a visually appealing interface.
* **Simple Calculator:**
    * Addition, subtraction, multiplication, division.
    * Decimal input.
    * Clear function.
    * Chained operations.
* **Scientific Calculator (Numerical Mode):**
    * Evaluates mathematical expressions respecting operator precedence (PEMDAS/BODMAS).
    * Supports parentheses for grouping.
    * **Functions:** `sin`, `cos`, `tan` (degrees), `log` (base 10), `ln` (natural), `sqrt` (square root), `x^y` (power), `x²` (square).
    * **Constants:** `π` (Pi), `e` (Euler's number).
    * **Fraction/Decimal Mode Toggle:**
        * **Fraction Mode:** Prioritizes calculations and display in fractional form where possible.
        * **Decimal Mode:** Prioritizes calculations and display in decimal form.
    * Backspace and Clear functionality.
    * Input expressions directly into the display field or use buttons.
* **Algebraic Calculator (Symbolic Mode):**
    * **Polynomial Expansion:** Expands expressions like `(ax+b)(cx+d)` or `term*(ax+b)`.
    * **Equation Solving:**
        * Solves linear equations (e.g., `ax + b = c`).
        * Solves quadratic equations (e.g., `ax^2 + bx + c = 0`), providing real and complex roots.
    * Handles basic polynomial parsing for single variable 'x'.
    * Input expressions and equations directly.

## Requirements

* Java Development Kit (JDK) version 8 or later.
* (Optional but Recommended) An IDE like VS Code with the "Extension Pack for Java" or IntelliJ IDEA / Eclipse.

## Setup and Compilation

1.  **Get the Code:** Clone the repository or download the source files.
2.  **Directory Structure:** Ensure all `.java` files are placed in their respective package directories under a `src` folder as outlined in the "Project Structure" section.
    * For example, `MainApp.java` should be at `src/com/example/calculator/MainApp.java`.
    * `MainMenu.java` should be at `src/com/example/calculator/ui/MainMenu.java`.
    * `ExpressionEvaluator.java` should be at `src/com/example/calculator/logic/ExpressionEvaluator.java`.
3.  **Compile:**
    * Open a terminal or command prompt.
    * Navigate to the `src` directory (the one containing the `com` folder).
    * Compile all Java files:
        ```bash
        javac com/example/calculator/MainApp.java com/example/calculator/ui/*.java com/example/calculator/logic/*.java
        ```
    * If you are in the project's root directory (the one containing `src`), you might use:
        ```bash
        javac src/com/example/calculator/MainApp.java src/com/example/calculator/ui/*.java src/com/example/calculator/logic/*.java
        ```
    * **Using an IDE (VS Code, IntelliJ, Eclipse):** The IDE will typically handle compilation automatically when the project is opened and configured as a Java project. Make sure the `src` folder is recognized as a source root.

## Running the Application

1.  **From Terminal/Command Prompt:**
    * After successful compilation, ensure you are in the `src` directory (or that your classpath is set to `src`).
    * Run the `MainApp` class:
        ```bash
        java com.example.calculator.MainApp
        ```
2.  **From an IDE (VS Code, IntelliJ, Eclipse):**
    * Locate the `MainApp.java` file.
    * Right-click on it and select "Run" or "Run MainApp.main()".
    * VS Code might show a "Run" code lens above the `main` method.

The application will start by displaying the Main Menu.

## How to Use

1.  **Main Menu:**
    * Click on one of the buttons to launch the desired calculator:
        * "Simple Calculator"
        * "Scientific Calculator (Numerical)"
        * "Algebraic Calculator (Symbolic)"
    * Click "Exit" to close the application.
    * Closing any calculator window will return you to the MainMenu.

2.  **Simple Calculator:**
    * Use number buttons and operator buttons (+, -, \*, /) for calculations.
    * Press "=" to see the result.
    * "C" clears the current input and state.

3.  **Scientific Calculator (Numerical):**
    * Type expressions directly into the display field or use the buttons.
    * Use `(` and `)` for grouping.
    * Functions like `sin`, `cos`, `log` should be followed by `(` e.g., `sin(90)`.
    * The `/` button is used for division. For fraction *input*, type numbers separated by `/` (e.g., `3/4`). The evaluator will attempt to parse this as a fraction.
    * **Mode Button ("Mode: Dec" / "Mode: Frac"):**
        * Toggles the preferred calculation and display mode.
        * **Decimal Mode:** Results are primarily shown as decimals. Fractions are converted to decimals.
        * **Fraction Mode:** Results are primarily shown as fractions. Decimals might be converted to fraction approximations for display. Numerical results from functions like `sin()` will be doubles, but the calculator might attempt to show them as fractions in this mode.
    * `←` (Backspace) removes the character before the cursor or the selected text.
    * `C` clears the display.
    * Press `=` or Enter (in the display field) to evaluate.

4.  **Algebraic Calculator (Symbolic):**
    * Type algebraic expressions or equations into the display field.
    * Use `x` for the variable.
    * **Expansion (FOIL-like):**
        * Enter expressions like `(x+2)(x-3)` or `(x+1)*(2x-5)`.
        * Also supports `term*(polynomial)` like `2(x+3)` or `x(x-1)`.
    * **Equation Solving:**
        * Enter equations with an equals sign, e.g., `2x+5=11` or `x^2-x-6=0`.
        * The calculator attempts to solve for `x` for linear and quadratic equations.
    * Press `=` or Enter to evaluate/solve.
    * The parser for algebraic terms (`Polynomial.parse`) is basic and works best with simple terms like `ax^b`, `ax`, or constants. Avoid complex nested structures within a single side of an equation unless they are part of the recognized expansion patterns.

## Limitations & Future Enhancements

* **Parser Robustness:** The parsers for both numerical (`ExpressionEvaluator`) and algebraic (`Polynomial.parse`) expressions are simplified. They may not handle all edge cases or complex syntaxes. A more robust solution would involve a formal grammar and parser generator (e.g., ANTLR).
* **Symbolic Algebra Scope:** The algebraic capabilities are limited to basic polynomial expansion and solving linear/quadratic equations for a single variable. It does not support:
    * More complex symbolic simplification.
    * Calculus (differentiation, integration).
    * Solving systems of equations or higher-degree polynomials generally.
    * Handling multiple variables in symbolic operations extensively.
* **Fraction Input:** Distinguishing fraction input (e.g., "3/4") from the division operator in `ScientificCalculatorGUI` relies on heuristics in the tokenizer and can be ambiguous.
* **Error Handling:** Error messages can be made more specific and user-friendly.
* **User Experience:**
    * No keyboard input support for buttons (only for the display field).
    * The layout could be further optimized, especially for the scientific and algebraic modes with many buttons.
* **Advanced Functions:** More mathematical functions could be added to the scientific calculator.
* **Mixed Numbers:** No direct support for inputting or displaying mixed numbers (e.g., "1 1/2").
