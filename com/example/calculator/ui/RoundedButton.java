package com.example.calculator.ui;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// Ensure this class is public and in a file named RoundedButton.java
public class RoundedButton extends JButton {
    // Enum to define button size categories
    public static enum ButtonSizeCategory { // Must be public static to be accessible
        STANDARD, SCIENTIFIC
    }

    private Color backgroundColor;
    private Color hoverBackgroundColor;
    private Color pressedBackgroundColor;
    private int cornerRadius = 15;
    private ButtonSizeCategory category;

    private static final Font STANDARD_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font SCIENTIFIC_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Dimension STANDARD_MIN_SIZE = new Dimension(65, 45);
    private static final Dimension SCIENTIFIC_MIN_SIZE = new Dimension(60, 40);

    public RoundedButton(String text) {
        this(text, ButtonSizeCategory.STANDARD);
    }

    public RoundedButton(String text, ButtonSizeCategory category) {
        super(text);
        this.category = category;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(Color.WHITE);

        if (this.category == ButtonSizeCategory.SCIENTIFIC) {
            setFont(SCIENTIFIC_FONT);
        } else {
            setFont(STANDARD_FONT);
        }

        backgroundColor = new Color(80, 80, 80);
        hoverBackgroundColor = new Color(100, 100, 100);
        pressedBackgroundColor = new Color(120, 120, 120);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(hoverBackgroundColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(backgroundColor);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                setBackground(pressedBackgroundColor);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (contains(evt.getPoint())) {
                    setBackground(hoverBackgroundColor);
                } else {
                    setBackground(backgroundColor);
                }
            }
        });
        setBackground(backgroundColor);
    }

    public void setButtonColor(Color color) {
        this.backgroundColor = color;
        this.hoverBackgroundColor = color.brighter();
        if (color.darker().equals(color.brighter()) || color.darker().equals(color)) {
            this.pressedBackgroundColor = new Color(
                Math.max(0, color.getRed() - 40),
                Math.max(0, color.getGreen() - 40),
                Math.max(0, color.getBlue() - 40)
            );
        } else {
            this.pressedBackgroundColor = color.darker();
        }
        setBackground(this.backgroundColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color colorToDraw;
        if (getModel().isPressed()) {
            colorToDraw = pressedBackgroundColor;
        } else if (getModel().isRollover()) {
            colorToDraw = hoverBackgroundColor;
        } else {
            colorToDraw = backgroundColor;
        }

        g2.setColor(colorToDraw);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        Dimension minSize = (this.category == ButtonSizeCategory.SCIENTIFIC) ? SCIENTIFIC_MIN_SIZE : STANDARD_MIN_SIZE;
        size.width = Math.max(size.width, minSize.width);
        size.height = Math.max(size.height, minSize.height);
        return size;
    }
}
