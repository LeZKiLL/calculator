package com.example.calculator.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.example.calculator.logic.SymbolicEvaluator;
import com.example.calculator.logic.ExpressionEvaluator; // Keep for potential fallback

public class AlgebraicCalculatorGUI extends JFrame implements ActionListener {

    private JTextField displayField;
    private RoundedButton[] numberButtons = new RoundedButton[10];
    private RoundedButton addButton, subButton, mulButton, insertEqualsButton; // Renamed
    private RoundedButton solveButton; // New Solve button
    private RoundedButton clrButton, backspaceButton;
    private RoundedButton openParenButton, closeParenButton;
    private RoundedButton xButton;
    private RoundedButton decButton;

    private JPanel panel;
    private SymbolicEvaluator symbolicEvaluator;
    private ExpressionEvaluator numericalEvaluator;

    private MainMenu mainMenuRef;

    private final Color numberColor = new Color(80, 80, 80);
    private final Color opColor = new Color(255, 150, 0);
    private final Color clearColor = new Color(220, 50, 50);
    private final Color solveButtonColor = new Color(50, 200, 50); // Same as old equals
    private final Color equalsSignColor = new Color(0, 120, 200); // Color for the '=' input button
    private final Color parenColor = new Color(150, 100, 200);
    private final Color varColor = new Color(0, 150, 136);


    public AlgebraicCalculatorGUI(MainMenu mainMenu) {
        this.mainMenuRef = mainMenu;
        symbolicEvaluator = new SymbolicEvaluator();
        numericalEvaluator = new ExpressionEvaluator();

        setTitle("Algebraic Calculator (Solve/Expand)");
        setSize(450, 580); // Adjusted size for new button potentially
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(30, 30, 30));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (mainMenuRef != null) {
                    mainMenuRef.showMenu();
                }
            }
        });

        displayField = new JTextField();
        displayField.setEditable(true);
        displayField.setHorizontalAlignment(JTextField.LEFT);
        displayField.setFont(new Font("Arial", Font.PLAIN, 20));
        displayField.setBackground(new Color(50, 50, 50));
        displayField.setForeground(Color.WHITE);
        displayField.setBorder(new EmptyBorder(15, 10, 15, 10));
        displayField.addActionListener(e -> calculateAndSolveExpression()); // Enter triggers solve

        initButtons();

        panel = new JPanel();
        panel.setLayout(new GridLayout(6, 4, 5, 5)); // Adjusted to 6 rows for Solve button
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Layout - Row 1
        panel.add(numberButtons[7]); panel.add(numberButtons[8]); panel.add(numberButtons[9]); panel.add(xButton);
        // Row 2
        panel.add(numberButtons[4]); panel.add(numberButtons[5]); panel.add(numberButtons[6]); panel.add(addButton);
        // Row 3
        panel.add(numberButtons[1]); panel.add(numberButtons[2]); panel.add(numberButtons[3]); panel.add(subButton);
        // Row 4
        panel.add(openParenButton); panel.add(numberButtons[0]); panel.add(closeParenButton); panel.add(mulButton);
        // Row 5
        panel.add(clrButton); panel.add(backspaceButton); panel.add(decButton); panel.add(insertEqualsButton); // Button to insert '='
        // Row 6 (New row for Solve button, potentially spanning or with placeholders)
        panel.add(new JLabel("")); // Placeholder
        panel.add(solveButton);    // Solve button
        panel.add(new JLabel("")); // Placeholder
        panel.add(new JLabel("")); // Placeholder


        setLayout(new BorderLayout(10, 10));
        add(displayField, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    private void initButtons() {
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new RoundedButton(String.valueOf(i));
            numberButtons[i].addActionListener(this);
            numberButtons[i].setButtonColor(numberColor);
        }

        addButton = new RoundedButton("+"); subButton = new RoundedButton("-");
        mulButton = new RoundedButton("*");
        insertEqualsButton = new RoundedButton("="); // For inserting the equals sign
        solveButton = new RoundedButton("Solve");    // New button to trigger solving
        clrButton = new RoundedButton("C"); backspaceButton = new RoundedButton("←");
        decButton = new RoundedButton(".");
        openParenButton = new RoundedButton("("); closeParenButton = new RoundedButton(")");
        xButton = new RoundedButton("x");

        RoundedButton[] opsAndParens = {
            addButton, subButton, mulButton, openParenButton, closeParenButton
        };
        for(RoundedButton btn : opsAndParens) {
            btn.addActionListener(this);
            if (btn == openParenButton || btn == closeParenButton) btn.setButtonColor(parenColor);
            else btn.setButtonColor(opColor);
        }
        xButton.addActionListener(this); xButton.setButtonColor(varColor);

        insertEqualsButton.addActionListener(this); insertEqualsButton.setButtonColor(equalsSignColor); // Different color for '=' input
        solveButton.addActionListener(this); solveButton.setButtonColor(solveButtonColor); // Use existing "equals" color for "Solve"

        clrButton.addActionListener(this); clrButton.setButtonColor(clearColor);
        backspaceButton.addActionListener(this); backspaceButton.setButtonColor(clearColor);
        decButton.addActionListener(this); decButton.setButtonColor(numberColor);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        Object source = e.getSource();

        if (source == solveButton) {
            calculateAndSolveExpression();
        } else {
            switch (command) {
                case "C": displayField.setText(""); break;
                case "←":
                    String currentTextBS = displayField.getText();
                    int cursorPosBS = displayField.getCaretPosition();
                    if (cursorPosBS > 0 && !currentTextBS.isEmpty()) {
                        String newText = currentTextBS.substring(0, cursorPosBS - 1) + currentTextBS.substring(cursorPosBS);
                        displayField.setText(newText);
                        displayField.setCaretPosition(cursorPosBS - 1);
                    }
                    break;
                // The "=" button now just inserts the character.
                // All other buttons insert their respective characters/commands.
                default:
                    insertIntoDisplay(command); break;
            }
        }
        displayField.requestFocusInWindow();
    }

    private void calculateAndSolveExpression() { // Renamed for clarity
        String expression = displayField.getText().trim();
        if (expression.isEmpty()) return;
        try {
            // SymbolicEvaluator is expected to handle equations (if '=' is present)
            // or expansions (if in (a)(b) format).
            String result = symbolicEvaluator.evaluate(expression);
            displayField.setText(result);
        } catch (IllegalArgumentException ex) {
            // If symbolic evaluation throws IllegalArgumentException (e.g., not recognized format)
            // We could try numerical evaluation as a fallback if the expression looks numerical
            // and does NOT contain an equals sign (which implies it was meant for symbolic solving).
            if (!expression.contains("=") && !expression.matches(".*[a-zA-Z]+.*")) { // No equals, no letters, try numerical
                try {
                    Object numResult = numericalEvaluator.evaluate(expression);
                    // Display numerical result (assuming a helper method or simple toString)
                    if (numResult instanceof Double) {
                        displayField.setText(formatNumericalResult((Double) numResult));
                    } else {
                        displayField.setText(numResult.toString());
                    }
                } catch (Exception numEx) {
                     String err = ex.getMessage() != null ? ex.getMessage() : "Invalid Expression";
                     if (err.length() > 35) err = err.substring(0,35) + "...";
                     displayField.setText("Error: " + err);
                }
            } else { // It was likely intended for symbolic and failed, or other symbolic error
                 String err = ex.getMessage() != null ? ex.getMessage() : "Invalid Format";
                 if (err.length() > 35) err = err.substring(0,35) + "...";
                 displayField.setText("Error: " + err);
            }
        } catch (Exception ex) { // Catch any other unexpected error
            displayField.setText("Error: Calculation failed");
            // ex.printStackTrace(); // For debugging
        }
        displayField.selectAll();
        displayField.requestFocusInWindow();
    }
    
    private String formatNumericalResult(double val) { // Helper for numerical fallback
        if (Double.isNaN(val) || Double.isInfinite(val)) return "Error";
        if (val == (long) val) return String.format("%d", (long) val);
        String formatted = String.format("%.10f", val).replaceAll("\\.?0+$", "");
        if (formatted.isEmpty() && val == 0) formatted = "0";
        else if (formatted.equals(".")) formatted = "0";
        return (formatted.length() > 18 || (Math.abs(val) > 1e12 || (Math.abs(val) < 1e-6 && val != 0))) ?
               String.format("%.6E", val) : formatted;
    }


    private void insertIntoDisplay(String text) {
        int cursorPos = displayField.getCaretPosition();
        String currentText = displayField.getText();
        String newText = currentText.substring(0, cursorPos) + text + currentText.substring(cursorPos);
        if (newText.length() < 100) { // Allow longer expressions for algebra
            displayField.setText(newText);
            displayField.setCaretPosition(cursorPos + text.length());
        }
        displayField.requestFocusInWindow();
    }
}
