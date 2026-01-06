package final_project.client.panels;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import final_project.client.Style;

public class NoticePanel extends JPanel {
    private JTextArea txtDisplay = new JTextArea();
    private JLabel statusLabel = new JLabel("Click Refresh to load.");

    public NoticePanel() {
        setLayout(new BorderLayout(10, 10));
        Style.themePanel(this);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- DISPLAY AREA (Terminal Look) ---
        txtDisplay.setFont(new Font("Consolas", Font.PLAIN, 15));
        txtDisplay.setBackground(new Color(10, 10, 10)); 
        txtDisplay.setForeground(new Color(0, 255, 0));  
        txtDisplay.setEditable(false);
        txtDisplay.setText("\n   > Waiting for server connection...");
        
        JScrollPane scroll = new JScrollPane(txtDisplay);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 0))); 
        add(scroll, BorderLayout.CENTER);

        // --- BUTTONS ---
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Style.PANEL_BG);
        
        JButton btnLoad = Style.createButton("FETCH NOTICES");
        
        statusLabel.setForeground(Color.GRAY);
        bottom.add(statusLabel, BorderLayout.WEST);
        bottom.add(btnLoad, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        btnLoad.addActionListener(e -> fetchNotices());
    }

    private void fetchNotices() {
        statusLabel.setText("Connecting...");
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8000/notices?t=" + System.currentTimeMillis());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false); conn.setRequestMethod("GET");

                if (conn.getResponseCode() != 200) throw new IOException("Error: " + conn.getResponseCode());

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line; StringBuilder content = new StringBuilder();
                while ((line = in.readLine()) != null) content.append(line).append("\n");
                in.close();

                SwingUtilities.invokeLater(() -> {
                    txtDisplay.setText(content.toString());
                    statusLabel.setText("Updated: Just Now");
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> txtDisplay.setText("Connection Failed: " + ex.getMessage()));
            }
        }).start();
    }
}