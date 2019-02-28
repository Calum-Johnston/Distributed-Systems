import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
	
public class Server  implements ServerInterface {

	private String myString = "";

	// Default Constructor
    public Server() { }

	// Implement methods from ServerInterface;
    public void setString(String s) {
		this.myString = s;
    }
	
	public String getString() {
		return myString;
	}
	
	// Main method: Instatntiates and registers an instance of the 
	// Server with the rmi registry
    public static void main(String args[]) {
		try {
			// Defines the server name
			String name = "Server";
			
			// Create server object
			Server obj = new Server();

			// Create remote object stub from server object
			ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(obj, 0);

			// Get registry
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1010);

			// Bind the remote object's stub in the registry
			registry.rebind(name, stub);

			// Write ready message to console
			System.err.println("Server ready");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
    }
}
