# Java Multi-Mode Calculator

A Swing-based desktop calculator application offering Simple, Scientific (Numerical), Algebraic (Symbolic), and basic Calculus modes.

## Overview

This project is a Java application that provides users with four distinct calculator functionalities accessible through a main menu:

1.  **Simple Calculator:** For basic arithmetic operations (+, -, \*, /).
2.  **Scientific Calculator (Numerical):** For evaluating complex numerical expressions with support for standard scientific functions, parentheses, operator precedence, and a toggleable mode for prioritizing fraction or decimal results.
3.  **Algebraic Calculator (Symbolic):** For basic symbolic algebra, including polynomial expansion (FOIL-like operations for binomials) and solving linear and quadratic equations for a single variable 'x'.
4.  **Calculus Calculator (Polynomials):** For basic differentiation and indefinite integration of polynomial expressions with respect to 'x'.

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
    * Separate input for `=` sign and a "Solve" button to trigger evaluation.
* **Calculus Calculator (Polynomials):**
    * **Differentiation:** Computes the derivative of polynomial expressions (e.g., `d/dx (3x^2 + 2x)`).
    * **Indefinite Integration:** Computes the indefinite integral of polynomial expressions, adding `+ C` (e.g., `∫ (6x + 2) dx`).
    * Input polynomial expressions using numbers, 'x', '+', '-', and '^'.


## Requirements

* Java Development Kit (JDK) version 8 or later.
* (Optional but Recommended) An IDE like VS Code with the "Extension Pack for Java" or IntelliJ IDEA / Eclipse.

## Setup and Compilation

1.  **Get the Code:** Clone the repository or download the source files.
2.  **Directory Structure:** Ensure all `.java` files are placed in their respective package directories under a `src` folder as outlined in the "Project Structure" section.
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
    * **Using an IDE:** The IDE will typically handle compilation automatically.

## Running the Application

1.  **From Terminal/Command Prompt:**
    * After successful compilation, ensure you are in the `src` directory.
    * Run the `MainApp` class:
        ```bash
        java com.example.calculator.MainApp
        ```
2.  **From an IDE:**
    * Locate and run `MainApp.java`.

The application will start by displaying the Main Menu.

## How to Use

1.  **Main Menu:**
    * Click on one of the buttons to launch the desired calculator:
        * "Simple Calculator"
        * "Scientific Calculator"
        * "Algebraic Calculator"
        * "Calculus Calculator"
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
    * `←` (Backspace) removes characters.
    * `C` clears the display.
    * Press `=` or Enter (in the display field) to evaluate.

4.  **Algebraic Calculator (Symbolic):**
    * Type algebraic expressions or equations into the display field.
    * Use `x` for the variable.
    * Use the `=` button to insert an equals sign when forming an equation.
    * **Expansion (FOIL-like):**
        * Enter expressions like `(x+2)(x-3)`.
        * Press the "Solve" button (or Enter) to see the expanded result.
    * **Equation Solving:**
        * Enter equations like `2x+5=11` or `x^2-x-6=0`.
        * Press the "Solve" button (or Enter) to see the solution(s).

5.  **Calculus Calculator (Polynomials):**
    * Enter a polynomial expression involving 'x' into the top input field (e.g., `3x^2 + 2x - 5`).
    * Use number buttons, `x`, `+`, `-`, `^` to construct your polynomial.
    * Press the "d/dx" button to differentiate the entered polynomial.
    * Press the "∫dx" button to find the indefinite integral of the entered polynomial.
    * The result will be displayed in the lower display field.
    * "C" clears both input and result fields.
    * `←` (Backspace) works on the input field.


## Limitations & Future Enhancements

* **Parser Robustness:** Parsers for numerical (`ExpressionEvaluator`) and algebraic (`Polynomial.parse`) expressions are simplified.
* **Symbolic Algebra Scope:** Algebraic capabilities are limited.
* **Calculus Scope:**
    * Limited to polynomial differentiation and indefinite integration w.r.t. 'x'.
    * No trigonometric, exponential, or logarithmic functions in calculus operations.
    * No definite integrals.
* **Fraction Input:** Distinguishing fraction input from division in `ScientificCalculatorGUI` can be ambiguous.
* **Error Handling:** Can be made more specific.
* **User Experience:**
    * Limited keyboard input support for buttons.
    * Layouts can be further optimized.
* **Advanced Functions:** More math functions could be added.
* **Mixed Numbers:** No direct support.