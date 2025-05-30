package com.example.calculator;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.example.calculator.ui.MainMenu; // Import MainMenu

public class MainApp {

    public static void main(String[] args) {
        // Set a more modern Look and Feel if available (Nimbus)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, use the default L&F
            // You can log this error or ignore it if default L&F is acceptable
            System.err.println("Nimbus L&F not found, using default.");
        }

        SwingUtilities.invokeLater(() -> {
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
        });
    }
}
