import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FrontEndServerInterface extends Remote {
    public void submitRating(String movie, int rating);
	public int getRating(String movie);
}
