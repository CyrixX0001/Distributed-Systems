package final_project.server;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import final_project.client.shared.RoomInterface;

public class RoomService extends UnicastRemoteObject implements RoomInterface {
    private Map<String, String> db = new HashMap<>();

    public RoomService() throws RemoteException {
        super();
        db.put("101", "Occupants: Alice & Bob | Warden: Dr. Smith");
        db.put("102", "Occupants: Charlie | Warden: Dr. Smith");
        db.put("201", "Occupants: Dave | Warden: Dr. Jones");
    }

    @Override
    public String getRoomDetails(String r) {
        return db.getOrDefault(r, "Room Not Found in Database.");
    }
}