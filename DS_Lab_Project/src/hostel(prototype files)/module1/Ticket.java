package hostel.module1;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Ticket implements Serializable, Comparable<Ticket> {
    private static final long serialVersionUID = 1L;
    public enum Priority { HIGH, MEDIUM, LOW }
    public enum Status { OPEN, IN_PROGRESS, RESOLVED }

    private String id;
    private String roomNumber;
    private String category;
    private String description;
    private Priority priority;
    private Status status;
    private LocalDateTime timestamp;

    public Ticket(String roomNumber, String category, String description) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.roomNumber = roomNumber;
        this.category = category;
        this.description = description;
        this.status = Status.OPEN;
        this.timestamp = LocalDateTime.now();
        this.priority = calculatePriority(category);
    }

    private Priority calculatePriority(String cat) {
        if (cat.equalsIgnoreCase("Water") || cat.equalsIgnoreCase("Electricity")) return Priority.HIGH;
        if (cat.equalsIgnoreCase("Cleanliness")) return Priority.MEDIUM;
        return Priority.LOW;
    }

    @Override
    public int compareTo(Ticket other) {
        int p = this.priority.compareTo(other.priority);
        if (p != 0) return p;
        return this.timestamp.compareTo(other.timestamp);
    }

    @Override
    public String toString() {
        return String.format("[%s] ID:%s | Room:%s | %s | Status:%s", priority, id, roomNumber, category, status);
    }
    public String getId() { return id; }
}