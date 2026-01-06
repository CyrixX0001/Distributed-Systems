package hostel.module2;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RoomInterface extends Remote {
    String getRoomDetails(String roomNumber) throws RemoteException;
}