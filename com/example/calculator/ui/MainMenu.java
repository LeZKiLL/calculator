package com.example.calculator.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.example.calculator.ui.RoundedButton; // Import the custom button class
import com.example.calculator.ui.SimpleCalculatorGUI; // Import the simple calculator GUI
import com.example.calculator.ui.ScientificCalculatorGUI; // Import the scientific calculator GUI
import com.example.calculator.ui.AlgebraicCalculatorGUI; // Import the algebraic calculator GUI

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

