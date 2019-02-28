import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    public String getString() throws RemoteException;
	public void setString(String s) throws RemoteException;
}
