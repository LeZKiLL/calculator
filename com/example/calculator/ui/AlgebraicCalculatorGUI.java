package com.example.calculator.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Import logic classes
import com.example.calculator.logic.SymbolicEvaluator;
// Assuming ExpressionEvaluator might be used as a fallback or for parts
import com.example.calculator.logic.ExpressionEvaluator;


public class AlgebraicCalculatorGUI extends JFrame implements ActionListener {

    private JTextField displayField;
    private RoundedButton[] numberButtons = new RoundedButton[10];
    private RoundedButton addButton, subButton, mulButton; // No div for poly usually
    private RoundedButton equButton, clrButton, backspaceButton;
    private RoundedButton openParenButton, closeParenButton;
    private RoundedButton xButton; // For the variable 'x'
    private RoundedButton decButton; // For coefficients

    private JPanel panel;
    private SymbolicEvaluator symbolicEvaluator;
    private ExpressionEvaluator numericalEvaluator; // For potential fallback or mixed mode

    private MainMenu mainMenuRef;

    // Colors
    private final Color numberColor = new Color(80, 80, 80);
    private final Color opColor = new Color(255, 150, 0);
    private final Color funcColor = new Color(60, 120, 180); // Might not have many functions here
    private final Color clearColor = new Color(220, 50, 50);
    private final Color equalsColor = new Color(50, 200, 50);
    private final Color parenColor = new Color(150, 100, 200);
    private final Color varColor = new Color(0, 150, 136);


    public AlgebraicCalculatorGUI(MainMenu mainMenu) {
        this.mainMenuRef = mainMenu;
        symbolicEvaluator = new SymbolicEvaluator();
        numericalEvaluator = new ExpressionEvaluator(); // Initialize if used

        setTitle("Algebraic Calculator (FOIL)");
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
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setFont(new Font("Arial", Font.BOLD, 24));
        displayField.setBackground(new Color(50, 50, 50));
        displayField.setForeground(Color.WHITE);
        displayField.setBorder(new EmptyBorder(15, 10, 15, 10));
        displayField.addActionListener(e -> calculateExpression());

        initButtons();

        panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4, 5, 5)); // Adjust grid as needed
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Example Layout (adjust for algebraic needs)
        // Row 1
        panel.add(numberButtons[7]); panel.add(numberButtons[8]); panel.add(numberButtons[9]); panel.add(xButton);
        // Row 2
        panel.add(numberButtons[4]); panel.add(numberButtons[5]); panel.add(numberButtons[6]); panel.add(addButton);
        // Row 3
        panel.add(numberButtons[1]); panel.add(numberButtons[2]); panel.add(numberButtons[3]); panel.add(subButton);
        // Row 4
        panel.add(openParenButton); panel.add(numberButtons[0]); panel.add(closeParenButton); panel.add(mulButton);
        // Row 5
        panel.add(clrButton); panel.add(backspaceButton); panel.add(decButton); panel.add(equButton);


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
        mulButton = new RoundedButton("*"); // For expressions like (poly1)*(poly2)
        equButton = new RoundedButton("=");
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

        equButton.addActionListener(this); equButton.setButtonColor(equalsColor);
        clrButton.addActionListener(this); clrButton.setButtonColor(clearColor);
        backspaceButton.addActionListener(this); backspaceButton.setButtonColor(clearColor);
        decButton.addActionListener(this); decButton.setButtonColor(numberColor);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        // Logic similar to ScientificCalculatorGUI for appending to display
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
            case "=": calculateExpression(); break;
            default: // Numbers, +, -, *, (, ), x, .
                insertIntoDisplay(command); break;
        }
        displayField.requestFocusInWindow();
    }

    private void calculateExpression() {
        String expression = displayField.getText().trim();
        if (expression.isEmpty()) return;
        try {
            String result = symbolicEvaluator.evaluate(expression);
            displayField.setText(result);
        } catch (IllegalArgumentException ex) {
            // Fallback or just show symbolic error
            String err = ex.getMessage() != null ? ex.getMessage() : "Invalid Symbolic Expression";
            if (err.length() > 35) err = err.substring(0,35) + "...";
            displayField.setText("Error: " + err);
        } catch (Exception ex) { // Catch any other unexpected error
            displayField.setText("Error: Calculation failed");
        }
        displayField.selectAll();
        displayField.requestFocusInWindow();
    }

    private void insertIntoDisplay(String text) {
        int cursorPos = displayField.getCaretPosition();
        String currentText = displayField.getText();
        String newText = currentText.substring(0, cursorPos) + text + currentText.substring(cursorPos);
        if (newText.length() < 60) {
            displayField.setText(newText);
            displayField.setCaretPosition(cursorPos + text.length());
        }
        displayField.requestFocusInWindow();
    }
}
