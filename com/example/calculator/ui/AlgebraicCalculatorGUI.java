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
    private RoundedButton addButton, subButton, mulButton, equalsButtonSymbol; // Renamed equButton
    private RoundedButton clrButton, backspaceButton;
    private RoundedButton openParenButton, closeParenButton;
    private RoundedButton xButton;
    private RoundedButton decButton;

    private JPanel panel;
    private SymbolicEvaluator symbolicEvaluator;
    private ExpressionEvaluator numericalEvaluator; // For fallback if symbolic parsing fails

    private MainMenu mainMenuRef;

    private final Color numberColor = new Color(80, 80, 80);
    private final Color opColor = new Color(255, 150, 0);
    // private final Color funcColor = new Color(60, 120, 180); // Not many functions here
    private final Color clearColor = new Color(220, 50, 50);
    private final Color equalsColor = new Color(50, 200, 50);
    private final Color parenColor = new Color(150, 100, 200);
    private final Color varColor = new Color(0, 150, 136);


    public AlgebraicCalculatorGUI(MainMenu mainMenu) {
        this.mainMenuRef = mainMenu;
        symbolicEvaluator = new SymbolicEvaluator();
        numericalEvaluator = new ExpressionEvaluator();

        setTitle("Algebraic Calculator (Solve/Expand)");
        setSize(450, 550);
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
        displayField.setHorizontalAlignment(JTextField.LEFT); // Better for expressions
        displayField.setFont(new Font("Arial", Font.PLAIN, 20)); // Slightly smaller for longer expressions/solutions
        displayField.setBackground(new Color(50, 50, 50));
        displayField.setForeground(Color.WHITE);
        displayField.setBorder(new EmptyBorder(15, 10, 15, 10));
        displayField.addActionListener(e -> calculateExpression());

        initButtons();

        panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4, 5, 5));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Layout
        panel.add(numberButtons[7]); panel.add(numberButtons[8]); panel.add(numberButtons[9]); panel.add(xButton);
        panel.add(numberButtons[4]); panel.add(numberButtons[5]); panel.add(numberButtons[6]); panel.add(addButton);
        panel.add(numberButtons[1]); panel.add(numberButtons[2]); panel.add(numberButtons[3]); panel.add(subButton);
        panel.add(openParenButton); panel.add(numberButtons[0]); panel.add(closeParenButton); panel.add(mulButton);
        panel.add(clrButton); panel.add(backspaceButton); panel.add(decButton); panel.add(equalsButtonSymbol);


        setLayout(new BorderLayout(10, 10));
        add(displayField, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        // No setVisible(true) here, MainMenu controls it.
    }

    private void initButtons() {
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new RoundedButton(String.valueOf(i));
            numberButtons[i].addActionListener(this);
            numberButtons[i].setButtonColor(numberColor);
        }

        addButton = new RoundedButton("+"); subButton = new RoundedButton("-");
        mulButton = new RoundedButton("*");
        equalsButtonSymbol = new RoundedButton("="); // Changed name from equButton
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

        equalsButtonSymbol.addActionListener(this); equalsButtonSymbol.setButtonColor(equalsColor);
        clrButton.addActionListener(this); clrButton.setButtonColor(clearColor);
        backspaceButton.addActionListener(this); backspaceButton.setButtonColor(clearColor);
        decButton.addActionListener(this); decButton.setButtonColor(numberColor);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (e.getSource() == equalsButtonSymbol) { // Check source for equals button
            calculateExpression();
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
                // Removed "=" case here as it's handled by source check
                default: // Numbers, +, -, *, (, ), x, .
                    insertIntoDisplay(command); break;
            }
        }
        displayField.requestFocusInWindow();
    }

    private void calculateExpression() {
        String expression = displayField.getText().trim();
        if (expression.isEmpty()) return;
        try {
            String result = symbolicEvaluator.evaluate(expression); // This now handles equations too
            displayField.setText(result);
        } catch (IllegalArgumentException ex) {
            // If symbolic evaluation throws IllegalArgumentException (e.g., not recognized format for symbolic)
            // We could try numerical evaluation as a fallback if the expression looks numerical
            if (expression.matches(".*[a-zA-Z]+.*") && !expression.contains("=")) { // Contains letters but not an equation, likely symbolic error
                 String err = ex.getMessage() != null ? ex.getMessage() : "Invalid Symbolic Expression";
                 if (err.length() > 35) err = err.substring(0,35) + "...";
                 displayField.setText("Error: " + err);
            } else if (!expression.contains("=")){ // No letters, not an equation, try numerical
                try {
                    Object numResult = numericalEvaluator.evaluate(expression); // Assuming numericalEvaluator handles Object
                    displayField.setText(numResult.toString()); // Simple toString for now
                } catch (Exception numEx) {
                     String err = ex.getMessage() != null ? ex.getMessage() : "Invalid Expression";
                     if (err.length() > 35) err = err.substring(0,35) + "...";
                     displayField.setText("Error: " + err);
                }
            } else { // It was an equation and symbolic failed, or other symbolic error
                 String err = ex.getMessage() != null ? ex.getMessage() : "Invalid Expression";
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
