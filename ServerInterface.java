import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    String sayHello() throws RemoteException;
}
