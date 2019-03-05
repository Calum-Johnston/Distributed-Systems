import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
	
public class FrontEndServer implements FrontEndServerInterface {

	int[] frontEndTS;  // Represents the latest version of data accessed by FE / observed by the client
	int updateID;  // Represents the ID for updates 

	// Default Constructor
	public FrontEndServer() {
		frontEndTS = new int[3];
		updateID = 0;
	}



		// QUERY OPERATION
	public int retrieveRating(String movie, String server){
		BackEndServerInterface stub = findBackEndServer(server);
		queryRequest query = new queryRequest(movie, frontEndTS);
		queryReturn q = null;
		try{
			q = stub.retrieveRating(query);
			if(q.getUpdate() == true){
				frontEndTS = mergeTimestamps(frontEndTS, q.getvalue_TS());
			}
		} catch (Exception e){
			System.out.println("Error in getting Query");
		}
		return q.getRating();
	}

	// UPDATE OPERATION
	public void updateRating(String movie, int rating, String server){
		BackEndServerInterface stub = findBackEndServer(server);
		updateRequest update = new updateRequest(movie, rating, frontEndTS, updateID);
		int[] returnedTS = null;
		try{
			returnedTS = stub.updateRating(update);  // ts represents the returned timestamp
			frontEndTS = mergeTimestamps(frontEndTS, returnedTS);  // Merges the returned timestamp with current one
		} catch (Exception e){}
		stub = null;
		updateID += 1;
	}





	// Implement methods from ServerInterface;
  	public BackEndServerInterface findBackEndServer(String server) {
		try{
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 8043);
			BackEndServerInterface stub = (BackEndServerInterface) registry.lookup(server);
			return stub;
			/*String[] serverList = registry.list();
			for(String serverName : serverList){
				BackEndServerInterface stub = (BackEndServerInterface) registry.lookup(serverName);
				if(stub.getStatus().equals("active")){
					return stub;				
				}
			}*/
		} catch (Exception e){
			System.err.println("Exception in locating server: " + e.toString());
			e.printStackTrace();
		}
		return null;
	}

	// Merges returned timestamp with 
	public int[] mergeTimestamps(int[] backEnd, int[] ts){
		for(int a = 0; a < backEnd.length; a++) {
		    if(backEnd[a] < ts[a]) {
			    backEnd[a] = ts[a];
			}
		}
		return backEnd;
	}
	




	// Main method: 
	// Instatntiates and registers an instance of the server with the rmi registry
    public static void main(String args[]) { 
		try {
			// Defines the server name
			String name = "frontEnd";
				
			// Create server object
			FrontEndServer obj = new FrontEndServer();

			// Create remote object stub from server object
			FrontEndServerInterface stub = (FrontEndServerInterface) UnicastRemoteObject.exportObject(obj, 0);

			// Get registry
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 8043);

			// Bind the remote object's stub in the registry
			registry.rebind(name, stub);

			// Write ready message to console
			System.err.println("Front End Server ==== READY");

		} catch (Exception e) {
			System.err.println("FRont End Server exception: " + e.toString());
			e.printStackTrace();
		}
    }
}
