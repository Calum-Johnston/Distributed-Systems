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

	// Variables relating to Gossip Architecture
	Map<String, Integer> movieRatings;  // Holds the data
	int[] value_Timestamp;  // Represents updates present in movieRating
	ArrayList<logRecord> logRecords = new ArrayList<logRecord>();  // Represents all updates recieved
	int[] replica_Timestamp;  // Represents updares accepted by RM (might not yet be processed)  
	ArrayList<Integer> operations = new ArrayList<Integer>();  // Contains a list of updates that have been applied
	int serverNum;  // Stores the server number

	// Default Constructor
	public BackEndServer1() {
		movieRatings = new HashMap<String, Integer>();
		value_Timestamp = new int[3];
		replica_Timestamp = new int[3];
		serverNum = 0;
		status = "active";
	}	 
	
	// Methods that will be remotely invoked
	public String getStatus(){
		return status;
	}

	public int retrieveRating(queryRequest query){
		// Compare timestamp prev and value_timestamp
		boolean applyQuery = true;
		for(int a = 0; a < query.getPrev().length; a++){
			if(query.getPrev()[a] > value_Timestamp[a]){
				applyQuery = false;
			}
		}

		if(applyQuery == true) {
			return movieRatings.get(query.getMovie());
		}else {
			// Request gossip messages, then 
		}

		// return value_timestamp to merge, and result of queryy
		return 0;
	}

	public int[] updateRating(updateRequest update) {
		// ts is a unique timestamp the RM assigns to the update
		int[] ts = update.getPrev();
		
		// Checks if RM has already processed the request
		if(!(operations.contains(update.getupdateID()))){ 

			// Increment replace timestamp for server
			// Counts number of updates recieved from FE
			replica_Timestamp[serverNum] += 1;
		  
			// Create the log record
			ts[serverNum] = replica_Timestamp[serverNum];
			logRecord log = new logRecord(serverNum, ts, update);
			logRecords.add(log);

			// Checks whether it can add the update
			boolean applyUpdate = true;
			for(int a = 0; a < value_Timestamp.length; a++) {
				if(update.getPrev()[a] > value_Timestamp[a]) {
					applyUpdate = false;
				}
			}

			// Applies the update
			if(applyUpdate = true) {
				movieRatings.put(update.getMovie(), update.getRating());
			}

			// Update value_timestamp
			for(int a = 0; a < value_Timestamp.length; a++) {
				if(value_Timestamp[a] < ts[a]) {
					value_Timestamp[a] = ts[a];
				}
			}

			// Added ID to executed table list
			operations.add(update.getupdateID());
		}

		return ts;
	}
	



	// Methods relating to gossip messaging
	public void sendGossip() {
		// Sends logRecords and its replica_Timestamp
	}

	public void recieveGossip(ArrayList<logRecord> incoming_logRecords, int[] incoming_replica_Timestamp) {
		// Merge arriving log with own log
		// 1. Add record from incoming_logRecords if

		

		// Apply updates that have become stable and not yet executed

		// Eliminate records from log and executed table 
		// when they have been known to be applied everywhere
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
