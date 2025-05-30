package com.example.calculator.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Assuming other calculator GUIs and RoundedButton are correctly in this package or imported

public class MainMenu extends JFrame implements ActionListener {

    private RoundedButton btnSimpleCalculator;
    private RoundedButton btnScientificCalculator;
    private RoundedButton btnAlgebraicCalculator;
    private RoundedButton btnCalculusCalculator;
    private RoundedButton btnSettings; // New Settings button
    private RoundedButton btnExit;

    public MainMenu() {
        setTitle("Calculator Main Menu");
        setSize(400, 450); // Increased height for new button
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
        btnCalculusCalculator = new RoundedButton("Calculus Calculator");
        btnSettings = new RoundedButton("Settings"); // Initialize
        btnExit = new RoundedButton("Exit");

        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        Color buttonColor = new Color(80, 80, 80);
        Color calculusButtonColor = new Color(60, 150, 80);
        Color settingsButtonColor = new Color(100, 100, 180); // Color for settings
        Color exitButtonColor = new Color(200, 60, 60);

        // Styling buttons
        btnSimpleCalculator.setFont(buttonFont); btnSimpleCalculator.setButtonColor(buttonColor);
        btnScientificCalculator.setFont(buttonFont); btnScientificCalculator.setButtonColor(buttonColor);
        btnAlgebraicCalculator.setFont(buttonFont); btnAlgebraicCalculator.setButtonColor(buttonColor);
        btnCalculusCalculator.setFont(buttonFont); btnCalculusCalculator.setButtonColor(calculusButtonColor);
        btnSettings.setFont(buttonFont); btnSettings.setButtonColor(settingsButtonColor); // Style settings
        btnExit.setFont(buttonFont); btnExit.setButtonColor(exitButtonColor);

        // Action Listeners
        btnSimpleCalculator.addActionListener(this);
        btnScientificCalculator.addActionListener(this);
        btnAlgebraicCalculator.addActionListener(this);
        btnCalculusCalculator.addActionListener(this);
        btnSettings.addActionListener(this); // Add listener
        btnExit.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 1, 10, 12)); // 6 rows now
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));

        buttonPanel.add(btnSimpleCalculator);
        buttonPanel.add(btnScientificCalculator);
        buttonPanel.add(btnAlgebraicCalculator);
        buttonPanel.add(btnCalculusCalculator);
        buttonPanel.add(btnSettings); // Add settings button to panel
        buttonPanel.add(btnExit);

        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == btnSettings) {
            SettingsGUI settingsDialog = new SettingsGUI(this, this); // 'this' is the owner Frame
            settingsDialog.setVisible(true);
            // MainMenu remains visible, settings dialog is modal
            return; // Don't hide main menu for settings
        }

        // Hide main menu for calculator launch (if not settings)
        this.setVisible(false);

        if (source == btnSimpleCalculator) {
            SwingUtilities.invokeLater(() -> new SimpleCalculatorGUI(this).setVisible(true));
        } else if (source == btnScientificCalculator) {
            SwingUtilities.invokeLater(() -> new ScientificCalculatorGUI(this).setVisible(true));
        } else if (source == btnAlgebraicCalculator) {
             SwingUtilities.invokeLater(() -> new AlgebraicCalculatorGUI(this).setVisible(true));
        } else if (source == btnCalculusCalculator) {
            SwingUtilities.invokeLater(() -> new CalculusCalculatorGUI(this).setVisible(true));
        } else if (source == btnExit) {
            System.exit(0);
        }
    }

    public void showMenu() {
        this.setVisible(true);
    }
}
