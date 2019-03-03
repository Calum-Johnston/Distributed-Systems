import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BackEndServerInterface extends Remote {    
    public String getStatus() throws RemoteException;
    public int retrieveRating(queryRequest query) throws RemoteException;
    public int[] updateRating(updateRequest update) throws RemoteException;
}