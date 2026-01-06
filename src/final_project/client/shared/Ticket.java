package final_project.client.shared;

import java.io.Serializable;

public class Ticket implements Serializable, Comparable<Ticket> {
    private static int ID_COUNTER = 1000;
    private String id;
    private String room;
    private String category;
    private String description;
    private String status;
    private String remarks;
    private long timestamp;

    public Ticket(String room, String category, String description) {
        this.id = "T-" + (ID_COUNTER++);
        this.room = room;
        this.category = category;
        this.description = description;
        this.status = "OPEN";
        this.remarks = "Pending review";
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getRoom() { return room; }
    public void setStatus(String s) { this.status = s; }
    public void setRemarks(String r) { this.remarks = r; }

    @Override
    public String toString() {
        return String.format("[%s] Room %s | %s\nDesc: %s\nStatus: %s\nRemarks: %s\n-------------------------------", 
               id, room, category, description, status, remarks);
    }

    @Override
    public int compareTo(Ticket o) {
        return Long.compare(o.timestamp, this.timestamp);
    }
}