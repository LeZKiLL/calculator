package com.example.calculator;

import javax.swing.SwingUtilities;
// UIManager is needed if we don't use SettingsManager.applyTheme() here directly
// import javax.swing.UIManager;

import com.example.calculator.logic.SettingsManager; // Import SettingsManager
import com.example.calculator.ui.MainMenu;

public class MainApp {

    public static void main(String[] args) {
        // Apply saved theme at startup
        String currentTheme = SettingsManager.loadTheme();
        SettingsManager.applyTheme(currentTheme); // This will set FlatLaf or fallback

        SwingUtilities.invokeLater(() -> {
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
        });
    }
}
