import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface BackEndServerInterface extends Remote {    
    public String getServerStatus() throws RemoteException;
    public queryReturn retrieveRating(queryRequest query) throws RemoteException;
    public int[] updateRating(updateRequest update) throws RemoteException;

    public ArrayList<logRecord> getLogRecord() throws RemoteException;
    public int[] getReplice_Timestamp() throws RemoteException;
    public int getServerNumber() throws RemoteException;
}