package hostel.module4;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class P2PNode extends JFrame {
    private JTextArea txtLog = new JTextArea();
    private JTextField txtTargetPort = new JTextField("6000");
    private JTextField txtMsg = new JTextField("Hello");
    private int myPort;

    public P2PNode(int port) {
        super("P2P Node: " + port);
        this.myPort = port;
        setLayout(new FlowLayout());
        add(new JLabel("Target Port:")); add(txtTargetPort);
        add(txtMsg);
        JButton btn = new JButton("Send");
        add(btn); add(new JScrollPane(txtLog));
        
        btn.addActionListener(e -> send());
        new Thread(this::startServer).start();
        
        setSize(300, 400); setVisible(true); setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void startServer() {
        try (ServerSocket ss = new ServerSocket(myPort)) {
            txtLog.append("Listening on " + myPort + "\n");
            while (true) {
                Socket s = ss.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                txtLog.append("Received: " + in.readLine() + "\n");
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void send() {
        try (Socket s = new Socket("localhost", Integer.parseInt(txtTargetPort.getText()));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
            out.println(txtMsg.getText());
            txtLog.append("Sent: " + txtMsg.getText() + "\n");
        } catch (Exception e) { txtLog.append("Error sending\n"); }
    }

    public static void main(String[] args) {
        String p = JOptionPane.showInputDialog("Enter Port (e.g., 6000 or 6001):");
        new P2PNode(Integer.parseInt(p));
    }
}