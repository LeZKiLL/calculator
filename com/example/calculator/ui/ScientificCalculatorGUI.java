package com.example.calculator.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.example.calculator.logic.ExpressionEvaluator;
import com.example.calculator.logic.SymbolicEvaluator;
import com.example.calculator.ui.RoundedButton.ButtonSizeCategory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

private MainMenu mainMenuRef;

public class ScientificCalculatorGUI extends JFrame implements ActionListener {

    private JTextField displayField;
    private RoundedButton[] numberButtons = new RoundedButton[10];
    private RoundedButton addButton, subButton, mulButton, divButton, equButton, clrButton, backspaceButton;
    private RoundedButton decButton;
    private RoundedButton openParenButton, closeParenButton;
    private RoundedButton xButton; // For the variable 'x'

    private RoundedButton sinButton, cosButton, tanButton, powYButton;
    private RoundedButton log10Button, lnButton, sqrtButton, squareButton;
    private RoundedButton percentButton;
    private RoundedButton piButton, eButton;

    private JPanel panel;
    private ExpressionEvaluator numericalEvaluator;
    private SymbolicEvaluator symbolicEvaluator;

    // Colors (same as before)
    private final Color numberColor = new Color(80, 80, 80);
    private final Color opColor = new Color(255, 150, 0);
    private final Color funcColor = new Color(60, 120, 180);
    private final Color specialFuncColor = new Color(100, 180, 100);
    private final Color clearColor = new Color(220, 50, 50);
    private final Color equalsColor = new Color(50, 200, 50);
    private final Color parenColor = new Color(150, 100, 200);
    private final Color varColor = new Color(0, 150, 136); // Teal for variable button


    public ScientificCalculatorGUI(MainMenu mainMenu) {
        this.mainMenuRef = mainMenu;
        this.evaluator = new ExpressionEvaluator(); // Ensure evaluator is initialized

        numericalEvaluator = new ExpressionEvaluator();
        symbolicEvaluator = new SymbolicEvaluator();

        setTitle("Scientific Calculator (Symbolic)");
        setSize(480, 640); // Adjusted size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(30, 30, 30));

        displayField = new JTextField();
        displayField.setEditable(true);
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setFont(new Font("Arial", Font.BOLD, 26));
        displayField.setBackground(new Color(50, 50, 50));
        displayField.setForeground(Color.WHITE);
        displayField.setBorder(new EmptyBorder(15, 10, 15, 10));
        displayField.addActionListener(e -> calculateTriggered());


        initButtons(); // Initialize buttons first

        panel = new JPanel();
        panel.setLayout(new GridLayout(7, 5, 5, 5)); // 7 rows, 5 columns
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel Layout (Example)
        // Row 1
        panel.add(sinButton); panel.add(cosButton); panel.add(tanButton);
        panel.add(openParenButton); panel.add(closeParenButton);
        // Row 2
        panel.add(log10Button); panel.add(lnButton); panel.add(sqrtButton);
        panel.add(squareButton); panel.add(powYButton); // x^y or ^
        // Row 3
        panel.add(piButton); panel.add(eButton); panel.add(percentButton);
        panel.add(xButton); // Variable 'x' button
        panel.add(new JLabel("")); // Placeholder
        // Row 4
        panel.add(numberButtons[7]); panel.add(numberButtons[8]); panel.add(numberButtons[9]);
        panel.add(divButton); panel.add(clrButton);
        // Row 5
        panel.add(numberButtons[4]); panel.add(numberButtons[5]); panel.add(numberButtons[6]);
        panel.add(mulButton); panel.add(backspaceButton);
        // Row 6
        panel.add(numberButtons[1]); panel.add(numberButtons[2]); panel.add(numberButtons[3]);
        panel.add(subButton); panel.add(equButton); // Equals button often spans 2 cells
        // Row 7
        panel.add(new JLabel("")); panel.add(numberButtons[0]); panel.add(decButton);
        panel.add(addButton); panel.add(new JLabel("")); // Equ button could take this spot if spanning


        setLayout(new BorderLayout(10, 10));
        add(displayField, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        setVisible(true);
        displayField.requestFocusInWindow();
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Important!
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (mainMenuRef != null) {
                    mainMenuRef.showMenu();
                }
            }
        });
    }

    private void initButtons() {
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new RoundedButton(String.valueOf(i)); // Uses default STANDARD size
            numberButtons[i].addActionListener(this);
            numberButtons[i].setButtonColor(numberColor);
        }

        addButton = new RoundedButton("+"); subButton = new RoundedButton("-");
        mulButton = new RoundedButton("*"); divButton = new RoundedButton("/");
        equButton = new RoundedButton("=");
        clrButton = new RoundedButton("C"); backspaceButton = new RoundedButton("←");
        decButton = new RoundedButton(".");
        openParenButton = new RoundedButton("("); closeParenButton = new RoundedButton(")");
        xButton = new RoundedButton("x"); // Variable x

        RoundedButton[] basicOpsAndParens = {
            addButton, subButton, mulButton, divButton,
            openParenButton, closeParenButton
        };
        for(RoundedButton btn : basicOpsAndParens) {
            btn.addActionListener(this);
            if (btn == openParenButton || btn == closeParenButton) btn.setButtonColor(parenColor);
            else btn.setButtonColor(opColor);
        }
        xButton.addActionListener(this); xButton.setButtonColor(varColor);

        equButton.addActionListener(this); equButton.setButtonColor(equalsColor);
        clrButton.addActionListener(this); clrButton.setButtonColor(clearColor);
        backspaceButton.addActionListener(this); backspaceButton.setButtonColor(clearColor);
        decButton.addActionListener(this); decButton.setButtonColor(numberColor);

        // Scientific Buttons - using SCIENTIFIC category for smaller size
        sinButton = new RoundedButton("sin", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        cosButton = new RoundedButton("cos", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        tanButton = new RoundedButton("tan", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        powYButton = new RoundedButton("^", RoundedButton.ButtonSizeCategory.SCIENTIFIC); // Use ^ for power
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
            if (btn == powYButton) btn.setButtonColor(opColor); // Power as operator color
            else if (btn == piButton || btn == eButton) btn.setButtonColor(specialFuncColor);
            else btn.setButtonColor(funcColor);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        int cursorPos = displayField.getCaretPosition();

        switch (command) {
            case "C": displayField.setText(""); break;
            case "←":
                String currentTextBS = displayField.getText();
                if (cursorPos > 0 && !currentTextBS.isEmpty()) {
                    String newText = currentTextBS.substring(0, cursorPos - 1) + currentTextBS.substring(cursorPos);
                    displayField.setText(newText);
                    displayField.setCaretPosition(cursorPos - 1);
                }
                break;
            case "=": calculateTriggered(); break;
            case "sin": case "cos": case "tan":
            case "log": case "ln": case "sqrt":
                insertIntoDisplay(command + "("); break;
            case "x²": insertIntoDisplay("^(2)"); break;
            case "π": insertIntoDisplay("pi"); break; // Symbolic evaluator expects "pi"
            case "e": insertIntoDisplay("e"); break;   // Symbolic evaluator expects "e"
            case "%": insertIntoDisplay("%"); break; // User will form like num% or (expr)%
            default: // Numbers, basic operators, parens, decimal, x
                insertIntoDisplay(command); break;
        }
        displayField.requestFocusInWindow();
    }

    private void calculateTriggered() {
        String expression = displayField.getText().trim();
        if (expression.isEmpty()) return;

        try {
            // Try symbolic evaluation first
            String symbolicResult = symbolicEvaluator.evaluate(expression);
            displayField.setText(symbolicResult);
        } catch (IllegalArgumentException symEx) {
            // If symbolic evaluation fails (e.g., not recognized format, or internal parsing error in symbolic)
            // try numerical evaluation as a fallback.
            try {
                double numericalResult = numericalEvaluator.evaluate(expression);
                displayNumericalResult(numericalResult);
            } catch (Exception numEx) {
                // If both fail, show the error from the symbolic attempt or a generic one
                 String err = symEx.getMessage() != null ? symEx.getMessage() : "Error";
                 if (err.length() > 35) err = err.substring(0,35) + "...";
                displayField.setText("Error: " + err);
            }
        } catch (Exception e) { // Catch any other unexpected error from symbolic evaluator
            displayField.setText("Error: Calculation failed");
        }
        displayField.selectAll();
        displayField.requestFocusInWindow();
    }

    private void insertIntoDisplay(String text) {
        int cursorPos = displayField.getCaretPosition();
        String currentText = displayField.getText();
        String newText = currentText.substring(0, cursorPos) + text + currentText.substring(cursorPos);
        if (newText.length() < 60) { // Max expression length
            displayField.setText(newText);
            displayField.setCaretPosition(cursorPos + text.length());
        }
        displayField.requestFocusInWindow();
    }

    private void displayNumericalResult(double result) {
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            displayField.setText("Error (Num)");
        } else if (result == (long) result) {
            displayField.setText(String.format("%d", (long) result));
        } else {
            String formatted = String.format("%.10f", result).replaceAll("\\.?0+$", "");
            if (formatted.length() > 18 || (Math.abs(result) > 1e12 || (Math.abs(result) < 1e-6 && result != 0))) {
                displayField.setText(String.format("%.6E", result));
            } else {
                displayField.setText(formatted);
            }
        }
    }
}
