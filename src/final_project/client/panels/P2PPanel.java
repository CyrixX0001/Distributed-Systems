package final_project.client.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import final_project.client.HostelClientApp;
import final_project.client.Style;

public class P2PPanel extends JPanel {
    private JTable fileTable;
    private DefaultTableModel tableModel;
    private JTextArea logArea = new JTextArea(4, 40);
    private int myP2PPort;
    private Map<String, File> mySharedFiles = new HashMap<>();

    public P2PPanel() {
        setLayout(new BorderLayout(10, 10));
        Style.themePanel(this);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. TOP: Title & Actions
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Style.PANEL_BG);
        
        JButton btnUpload = Style.createButton("Share File");
        JButton btnRefresh = Style.createButton("Refresh List");
        JButton btnDownload = Style.createButton("Download");
        btnDownload.setBackground(new Color(40, 167, 69));

        top.add(btnUpload);
        top.add(btnRefresh);
        top.add(btnDownload);
        add(top, BorderLayout.NORTH);

        // 2. CENTER: File List (Added VISIBILITY column)
        String[] cols = {"File Name", "Owner", "Owner IP", "Port", "Visibility"};
        tableModel = new DefaultTableModel(cols, 0);
        fileTable = new JTable(tableModel);
        
        fileTable.setBackground(new Color(60, 60, 60));
        fileTable.setForeground(Color.WHITE);
        fileTable.setGridColor(new Color(100, 100, 100));
        fileTable.setRowHeight(25);
        fileTable.getTableHeader().setBackground(new Color(40, 40, 40));
        fileTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane tableScroll = new JScrollPane(fileTable);
        tableScroll.getViewport().setBackground(new Color(60, 60, 60)); 
        add(tableScroll, BorderLayout.CENTER);

        // 3. BOTTOM: Logs
        logArea.setEditable(false);
        logArea.setFont(Style.DATA_FONT);
        logArea.setBackground(new Color(10, 10, 10));
        logArea.setForeground(new Color(0, 255, 255)); 
        
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "P2P Network Logs", 0,0, Style.NORM_FONT, Style.TEXT_GRAY));
        add(logScroll, BorderLayout.SOUTH);

        startMyP2PServer();

        btnUpload.addActionListener(e -> uploadFile());
        btnRefresh.addActionListener(e -> fetchFileList());
        btnDownload.addActionListener(e -> downloadFile());
    }

    private void startMyP2PServer() {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(0)) { 
                myP2PPort = server.getLocalPort();
                log("✅ My P2P Server started on Port: " + myP2PPort);
                while (true) {
                    Socket s = server.accept();
                    new Thread(() -> handlePeerDownload(s)).start();
                }
            } catch (IOException e) { log("❌ P2P Server Error: " + e.getMessage()); }
        }).start();
    }

    private void handlePeerDownload(Socket s) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             OutputStream out = s.getOutputStream()) {
            String req = in.readLine();
            if (req != null && req.startsWith("GET::")) {
                String fileName = req.split("::")[1];
                File f = mySharedFiles.get(fileName);
                if (f != null && f.exists()) {
                    log("📤 Peer downloading: " + fileName);
                    FileInputStream fis = new FileInputStream(f);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    fis.close();
                    log("✅ Transfer Complete: " + fileName);
                }
            }
        } catch (Exception e) { log("❌ Transfer Failed"); }
    }

    private void uploadFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            
            // --- NEW: ASK FOR RECIPIENT ---
            String target = JOptionPane.showInputDialog(this, "Who is this file for?\n(Leave EMPTY for Everyone)", "Private Transfer", JOptionPane.QUESTION_MESSAGE);
            if (target == null) return; // Cancelled
            if (target.trim().isEmpty()) target = "ALL";
            
            mySharedFiles.put(f.getName(), f); 
            
            try (Socket s = new Socket("localhost", 5000);
                 PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
                
                // CMD: P2P_REGISTER::FileName::Owner::Port::TargetUser
                out.println("P2P_REGISTER::" + f.getName() + "::" + HostelClientApp.CURRENT_USER + "::" + myP2PPort + "::" + target.trim());
                
                log("📢 Shared: " + f.getName() + " (For: " + target + ")");
                fetchFileList(); 
            } catch (Exception ex) { log("❌ Connection Error"); }
        }
    }

    private void fetchFileList() {
        try (Socket s = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            
            // CMD: P2P_LIST::MyUsername
            out.println("P2P_LIST::" + HostelClientApp.CURRENT_USER);
            
            String resp = in.readLine(); 
            if (resp != null && resp.startsWith("P2P_FILES::")) {
                tableModel.setRowCount(0); 
                if (resp.length() > 11) {
                    String[] files = resp.substring(11).split("\\|\\|");
                    for (String f : files) {
                        // Data: FileName::Owner::IP::Port::Target
                        String[] parts = f.split("::");
                        tableModel.addRow(parts);
                    }
                }
            }
        } catch (Exception ex) { log("❌ Fetch Error"); }
    }

    private void downloadFile() {
        int row = fileTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a file first!"); return; }
        
        String fileName = (String) tableModel.getValueAt(row, 0);
        String ip = (String) tableModel.getValueAt(row, 2);
        int port = Integer.parseInt((String) tableModel.getValueAt(row, 3));
        
        log("⬇️ Connecting to Peer " + ip + ":" + port + "...");

        new Thread(() -> {
            try (Socket s = new Socket(ip, port);
                 PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                 InputStream in = s.getInputStream()) {
                out.println("GET::" + fileName);
                File downloadDir = new File("Downloads");
                if (!downloadDir.exists()) downloadDir.mkdir();
                File outFile = new File(downloadDir, "Downloaded_" + fileName);
                FileOutputStream fos = new FileOutputStream(outFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) fos.write(buffer, 0, bytesRead);
                fos.close();
                log("✅ Saved to: " + outFile.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "File Downloaded!");
            } catch (Exception ex) { log("❌ Download Failed: Peer might be offline."); }
        }).start();
    }

    private void log(String msg) { SwingUtilities.invokeLater(() -> logArea.append(msg + "\n")); }
}