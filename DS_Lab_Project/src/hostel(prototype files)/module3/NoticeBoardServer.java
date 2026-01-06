package hostel.module3;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class NoticeBoardServer {
    private static List<String> notices = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        
        server.createContext("/notices", exchange -> {
            StringBuilder r = new StringBuilder("<html><body><h2>Notices</h2><ul>");
            for (String n : notices) r.append("<li>").append(n).append("</li>");
            r.append("</ul></body></html>");
            send(exchange, r.toString());
        });

        server.createContext("/add", exchange -> {
            String q = exchange.getRequestURI().getQuery();
            if (q != null && q.startsWith("msg=")) {
                notices.add(q.split("=")[1].replace("_", " "));
                send(exchange, "Added!");
            } else send(exchange, "Error");
        });

        server.start();
        System.out.println(">>> REST API STARTED: http://localhost:8000/notices");
    }

    private static void send(HttpExchange ex, String resp) throws IOException {
        ex.sendResponseHeaders(200, resp.length());
        OutputStream os = ex.getResponseBody();
        os.write(resp.getBytes());
        os.close();
    }
}