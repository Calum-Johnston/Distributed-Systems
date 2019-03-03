import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface BackEndServerInterface extends Remote {    
    public String getStatus() throws RemoteException;
    public int retrieveRating(queryRequest query) throws RemoteException;
    public int[] updateRating(updateRequest update) throws RemoteException;
    public void applyGossip(ArrayList<logRecord> incoming_logRecords, int[] incoming_replica_Timestamp) throws RemoteException;

    public ArrayList<logRecord> getLogRecord() throws RemoteException;
    public int[] getReplace_Timestamp() throws RemoteException;
}