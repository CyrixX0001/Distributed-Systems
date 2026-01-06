package final_project.server;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.util.concurrent.*;
import final_project.client.shared.Ticket;

public class HostelServer {
    public static PriorityBlockingQueue<Ticket> tickets = new PriorityBlockingQueue<>();
    private static HashMap<String, String> userDB = new HashMap<>();
    private static List<String> p2pRegistry = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        setupDatabase();
        System.out.println(">>> HOSTEL SERVER ONLINE");
        System.out.println(">>> Loaded " + userDB.size() + " users (50 Students + Wardens).");
        
        new Thread(HostelServer::startSocketServer).start(); 
        new Thread(NoticeService::start).start();            
        startRMIService();                                   
    }

    private static void setupDatabase() {
        // --- 1. WARDENS & ADMIN ---
        // We have Admin + 2 Specific Wardens as requested
        userDB.put("admin",   "admin::warden::OFFICE");
        userDB.put("warden1", "warden1::warden::OFFICE"); // Password is "warden1"
        userDB.put("warden2", "warden2::warden::OFFICE"); // Password is "warden2"
        
        // Legacy Test Users (Optional, good for quick testing)
        userDB.put("alice", "123::student::101");
        userDB.put("bob",   "123::student::102");

        // --- 2. GENERATE 50 INDIAN STUDENTS (Rooms 101-110) ---
        // Now 5 Students per Room to fit 50 people in 10 rooms.
        String[] names = {
            // Room 101 (5 students)
            "Aarav", "Vihaan", "Aditya", "Sai", "Vikram",
            // Room 102
            "Arjun", "Reyansh", "Muhammad", "Rohan", "Rahul",
            // Room 103
            "Krishna", "Ishaan", "Shaurya", "Atharva", "Nikhil",
            // Room 104
            "Neel", "Kabir", "Rudra", "Ayaan", "Om",
            // Room 105
            "Dhruv", "Siddharth", "Jai", "Arav", "Yash",
            // Room 106
            "Ananya", "Diya", "Saanvi", "Aadhya", "Sneha",
            // Room 107
            "Kiara", "Fatima", "Pari", "Myra", "Pooja",
            // Room 108
            "Riya", "Anvi", "Sarah", "Prisha", "Tanvi",
            // Room 109
            "Amaya", "Kavya", "Zara", "Meera", "Roshni",
            // Room 110
            "Naira", "Aditi", "Jiya", "Sana", "Simran"
        };

        int nameIdx = 0;
        for (int room = 101; room <= 110; room++) {
            for (int bed = 0; bed < 5; bed++) { // Increased to 5 per room
                if (nameIdx >= names.length) break;
                
                String rawName = names[nameIdx++];
                String username = rawName.toLowerCase(); 
                String password = username + "123"; // e.g., "vikram123"
                
                // Save: "password::role::room"
                userDB.put(username, password + "::student::" + room);
            }
        }
    }

    private static void startRMIService() {
        try {
            LocateRegistry.createRegistry(1099);
            Naming.rebind("MessService", new MessServiceImpl());
            System.out.println("[Module 5] RMI Registry started on Port 1099");
        } catch (Exception e) { System.err.println("RMI Error: " + e.getMessage()); }
    }

    private static void startSocketServer() {
        try (ServerSocket server = new ServerSocket(5000)) {
            while (true) {
                Socket s = server.accept();
                new Thread(() -> handleClient(s)).start();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static void handleClient(Socket s) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
            
            String req;
            while ((req = in.readLine()) != null) {
                if (req.startsWith("LOGIN")) {
                    String[] p = req.split("::");
                    if (userDB.containsKey(p[1]) && userDB.get(p[1]).startsWith(p[2] + "::")) {
                        out.println("SUCCESS::" + userDB.get(p[1]).split("::")[1] + "::" + userDB.get(p[1]).split("::")[2]);
                    } else out.println("FAIL::Invalid");
                } 
                else if (req.startsWith("GET_RESIDENTS")) {
                    String roomTarget = req.split("::")[1];
                    StringBuilder sb = new StringBuilder("RESIDENTS::");
                    for (Map.Entry<String, String> entry : userDB.entrySet()) {
                        String[] parts = entry.getValue().split("::");
                        if (parts.length >= 3 && parts[2].equals(roomTarget)) {
                            sb.append(entry.getKey()).append(", ");
                        }
                    }
                    out.println(sb.toString());
                }
                else if (req.startsWith("LODGE")) {
                    String[] p = req.split("::");
                    tickets.add(new Ticket(p[1], p[2], p[3]));
                    out.println("SUCCESS: Ticket Logged");
                } 
                else if (req.startsWith("VIEW")) {
                    String[] p = req.split("::");
                    String filter = (p.length > 1) ? p[1] : "ALL";
                    StringBuilder sb = new StringBuilder("LIST::");
                    for (Ticket t : tickets) {
                        if (filter.equals("ALL") || t.getRoom().equals(filter)) 
                            sb.append(t.toString()).append("||");
                    }
                    out.println(sb.toString());
                }
                else if (req.startsWith("UPDATE")) {
                    String[] p = req.split("::");
                    boolean found = false;
                    for(Ticket t : tickets) {
                        // Handle both ID formats (T-1000 or 1000)
                        if(t.getId().equals(p[1]) || t.getId().equals("T-" + p[1])) {
                            t.setStatus(p[2]); 
                            t.setRemarks(p[3]);
                            if (p.length > 4) t.setResolvedBy(p[4]); // Save Warden Name
                            found = true; break;
                        }
                    }
                    out.println(found ? "SUCCESS" : "FAIL");
                }
                // --- PRIVATE P2P LOGIC ---
                else if (req.startsWith("P2P_REGISTER")) {
                    String[] p = req.split("::");
                    String target = (p.length > 4) ? p[4] : "ALL";
                    p2pRegistry.add(p[1] + "::" + p[2] + "::" + s.getInetAddress().getHostAddress() + "::" + p[3] + "::" + target);
                    out.println("REGISTERED");
                }
                else if (req.startsWith("P2P_LIST")) {
                    String[] p = req.split("::");
                    String requester = (p.length > 1) ? p[1] : "guest";
                    StringBuilder sb = new StringBuilder("P2P_FILES::");
                    for(String f : p2pRegistry) {
                        String[] parts = f.split("::");
                        String target = (parts.length > 4) ? parts[4] : "ALL";
                        // Show if public OR specifically for this requester
                        if (target.equalsIgnoreCase("ALL") || target.equalsIgnoreCase(requester)) {
                            sb.append(f).append("||");
                        }
                    }
                    out.println(sb.toString());
                }
            }
        } catch (Exception e) {}
    }
}