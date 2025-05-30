package com.example.calculator.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Import logic classes
import com.example.calculator.logic.ExpressionEvaluator;
// SymbolicEvaluator might not be strictly needed here if this GUI only does numerical
// but it's fine to have if there's any shared calculation logic or future plans.
import com.example.calculator.logic.SymbolicEvaluator;


public class ScientificCalculatorGUI extends JFrame implements ActionListener {

    private MainMenu mainMenuRef; // Reference to show the main menu when this closes

    private JTextField displayField;
    private RoundedButton[] numberButtons = new RoundedButton[10];
    private RoundedButton addButton, subButton, mulButton, divButton, equButton, clrButton, backspaceButton;
    private RoundedButton decButton;
    private RoundedButton openParenButton, closeParenButton;
    // Removed xButton as this is primarily for numerical expressions.
    // If 'x' is to be treated as a variable for plotting or something else, it could be added back.

    private RoundedButton sinButton, cosButton, tanButton, powYButton;
    private RoundedButton log10Button, lnButton, sqrtButton, squareButton;
    private RoundedButton percentButton;
    private RoundedButton piButton, eButton;
    // Removed factorialButton and reciprocalButton as their direct input into an expression string
    // can be tricky. They are better handled as functions like fact() or by user typing 1/().
    // For simplicity, they are omitted here but can be added back with careful thought on expression integration.

    private JPanel panel;
    private ExpressionEvaluator numericalEvaluator;
    // SymbolicEvaluator is declared but might not be heavily used if this GUI focuses on numerical.
    // It's included in case the calculateTriggered logic wants to try it as a very first step,
    // though for a primarily numerical calculator, directly using numericalEvaluator is more common.
    private SymbolicEvaluator symbolicEvaluator;


    // Colors
    private final Color numberColor = new Color(80, 80, 80);
    private final Color opColor = new Color(255, 150, 0);
    private final Color funcColor = new Color(60, 120, 180);
    private final Color specialFuncColor = new Color(100, 180, 100);
    private final Color clearColor = new Color(220, 50, 50);
    private final Color equalsColor = new Color(50, 200, 50);
    private final Color parenColor = new Color(150, 100, 200);


    public ScientificCalculatorGUI(MainMenu mainMenu) {
        this.mainMenuRef = mainMenu;
        this.numericalEvaluator = new ExpressionEvaluator(); // Initialize numerical evaluator
        this.symbolicEvaluator = new SymbolicEvaluator();   // Initialize symbolic evaluator (if used)

        setTitle("Scientific Calculator (Numerical)");
        setSize(480, 600); // Adjusted size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose this window, don't exit app
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(30, 30, 30));

        // Add window listener to show main menu when this calculator is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (mainMenuRef != null) {
                    mainMenuRef.showMenu();
                }
            }
        });

        displayField = new JTextField();
        displayField.setEditable(true); // User can type expressions
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setFont(new Font("Arial", Font.BOLD, 26));
        displayField.setBackground(new Color(50, 50, 50));
        displayField.setForeground(Color.WHITE);
        displayField.setBorder(new EmptyBorder(15, 10, 15, 10));
        // Add ActionListener to the display field to handle "Enter" key press
        displayField.addActionListener(e -> calculateExpression());


        initButtons(); // Initialize all buttons

        panel = new JPanel();
        panel.setLayout(new GridLayout(7, 5, 5, 5)); // Rows, Cols, HGap, VGap
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel Layout (Example - adjust as needed for scientific functions)
        // Row 1
        panel.add(sinButton); panel.add(cosButton); panel.add(tanButton);
        panel.add(openParenButton); panel.add(closeParenButton);
        // Row 2
        panel.add(log10Button); panel.add(lnButton); panel.add(sqrtButton);
        panel.add(squareButton); panel.add(powYButton); // x^y or ^
        // Row 3
        panel.add(piButton); panel.add(eButton); panel.add(percentButton);
        panel.add(clrButton); panel.add(backspaceButton);
        // Row 4
        panel.add(numberButtons[7]); panel.add(numberButtons[8]); panel.add(numberButtons[9]);
        panel.add(divButton); panel.add(new JLabel("")); // Placeholder for alignment or future button
        // Row 5
        panel.add(numberButtons[4]); panel.add(numberButtons[5]); panel.add(numberButtons[6]);
        panel.add(mulButton); panel.add(new JLabel(""));
        // Row 6
        panel.add(numberButtons[1]); panel.add(numberButtons[2]); panel.add(numberButtons[3]);
        panel.add(subButton); panel.add(new JLabel(""));
        // Row 7
        panel.add(new JLabel(""));panel.add(numberButtons[0]); panel.add(decButton);
        panel.add(addButton); panel.add(equButton);


        setLayout(new BorderLayout(10, 10));
        add(displayField, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        // setVisible(true) will be handled by MainMenu
        displayField.requestFocusInWindow(); // Set focus to display field when window opens
    }

    private void initButtons() {
        // Number buttons
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new RoundedButton(String.valueOf(i));
            numberButtons[i].addActionListener(this);
            numberButtons[i].setButtonColor(numberColor);
        }

        // Basic operators and parentheses
        addButton = new RoundedButton("+"); subButton = new RoundedButton("-");
        mulButton = new RoundedButton("*"); divButton = new RoundedButton("/");
        openParenButton = new RoundedButton("("); closeParenButton = new RoundedButton(")");
        decButton = new RoundedButton(".");

        RoundedButton[] basicOpsAndParens = {
            addButton, subButton, mulButton, divButton,
            openParenButton, closeParenButton
        };
        for(RoundedButton btn : basicOpsAndParens) {
            btn.addActionListener(this);
            if (btn == openParenButton || btn == closeParenButton) btn.setButtonColor(parenColor);
            else btn.setButtonColor(opColor);
        }
        decButton.addActionListener(this); decButton.setButtonColor(numberColor);

        // Control buttons
        equButton = new RoundedButton("=");
        clrButton = new RoundedButton("C");
        backspaceButton = new RoundedButton("←");

        equButton.addActionListener(this); equButton.setButtonColor(equalsColor);
        clrButton.addActionListener(this); clrButton.setButtonColor(clearColor);
        backspaceButton.addActionListener(this); backspaceButton.setButtonColor(clearColor);


        // Scientific function buttons
        sinButton = new RoundedButton("sin", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        cosButton = new RoundedButton("cos", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        tanButton = new RoundedButton("tan", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        powYButton = new RoundedButton("^", RoundedButton.ButtonSizeCategory.SCIENTIFIC); // Using ^ for power
        log10Button = new RoundedButton("log", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        lnButton = new RoundedButton("ln", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        sqrtButton = new RoundedButton("sqrt", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        squareButton = new RoundedButton("x²", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        percentButton = new RoundedButton("%", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        piButton = new RoundedButton("π", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        eButton = new RoundedButton("e", RoundedButton.ButtonSizeCategory.SCIENTIFIC);

        RoundedButton[] sciOps = {
            sinButton, cosButton, tanButton, powYButton, log10Button, lnButton, sqrtButton,
            squareButton, percentButton, piButton, eButton
        };
        for (RoundedButton btn : sciOps) {
            btn.addActionListener(this);
            if (btn == powYButton) btn.setButtonColor(opColor); // Power styled as an operator
            else if (btn == piButton || btn == eButton) btn.setButtonColor(specialFuncColor);
            else btn.setButtonColor(funcColor); // Default scientific function color
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        // int cursorPos = displayField.getCaretPosition(); // Useful for inserting text

        switch (command) {
            case "C":
                displayField.setText("");
                break;
            case "←": // Backspace
                String currentTextBS = displayField.getText();
                int cursorPosBS = displayField.getCaretPosition();
                if (cursorPosBS > 0 && !currentTextBS.isEmpty()) {
                    String newText = currentTextBS.substring(0, cursorPosBS - 1) + currentTextBS.substring(cursorPosBS);
                    displayField.setText(newText);
                    displayField.setCaretPosition(cursorPosBS - 1);
                } else if (!currentTextBS.isEmpty()){ // If cursor is at start, remove first char
                     displayField.setText(currentTextBS.substring(1));
                     displayField.setCaretPosition(0);
                }
                break;
            case "=":
                calculateExpression();
                break;
            // Functions that typically need an opening parenthesis
            case "sin": case "cos": case "tan":
            case "log": case "ln": case "sqrt":
                insertIntoDisplay(command + "(");
                break;
            // Operators, numbers, parentheses, decimal
            case "+": case "-": case "*": case "/": case "^":
            case "(": case ")": case ".":
            case "0": case "1": case "2": case "3": case "4":
            case "5": case "6": case "7": case "8": case "9":
                insertIntoDisplay(command);
                break;
            case "π":
                insertIntoDisplay("pi"); // ExpressionEvaluator expects "pi"
                break;
            case "e":
                insertIntoDisplay("e");  // ExpressionEvaluator expects "e"
                break;
            case "x²":
                insertIntoDisplay("^(2)"); // Append as power of 2, with parentheses for clarity
                break;
            case "%":
                // For numerical, % usually means "divide by 100" or applies to previous number.
                // Appending "/100" is a simple way to achieve one interpretation.
                // A more advanced % would parse the expression before it.
                insertIntoDisplay("/100");
                break;
            default:
                // This case should ideally not be reached if all buttons are handled.
                break;
        }
        displayField.requestFocusInWindow(); // Keep focus on the display field
    }

    private void calculateExpression() {
        String expression = displayField.getText().trim();
        if (expression.isEmpty()) {
            return; // Do nothing if display is empty
        }

        try {
            // This calculator primarily uses the numerical evaluator
            double result = numericalEvaluator.evaluate(expression);
            displayNumericalResult(result);
        } catch (IllegalArgumentException | ArithmeticException ex) {
            String errorMessage = ex.getMessage();
            if (errorMessage != null && errorMessage.length() > 30) {
                errorMessage = errorMessage.substring(0, 30) + "...";
            }
            displayField.setText("Error: " + (errorMessage != null ? errorMessage : "Invalid Expression"));
        } catch (Exception ex) { // Catch any other unexpected errors
            displayField.setText("Error: Calculation failed");
        }
        displayField.selectAll(); // Select the result/error for easy override
        displayField.requestFocusInWindow();
    }


    private void insertIntoDisplay(String text) {
        int cursorPos = displayField.getCaretPosition();
        String currentText = displayField.getText();
        String newText = currentText.substring(0, cursorPos) + text + currentText.substring(cursorPos);

        // Basic length check for the display field
        if (newText.length() < 40) { // Arbitrary max expression length
            displayField.setText(newText);
            // Set cursor position after the inserted text
            displayField.setCaretPosition(cursorPos + text.length());
        }
        displayField.requestFocusInWindow();
    }

    private void displayNumericalResult(double result) {
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            displayField.setText("Error"); // Or "Undefined", "Infinity"
        } else if (result == (long) result) { // Check if it's an integer
            displayField.setText(String.format("%d", (long) result));
        } else {
            // Smart formatting for doubles
            String formatted = String.format("%.10f", result).replaceAll("\\.?0+$", ""); // Remove trailing zeros
            // Switch to scientific notation for very large or very small numbers, or if too long
            if (formatted.length() > 18 || (Math.abs(result) > 1e12 || (Math.abs(result) < 1e-6 && result != 0))) {
                displayField.setText(String.format("%.6E", result)); // Format as scientific notation
            } else {
                displayField.setText(formatted);
            }
        }
    }

    // No main method here, MainApp.java is the entry point.
}
