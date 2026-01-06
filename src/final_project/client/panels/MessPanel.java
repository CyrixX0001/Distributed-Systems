package final_project.client.panels;

import javax.swing.*;
import java.awt.*;
import java.rmi.Naming;
import java.util.List;
import final_project.client.HostelClientApp;
import final_project.client.Style;
import final_project.client.shared.MessService;

public class MessPanel extends JPanel {
    private JTextArea txtMenu = new JTextArea(5, 30);
    private JTextArea txtFeedbackLog = new JTextArea(5, 30);
    
    private JComboBox<String> cmbRating = new JComboBox<>(new String[]{"⭐⭐⭐⭐⭐ (Excellent)", "⭐⭐⭐⭐ (Good)", "⭐⭐⭐ (Average)", "⭐⭐ (Bad)", "⭐ (Terrible)"});
    private JTextField txtComment = new JTextField(20);

    public MessPanel() {
        setLayout(new BorderLayout(10, 10));
        Style.themePanel(this);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- 1. TOP: MENU DISPLAY ---
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Style.PANEL_BG);
        top.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Today's Menu", 0,0, Style.HEADER_FONT, Style.ACCENT));
        
        txtMenu.setFont(Style.DATA_FONT);
        txtMenu.setEditable(false);
        txtMenu.setBackground(new Color(60, 60, 60)); 
        txtMenu.setForeground(Color.CYAN);
        top.add(new JScrollPane(txtMenu), BorderLayout.CENTER);
        
        JButton btnLoadMenu = Style.createButton("Load Menu");
        btnLoadMenu.addActionListener(e -> loadMenu());
        top.add(btnLoadMenu, BorderLayout.SOUTH);
        
        add(top, BorderLayout.NORTH);

        // --- 2. CENTER: FEEDBACK FORM ---
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Style.PANEL_BG);
        center.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Submit Feedback", 0,0, Style.HEADER_FONT, Style.ACCENT));
        
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5); g.fill = GridBagConstraints.HORIZONTAL; g.gridx=0; g.gridy=0;
        
        center.add(Style.createLabel("Rating:"), g); g.gridx=1;
        center.add(cmbRating, g); 
        
        g.gridx=0; g.gridy=1;
        center.add(Style.createLabel("Comment:"), g); g.gridx=1;
        Style.themeField(txtComment);
        center.add(txtComment, g);
        
        g.gridx=1; g.gridy=2;
        JButton btnSubmit = Style.createButton("Submit Feedback");
        btnSubmit.setBackground(new Color(40, 167, 69)); // Manually override green if desired
        btnSubmit.addActionListener(e -> submitFeedback());
        center.add(btnSubmit, g);
        
        add(center, BorderLayout.CENTER);
        
        // --- 3. BOTTOM: RECENT FEEDBACK ---
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Style.PANEL_BG);
        bottom.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Community Feedback", 0,0, Style.HEADER_FONT, Style.ACCENT));
        
        txtFeedbackLog.setEditable(false);
        txtFeedbackLog.setFont(Style.DATA_FONT);
        txtFeedbackLog.setBackground(new Color(30, 30, 30));
        txtFeedbackLog.setForeground(Color.LIGHT_GRAY);
        
        bottom.add(new JScrollPane(txtFeedbackLog));
        JButton btnLoadFeed = Style.createButton("Refresh Feedback");
        btnLoadFeed.addActionListener(e -> loadFeedback());
        bottom.add(btnLoadFeed, BorderLayout.SOUTH);
        
        add(bottom, BorderLayout.SOUTH);
    }

    private MessService getService() throws Exception {
        return (MessService) Naming.lookup("rmi://localhost:1099/MessService");
    }

    private void loadMenu() {
        try {
            String menu = getService().getDailyMenu();
            txtMenu.setText(menu);
        } catch (Exception ex) { txtMenu.setText("RMI Error: " + ex.getMessage()); }
    }

    private void submitFeedback() {
        try {
            String resp = getService().submitFeedback(HostelClientApp.CURRENT_USER, (String) cmbRating.getSelectedItem(), txtComment.getText());
            JOptionPane.showMessageDialog(this, resp);
            txtComment.setText("");
            loadFeedback(); 
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void loadFeedback() {
        try {
            List<String> list = getService().getAllFeedback();
            StringBuilder sb = new StringBuilder();
            for(String s : list) sb.append(s).append("\n");
            txtFeedbackLog.setText(sb.toString());
        } catch (Exception ex) { txtFeedbackLog.setText("Error loading feedback."); }
    }
}