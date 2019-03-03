import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
	
public class BackEndServer1 implements BackEndServerInterface {

	/* 
	============================================
	Variable definitions
	============================================
	*/  

	// Variables relating to the server
	private String status;
	private String serverName;

	// Variables relating to Gossip Architecture
	Map<String, Integer> movieRatings;  // Holds the data
	int[] value_Timestamp;  // Represents updates present in movieRating
	ArrayList<logRecord> logRecords = new ArrayList<logRecord>();  // Represents all updates recieved
	int[] replica_Timestamp;  // Represents updares accepted by RM (might not yet be processed)  
	ArrayList<Integer> operations = new ArrayList<Integer>();  // Contains a list of updates that have been applied
	int serverNum;  // Stores the server number





	/* 
	============================================
	Default Constructor
	============================================
	*/  
	public BackEndServer1(String name) {
		movieRatings = new HashMap<String, Integer>();
		value_Timestamp = new int[3];
		replica_Timestamp = new int[3];
		serverNum = 0;
		status = "active";
		serverName = name;
	}	 
	




	/* 
	============================================
	Methods that will be remotely invoked
	============================================
	*/  
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
			getGossip();
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
				// Update value_timestamp
				for(int a = 0; a < value_Timestamp.length; a++) {
					if(value_Timestamp[a] < ts[a]) {
						value_Timestamp[a] = ts[a];
					}
				}
				// Added ID to executed table list
				operations.add(update.getupdateID());
			}

			// If not applied, means there has been an update elsewhere
			// that has not been recieved by RM

		}

		return ts;
	}
	




	/* 
	============================================
	Methods relating to gossip messaging
	============================================
	*/  
	
	// Returns the log record
	public ArrayList<logRecord> getLogRecord(){
		return logRecords;
	}

	// Returns the replica timestamp
	public int[] getReplace_Timestamp(){
		return replica_Timestamp;
	}

	// Method gets data from other RM machines to synchronise with
	public void getGossip(){
		try{
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 8043);
			String[] serverList = registry.list();
			for(String registryServerName : serverList){
				if(!(registryServerName.equals(serverName))){
					BackEndServerInterface stub = (BackEndServerInterface) registry.lookup(serverName);
					ArrayList<logRecord> temp_Record = stub.getLogRecord();
					int[] temp_replica = stub.getReplace_Timestamp();
					applyGossip(temp_Record, temp_replica);
				}
			}
		} catch (Exception e){
			System.err.println("Exception in locating server: " + e.toString());
			e.printStackTrace();
		}
	}

	// Method sends current data to other RM machines to synchronise
	public void sendGossip() {
		try{
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 8043);
			String[] serverList = registry.list();
			for(String registryServerName : serverList){
				if(!(registryServerName.equals(serverName))){
					BackEndServerInterface stub = (BackEndServerInterface) registry.lookup(serverName);
					stub.applyGossip(logRecords, replica_Timestamp);
				}
			}
		} catch (Exception e){
			System.err.println("Exception in locating server: " + e.toString());
			e.printStackTrace();
		}
	}

	// Method takes data from another RM and
	public void applyGossip(ArrayList<logRecord> incoming_logRecords, int[] incoming_replica_Timestamp) {
		// Merge arriving log with own log
		// 1. Add record from incoming_logRecords if record timestamp is less than current replica_Timestamp
		for(logRecord log : incoming_logRecords) {
			boolean addLog = false;
			for(int a = 0; a < replica_Timestamp.length; a++) {
				if(log.getTS()[a] > replica_Timestamp[a]){
					addLog = true;
				}
			}
			if(addLog = true){
				logRecords.add(log);
			}
		}

		// 2. RM merges replica_timestamp with incoming timestamp
		for(int a = 0; a < incoming_replica_Timestamp.length; a++) {
			if(replica_Timestamp[a] < incoming_replica_Timestamp[a]){
				replica_Timestamp[a] = incoming_replica_Timestamp[a];
			}
		}

		// Apply updates that have become stable and not yet executed
		ArrayList<logRecord> stableUpdates = new ArrayList<logRecord>();

		// 3: Loop through RM log of updates and add stable ones
		for(logRecord log : logRecords){
			// If update has not already been applied
			if(!(operations.contains(log.getUpdate().getupdateID()))) {
				// If the update is stable
				boolean stable = true;
				for(int a = 0; a < value_Timestamp.length; a++){
					if(log.getUpdate().getPrev()[a] > value_Timestamp[a]) {
						stable = false;
					}
				}
				if(stable == true){
					stableUpdates.add(log);
				}
			}
		}		
		
		// 4: Order the logs into suitable execution order (NOT IMPLEMENTED)
		/*for(int a = 0; a < stableUpdates.size(); a++){
			for(int b = 0; b < stableUpdates.size() - a; b++) {
				for(int c = 0; c < )
			}
		}*/

		// 5: Apply the stable updates
		for(logRecord log : stableUpdates){
			movieRatings.put(log.getUpdate().getMovie(), log.getUpdate().getRating());
			// Update value_timestamp
			for(int a = 0; a < value_Timestamp.length; a++) {
				if(value_Timestamp[a] < log.getTS()[a]) {
					value_Timestamp[a] = log.getTS()[a];
				}
			}
			// Added ID to executed table list
			operations.add(log.getUpdate().getupdateID());
		}

		// Eliminate records from log and executed table 
		// when they have been known to be applied everywhere (NOT IMPLEMENTED)
	}





	/* 
	============================================
	Method that creates an instance of the front end object
	============================================
	*/  
    public static void main(String args[]) { 
			try {
				// Defines the server name
				String name = "backEnd1";
				
				// Create server object
				BackEndServer1 obj = new BackEndServer1(name);

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
