package final_project.client;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;

public class Style {
    // --- PALETTE ---
    public static final Color APP_BG     = new Color(35, 35, 35);
    public static final Color PANEL_BG   = new Color(50, 50, 50);
    public static final Color ACCENT     = new Color(0, 120, 215);
    public static final Color ACCENT_HOVER = new Color(0, 160, 255); // Lighter Blue
    public static final Color TEXT_WHITE = new Color(255, 255, 255);
    public static final Color TEXT_GRAY  = new Color(180, 180, 180);
    public static final Color FIELD_BG   = new Color(30, 30, 30);
    public static final Color FIELD_FOCUS= new Color(45, 45, 45);    // Slightly lighter on focus

    public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font DATA_FONT   = new Font("Monospaced", Font.PLAIN, 14);
    public static final Font NORM_FONT   = new Font("SansSerif", Font.PLAIN, 14);

    public static void themePanel(JPanel p) {
        p.setBackground(APP_BG);
    }

    // --- ANIMATED INPUT FIELDS ---
    public static void themeField(JTextComponent t) {
        t.setBackground(FIELD_BG);
        t.setForeground(TEXT_WHITE);
        t.setCaretColor(TEXT_WHITE);
        t.setFont(NORM_FONT);
        t.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Smooth Focus Animation
        t.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { animateColor(t, FIELD_BG, FIELD_FOCUS); }
            @Override
            public void focusLost(FocusEvent e)   { animateColor(t, FIELD_FOCUS, FIELD_BG); }
        });
    }

    // --- ANIMATED BUTTONS ---
    public static JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(ACCENT);
        b.setForeground(TEXT_WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Smooth Hover Animation
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { animateColor(b, ACCENT, ACCENT_HOVER); }
            public void mouseExited(MouseEvent e)  { animateColor(b, ACCENT_HOVER, ACCENT); }
        });
        return b;
    }

    // --- ANIMATED SIDEBAR BUTTONS ---
    public static JButton createNavButton(String text) {
        JButton b = new JButton(text);
        b.setFont(NORM_FONT);
        Color normal = new Color(50, 50, 50);
        Color hover = new Color(70, 70, 70);
        
        b.setBackground(normal);
        b.setForeground(TEXT_WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { animateColor(b, normal, hover); }
            public void mouseExited(MouseEvent e)  { animateColor(b, hover, normal); }
        });
        return b;
    }
    
    public static JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_GRAY);
        l.setFont(NORM_FONT);
        return l;
    }

    // --- ANIMATION ENGINE (Color Interpolation) ---
    private static void animateColor(JComponent comp, Color from, Color to) {
        Timer timer = new Timer(15, null); // 15ms update rate (~60fps)
        final long startTime = System.currentTimeMillis();
        final int duration = 150; // Animation takes 150ms

        timer.addActionListener(e -> {
            long now = System.currentTimeMillis();
            float fraction = (float)(now - startTime) / duration;

            if (fraction >= 1f) {
                comp.setBackground(to);
                timer.stop();
            } else {
                // Linear Interpolation (Lerp) logic
                int r = (int)(from.getRed()   + (to.getRed()   - from.getRed())   * fraction);
                int g = (int)(from.getGreen() + (to.getGreen() - from.getGreen()) * fraction);
                int b = (int)(from.getBlue()  + (to.getBlue()  - from.getBlue())  * fraction);
                comp.setBackground(new Color(r, g, b));
            }
            comp.repaint();
        });
        timer.start();
    }
}