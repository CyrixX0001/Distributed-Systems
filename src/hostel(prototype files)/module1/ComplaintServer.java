package hostel.module1;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ComplaintServer {
    private static final int PORT = 5000;
    public static PriorityBlockingQueue<Ticket> ticketQueue = new PriorityBlockingQueue<>();

    public static void main(String[] args) {
        System.out.println(">>> SOCKET SERVER STARTED ON PORT " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) new Thread(new ClientHandler(serverSocket.accept())).start();
        } catch (IOException e) { e.printStackTrace(); }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    public ClientHandler(Socket socket) { this.socket = socket; }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("REQ: " + request);
                String[] parts = request.split("::");
                if (parts[0].equals("LODGE")) {
                    Ticket t = new Ticket(parts[1], parts[2], parts[3]);
                    ComplaintServer.ticketQueue.add(t);
                    out.println("SUCCESS::Ticket Created ID: " + t.getId());
                } else if (parts[0].equals("VIEW_ALL")) {
                    StringBuilder sb = new StringBuilder("LIST::");
                    for (Ticket t : ComplaintServer.ticketQueue) sb.append(t.toString()).append("||");
                    out.println(sb.toString());
                }
            }
        } catch (IOException e) { System.out.println("Client Disconnected"); }
    }
}