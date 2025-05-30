package com.example.calculator.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.example.calculator.logic.CalculusEvaluator;
// Assuming RoundedButton is in this package

public class CalculusCalculatorGUI extends JFrame implements ActionListener {

    private MainMenu mainMenuRef;
    private JTextField expressionInputField; // For inputting the polynomial
    private JTextField resultDisplayField;   // For showing the result (derivative/integral)

    private RoundedButton[] numberButtons = new RoundedButton[10];
    private RoundedButton addButton, subButton; //mulButton; // For constructing terms if needed
    private RoundedButton xButton, powerButton, decimalButton;
    private RoundedButton openParenButton, closeParenButton; // For terms like (x+1) if parser handles it

    private RoundedButton differentiateButton;
    private RoundedButton integrateButton;
    private RoundedButton clearButton, backspaceButton;

    private JPanel buttonPanel;
    private CalculusEvaluator calculusEvaluator;

    // Colors
    private final Color numberColor = new Color(80, 80, 80);
    private final Color opColor = new Color(255, 150, 0);
    private final Color varColor = new Color(0, 150, 136); // Teal for 'x'
    private final Color calcOpColor = new Color(50, 150, 220); // Blue for d/dx, ∫dx
    private final Color clearColor = new Color(220, 50, 50);
    private final Color parenColor = new Color(150, 100, 200);


    public CalculusCalculatorGUI(MainMenu mainMenu) {
        this.mainMenuRef = mainMenu;
        this.calculusEvaluator = new CalculusEvaluator();

        setTitle("Calculus Calculator (Polynomials)");
        setSize(500, 600);
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

        // Input Field
        expressionInputField = new JTextField();
        expressionInputField.setToolTipText("Enter polynomial e.g., 3x^2 + 2x - 5");
        expressionInputField.setHorizontalAlignment(JTextField.LEFT);
        expressionInputField.setFont(new Font("Arial", Font.PLAIN, 20));
        expressionInputField.setBackground(new Color(50, 50, 50));
        expressionInputField.setForeground(Color.WHITE);
        expressionInputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "f(x) = ",
                                            javax.swing.border.TitledBorder.LEFT,
                                            javax.swing.border.TitledBorder.TOP,
                                            new Font("Arial", Font.BOLD, 14), Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));
        // Add ActionListener to input field for Enter key
        expressionInputField.addActionListener(e -> {
            // Default to differentiate if Enter is pressed, or choose one action
            if(differentiateButton.isEnabled()) performDifferentiate();
        });


        // Result Display Field
        resultDisplayField = new JTextField();
        resultDisplayField.setEditable(false);
        resultDisplayField.setHorizontalAlignment(JTextField.LEFT);
        resultDisplayField.setFont(new Font("Arial", Font.BOLD, 20));
        resultDisplayField.setBackground(new Color(40, 40, 40));
        resultDisplayField.setForeground(new Color(100, 220, 100)); // Greenish for result
        resultDisplayField.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Result: ",
                                            javax.swing.border.TitledBorder.LEFT,
                                            javax.swing.border.TitledBorder.TOP,
                                            new Font("Arial", Font.BOLD, 14), Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));


        initButtons();

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 5, 5)); // Adjust grid as needed
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Button Layout (Example)
        // Row 1
        buttonPanel.add(numberButtons[7]); buttonPanel.add(numberButtons[8]);
        buttonPanel.add(numberButtons[9]); buttonPanel.add(xButton);
        // Row 2
        buttonPanel.add(numberButtons[4]); buttonPanel.add(numberButtons[5]);
        buttonPanel.add(numberButtons[6]); buttonPanel.add(powerButton); // '^'
        // Row 3
        buttonPanel.add(numberButtons[1]); buttonPanel.add(numberButtons[2]);
        buttonPanel.add(numberButtons[3]); buttonPanel.add(addButton);  // '+'
        // Row 4
        buttonPanel.add(decimalButton); buttonPanel.add(numberButtons[0]);
        buttonPanel.add(openParenButton); // For terms like 2(x+1) if Polynomial.parse is enhanced
        buttonPanel.add(subButton); // '-'
        // Row 5
        buttonPanel.add(clearButton); buttonPanel.add(backspaceButton);
        buttonPanel.add(differentiateButton); buttonPanel.add(integrateButton);


        // Main layout for input, result, and buttons
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5)); // Input and Result fields
        topPanel.setOpaque(false);
        topPanel.add(expressionInputField);
        topPanel.add(resultDisplayField);

        setLayout(new BorderLayout(10, 10));
        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        
        expressionInputField.requestFocusInWindow();
    }

    private void initButtons() {
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new RoundedButton(String.valueOf(i));
            numberButtons[i].addActionListener(this);
            numberButtons[i].setButtonColor(numberColor);
        }

        addButton = new RoundedButton("+"); subButton = new RoundedButton("-");
        // mulButton might not be needed if Polynomial.parse doesn't handle implicit mult for input
        xButton = new RoundedButton("x"); powerButton = new RoundedButton("^");
        decimalButton = new RoundedButton(".");
        openParenButton = new RoundedButton("("); closeParenButton = new RoundedButton(")"); // Add to panel if used

        differentiateButton = new RoundedButton("d/dx");
        integrateButton = new RoundedButton("∫dx");
        clearButton = new RoundedButton("C");
        backspaceButton = new RoundedButton("←");

        // Add ActionListeners and set colors
        RoundedButton[] inputHelpers = {
            addButton, subButton, xButton, powerButton, decimalButton, openParenButton, closeParenButton
        };
        for (RoundedButton btn : inputHelpers) {
            if (btn == null) continue; // Skip if any button wasn't initialized (like mulButton)
            btn.addActionListener(this);
            if (btn == xButton) btn.setButtonColor(varColor);
            else if (btn == openParenButton || btn == closeParenButton) btn.setButtonColor(parenColor);
            else btn.setButtonColor(opColor);
        }

        differentiateButton.addActionListener(this); differentiateButton.setButtonColor(calcOpColor);
        integrateButton.addActionListener(this); integrateButton.setButtonColor(calcOpColor);
        clearButton.addActionListener(this); clearButton.setButtonColor(clearColor);
        backspaceButton.addActionListener(this); backspaceButton.setButtonColor(clearColor);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        String command = e.getActionCommand();

        if (source == differentiateButton) {
            performDifferentiate();
        } else if (source == integrateButton) {
            performIntegrate();
        } else if (source == clearButton) {
            expressionInputField.setText("");
            resultDisplayField.setText("");
        } else if (source == backspaceButton) {
            String currentText = expressionInputField.getText();
            int cursorPos = expressionInputField.getCaretPosition();
            if (cursorPos > 0 && !currentText.isEmpty()) {
                String newText = currentText.substring(0, cursorPos - 1) + currentText.substring(cursorPos);
                expressionInputField.setText(newText);
                expressionInputField.setCaretPosition(cursorPos - 1);
            }
        } else { // Append to input field (numbers, x, +, -, ^, ., (, ) )
            insertIntoInputField(command);
        }
        expressionInputField.requestFocusInWindow();
    }

    private void performDifferentiate() {
        String expression = expressionInputField.getText().trim();
        if (expression.isEmpty()) {
            resultDisplayField.setText("0");
            return;
        }
        String result = calculusEvaluator.differentiate(expression);
        resultDisplayField.setText(result);
    }

    private void performIntegrate() {
        String expression = expressionInputField.getText().trim();
        if (expression.isEmpty()) {
            resultDisplayField.setText("C");
            return;
        }
        String result = calculusEvaluator.integrate(expression);
        resultDisplayField.setText(result);
    }

    private void insertIntoInputField(String text) {
        int cursorPos = expressionInputField.getCaretPosition();
        String currentText = expressionInputField.getText();
        String newText = currentText.substring(0, cursorPos) + text + currentText.substring(cursorPos);
        if (newText.length() < 100) { // Max expression length
            expressionInputField.setText(newText);
            expressionInputField.setCaretPosition(cursorPos + text.length());
        }
    }
}
