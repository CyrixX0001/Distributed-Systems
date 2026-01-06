package hostel.module2;
import java.rmi.Naming;
import javax.swing.*;

public class RMIClient {
    public static void main(String[] args) {
        String room = JOptionPane.showInputDialog("Enter Room Number:");
        if (room != null) {
            try {
                RoomInterface service = (RoomInterface) Naming.lookup("rmi://localhost/HostelRoomService");
                JOptionPane.showMessageDialog(null, service.getRoomDetails(room));
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}