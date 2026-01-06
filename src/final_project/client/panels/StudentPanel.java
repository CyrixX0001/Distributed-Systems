package final_project.client.panels;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import final_project.client.Style;

public class StudentPanel extends JPanel {
    private JTextField txtRoom = new JTextField(10);
    // --- UPDATED: ADDED "Other" CATEGORY ---
    private JComboBox<String> cmbCat = new JComboBox<>(new String[]{"Water", "Electricity", "Cleanliness", "Other"});
    private JTextArea txtDesc = new JTextArea(5, 20);
    private JTextArea txtHistory = new JTextArea(5, 40);
    private String myRoomNumber = "";

    public StudentPanel() {
        setLayout(new BorderLayout(20, 20));
        Style.themePanel(this);
        setBorder(BorderFactory.createEmptyBorder(30, 150, 30, 150));

        // --- TOP FORM ---
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setBackground(Style.PANEL_BG);
        topContainer.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "New Complaint", 0,0, Style.HEADER_FONT, Style.ACCENT));

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBackground(Style.PANEL_BG);
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        form.add(Style.createLabel("Room Number:"));
        txtRoom.setEditable(false); Style.themeField(txtRoom); form.add(txtRoom); 
        
        form.add(Style.createLabel("Category:"));
        cmbCat.setBackground(Color.WHITE); form.add(cmbCat);
        
        form.add(Style.createLabel("Description:"));
        Style.themeField(txtDesc);
        form.add(new JScrollPane(txtDesc));

        JButton btnSend = Style.createButton("LODGE COMPLAINT");
        btnSend.setPreferredSize(new Dimension(0, 50));

        topContainer.add(form, BorderLayout.CENTER);
        topContainer.add(btnSend, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // --- BOTTOM HISTORY ---
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(Style.PANEL_BG);
        historyPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Ticket History", 0,0, Style.HEADER_FONT, Style.ACCENT));
        
        txtHistory.setEditable(false);
        txtHistory.setFont(Style.DATA_FONT);
        txtHistory.setBackground(new Color(20, 20, 20)); 
        txtHistory.setForeground(Color.GREEN);
        
        historyPanel.add(new JScrollPane(txtHistory), BorderLayout.CENTER);
        JButton btnRefresh = Style.createNavButton("Refresh History");
        btnRefresh.addActionListener(e -> fetchMyTickets());
        historyPanel.add(btnRefresh, BorderLayout.SOUTH);

        add(historyPanel, BorderLayout.CENTER);
        btnSend.addActionListener(e -> sendComplaint());
    }

    public void updateRoom(String room) {
        this.myRoomNumber = room;
        txtRoom.setText(room);
        fetchMyTickets();
    }

    private void sendComplaint() {
        try (Socket s = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            out.println("LODGE::" + myRoomNumber + "::" + cmbCat.getSelectedItem() + "::" + txtDesc.getText());
            JOptionPane.showMessageDialog(this, in.readLine());
            txtDesc.setText(""); fetchMyTickets();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error"); }
    }

    private void fetchMyTickets() {
        try (Socket s = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            out.println("VIEW::" + myRoomNumber);
            String resp = in.readLine();
            if(resp != null && resp.startsWith("LIST::")) {
                txtHistory.setText(resp.substring(6).replace("||", "\n"));
            }
        } catch (Exception ex) {}
    }
}