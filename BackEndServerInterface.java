import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BackEndServerInterface extends Remote {    
    public String getStatus() throws RemoteException;
	public void submitRating(String movie, int rating) throws RemoteException;
    public int getRating(String movie) throws RemoteException;
}
