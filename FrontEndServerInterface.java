import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FrontEndServerInterface extends Remote {
    public void submitRating(String movie, int rating) throws RemoteException;
	public int getRating(String movie) throws RemoteException;
}
