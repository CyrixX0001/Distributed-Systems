package final_project.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import final_project.client.panels.*;

public class HostelClientApp extends JFrame {
    private CardLayout rootLayout = new CardLayout();
    private JPanel rootPanel = new JPanel(rootLayout);
    private JPanel contentArea = new JPanel(new CardLayout());
    private JPanel sidebar = new JPanel();
    
    // We need a reference to the login box to animate it
    private JPanel loginBox; 

    public static String CURRENT_USER = ""; 
    public static String CURRENT_ROLE = "";
    public static String CURRENT_ROOM = "";

    public HostelClientApp() {
        super("Hostel Assist | Full Screen");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        
        rootPanel.add(createLoginScreen(), "LOGIN");
        rootPanel.add(createDashboardScreen(), "DASHBOARD");
        add(rootPanel);
        setVisible(true);
    }

    private JPanel createLoginScreen() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Style.APP_BG);
        
        loginBox = new JPanel(new GridLayout(6, 1, 15, 15));
        loginBox.setBackground(Style.PANEL_BG);
        loginBox.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); 
        loginBox.setPreferredSize(new Dimension(500, 650)); 
        
        JLabel title = new JLabel("Hostel Login");
        title.setFont(new Font("SansSerif", Font.BOLD, 28)); 
        title.setForeground(Style.ACCENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Student", "Warden"});
        roleCombo.setBackground(Color.WHITE);
        roleCombo.setFont(Style.NORM_FONT);
        
        JTextField userField = new JTextField(); 
        Style.themeField(userField);
        userField.setBorder(BorderFactory.createTitledBorder(null, "Username", 0,0, Style.NORM_FONT, Color.WHITE));

        JPasswordField passField = new JPasswordField();
        Style.themeField(passField);
        passField.setBorder(BorderFactory.createTitledBorder(null, "Password", 0,0, Style.NORM_FONT, Color.WHITE));

        JButton loginBtn = Style.createButton("LOGIN");
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 16)); 

        loginBox.add(title);
        loginBox.add(roleCombo);
        loginBox.add(userField);
        loginBox.add(passField);
        loginBox.add(new JLabel("")); // Spacer
        loginBox.add(loginBtn);
        
        panel.add(loginBox);

        loginBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword()).trim();
            attemptLogin(u, p, (String)roleCombo.getSelectedItem());
        });
        
        return panel;
    }

    private void attemptLogin(String user, String pass, String selectedRole) {
        try (Socket s = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            
            out.println("LOGIN::" + user + "::" + pass);
            String resp = in.readLine(); 

            if (resp != null && resp.startsWith("SUCCESS")) {
                String[] parts = resp.split("::");
                if (!parts[1].equalsIgnoreCase(selectedRole)) {
                    JOptionPane.showMessageDialog(this, "Role Mismatch! You are a " + parts[1]); return;
                }
                CURRENT_USER = user; CURRENT_ROLE = parts[1]; CURRENT_ROOM = parts[2];
                rebuildSidebar();
                
                Component comp = findPanel("COMPLAINT");
                if (comp instanceof StudentPanel) ((StudentPanel) comp).updateRoom(CURRENT_ROOM);
                
                rootLayout.show(rootPanel, "DASHBOARD");
            } else {
                // --- TRIGGER ANIMATION ON FAILURE ---
                shakeComponent(loginBox);
                JOptionPane.showMessageDialog(this, "Invalid Username/Password");
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Server Offline"); }
    }

    // --- ANIMATION LOGIC ---
    private void shakeComponent(JComponent component) {
        final Point originalLocation = component.getLocation();
        final int shakeDistance = 10;
        final int shakeSpeed = 5; // Lower is faster

        Timer timer = new Timer(shakeSpeed, null);
        timer.addActionListener(new ActionListener() {
            int count = 0;
            int direction = 1;

            @Override
            public void actionPerformed(ActionEvent e) {
                int xOffset = direction * shakeDistance;
                component.setLocation(originalLocation.x + xOffset, originalLocation.y);
                direction = -direction;
                count++;

                if (count >= 10) { // Shake 10 times
                    component.setLocation(originalLocation);
                    timer.stop();
                }
            }
        });
        timer.start();
    }

    private void rebuildSidebar() {
        sidebar.removeAll();
        sidebar.setLayout(new GridLayout(10, 1, 5, 5));
        sidebar.setBackground(new Color(30, 30, 30));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); 
        sidebar.setPreferredSize(new Dimension(250, 0)); 
        
        JLabel brand = new JLabel("  " + CURRENT_USER.toUpperCase());
        brand.setFont(new Font("SansSerif", Font.BOLD, 20));
        brand.setForeground(Style.ACCENT);
        sidebar.add(brand);

        if (CURRENT_ROLE.equals("student")) {
            addNavButton("Lodge Complaint", "COMPLAINT");
            addNavButton("My Room Info", "ROOM");
        } else {
            addNavButton("Ticket Manager", "WARDEN");
            addNavButton("Lodge Complaint", "COMPLAINT");
            addNavButton("Room Database", "ROOM");
        }
        
        addNavButton("Notice Board", "NOTICE");
        addNavButton("Resource Share", "P2P");
        addNavButton("Mess Feedback", "MESS");

        JButton logout = Style.createNavButton("Logout");
        logout.setBackground(new Color(180, 50, 50));
        logout.addActionListener(e -> {
            CURRENT_USER = ""; rootLayout.show(rootPanel, "LOGIN");
        });
        sidebar.add(logout);
        
        sidebar.revalidate(); sidebar.repaint();
    }

    private void addNavButton(String text, String card) {
        JButton b = Style.createNavButton(text);
        b.addActionListener(e -> ((CardLayout)contentArea.getLayout()).show(contentArea, card));
        sidebar.add(b);
    }

    private JPanel createDashboardScreen() {
        JPanel d = new JPanel(new BorderLayout());
        contentArea.setBackground(Style.APP_BG);
        
        contentArea.add(new StudentPanel(), "COMPLAINT");
        contentArea.add(new RoomPanel(), "ROOM");
        contentArea.add(new NoticePanel(), "NOTICE");
        contentArea.add(new P2PPanel(), "P2P");
        contentArea.add(new MessPanel(), "MESS");
        contentArea.add(new WardenPanel(), "WARDEN");

        d.add(sidebar, BorderLayout.WEST);
        d.add(contentArea, BorderLayout.CENTER);
        return d;
    }
    
    private Component findPanel(String name) {
        for (Component c : contentArea.getComponents()) {
            if (c instanceof StudentPanel && name.equals("COMPLAINT")) return c;
        } return null;
    }

    public static void main(String[] args) { 
        System.setProperty("awt.useSystemAAFontSettings", "on");
        SwingUtilities.invokeLater(HostelClientApp::new); 
    }
}