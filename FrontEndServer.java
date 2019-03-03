import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
	
public class FrontEndServer implements FrontEndServerInterface {

		// Represents the latest version of data accessed by FE
		int[] prev_frontEnd;
		int[] prev_backEnd1;
		int[] prev_backEnd2;
		int[] prev_backEnd3;
		int updateID; 

		// Default Constructor
		public FrontEndServer() {
				prev_backEnd1 = new int[3];
				prev_backEnd2 = new int[3];
				prev_backEnd3 = new int[3];
				prev_frontEnd = new int[3];
				updateID = 0;
		}



		// QUERY OPERATION
		public int retrieveRating(String movie){
				int rating = 0;
				BackEndServerInterface stub = findBackEndServer();
				try{
						rating = stub.retrieveRating(movie, prev_frontEnd, updateID);
				} catch (Exception e){}
				return rating;
		}

		// UPDATE/INSERT OPERATION
		public void updateRating(String movie, int rating){
				BackEndServerInterface stub = findBackEndServer();
				updateRecord u = new updateRecord(movie, rating, prev_frontEnd, updateID);
				try{
						stub.updateRating(u);
				} catch (Exception e){}
		}





		// Implement methods from ServerInterface;
  	public BackEndServerInterface findBackEndServer() {
				BackEndServerInterface stub = null;
				try{
						Registry registry = LocateRegistry.getRegistry("127.0.0.1", 8043);
						String[] serverList = registry.list();
						for(String serverName : serverList){
								stub = (BackEndServerInterface) registry.lookup(serverName);
								if(stub.getStatus().equals("active")){
										break;				
								}
						}
				} catch (Exception e){
						System.err.println("Exception in locating server: " + e.toString());
						e.printStackTrace();
				}
				return stub;
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
