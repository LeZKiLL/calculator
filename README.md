# Java Multi-Mode Calculator

A Swing-based desktop calculator application offering Simple, Scientific (Numerical), Algebraic (Symbolic), and basic Calculus modes, with a settings panel for customization. Also, a hobby and a vibing code project.


## Overview

This project is a Java application that provides users with four distinct calculator functionalities accessible through a main menu:

1.  **Simple Calculator:** For basic arithmetic operations (+, -, \*, /).
2.  **Scientific Calculator (Numerical):** For evaluating complex numerical expressions with support for standard scientific functions, parentheses, operator precedence. It features a toggleable mode for prioritizing fraction or decimal results, and its angle units (Degrees/Radians) for trigonometric functions are configurable via a global Settings page.
3.  **Algebraic Calculator (Symbolic):** For basic symbolic algebra, including polynomial expansion (FOIL-like operations for binomials) and solving linear and quadratic equations for a single variable 'x'. Users can input equations using an `=` sign and then use a dedicated "Solve" button.
4.  **Calculus Calculator (Polynomials):** For basic differentiation and indefinite integration of polynomial expressions with respect to 'x'.

The application features a custom rounded button UI and theme options (Light/Dark via FlatLaf) configurable through a Settings page.


## Features

* **Main Menu:** Easy navigation to select calculator modes or open settings.
* **Settings Page:**
    * **Theme Selection:** Light or Dark mode for the application (using FlatLaf).
    * **Angle Unit:** Set trigonometric functions to use Degrees or Radians (globally affects Scientific Calculator).
* **Custom UI:** Uses `RoundedButton` for a visually appealing interface.
* **Simple Calculator:**
    * Addition, subtraction, multiplication, division.
    * Decimal input.
    * Clear function.
    * Chained operations.
* **Scientific Calculator (Numerical Mode):**
    * Evaluates mathematical expressions respecting operator precedence (PEMDAS/BODMAS).
    * Supports parentheses for grouping.
    * **Functions:** `sin`, `cos`, `tan` (respects global Degree/Radian setting from Settings), `log` (base 10), `ln` (natural), `sqrt` (square root), `x^y` (power), `x²` (square).
    * **Constants:** `π` (Pi), `e` (Euler's number).
    * **Fraction/Decimal Mode Toggle (on calculator UI):**
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
    * Separate button for inserting the `=` character and a distinct "Solve" button to trigger evaluation/solving.
* **Calculus Calculator (Polynomials):**
    * **Differentiation:** Computes the derivative of polynomial expressions (e.g., `d/dx (3x^2 + 2x)`).
    * **Indefinite Integration:** Computes the indefinite integral of polynomial expressions, adding `+ C` (e.g., `∫ (6x + 2) dx`).
    * Input polynomial expressions using numbers, 'x', '+', '-', and '^'.


## Requirements

* Java Development Kit (JDK) version 8 or later.
* **FlatLaf JAR:** For modern theming (e.g., `flatlaf-3.x.jar`). Download from [https://www.formdev.com/flatlaf/](https://www.formdev.com/flatlaf/) and place it in a `lib` folder in your project root.
* (Optional but Recommended) An IDE like VS Code with the "Extension Pack for Java" or IntelliJ IDEA / Eclipse.

## Setup and Compilation

1.  **Get the Code & FlatLaf:** Download all source files and the FlatLaf JAR.
2.  **Directory Structure:**
    * Create a `lib` folder in your project's root directory (e.g., `CalculatorProject/lib/`).
    * Place the downloaded `flatlaf-x.y.z.jar` into this `lib` folder.
    * Ensure all `.java` files are in their respective package directories under the `src` folder as outlined in the "Project Structure" section.
3.  **Classpath (for manual compilation/running):** The FlatLaf JAR needs to be in the classpath.
4.  **Compile:**
    * Open a terminal or command prompt.
    * Navigate to the `src` directory (the one containing the `com` folder).
    * **Compile command (adjust path to FlatLaf JAR as needed):**
        ```bash
        # Windows (using ; for classpath separator)
        javac -cp ".;../lib/flatlaf-x.y.z.jar" com/example/calculator/MainApp.java com/example/calculator/ui/*.java com/example/calculator/logic/*.java

        # Linux/macOS (using : for classpath separator)
        javac -cp ".:../lib/flatlaf-x.y.z.jar" com/example/calculator/MainApp.java com/example/calculator/ui/*.java com/example/calculator/logic/*.java
        ```
        (Replace `flatlaf-x.y.z.jar` with the actual name of your FlatLaf JAR file).
    * **Using an IDE (VS Code, IntelliJ, Eclipse):**
        * Add the `flatlaf-x.y.z.jar` from your `lib` folder to the project's "Referenced Libraries" or build path.
        * The IDE will typically handle compilation automatically.


## Running the Application

1.  **From Terminal/Command Prompt (from `src` directory):**
    * Ensure the FlatLaf JAR is in the classpath.
    ```bash
    # Windows
    java -cp ".;../lib/flatlaf-x.y.z.jar" com.example.calculator.MainApp

    # Linux/macOS
    java -cp ".:../lib/flatlaf-x.y.z.jar" com.example.calculator.MainApp
    ```
2.  **From an IDE (VS Code, IntelliJ, Eclipse):**
    * Ensure the FlatLaf JAR is correctly added to the project's libraries/build path.
    * Locate the `MainApp.java` file.
    * Right-click on it and select "Run" or "Run MainApp.main()".

The application will start by displaying the Main Menu, styled with the default FlatLaf theme (Dark, as set in `SettingsManager`).


## How to Use

1.  **Main Menu:**
    * Click on one of the buttons to launch the desired calculator:
        * "Simple Calculator"
        * "Scientific Calculator"
        * "Algebraic Calculator"
        * "Calculus Calculator"
    * Click "Settings" to open the settings dialog.
    * Click "Exit" to close the application.
    * Closing any calculator or settings window will return you to the Main Menu (except for "Exit").

2.  **Settings Page:**
    * **Theme:** Choose "Light" or "Dark". Changes apply immediately to standard Swing components. Custom components like `RoundedButton` primarily use their own defined colors but the overall window frame will change.
    * **Angle Unit:** Choose "Degrees" or "Radians". This setting affects trigonometric functions in the Scientific Calculator.
    * Click "Save" to apply and store settings. "Cancel" discards changes and closes the dialog.

3.  **Simple Calculator:**
    * Use number buttons and operator buttons (+, -, \*, /) for calculations.
    * Press "=" to see the result.
    * "C" clears the current input and state.

4.  **Scientific Calculator (Numerical):**
    * Type expressions directly into the display field or use the buttons.
    * Use `(` and `)` for grouping.
    * Functions like `sin`, `cos`, `log` should be followed by `(` e.g., `sin(90)`. Trigonometric functions will respect the global Angle Unit setting.
    * The `/` button is used for division. For fraction *input*, type numbers separated by `/` (e.g., `3/4`). The evaluator will attempt to parse this as a fraction.
    * **Mode Button ("Mode: Dec" / "Mode: Frac") on its UI:** Toggles the *preferred* calculation and display mode for results of operations within this calculator.
    * `←` (Backspace) removes characters.
    * `C` clears the display.
    * Press `=` or Enter (in the display field) to evaluate.

5.  **Algebraic Calculator (Symbolic):**
    * Type algebraic expressions or equations into the display field.
    * Use `x` for the variable.
    * Use the `=` button on the calculator UI to *insert* an equals sign when forming an equation.
    * **Expansion (FOIL-like):**
        * Enter expressions like `(x+2)(x-3)`.
        * Press the "Solve" button (or Enter in the display field) to see the expanded result.
    * **Equation Solving:**
        * Enter equations like `2x+5=11` or `x^2-x-6=0`.
        * Press the "Solve" button (or Enter in the display field) to see the solution(s).

6.  **Calculus Calculator (Polynomials):**
    * Enter a polynomial expression involving 'x' into the top input field (e.g., `3x^2 + 2x - 5`).
    * Use number buttons, `x`, `+`, `-`, `^` to construct your polynomial.
    * Press the "d/dx" button to differentiate the entered polynomial.
    * Press the "∫dx" button to find the indefinite integral of the entered polynomial.
    * The result will be displayed in the lower display field.
    * "C" clears both input and result fields.
    * `←` (Backspace) works on the input field.


## Limitations & Future Enhancements

* **Parser Robustness:** Parsers for numerical (`ExpressionEvaluator`) and algebraic (`Polynomial.parse`) expressions are simplified and may not handle all complex syntaxes or edge cases.
* **Symbolic Algebra Scope:** Capabilities are limited to basic polynomial operations and linear/quadratic equation solving for a single variable.
* **Calculus Scope:** Limited to polynomial differentiation and indefinite integration with respect to 'x'. Does not support other functions or definite integrals.
* **Fraction Input (Scientific Calc):** Distinguishing "a/b" as a fraction token versus a division operation relies on heuristics in the tokenizer and can be ambiguous.
* **Live Theme Updates for Custom Components:** `RoundedButton` uses explicitly set colors. For them to fully adapt to L&F changes dynamically, they would need to read colors from `UIManager` properties or have a dedicated theme update mechanism.
* **Error Handling:** Can be made more specific and user-friendly across all modules.
* **User Experience:**
    * Keyboard input for buttons (mnemonics, accelerators) is not implemented.
    * Layouts could be further refined, possibly using more advanced layout managers for better control.
* **Mixed Numbers:** No direct support for inputting or displaying mixed numbers.
