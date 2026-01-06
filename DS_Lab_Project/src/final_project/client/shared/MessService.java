package final_project.client.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

// The RMI Interface (Must extend Remote)
public interface MessService extends Remote {
    // 1. Get the Daily Menu
    String getDailyMenu() throws RemoteException;
    
    // 2. Submit Feedback
    String submitFeedback(String studentName, String rating, String comment) throws RemoteException;
    
    // 3. (For Warden) View all feedback
    List<String> getAllFeedback() throws RemoteException;
}