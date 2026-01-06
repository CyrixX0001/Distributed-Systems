package final_project.client.panels;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;
import final_project.client.Style;

public class WardenPanel extends JPanel {
    private JTextArea txtList = new JTextArea();
    private JTextField txtTicketID = new JTextField();
    private JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"IN_PROGRESS", "RESOLVED", "REJECTED"});
    private JTextArea txtRemarks = new JTextArea(3, 20);
    private JTextField txtNotice = new JTextField();

    public WardenPanel() {
        setLayout(new BorderLayout(15, 15));
        Style.themePanel(this);
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // --- LEFT SIDE: TICKET MANAGER ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Style.PANEL_BG);
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Ticket Manager", 0,0, Style.HEADER_FONT, Style.ACCENT));
        
        txtList.setEditable(false);
        txtList.setFont(Style.DATA_FONT);
        txtList.setBackground(new Color(30,30,30));
        txtList.setForeground(Color.GREEN);
        leftPanel.add(new JScrollPane(txtList), BorderLayout.CENTER);
        
        JButton btnRefresh = Style.createButton("Refresh Tickets");
        btnRefresh.addActionListener(e -> refresh());
        leftPanel.add(btnRefresh, BorderLayout.SOUTH);
        
        add(leftPanel, BorderLayout.CENTER);

        // --- RIGHT SIDE: ACTIONS ---
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 10, 10)); 
        rightPanel.setOpaque(false); 
        rightPanel.setPreferredSize(new Dimension(350, 0));

        // 1. Ticket Action Box
        JPanel ticketAction = new JPanel(new GridBagLayout());
        ticketAction.setBackground(Style.PANEL_BG);
        ticketAction.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Resolve Ticket", 0,0, Style.HEADER_FONT, Style.ACCENT));
        
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5); g.fill = GridBagConstraints.HORIZONTAL; g.gridx=0; g.gridy=0;
        
        ticketAction.add(Style.createLabel("Ticket ID:"), g); g.gridy++;
        Style.themeField(txtTicketID); ticketAction.add(txtTicketID, g); g.gridy++;
        
        ticketAction.add(Style.createLabel("Status:"), g); g.gridy++;
        ticketAction.add(cmbStatus, g); g.gridy++;
        
        ticketAction.add(Style.createLabel("Remarks:"), g); g.gridy++;
        txtRemarks.setBackground(new Color(30,30,30)); txtRemarks.setForeground(Color.WHITE);
        ticketAction.add(new JScrollPane(txtRemarks), g); g.gridy++;
        
        JButton btnUpdate = Style.createButton("Update Ticket");
        btnUpdate.addActionListener(e -> updateTicket());
        ticketAction.add(btnUpdate, g);

        // 2. Post Notice Box
        JPanel noticeAction = new JPanel(new GridBagLayout());
        noticeAction.setBackground(Style.PANEL_BG);
        noticeAction.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Post Notice (REST)", 0,0, Style.HEADER_FONT, Style.ACCENT));
        g.gridy = 0; 
        
        noticeAction.add(Style.createLabel("Announcement:"), g); g.gridy++;
        Style.themeField(txtNotice);
        noticeAction.add(txtNotice, g); g.gridy++;
        
        JButton btnPost = Style.createButton("Post Notice");
        btnPost.setBackground(new Color(40, 167, 69)); // Green
        btnPost.addActionListener(e -> postNotice());
        noticeAction.add(btnPost, g);

        rightPanel.add(ticketAction);
        rightPanel.add(noticeAction);
        
        add(rightPanel, BorderLayout.EAST);
    }

    private void postNotice() {
        String msg = txtNotice.getText().trim();
        if (msg.isEmpty()) return;
        String safeMsg = msg.replace(" ", "_");
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8000/add?msg=" + safeMsg);
                Scanner sc = new Scanner(url.openStream());
                String resp = sc.nextLine();
                SwingUtilities.invokeLater(() -> { JOptionPane.showMessageDialog(this, resp); txtNotice.setText(""); });
            } catch (Exception ex) { SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "REST Error")); }
        }).start();
    }

    private void refresh() {
        try (Socket s = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            out.println("VIEW::ALL");
            String resp = in.readLine();
            if(resp != null && resp.startsWith("LIST::")) {
                txtList.setText(resp.substring(6).replace("||", "\n\n"));
            } else { txtList.setText("No pending tickets."); }
        } catch (Exception ex) { txtList.setText("Error connecting to server."); }
    }

    private void updateTicket() {
        if (txtTicketID.getText().isEmpty()) return;
        try (Socket s = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            String cmd = "UPDATE::" + txtTicketID.getText().trim() + "::" + 
                         cmbStatus.getSelectedItem() + "::" + txtRemarks.getText();
            out.println(cmd);
            JOptionPane.showMessageDialog(this, in.readLine());
            refresh();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error"); }
    }
}