package com.example.calculator.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.example.calculator.logic.SettingsManager; // Import SettingsManager

public class SettingsGUI extends JDialog implements ActionListener {

    private JRadioButton radioDarkTheme, radioLightTheme;
    private JRadioButton radioDegrees, radioRadians;
    private RoundedButton saveButton, cancelButton;
    private MainMenu mainMenuRef; // To potentially refresh or notify

    public SettingsGUI(Frame owner, MainMenu mainMenu) {
        super(owner, "Settings", true); // Modal dialog
        this.mainMenuRef = mainMenu;

        setSize(400, 300);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(50, 50, 50)); // Consistent darkish background
        setLayout(new BorderLayout(10, 10));

        // --- Main Panel for Settings ---
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setOpaque(false);
        settingsPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Theme Setting ---
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        themePanel.setOpaque(false);
        themePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Theme",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14), Color.WHITE
        ));
        radioDarkTheme = new JRadioButton("Dark");
        radioLightTheme = new JRadioButton("Light");
        styleRadioButton(radioDarkTheme);
        styleRadioButton(radioLightTheme);
        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(radioDarkTheme);
        themeGroup.add(radioLightTheme);
        themePanel.add(radioDarkTheme);
        themePanel.add(radioLightTheme);
        settingsPanel.add(themePanel);

        // --- Angle Unit Setting ---
        JPanel angleUnitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        angleUnitPanel.setOpaque(false);
        angleUnitPanel.setBorder(BorderFactory.createTitledBorder(
             BorderFactory.createLineBorder(Color.GRAY), "Angle Unit (for Trig Functions)",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14), Color.WHITE
        ));
        radioDegrees = new JRadioButton("Degrees");
        radioRadians = new JRadioButton("Radians");
        styleRadioButton(radioDegrees);
        styleRadioButton(radioRadians);
        ButtonGroup angleGroup = new ButtonGroup();
        angleGroup.add(radioDegrees);
        angleGroup.add(radioRadians);
        angleUnitPanel.add(radioDegrees);
        angleUnitPanel.add(radioRadians);
        settingsPanel.add(angleUnitPanel);

        settingsPanel.add(Box.createVerticalStrut(20)); // Spacer

        // --- Buttons Panel ---
        JPanel buttonsBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsBar.setOpaque(false);
        saveButton = new RoundedButton("Save");
        cancelButton = new RoundedButton("Cancel");

        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);
        saveButton.setButtonColor(new Color(50, 180, 50));
        cancelButton.setButtonColor(new Color(180, 80, 80));

        buttonsBar.add(saveButton);
        buttonsBar.add(cancelButton);
        settingsPanel.add(buttonsBar);

        add(settingsPanel, BorderLayout.CENTER);
        loadSettings(); // Load current settings into UI
    }

    private void styleRadioButton(JRadioButton button) {
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private void loadSettings() {
        String currentTheme = SettingsManager.loadTheme();
        if (SettingsManager.THEME_LIGHT.equals(currentTheme)) {
            radioLightTheme.setSelected(true);
        } else {
            radioDarkTheme.setSelected(true);
        }

        String currentAngleUnit = SettingsManager.loadAngleUnit();
        if (SettingsManager.ANGLE_UNIT_RADIANS.equals(currentAngleUnit)) {
            radioRadians.setSelected(true);
        } else {
            radioDegrees.setSelected(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == saveButton) {
            String selectedTheme = radioLightTheme.isSelected() ? SettingsManager.THEME_LIGHT : SettingsManager.THEME_DARK;
            SettingsManager.saveTheme(selectedTheme);

            String selectedAngleUnit = radioRadians.isSelected() ? SettingsManager.ANGLE_UNIT_RADIANS : SettingsManager.ANGLE_UNIT_DEGREES;
            SettingsManager.saveAngleUnit(selectedAngleUnit);

            // Apply theme immediately
            SettingsManager.applyTheme(selectedTheme);

            JOptionPane.showMessageDialog(this, "Settings saved. Some changes may require a restart to fully apply to all custom components.", "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close settings dialog
        } else if (source == cancelButton) {
            dispose(); // Close settings dialog
        }
    }
}