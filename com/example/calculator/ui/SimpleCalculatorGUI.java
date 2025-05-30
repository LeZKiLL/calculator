package com.example.calculator.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Assuming RoundedButton is in the same package: com.example.calculator.ui
// Assuming MainMenu is in the same package: com.example.calculator.ui

public class SimpleCalculatorGUI extends JFrame implements ActionListener {

    private JTextField displayField;
    private RoundedButton[] numberButtons = new RoundedButton[10];
    private RoundedButton addButton, subButton, mulButton, divButton;
    private RoundedButton equButton, clrButton, decButton;
    private JPanel buttonPanel; // Panel for the main grid of buttons
    private JPanel equalsPanel; // Separate panel for the equals button for different layout if needed

    private String currentInput = "";
    private double previousValue = 0;
    private char currentOperator = ' '; // Use a space or null char to indicate no operator
    private boolean newNumberStarted = true; // Tracks if the next digit starts a new number

    private MainMenu mainMenuRef; // Reference to the main menu to show it when this closes

    public SimpleCalculatorGUI(MainMenu mainMenu) {
        this.mainMenuRef = mainMenu;

        setTitle("Simple Calculator");
        setSize(360, 500); // Adjusted size for better fit
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close, don't exit app
        setLocationRelativeTo(null); // Center the window
        setResizable(false);
        getContentPane().setBackground(new Color(30, 30, 30)); // Dark background for the frame

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
        displayField.setFont(new Font("Arial", Font.BOLD, 32)); // Larger font for display
        displayField.setBackground(new Color(50, 50, 50));
        displayField.setForeground(Color.WHITE);
        displayField.setBorder(new EmptyBorder(15, 10, 15, 10)); // Padding for display

        initButtons(); // Initialize all buttons

        // Panel for number and basic operator buttons
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 4, 8, 8)); // 4 rows, 4 columns, with gaps
        buttonPanel.setOpaque(false); // Make panel transparent to show frame background
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding around the grid

        // Add buttons to panel in specific order
        // Row 1
        buttonPanel.add(numberButtons[7]);
        buttonPanel.add(numberButtons[8]);
        buttonPanel.add(numberButtons[9]);
        buttonPanel.add(divButton);
        // Row 2
        buttonPanel.add(numberButtons[4]);
        buttonPanel.add(numberButtons[5]);
        buttonPanel.add(numberButtons[6]);
        buttonPanel.add(mulButton);
        // Row 3
        buttonPanel.add(numberButtons[1]);
        buttonPanel.add(numberButtons[2]);
        buttonPanel.add(numberButtons[3]);
        buttonPanel.add(subButton);
        // Row 4
        buttonPanel.add(clrButton);
        buttonPanel.add(numberButtons[0]);
        buttonPanel.add(decButton);
        buttonPanel.add(addButton);

        // Panel for the equals button, allowing it to be styled/positioned separately
        equalsPanel = new JPanel(new BorderLayout(5,5)); // Use BorderLayout to center the button
        equalsPanel.setOpaque(false);
        equalsPanel.add(equButton, BorderLayout.CENTER);
        equalsPanel.setBorder(new EmptyBorder(5, 10, 10, 10)); // Padding for equals button panel


        // Add components to the frame
        setLayout(new BorderLayout(8, 8)); // Gaps between components
        add(displayField, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(equalsPanel, BorderLayout.SOUTH);

        // setVisible(true) will be called by MainMenu when this calculator is launched
    }

    private void initButtons() {
        // Define colors for buttons
        Color numColor = new Color(80, 80, 80);
        Color opColor = new Color(255, 150, 0); // Orange for operators
        Color clearColor = new Color(220, 50, 50);     // Red for clear
        Color equalsColor = new Color(50, 200, 50);    // Green for equals

        // Number buttons (0-9)
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new RoundedButton(String.valueOf(i));
            numberButtons[i].addActionListener(this);
            numberButtons[i].setButtonColor(numColor);
            numberButtons[i].setFont(new Font("Arial", Font.BOLD, 18));
        }

        // Operator and other buttons
        addButton = new RoundedButton("+");
        subButton = new RoundedButton("-");
        mulButton = new RoundedButton("*");
        divButton = new RoundedButton("/");
        clrButton = new RoundedButton("C");
        decButton = new RoundedButton(".");
        equButton = new RoundedButton("="); // Initialized here for consistency

        RoundedButton[] ops = {addButton, subButton, mulButton, divButton};
        for (RoundedButton btn : ops) {
            btn.addActionListener(this);
            btn.setButtonColor(opColor);
            btn.setFont(new Font("Arial", Font.BOLD, 18));
        }

        clrButton.addActionListener(this);
        clrButton.setButtonColor(clearColor);
        clrButton.setFont(new Font("Arial", Font.BOLD, 18));

        decButton.addActionListener(this);
        decButton.setButtonColor(numColor); // Decimal button styled like numbers
        decButton.setFont(new Font("Arial", Font.BOLD, 18));

        equButton.addActionListener(this);
        equButton.setButtonColor(equalsColor);
        equButton.setFont(new Font("Arial", Font.BOLD, 20)); // Equals button might be slightly larger font
        equButton.setPreferredSize(new Dimension(0, 55)); // Give equals button some good height
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ((command.charAt(0) >= '0' && command.charAt(0) <= '9')) { // Number input
            if (newNumberStarted) {
                currentInput = ""; // Clear previous input if starting a new number
                newNumberStarted = false;
            }
            if (currentInput.equals("0") && command.equals("0")) { /* Do nothing if current is 0 and 0 is pressed */ }
            else if (currentInput.equals("0") && !command.equals("0")) { currentInput = command; } // Overwrite leading 0
            else if (currentInput.length() < 16) { // Limit input length
                 currentInput += command;
            }
            displayField.setText(currentInput);
        } else if (command.equals(".")) { // Decimal point
            if (newNumberStarted) { // If starting a new number with decimal, prepend 0
                currentInput = "0";
                newNumberStarted = false;
            }
            if (!currentInput.contains(".")) { // Only add decimal if not already present
                if (currentInput.isEmpty()) currentInput = "0"; // Ensure "0." if empty
                currentInput += ".";
            }
            displayField.setText(currentInput);
        } else if (command.equals("C")) { // Clear
            resetCalculator();
            displayField.setText("0");
        } else if (command.equals("=")) { // Equals
            if (!currentInput.isEmpty() && currentOperator != ' ' && !newNumberStarted) {
                performCalculation();
                currentOperator = ' '; // Ready for a new operation sequence
                newNumberStarted = true; // Next number input will clear and start fresh
                                         // currentInput now holds the result
            } else if (currentOperator == ' ' && !currentInput.isEmpty()) {
                // If only a number is entered and then equals, just show that number
                // or make previousValue = currentInput. For now, do nothing or set previous.
                try {
                     previousValue = Double.parseDouble(currentInput);
                     displayField.setText(formatDisplay(previousValue));
                } catch (NumberFormatException ex) { /* ignore */ }
                newNumberStarted = true;
            }
        } else { // Operator (+, -, *, /)
            if (!currentInput.isEmpty() && !newNumberStarted) { // A number has been entered
                if (currentOperator != ' ') { // If there's a pending operation (e.g., 5 + 3 then * pressed)
                    performCalculation(); // Calculate the pending operation first
                                          // currentInput will hold the result of that calculation
                }
                // Now, currentInput (which might be a result) becomes previousValue for the new operation
                try {
                    previousValue = Double.parseDouble(currentInput);
                } catch (NumberFormatException ex) {
                    displayField.setText("Error");
                    resetCalculator();
                    return;
                }
            }
            // If currentInput is empty but previousValue is set (e.g., result shown, user presses new operator)
            // previousValue is already correct from the last calculation.

            currentOperator = command.charAt(0);
            displayField.setText(formatDisplay(previousValue) + " " + currentOperator);
            newNumberStarted = true; // Expecting a new number for the operation
        }
    }

    private void performCalculation() {
        double currentValue;
        try {
            // If currentInput is empty after an operator, it implies using previousValue again (e.g. 5 * =)
            // However, our logic ensures currentInput should not be empty here if newNumberStarted is false.
            if(currentInput.isEmpty()) { // Should ideally not happen with proper state management
                displayField.setText("Error: No Input");
                resetCalculator();
                return;
            }
            currentValue = Double.parseDouble(currentInput);
        } catch (NumberFormatException ex) {
            displayField.setText("Format Error");
            resetCalculator();
            return;
        }

        switch (currentOperator) {
            case '+': previousValue += currentValue; break;
            case '-': previousValue -= currentValue; break;
            case '*': previousValue *= currentValue; break;
            case '/':
                if (currentValue == 0) {
                    displayField.setText("Error: Div by 0");
                    resetCalculator();
                    return;
                }
                previousValue /= currentValue;
                break;
            default:
                // No valid operator, perhaps do nothing or show error
                return;
        }
        displayField.setText(formatDisplay(previousValue));
        currentInput = String.valueOf(previousValue); // The result is now the current input, for display or chaining
                                                      // newNumberStarted will be set to true after this if '=' was pressed
    }

    private String formatDisplay(double value) {
        // Avoid scientific notation for simple calculator unless number is huge/tiny
        if (Double.isNaN(value) || Double.isInfinite(value)) return "Error";

        String formatted;
        if (value == (long) value) { // Check if it's an integer
            formatted = String.format("%d", (long) value);
        } else {
            formatted = String.format("%.8f", value).replaceAll("\\.?0+$", ""); // Remove trailing zeros and point if integer
        }
        // Limit display length if too long
        if (formatted.length() > 16 && !formatted.contains("E")) {
            // If very long but not yet scientific, try to format with fewer decimals or switch to sci
            // This is a simple approach; a more robust one would consider magnitude.
            return String.format("%.6E", value);
        }
        return formatted;
    }

    private void resetCalculator() {
        currentInput = "";
        previousValue = 0;
        currentOperator = ' ';
        newNumberStarted = true;
    }
}
