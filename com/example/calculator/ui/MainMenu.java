package com.example.calculator.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Import other calculator GUIs
// import com.example.calculator.ui.SimpleCalculatorGUI;
// import com.example.calculator.ui.ScientificCalculatorGUI;
// import com.example.calculator.ui.AlgebraicCalculatorGUI;
// Assuming RoundedButton is in this package

public class MainMenu extends JFrame implements ActionListener {

    private RoundedButton btnSimpleCalculator;
    private RoundedButton btnScientificCalculator;
    private RoundedButton btnAlgebraicCalculator;
    private RoundedButton btnCalculusCalculator; // New button
    private RoundedButton btnExit;

    public MainMenu() {
        setTitle("Calculator Main Menu");
        setSize(400, 400); // Increased height for new button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(40, 40, 40));

        JLabel titleLabel = new JLabel("Select Calculator Type", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        btnSimpleCalculator = new RoundedButton("Simple Calculator");
        btnScientificCalculator = new RoundedButton("Scientific Calculator");
        btnAlgebraicCalculator = new RoundedButton("Algebraic Calculator");
        btnCalculusCalculator = new RoundedButton("Calculus Calculator"); // New
        btnExit = new RoundedButton("Exit");

        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        Color buttonColor = new Color(80, 80, 80);
        Color calculusButtonColor = new Color(60, 150, 80); // Different color for calculus
        Color exitButtonColor = new Color(200, 60, 60);

        btnSimpleCalculator.setFont(buttonFont);
        btnSimpleCalculator.setButtonColor(buttonColor);
        btnScientificCalculator.setFont(buttonFont);
        btnScientificCalculator.setButtonColor(buttonColor);
        btnAlgebraicCalculator.setFont(buttonFont);
        btnAlgebraicCalculator.setButtonColor(buttonColor);
        btnCalculusCalculator.setFont(buttonFont); // New
        btnCalculusCalculator.setButtonColor(calculusButtonColor); // New
        btnExit.setFont(buttonFont);
        btnExit.setButtonColor(exitButtonColor);

        btnSimpleCalculator.addActionListener(this);
        btnScientificCalculator.addActionListener(this);
        btnAlgebraicCalculator.addActionListener(this);
        btnCalculusCalculator.addActionListener(this); // New
        btnExit.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 15)); // 5 rows now
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));

        buttonPanel.add(btnSimpleCalculator);
        buttonPanel.add(btnScientificCalculator);
        buttonPanel.add(btnAlgebraicCalculator);
        buttonPanel.add(btnCalculusCalculator); // New
        buttonPanel.add(btnExit);

        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        this.setVisible(false);

        if (source == btnSimpleCalculator) {
            SwingUtilities.invokeLater(() -> {
                SimpleCalculatorGUI simpleCalc = new SimpleCalculatorGUI(this);
                simpleCalc.setVisible(true);
            });
        } else if (source == btnScientificCalculator) {
            SwingUtilities.invokeLater(() -> {
                ScientificCalculatorGUI scientificCalc = new ScientificCalculatorGUI(this);
                scientificCalc.setVisible(true);
            });
        } else if (source == btnAlgebraicCalculator) {
             SwingUtilities.invokeLater(() -> {
                AlgebraicCalculatorGUI algebraicCalc = new AlgebraicCalculatorGUI(this);
                algebraicCalc.setVisible(true);
            });
        } else if (source == btnCalculusCalculator) { // New
            SwingUtilities.invokeLater(() -> {
                CalculusCalculatorGUI calculusCalc = new CalculusCalculatorGUI(this);
                calculusCalc.setVisible(true);
            });
        }
        else if (source == btnExit) {
            System.exit(0);
        }
    }

    public void showMenu() {
        this.setVisible(true);
    }
}
