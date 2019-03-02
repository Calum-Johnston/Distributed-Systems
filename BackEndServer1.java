import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
	
public class BackEndServer1 implements BackEndServerInterface {

	private String status = "active";
	Map<String, Integer> movieRatings = new HashMap<String, Integer>();

	// Default Constructor
	public BackEndServer1() { }
	
	public String getStatus(){
		return status;
	}

	public int retrieveRating(String movie){
		return movieRatings.get(movie);
	}

	public void submitRating(String movie, int rating){
		movieRatings.put(movie, rating);
	}

	public void updateRating(String movie, int rating){
		movieRatings.put(movie, rating);
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
				System.err.println("Back End Server ==== READY");


			} catch (Exception e) {
				System.err.println("Back End Server exception: " + e.toString());
				e.printStackTrace();
			}
    }
}
