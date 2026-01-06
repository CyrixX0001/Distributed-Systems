package final_project.server;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.nio.charset.StandardCharsets;

public class NoticeService {
    // RAM MEMORY
    private static List<String> memoryNotices = new CopyOnWriteArrayList<>();

    public static void start() {
        // --- CLEANED: NO DUMMY NOTICES ---
        // memoryNotices.add("Welcome...");  <-- REMOVED

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            
            // --- VIEW NOTICES ---
            server.createContext("/notices", ex -> {
                StringBuilder sb = new StringBuilder();
                sb.append("=== 📢 HOSTEL NOTICE BOARD ===\n\n");
                
                if (memoryNotices.isEmpty()) {
                    sb.append("(No notices posted yet)");
                } else {
                    for (int i = memoryNotices.size() - 1; i >= 0; i--) {
                        sb.append(" • ").append(memoryNotices.get(i)).append("\n\n");
                    }
                }
                
                String response = sb.toString();
                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                ex.sendResponseHeaders(200, responseBytes.length); 
                OutputStream os = ex.getResponseBody();
                os.write(responseBytes);
                os.close();
            });

            // --- ADD NOTICE ---
            server.createContext("/add", ex -> {
                String q = ex.getRequestURI().getQuery();
                if (q != null && q.startsWith("msg=")) {
                    String msg = q.split("=")[1].replace("_", " ");
                    memoryNotices.add(msg + "  [" + new java.util.Date() + "]");
                    
                    String resp = "Saved!";
                    byte[] respBytes = resp.getBytes(StandardCharsets.UTF_8);
                    
                    ex.sendResponseHeaders(200, respBytes.length);
                    OutputStream os = ex.getResponseBody();
                    os.write(respBytes);
                    os.close();
                }
            });
            
            server.start();
            System.out.println("[Module 3] REST Server running on Port 8000 (Clean Mode)");
        } catch (Exception e) { e.printStackTrace(); }
    }
}