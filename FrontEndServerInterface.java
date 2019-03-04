import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FrontEndServerInterface extends Remote {
    public int retrieveRating(String movie, String server) throws RemoteException;
    public void updateRating(String movie, int rating, String server) throws RemoteException;
}
