import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
	
public class FrontEndServer implements FrontEndServerInterface {

		int[] prev;  // Represents the latest version of data accessed by FE / observed by the client
		int updateID;  // Represents the ID for updates 

		// Default Constructor
		public FrontEndServer() {
				prev = new int[3];
				updateID = 0;
		}



		// QUERY OPERATION
		public int retrieveRating(String movie){
				int rating = 0;
				BackEndServerInterface stub = findBackEndServer();
				queryRequest query = new queryRequest(movie, prev);
				try{
						rating = stub.retrieveRating(query);
				} catch (Exception e){}
				return rating;
		}

		// UPDATE/INSERT OPERATION
		public void updateRating(String movie, int rating){
				BackEndServerInterface stub = findBackEndServer();
				updateRequest update = new updateRequest(movie, rating, prev, updateID);
				try{
						System.out.println("hi");
						int[] ts = stub.updateRating(update);  // ts represents the returned timestamp
						System.out.println("hello");
						prev = mergeTimestamps(prev, ts);  // Merges the returned timestamp with current one
				} catch (Exception e){}
				updateID += 1;
		}





		// Implement methods from ServerInterface;
  	public BackEndServerInterface findBackEndServer() {
				try{
						Registry registry = LocateRegistry.getRegistry("127.0.0.1", 8043);
						String[] serverList = registry.list();
						for(String serverName : serverList){
								BackEndServerInterface stub = (BackEndServerInterface) registry.lookup(serverName);
								System.out.println(serverName);
								if(stub.getStatus().equals("active")){
										return stub;				
								}
						}
				} catch (Exception e){
						System.err.println("Exception in locating server: " + e.toString());
						e.printStackTrace();
				}
				return null;
		}

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
