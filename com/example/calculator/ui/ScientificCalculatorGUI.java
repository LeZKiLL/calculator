package com.example.calculator.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.example.calculator.logic.ExpressionEvaluator;
import com.example.calculator.logic.Fraction;
import com.example.calculator.logic.SettingsManager; // Import SettingsManager

public class ScientificCalculatorGUI extends JFrame implements ActionListener {

    private MainMenu mainMenuRef;

    private JTextField displayField;
    private RoundedButton[] numberButtons = new RoundedButton[10];
    private RoundedButton addButton, subButton, mulButton, divButton, equButton, clrButton, backspaceButton;
    private RoundedButton decButton;
    private RoundedButton openParenButton, closeParenButton;
    private RoundedButton modeToggleButton;

    private RoundedButton sinButton, cosButton, tanButton, powYButton;
    private RoundedButton log10Button, lnButton, sqrtButton, squareButton;
    private RoundedButton percentButton;
    private RoundedButton piButton, eButton;

    private JPanel panel;
    private ExpressionEvaluator numericalEvaluator;
    private boolean preferFractionMode = false;

    // Colors remain the same
    private final Color numberColor = new Color(80, 80, 80);
    private final Color opColor = new Color(255, 150, 0);
    private final Color funcColor = new Color(60, 120, 180);
    private final Color specialFuncColor = new Color(100, 180, 100);
    private final Color utilityColor = new Color(100, 100, 100);
    private final Color clearColor = new Color(220, 50, 50);
    private final Color equalsColor = new Color(50, 200, 50);
    private final Color parenColor = new Color(150, 100, 200);

    public ScientificCalculatorGUI(MainMenu mainMenu) {
        this.mainMenuRef = mainMenu;
        this.numericalEvaluator = new ExpressionEvaluator();

        // Load initial fraction preference from settings (if you add such a setting)
        // For now, it defaults to false (decimal mode).
        // this.preferFractionMode = SettingsManager.loadDefaultFractionPreference(); // Example

        setTitle("Scientific Calculator");
        setSize(480, 640);
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
        displayField.setFont(new Font("Arial", Font.BOLD, 26));
        displayField.setBackground(new Color(50, 50, 50));
        displayField.setForeground(Color.WHITE);
        displayField.setBorder(new EmptyBorder(15, 10, 15, 10));
        displayField.addActionListener(e -> calculateExpression());

        initButtons();
        updateModeButtonTextAndTitle(); // Update based on initial preferFractionMode

        panel = new JPanel();
        panel.setLayout(new GridLayout(8, 5, 5, 5));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel Layout (as defined before)
        // Row 0
        panel.add(modeToggleButton); panel.add(openParenButton); panel.add(closeParenButton);
        panel.add(backspaceButton); panel.add(clrButton);
        // Row 1
        panel.add(sinButton); panel.add(cosButton); panel.add(tanButton);
        panel.add(log10Button); panel.add(lnButton);
        // Row 2
        panel.add(squareButton); panel.add(sqrtButton); panel.add(powYButton);
        panel.add(piButton); panel.add(eButton);
        // Row 3
        panel.add(numberButtons[7]); panel.add(numberButtons[8]); panel.add(numberButtons[9]);
        panel.add(divButton); panel.add(percentButton);
        // Row 4
        panel.add(numberButtons[4]); panel.add(numberButtons[5]); panel.add(numberButtons[6]);
        panel.add(mulButton); panel.add(new JLabel("")); // Placeholder
        // Row 5
        panel.add(numberButtons[1]); panel.add(numberButtons[2]); panel.add(numberButtons[3]);
        panel.add(subButton); panel.add(new JLabel("")); // Placeholder
        // Row 6
        panel.add(new JLabel("")); panel.add(numberButtons[0]); panel.add(decButton);
        panel.add(addButton); panel.add(equButton);
        // Row 7
        panel.add(new JLabel(""));panel.add(new JLabel(""));panel.add(new JLabel(""));
        panel.add(new JLabel(""));panel.add(new JLabel(""));


        setLayout(new BorderLayout(10, 10));
        add(displayField, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        displayField.requestFocusInWindow();
    }

    private void initButtons() {
        // ... (button initialization remains largely the same as your "Shift/Mode" version)
        // Ensure modeToggleButton is initialized:
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new RoundedButton(String.valueOf(i)); numberButtons[i].addActionListener(this); numberButtons[i].setButtonColor(numberColor);
        }
        addButton = new RoundedButton("+"); subButton = new RoundedButton("-"); mulButton = new RoundedButton("*"); divButton = new RoundedButton("/");
        openParenButton = new RoundedButton("("); closeParenButton = new RoundedButton(")"); decButton = new RoundedButton(".");
        RoundedButton[] basicOpsAndParens = { addButton, subButton, mulButton, divButton, openParenButton, closeParenButton };
        for(RoundedButton btn : basicOpsAndParens) {
            btn.addActionListener(this);
            if (btn == openParenButton || btn == closeParenButton) btn.setButtonColor(parenColor); else btn.setButtonColor(opColor);
        }
        decButton.addActionListener(this); decButton.setButtonColor(numberColor);
        equButton = new RoundedButton("="); clrButton = new RoundedButton("C"); backspaceButton = new RoundedButton("←");
        modeToggleButton = new RoundedButton("Mode: Dec"); // Initial Text
        equButton.addActionListener(this); equButton.setButtonColor(equalsColor);
        clrButton.addActionListener(this); clrButton.setButtonColor(clearColor);
        backspaceButton.addActionListener(this); backspaceButton.setButtonColor(clearColor); // Changed utilityColor to clearColor
        modeToggleButton.addActionListener(this); modeToggleButton.setButtonColor(utilityColor); // Use utilityColor
        modeToggleButton.setFont(new Font("Arial", Font.PLAIN, 12));

        sinButton = new RoundedButton("sin", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        cosButton = new RoundedButton("cos", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        tanButton = new RoundedButton("tan", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        powYButton = new RoundedButton("^", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        log10Button = new RoundedButton("log", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        lnButton = new RoundedButton("ln", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        sqrtButton = new RoundedButton("sqrt", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        squareButton = new RoundedButton("x²", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        percentButton = new RoundedButton("%", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        piButton = new RoundedButton("π", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        eButton = new RoundedButton("e", RoundedButton.ButtonSizeCategory.SCIENTIFIC);
        RoundedButton[] sciOps = { sinButton, cosButton, tanButton, powYButton, log10Button, lnButton, sqrtButton, squareButton, percentButton, piButton, eButton };
        for (RoundedButton btn : sciOps) {
            btn.addActionListener(this);
            if (btn == powYButton) btn.setButtonColor(opColor);
            else if (btn == piButton || btn == eButton) btn.setButtonColor(specialFuncColor);
            else btn.setButtonColor(funcColor);
        }
    }

    private void updateModeButtonTextAndTitle() {
        if (preferFractionMode) {
            modeToggleButton.setText("Mode: Frac");
            setTitle("Scientific Calculator (Fraction Mode)");
        } else {
            modeToggleButton.setText("Mode: Dec");
            setTitle("Scientific Calculator (Decimal Mode)");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        String command = e.getActionCommand();

        if (source == modeToggleButton) {
            preferFractionMode = !preferFractionMode;
            updateModeButtonTextAndTitle();
            displayField.requestFocusInWindow();
            return;
        }
        // ... (rest of actionPerformed for C, ←, =, functions, numbers, ops remains same)
        switch (command) {
            case "C": displayField.setText(""); break;
            case "←":
                String currentTextBS = displayField.getText(); int cursorPosBS = displayField.getCaretPosition();
                if (cursorPosBS > 0 && !currentTextBS.isEmpty()) {
                    String newText = currentTextBS.substring(0, cursorPosBS - 1) + currentTextBS.substring(cursorPosBS);
                    displayField.setText(newText); displayField.setCaretPosition(cursorPosBS - 1);
                }
                break;
            case "=": calculateExpression(); break;
            case "sin": case "cos": case "tan": case "log": case "ln": case "sqrt":
                insertIntoDisplay(command + "("); break;
            case "x²": insertIntoDisplay("^(2)"); break;
            case "π": insertIntoDisplay("pi"); break;
            case "e": insertIntoDisplay("e"); break;
            case "%": insertIntoDisplay("/100"); break;
            default: insertIntoDisplay(command); break;
        }
        displayField.requestFocusInWindow();
    }

    private void calculateExpression() {
        String expression = displayField.getText().trim();
        if (expression.isEmpty()) return;

        try {
            // Get current angle unit from settings
            String angleUnit = SettingsManager.loadAngleUnit();
            Object result = numericalEvaluator.evaluate(expression, preferFractionMode, angleUnit); // Pass angleUnit
            displayResult(result);
        } catch (IllegalArgumentException | ArithmeticException ex) {
            String errorMessage = ex.getMessage();
            if (errorMessage != null && errorMessage.length() > 30) errorMessage = errorMessage.substring(0, 30) + "...";
            displayField.setText("Error: " + (errorMessage != null ? errorMessage : "Invalid"));
        } catch (Exception ex) {
            displayField.setText("Error: Calc Failed");
        }
        displayField.selectAll();
        displayField.requestFocusInWindow();
    }

    private void insertIntoDisplay(String text) {
        // ... (same as before)
        int cursorPos = displayField.getCaretPosition(); String currentText = displayField.getText();
        String newText = currentText.substring(0, cursorPos) + text + currentText.substring(cursorPos);
        if (newText.length() < 60) { displayField.setText(newText); displayField.setCaretPosition(cursorPos + text.length());}
        displayField.requestFocusInWindow();
    }

    private void displayResult(Object result) {
        // ... (same as before, uses preferFractionMode)
        if (result instanceof Fraction) {
            Fraction fracResult = (Fraction) result;
            if (preferFractionMode || fracResult.getDenominator() != 1) { displayField.setText(fracResult.toString());
            } else { displayField.setText(String.valueOf(fracResult.getNumerator())); }
        } else if (result instanceof Double) {
            double doubleResult = (Double) result;
            if (Double.isNaN(doubleResult) || Double.isInfinite(doubleResult)) { displayField.setText("Error");
            } else if (!preferFractionMode && doubleResult == (long) doubleResult) { displayField.setText(String.format("%d", (long) doubleResult));
            } else if (preferFractionMode) {
                Fraction convertedFraction = ExpressionEvaluator.doubleToFraction(doubleResult, 1000000);
                if (convertedFraction.getDenominator() == 1) displayField.setText(String.valueOf(convertedFraction.getNumerator()));
                else displayField.setText(convertedFraction.toString());
            } else {
                String formatted = String.format("%.10f", doubleResult).replaceAll("\\.?0+$", "");
                if (formatted.isEmpty() && doubleResult == 0) formatted = "0"; else if (formatted.equals(".")) formatted = "0";
                if (formatted.length() > 18 || (Math.abs(doubleResult)>1e12 || (Math.abs(doubleResult)<1e-6 && doubleResult!=0))) {
                    displayField.setText(String.format("%.6E", doubleResult));
                } else { displayField.setText(formatted); }
            }
        } else if (result != null) { displayField.setText(result.toString());
        } else { displayField.setText("Error"); }
    }
}
