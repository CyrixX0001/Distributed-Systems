package hostel.module2;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class RoomServer extends UnicastRemoteObject implements RoomInterface {
    private static Map<String, String> roomDB = new HashMap<>();

    protected RoomServer() throws RemoteException {
        super();
        roomDB.put("101", "Occupants: Alice, Bob | Warden: Dr. Smith");
        roomDB.put("102", "Occupants: Charlie | Warden: Dr. Doe");
    }

    @Override
    public String getRoomDetails(String roomNumber) throws RemoteException {
        return roomDB.getOrDefault(roomNumber, "Room Not Found");
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            Naming.rebind("HostelRoomService", new RoomServer());
            System.out.println(">>> RMI SERVER READY ON PORT 1099");
        } catch (Exception e) { e.printStackTrace(); }
    }
}