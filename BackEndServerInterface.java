import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BackEndServerInterface extends Remote {    
    public String getStatus() throws RemoteException;
    public int retrieveRating(String movie, int[] prev_frontEnd, int updateID) throws RemoteException;
    public void updateRating(String movie, int rating, int[] prev_frontEnd, int updateID) throws RemoteException;
}