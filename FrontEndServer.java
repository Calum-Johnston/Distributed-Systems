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
	public int retrieveRating(String movie){
		BackEndServerInterface stub = findBackEndServer();
		queryRequest query = new queryRequest(movie, frontEndTS);
		queryReturn q = null;
		try{
			q = stub.retrieveRating(query);
			if(q.getUpdate() == true){
				frontEndTS = mergeTimestamps(frontEndTS, q.getvalue_TS());
			}
		} catch (Exception e){
			System.out.println("Error in getting query: " + e.toString());
			return -2;
		}
		return q.getRating();
	}

	// UPDATE OPERATION
	public boolean updateRating(String movie, int rating){
		BackEndServerInterface stub = findBackEndServer();
		updateRequest update = new updateRequest(movie, rating, frontEndTS, updateID);
		int[] returnedTS = null;
		try{
			returnedTS = stub.updateRating(update);  // ts represents the returned timestamp
			frontEndTS = mergeTimestamps(frontEndTS, returnedTS);  // Merges the returned timestamp with current one
		} catch (Exception e){
				System.out.println("Error in getting Update: " + e.toString());
				return false;
		}
		stub = null;
		updateID += 1;
		return true;
	}





	// Implement methods from ServerInterface;
  public BackEndServerInterface findBackEndServer() {
		int count = 0;

		try{
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 8043);
			String[] serverList = registry.list();

			// Check for active servers
			for(String serverName : serverList){
				System.out.println(serverName);
				if(!(serverName.contains("front"))){
					BackEndServerInterface stub = (BackEndServerInterface) registry.lookup(serverName);
					if(stub.getServerStatus().equals("active")){
						return stub;				
					}
				}
			}

			// Check for overloaded servers
			for(String serverName : serverList){
				if(!(serverName.contains("front"))){
					BackEndServerInterface stub = (BackEndServerInterface) registry.lookup(serverName);
					if(stub.getServerStatus().equals("overloaded")){
						return stub;				
					}
				}
			}
		} catch (Exception e){
			System.err.println("Exception in locating server: " + e.toString());
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
			String name = "frontEnd1";
				
			// Create server object
			FrontEndServer obj = new FrontEndServer();

			// Create remote object stub from server object
			FrontEndServerInterface stub = (FrontEndServerInterface) UnicastRemoteObject.exportObject(obj, 0);

			// Get registry
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 8043);

			// Bind the remote object's stub in the registry
			registry.rebind(name, stub);

			// Write ready message to console
			System.err.println("Front End Server 1 ==== READY");

		} catch (Exception e) {
			System.err.println("Front End Server 1 exception: " + e.toString());
		}
    }
}
