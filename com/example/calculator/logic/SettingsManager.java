package com.example.calculator.logic;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Frame;
import java.util.prefs.Preferences;
import com.formdev.flatlaf.FlatDarkLaf; // For FlatLaf Dark
import com.formdev.flatlaf.FlatLightLaf; // For FlatLaf Light


public class SettingsManager {
    private static final String PREF_NODE_PATH = "com/example/calculator";
    private static final String THEME_KEY = "theme";
    private static final String ANGLE_UNIT_KEY = "angleUnit";

    public static final String THEME_DARK = "dark";
    public static final String THEME_LIGHT = "light";
    public static final String ANGLE_UNIT_DEGREES = "degrees";
    public static final String ANGLE_UNIT_RADIANS = "radians";

    private static Preferences getPreferences() {
        return Preferences.userRoot().node(PREF_NODE_PATH);
    }

    // --- Theme Settings ---
    public static void saveTheme(String themeName) {
        getPreferences().put(THEME_KEY, themeName);
    }

    public static String loadTheme() {
        return getPreferences().get(THEME_KEY, THEME_DARK); // Default to Dark
    }

    public static void applyTheme(String themeName) {
        try {
            if (THEME_LIGHT.equals(themeName)) {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } else { // Default to Dark
                UIManager.setLookAndFeel(new FlatDarkLaf());
            }
            // Update all existing frames
            for (Frame frame : Frame.getFrames()) {
                SwingUtilities.updateComponentTreeUI(frame);
            }
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to set theme: " + themeName + ". Error: " + e.getMessage());
            // Fallback if FlatLaf is not available or fails
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Failed to set fallback LookAndFeel: " + ex.getMessage());
            }
        }
    }


    // --- Angle Unit Settings ---
    public static void saveAngleUnit(String angleUnit) {
        getPreferences().put(ANGLE_UNIT_KEY, angleUnit);
    }

    public static String loadAngleUnit() {
        return getPreferences().get(ANGLE_UNIT_KEY, ANGLE_UNIT_DEGREES); // Default to Degrees
    }
}
