package final_project.client;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class Style {
    // --- SAFE DARK THEME PALETTE ---
    public static final Color APP_BG     = new Color(40, 40, 40);    // Main Background (Dark Grey)
    public static final Color PANEL_BG   = new Color(60, 60, 60);    // Panels (Lighter Grey)
    public static final Color ACCENT     = new Color(0, 120, 215);   // Windows Blue
    public static final Color TEXT_WHITE = new Color(255, 255, 255); // Pure White
    public static final Color TEXT_GRAY  = new Color(200, 200, 200); // Readable Gray
    public static final Color FIELD_BG   = new Color(30, 30, 30);    // Very Dark for Inputs
    
    public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font DATA_FONT   = new Font("Monospaced", Font.PLAIN, 14);
    public static final Font NORM_FONT   = new Font("SansSerif", Font.PLAIN, 14);

    // --- HELPER: APPLY THEME TO PANELS ---
    public static void themePanel(JPanel p) {
        p.setBackground(APP_BG);
    }

    // --- HELPER: STYLED INPUT FIELDS ---
    public static void themeField(JTextComponent t) {
        t.setBackground(FIELD_BG);
        t.setForeground(TEXT_WHITE);
        t.setCaretColor(TEXT_WHITE);
        t.setFont(NORM_FONT);
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    // --- HELPER: BLUE ACTION BUTTONS ---
    public static JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(ACCENT);
        b.setForeground(TEXT_WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false); // Removes the weird 3D border effect
        b.setOpaque(true);         // Forces the blue color to show
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Add padding
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return b;
    }

    // --- HELPER: SIDEBAR NAVIGATION BUTTONS ---
    public static JButton createNavButton(String text) {
        JButton b = new JButton(text);
        b.setFont(NORM_FONT);
        b.setBackground(new Color(50, 50, 50)); // Dark Grey Button
        b.setForeground(TEXT_WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
    
    // --- HELPER: LABELS ---
    public static JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_GRAY);
        l.setFont(NORM_FONT);
        return l;
    }
}