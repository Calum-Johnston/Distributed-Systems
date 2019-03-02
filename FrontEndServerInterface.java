import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FrontEndServerInterface extends Remote {
    public int retrieveRating(String movie) throws RemoteException;
    public void submitRating(String movie, int rating) throws RemoteException;
    public void updateRating(String movie, int rating) throws RemoteException;
}
