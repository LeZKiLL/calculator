package com.example.calculator.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.example.calculator.ui.RoundedButton; // Import the custom button class
import com.example.calculator.ui.SimpleCalculatorGUI; // Import the simple calculator GUI

// Import the calculator GUI classes
// Assuming they are (or will be) in the same package 'com.example.calculator.ui'
// If they are in different sub-packages, adjust the import accordingly.

public class MainMenu extends JFrame implements ActionListener {

    private RoundedButton btnSimpleCalculator;
    private RoundedButton btnScientificCalculator;
    private RoundedButton btnAlgebraicCalculator;
    private RoundedButton btnExit;

    public MainMenu() {
        setTitle("Calculator Main Menu");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setResizable(false);
        getContentPane().setBackground(new Color(40, 40, 40)); // Dark background

        // Title Label
        JLabel titleLabel = new JLabel("Select Calculator Type", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Buttons
        btnSimpleCalculator = new RoundedButton("Simple Calculator");
        btnScientificCalculator = new RoundedButton("Scientific Calculator (Numerical)");
        btnAlgebraicCalculator = new RoundedButton("Algebraic Calculator (Symbolic)");
        btnExit = new RoundedButton("Exit");

        // Style buttons
        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        Color buttonColor = new Color(80, 80, 80);
        Color exitButtonColor = new Color(200, 60, 60);

        btnSimpleCalculator.setFont(buttonFont);
        btnSimpleCalculator.setButtonColor(buttonColor);
        btnScientificCalculator.setFont(buttonFont);
        btnScientificCalculator.setButtonColor(buttonColor);
        btnAlgebraicCalculator.setFont(buttonFont);
        btnAlgebraicCalculator.setButtonColor(buttonColor);
        btnExit.setFont(buttonFont);
        btnExit.setButtonColor(exitButtonColor);

        // Add action listeners
        btnSimpleCalculator.addActionListener(this);
        btnScientificCalculator.addActionListener(this);
        btnAlgebraicCalculator.addActionListener(this);
        btnExit.addActionListener(this);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 15)); // Rows, Cols, HGap, VGap
        buttonPanel.setOpaque(false); // Make panel transparent
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50)); // Padding

        buttonPanel.add(btnSimpleCalculator);
        buttonPanel.add(btnScientificCalculator);
        buttonPanel.add(btnAlgebraicCalculator);
        buttonPanel.add(btnExit);

        // Add components to frame
        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        this.setVisible(false); // Hide main menu when a calculator is launched

        if (source == btnSimpleCalculator) {
            SwingUtilities.invokeLater(() -> {
                SimpleCalculatorGUI simpleCalc = new SimpleCalculatorGUI(this); // Pass reference to main menu
                simpleCalc.setVisible(true);
            });
        } else if (source == btnScientificCalculator) {
            SwingUtilities.invokeLater(() -> {
                // Assuming ScientificCalculatorGUI is your existing numerical expression calculator
                ScientificCalculatorGUI scientificCalc = new ScientificCalculatorGUI(this); // Pass reference
                scientificCalc.setVisible(true);
            });
        } else if (source == btnAlgebraicCalculator) {
             SwingUtilities.invokeLater(() -> {
                AlgebraicCalculatorGUI algebraicCalc = new AlgebraicCalculatorGUI(this); // Pass reference
                algebraicCalc.setVisible(true);
            });
        } else if (source == btnExit) {
            System.exit(0);
        }
    }

    // Method to make the main menu visible again (called by calculators when they close)
    public void showMenu() {
        this.setVisible(true);
    }
}
```

**4. `SimpleCalculatorGUI.java` (New)**

A basic calculator for simple arithmetic.


```java
package com.example.calculator.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimpleCalculatorGUI extends JFrame implements ActionListener {

    private JTextField displayField;
    private RoundedButton[] numberButtons = new RoundedButton[10];
    private RoundedButton addButton, subButton, mulButton, divButton;
    private RoundedButton equButton, clrButton, decButton;
    private JPanel panel;

    private String currentInput = "";
    private double previousValue = 0;
    private char currentOperator = ' ';
    private boolean newNumberStarted = true;

    private MainMenu mainMenuRef; // Reference to the main menu

    public SimpleCalculatorGUI(MainMenu mainMenu) {
        this.mainMenuRef = mainMenu;

        setTitle("Simple Calculator");
        setSize(350, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close, don't exit app
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(40, 40, 40));

        // Handle window closing to show main menu again
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (mainMenuRef != null) {
                    mainMenuRef.showMenu();
                }
            }
        });

        displayField = new JTextField("0");
        displayField.setEditable(false);
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setFont(new Font("Arial", Font.BOLD, 28));
        displayField.setBackground(new Color(60, 60, 60));
        displayField.setForeground(Color.WHITE);
        displayField.setBorder(new EmptyBorder(10, 10, 10, 10));

        initButtons();

        panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4, 5, 5));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Row 1
        panel.add(numberButtons[7]); panel.add(numberButtons[8]); panel.add(numberButtons[9]); panel.add(divButton);
        // Row 2
        panel.add(numberButtons[4]); panel.add(numberButtons[5]); panel.add(numberButtons[6]); panel.add(mulButton);
        // Row 3
        panel.add(numberButtons[1]); panel.add(numberButtons[2]); panel.add(numberButtons[3]); panel.add(subButton);
        // Row 4
        panel.add(clrButton); panel.add(numberButtons[0]); panel.add(decButton); panel.add(addButton);
        // Row 5 (Equals button can be separate or integrated)
        JPanel equalsPanel = new JPanel(new BorderLayout(5,5));
        equalsPanel.setOpaque(false);
        equButton = new RoundedButton("=");
        equButton.addActionListener(this);
        equButton.setButtonColor(new Color(50, 200, 50));
        equButton.setFont(new Font("Arial", Font.BOLD, 18));
        equalsPanel.add(equButton, BorderLayout.CENTER);
        equalsPanel.setBorder(new EmptyBorder(5, 10, 10, 10));


        setLayout(new BorderLayout(5, 5));
        add(displayField, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(equalsPanel, BorderLayout.SOUTH);

        // No setVisible(true) here, MainMenu will control visibility
    }

    private void initButtons() {
        Color numColor = new Color(80, 80, 80);
        Color opColor = new Color(255, 150, 0);
        Color clearColor = new Color(220, 50, 50);

        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new RoundedButton(String.valueOf(i));
            numberButtons[i].addActionListener(this);
            numberButtons[i].setButtonColor(numColor);
        }
        addButton = new RoundedButton("+"); subButton = new RoundedButton("-");
        mulButton = new RoundedButton("*"); divButton = new RoundedButton("/");
        clrButton = new RoundedButton("C"); decButton = new RoundedButton(".");

        RoundedButton[] ops = {addButton, subButton, mulButton, divButton, clrButton, decButton};
        for (RoundedButton btn : ops) {
            btn.addActionListener(this);
            btn.setButtonColor(btn == clrButton ? clearColor : opColor);
            if(btn == decButton) btn.setButtonColor(numColor);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ((command.charAt(0) >= '0' && command.charAt(0) <= '9') || command.equals(".")) {
            if (newNumberStarted) {
                currentInput = "";
                newNumberStarted = false;
            }
            if (command.equals(".") && currentInput.contains(".")) return;
            if (currentInput.length() < 16) {
                 currentInput += command;
                 displayField.setText(currentInput);
            }
        } else if (command.equals("C")) {
            currentInput = "";
            previousValue = 0;
            currentOperator = ' ';
            newNumberStarted = true;
            displayField.setText("0");
        } else if (command.equals("=")) {
            if (!currentInput.isEmpty() && currentOperator != ' ') {
                performCalculation();
                currentOperator = ' '; // Reset operator after equals
                newNumberStarted = true; // Next number will be fresh
            }
        } else { // Operator (+, -, *, /)
            if (!currentInput.isEmpty()) {
                if (currentOperator != ' ' && !newNumberStarted) { // Chained operation: 5 + 3 * (pressed *)
                    performCalculation(); // Calculate 5+3 first
                } else { // First number of an operation or new operation after equals
                    try {
                         previousValue = Double.parseDouble(currentInput.isEmpty() ? "0" : currentInput);
                    } catch (NumberFormatException ex) {
                        displayField.setText("Error");
                        resetState();
                        return;
                    }
                }
            }
            // If currentInput is empty but there's a previousValue (e.g. after result, user presses new op)
            // previousValue is already set.

            currentOperator = command.charAt(0);
            newNumberStarted = true; // Ready for the next number
            displayField.setText(formatDisplay(previousValue) + " " + currentOperator);
        }
    }

    private void performCalculation() {
        if (currentInput.isEmpty()) return; // Nothing to calculate with current input
        double currentValue;
        try {
            currentValue = Double.parseDouble(currentInput);
        } catch (NumberFormatException ex) {
            displayField.setText("Error");
            resetState();
            return;
        }

        switch (currentOperator) {
            case '+': previousValue += currentValue; break;
            case '-': previousValue -= currentValue; break;
            case '*': previousValue *= currentValue; break;
            case '/':
                if (currentValue == 0) {
                    displayField.setText("Error: Div by 0");
                    resetState();
                    return;
                }
                previousValue /= currentValue;
                break;
        }
        displayField.setText(formatDisplay(previousValue));
        currentInput = String.valueOf(previousValue); // Result becomes new currentInput for potential chaining if user types number next
                                                      // but newNumberStarted will be true, so it will be overwritten
    }
    
    private String formatDisplay(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        } else {
            return String.format("%.8f", value).replaceAll("\\.?0+$", "");
        }
    }

    private void resetState() {
        currentInput = "";
        previousValue = 0;
        currentOperator = ' ';
        newNumberStarted = true;
    }
}
