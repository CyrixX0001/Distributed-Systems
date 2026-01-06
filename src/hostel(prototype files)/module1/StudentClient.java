package hostel.module1;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class StudentClient extends JFrame {
    private JTextField txtRoom = new JTextField(10);
    private JComboBox<String> cmbCategory = new JComboBox<>(new String[]{"Water", "Electricity", "Cleanliness"});
    private JTextArea txtDesc = new JTextArea(5, 20);
    private JButton btnSubmit = new JButton("Lodge Complaint");
    private JTextArea txtLog = new JTextArea(10, 30);

    public StudentClient() {
        super("Student Client");
        setLayout(new FlowLayout());
        add(new JLabel("Room:")); add(txtRoom);
        add(new JLabel("Cat:")); add(cmbCategory);
        add(new JScrollPane(txtDesc));
        add(btnSubmit);
        add(new JScrollPane(txtLog));
        
        btnSubmit.addActionListener(e -> send());
        setSize(400, 500); setVisible(true); setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void send() {
        try (Socket s = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            
            out.println("LODGE::" + txtRoom.getText() + "::" + cmbCategory.getSelectedItem() + "::" + txtDesc.getText());
            txtLog.append("Server: " + in.readLine() + "\n");
        } catch (IOException ex) { txtLog.append("Error: " + ex.getMessage() + "\n"); }
    }
    public static void main(String[] args) { new StudentClient(); }
}