import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
	
public class BackEndServer1 implements BackEndServerInterface {

	private String myString = "";
	private String status = "active";

	// Default Constructor
  public BackEndServer1() { }

	// Implement methods from ServerInterface;
  public void setString(String s) {
		this.myString = s;
  }
	
	public String getString() {
		return myString;
	}
	
	// Main method: 
	// Instatntiates and registers an instance of the server with the rmi registry
    public static void main(String args[]) { 
			try {
				// Defines the server name
				String name = "backEnd";
				
				// Create server object
				BackEndServer1 obj = new BackEndServer1();

				// Create remote object stub from server object
				BackEndServerInterface stub = (BackEndServerInterface) UnicastRemoteObject.exportObject(obj, 0);

				// Get registry
				Registry registry = LocateRegistry.getRegistry("127.0.0.1", 8043);

				// Bind the remote object's stub in the registry
				registry.rebind(name, stub);

				// Write ready message to console
				System.err.println("BACK END SERVER ==== READY");
				System.out.println(registry.list());


			} catch (Exception e) {
				System.err.println("Back End Server exception: " + e.toString());
				e.printStackTrace();
			}
    }
}
