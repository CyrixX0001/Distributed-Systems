package final_project.client.panels;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import final_project.client.Style;

public class RoomPanel extends JPanel {
    private JTextField txtRoom = new JTextField(10);
    private JTextArea txtResult = new JTextArea();

    public RoomPanel() {
        setLayout(new BorderLayout(20, 20));
        Style.themePanel(this);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // --- TOP: SEARCH BAR ---
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        top.setBackground(Style.PANEL_BG);
        top.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Room Database Search", 0,0, Style.HEADER_FONT, Style.ACCENT));

        top.add(Style.createLabel("Enter Room No (101-110):"));
        Style.themeField(txtRoom);
        top.add(txtRoom);

        JButton btnSearch = Style.createButton("Search Database");
        btnSearch.addActionListener(e -> searchRoom());
        top.add(btnSearch);

        add(top, BorderLayout.NORTH);

        // --- CENTER: RESULTS ---
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Style.PANEL_BG);
        center.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Room Details & Tickets", 0,0, Style.HEADER_FONT, Style.ACCENT));

        txtResult.setEditable(false);
        txtResult.setFont(Style.DATA_FONT);
        txtResult.setBackground(new Color(20, 20, 20)); 
        txtResult.setForeground(Color.CYAN);            
        txtResult.setMargin(new Insets(10, 10, 10, 10));

        center.add(new JScrollPane(txtResult));
        add(center, BorderLayout.CENTER);
    }

    private void searchRoom() {
        String room = txtRoom.getText().trim();
        if(room.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter Room Number"); return; }

        txtResult.setText("Searching Database...");

        new Thread(() -> {
            try (Socket s = new Socket("localhost", 5000);
                 PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                
                // 1. GET RESIDENTS
                out.println("GET_RESIDENTS::" + room);
                String residentsResp = in.readLine(); // "RESIDENTS::name, name..."
                
                // 2. GET TICKETS
                out.println("VIEW::" + room);
                String ticketsResp = in.readLine();   // "LIST::..."

                SwingUtilities.invokeLater(() -> {
                    StringBuilder display = new StringBuilder();
                    
                    // Show Residents
                    display.append("=== 🏠 RESIDENTS OF ROOM ").append(room).append(" ===\n");
                    if (residentsResp != null && residentsResp.startsWith("RESIDENTS::") && residentsResp.length() > 11) {
                        display.append(residentsResp.substring(11).replace(", ", "\n • "));
                    } else {
                        display.append("(No students found or Room Empty)");
                    }
                    
                    display.append("\n\n=== 🎫 TICKET HISTORY ===\n");
                    // Show Tickets
                    if(ticketsResp != null && ticketsResp.startsWith("LIST::") && ticketsResp.length() > 6) {
                        display.append(ticketsResp.substring(6).replace("||", "\n\n"));
                    } else {
                        display.append("(No active tickets)");
                    }
                    
                    txtResult.setText(display.toString());
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> txtResult.setText("Connection Error: Server Offline"));
            }
        }).start();
    }
}