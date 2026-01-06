package final_project.client.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RoomInterface extends Remote {
    String getRoomDetails(String roomNumber) throws RemoteException;
}