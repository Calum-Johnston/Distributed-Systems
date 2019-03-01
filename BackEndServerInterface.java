import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BackEndServerInterface extends Remote {
    public String getStatus();
	public void submitRating(String movie, int rating);
    public int getRating(String movie);
}
