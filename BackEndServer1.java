import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
	
public class BackEndServer1 implements BackEndServerInterface {

	/* 
	============================================
	Variable definitions
	============================================
	*/  

	// Variables relating to the server
	private String serverStatus;
	private String serverName;

	// Variables relating to Gossip Architecture
	Map<String, Integer> movieRatings;  // Holds the data
	int[] backEndTS;  // Represents updates present in RM
	ArrayList<logRecord> logRecords = new ArrayList<logRecord>();  // Represents all updates recieved
	ArrayList<Integer> operations = new ArrayList<Integer>();  // Contains a list of updates that have been applied
	int[][] tableTS;  // Holds most recently accessed replica timestamps from other RM's and its own replica timestamp
	int serverNumber;  // Stores the server number





	/* 
	============================================
	Default Constructor
	============================================
	*/  
	public BackEndServer1(String name) {
		movieRatings = new HashMap<String, Integer>();
		backEndTS = new int[3];
		serverNumber = 0;
		serverStatus = "active";
		serverName = name;
		tableTS = new int[3][3];
		readMovieData();
	}	 
	




	/* 
	============================================
	Default Constructor
	============================================
	*/  

	public void readMovieData() {
		try(BufferedReader in  = new BufferedReader(new FileReader("moviedata.txt"))){
			String str;
			while((str = in.readLine()) != null){
				String[] data = str.split(",");
				movieRatings.put(data[0], Integer.parseInt(data[1]));
			}
		} catch (IOException e){
			System.out.println("Failed to read in movie data set");
		}
	}





	/* 
	============================================
	Methods that will be remotely invoked
	============================================
	*/  
	public String getServerStatus(){
		return serverStatus;
	}

	public queryReturn retrieveRating(queryRequest query){
		// Compare timestamp prev and backEndTS
		boolean applyQuery = true;
		for(int a = 0; a < query.getPrev().length; a++){
			if(query.getPrev()[a] > backEndTS[a]){
				applyQuery = false;
			}
		}

		queryReturn returnQ;

		// Gets gossip messages and then creates a query return (-1 rating means movie not present)
		if(applyQuery == true) {
			try{
				returnQ = new queryReturn(movieRatings.get(query.getMovie()), null, false);
			} catch (Exception e){
				returnQ = new queryReturn(-1, null, false);
			}
		}else {
			requestAllGossipData();
			try{
				returnQ = new queryReturn(movieRatings.get(query.getMovie()), backEndTS, true);
			} catch (Exception e){
				returnQ = new queryReturn(-1, backEndTS, true);
			}
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
			tableTS[serverNumber][serverNumber] += 1;

			// Create the log record
			ts[serverNumber] = tableTS[serverNumber][serverNumber];
			logRecord log = new logRecord(serverNumber, ts, update);
			logRecords.add(log);

			// Checks whether it can add the update
			boolean applyUpdate = true;
			for(int a = 0; a < backEndTS.length; a++) {
				if(update.getPrev()[a] > backEndTS[a]) {
					applyUpdate = false;
				}
			}

			// Gets updates if needed
			if(applyUpdate == false) {
				requestAllGossipData();
			}

			// Add movie to movie rating (or update it)
			movieRatings.put(update.getMovie(), update.getRating());

			// Update backEndTS
			for(int a = 0; a < backEndTS.length; a++) {
				if(backEndTS[a] < ts[a]) {
					backEndTS[a] = ts[a];
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
	public int[] getReplice_Timestamp(){
		return tableTS[serverNumber];
	}

	public int getServerNumber() {
		return serverNumber;
	}

	// Method gets data from other RM machines to synchronise with
	public void requestAllGossipData(){
		try{
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 8043);
			String[] serverList = registry.list();
			for(String registryServerName : serverList){
				if(!(registryServerName.equals(serverName)) && !(registryServerName.contains("front"))){
					BackEndServerInterface stub = (BackEndServerInterface) registry.lookup(registryServerName);
					if(!(stub.getServerStatus().equals("offline"))){
						ArrayList<logRecord> temp_Record = stub.getLogRecord();
						int[] temp_replica = stub.getReplice_Timestamp();
						int temp_serverNumber = stub.getServerNumber();
						tableTS[temp_serverNumber] = temp_replica.clone();
						updateLogs(temp_Record, temp_replica);
					}	
				}
			}
		} catch (Exception e){
			System.err.println("Exception in locating server: " + e.toString());
			e.printStackTrace();
		}
		orderLogs();
		addStableUpdates();
		removeRedundantLogs();
	}

	// Method takes data from another RM and
	public void updateLogs(ArrayList<logRecord> incoming_logRecords, int[] incoming_backEndTS_Replica) {

		// Merge arriving log from RM with current log
		for(logRecord log : incoming_logRecords) {
			boolean addLog = false;

			// If log's TS is greater than the replica's TS than add the log
			for(int a = 0; a < tableTS[serverNumber].length; a++) {
				if(log.getTS()[a] > tableTS[serverNumber][a]){
					addLog = true;
				}
			}

			// Add the log to the logRecords
			if(addLog = true){
				logRecords.add(log);
			}
		}

		// Merges backEndTS_Replica with incoming timestamp 
		for(int a = 0; a < incoming_backEndTS_Replica.length; a++) {
			if(tableTS[serverNumber][a] < incoming_backEndTS_Replica[a]){
				tableTS[serverNumber][a] = incoming_backEndTS_Replica[a];
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
				for(int a = 0; a < backEndTS.length; a++){
					if(log.getUpdate().getPrev()[a] > backEndTS[a]) {
						stable = false;
					}
				}

				// If the update is stable
				if(stable == true){
					applyStableUpdate(log);
				}
			}
		}		
	}

	public void applyStableUpdate(logRecord log) {
		// Put data in movie rating
		movieRatings.put(log.getUpdate().getMovie(), log.getUpdate().getRating());
		
		// Update backEndTS
		for(int a = 0; a < backEndTS.length; a++) {
			if(backEndTS[a] < log.getTS()[a]) {
				backEndTS[a] = log.getTS()[a];
			}
		}
		
		// Added ID to executed table list
		operations.add(log.getUpdate().getupdateID());		
	}

	// Removes logs that have been recieved everywhere
	public void removeRedundantLogs() {

		ListIterator<logRecord> iter = logRecords.listIterator();

		while(iter.hasNext()){
			boolean removeRecord = true;
			logRecord log = iter.next();

			for(int i = 0; i < tableTS[serverNumber].length; i++) {
				if(tableTS[i][log.getI()] < log.getTS()[log.getI()]){
					removeRecord = false;
				}
			}

			if(removeRecord) {
				iter.remove();
			}
		}
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
				System.err.println("Back End Server 1 ==== READY");


			} catch (Exception e) {
				System.err.println("Back End Server 1 exception: " + e.toString());
				e.printStackTrace();
			}
    }
}
