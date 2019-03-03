import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
	
public class BackEndServer1 implements BackEndServerInterface {

	// Variables relating to the server
	private String status;

	// Variables relating to Gossip ArchitAdecture
	Map<String, Integer> movieRatings;  // Acts as the value
	int[] value_Timestamp;  // Acts as value timestamp 
	int[] replica_Timestamp;  //  
	ArrayList<Integer> operations = new ArrayList<Integer>();  //Executed operations table (contains list of update IDs)
	int serverNum = 0;

	// Default Constructor
	public BackEndServer1() {
		movieRatings = new HashMap<String, Integer>();
		value_Timestamp = new int[3];
		replica_Timestamp = new int[3];
		status = "active";
	}	 
	

	// Methods that will be remotely invoked
	public String getStatus(){
		return status;
	}

	public int retrieveRating(String movie, int[] prev_frontEnd, int updateID){
		return movieRatings.get(movie);
	}

	public void updateRating(String movie, int rating, int[] prev_frontEnd, int updateID){
		
		// Operation not yet applied
		if(!(operations.contains(updateID))){ 

			// Increment replace timestamp for server
			// Counts number of updates recieved from FE
			replica_Timestamp[serverNum] += 1;  

			// Create log record
			//(make new class)
		}
	    movieRatings.put(movie, rating);
	}
	



	// Main method: 
	// Instatntiates and registers an instance of the server with the rmi registry
    public static void main(String args[]) { 
			try {
				// Defines the server name
				String name = "backEnd1";
				
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
