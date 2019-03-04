import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
	
public class BackEndServer2 implements BackEndServerInterface {

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
	public BackEndServer2(String name) {
		movieRatings = new HashMap<String, Integer>();
		value_Timestamp = new int[3];
		replica_Timestamp = new int[3];
		serverNum = 1;
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

	public queryReturn retrieveRating(queryRequest query){
		// Compare timestamp prev and value_timestamp
		boolean applyQuery = true;
		for(int a = 0; a < query.getPrev().length; a++){
			if(query.getPrev()[a] > value_Timestamp[a]){
				applyQuery = false;
			}
		}

		queryReturn returnQ;

		if(applyQuery == true) {
			returnQ = new queryReturn(movieRatings.get(query.getMovie()), null, false);
		}else {
			requestAllGossipData();
			returnQ = new queryReturn(movieRatings.get(query.getMovie()), value_Timestamp, true);
		}
		return returnQ;
	}

	public int[] updateRating(updateRequest update) {

		// ts is a unique timestamp the RM assigns to the update
		int[] ts = update.getPrev().clone();

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

			System.out.println(applyUpdate);

			// Gets updates if needed
			if(applyUpdate == false) {
				requestAllGossipData();
			}

			// Add movie to movie rating (or update it)
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
	public void requestAllGossipData(){
		try{
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 8043);
			String[] serverList = registry.list();
			for(String registryServerName : serverList){
				if(!(registryServerName.equals(serverName)) && !(registryServerName.equals("frontEnd"))){
					BackEndServerInterface stub = (BackEndServerInterface) registry.lookup(registryServerName);
					ArrayList<logRecord> temp_Record = stub.getLogRecord();
					int[] temp_replica = stub.getReplace_Timestamp();
					updateLogs(temp_Record, temp_replica);
				}
			}
		} catch (Exception e){
			System.err.println("Exception in locating server: " + e.toString());
			e.printStackTrace();
		}
		orderLogs();
		addStableUpdates();
		for(Map.Entry<String, Integer> entry : movieRatings.entrySet()){
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}	
	}

	// Method takes data from another RM and
	public void updateLogs(ArrayList<logRecord> incoming_logRecords, int[] incoming_replica_Timestamp) {

		// Merge arriving log from RM with current log
		for(logRecord log : incoming_logRecords) {
			boolean addLog = false;

			// If log's TS is greater than the replica's TS than add the log
			for(int a = 0; a < replica_Timestamp.length; a++) {
				if(log.getTS()[a] > replica_Timestamp[a]){
					addLog = true;
				}
			}

			// Add the log to the logRecords
			if(addLog = true){
				logRecords.add(log);
			}
		}

		// Merges replica_timestamp with incoming timestamp 
		for(int a = 0; a < incoming_replica_Timestamp.length; a++) {
			if(replica_Timestamp[a] < incoming_replica_Timestamp[a]){
				replica_Timestamp[a] = incoming_replica_Timestamp[a];
			}
		}
	}

	public void orderLogs() {
		for(int a = 0; a < logRecords.size(); a++) {
			for(int b = 1; b < (logRecords.size() - a); b++){
				int[] ts_a = logRecords.get(b - 1).getTS();
				int[] ts_b = logRecords.get(b).getTS();
				boolean less = false;
				boolean more = false;

				for(int i = 0; i < ts_a.length; i++){
					if(ts_a[i] > ts_b[i]){
						more = true;
					}else if(ts_a[i] < ts_b[i]){
						less = true;
					}
				}

				// Swap them 
				if(more == true && less == false){
					logRecord temp = logRecords.get(b - 1);
					logRecords.set(b - 1, logRecords.get(b));
					logRecords.set(b, temp);
				}
			}
		}
	}

	// Apply updates that have become stable and not yet executed
	public void addStableUpdates() {

		// Loop through RM log of updates and update stable ones
		for(logRecord log : logRecords){

			// If update has not already been applied
			if(!(operations.contains(log.getUpdate().getupdateID()))) {
				boolean stable = true;

				// Check is update is stable
				for(int a = 0; a < value_Timestamp.length; a++){
					if(log.getUpdate().getPrev()[a] > value_Timestamp[a]) {
						stable = false;
					}
				}

				// If the update is stable
				if(stable == true){
					applyStableUpdate(log);
				}
			}
		}		

		// Eliminate records from log and executed table 
		// when they have been known to be applied everywhere (NOT IMPLEMENTED)
	}

	public void applyStableUpdate(logRecord log) {
		// Put data in movie rating
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





	/* 
	============================================
	Method that creates an instance of the front end object
	============================================
	*/  
    public static void main(String args[]) { 
			try {
				// Defines the server name
				String name = "backEnd2";
				
				// Create server object
				BackEndServer2 obj = new BackEndServer2(name);

				// Create remote object stub from server object
				BackEndServerInterface stub = (BackEndServerInterface) UnicastRemoteObject.exportObject(obj, 0);

				// Get registry
				Registry registry = LocateRegistry.getRegistry("127.0.0.1", 8043);

				// Bind the remote object's stub in the registry
				registry.rebind(name, stub);

				// Write ready message to console
				System.err.println("Back End Server 2 ==== READY");


			} catch (Exception e) {
				System.err.println("Back End Server 2 exception: " + e.toString());
				e.printStackTrace();
			}
    }
}
