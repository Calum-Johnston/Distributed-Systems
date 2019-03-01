import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
	
public class FrontEndServer implements FrontEndServerInterface {

	private String myString = "";

	// Default Constructor
	public FrontEndServer() { }




	
	public void submitRating(String movie, int rating){

	}

	public int getRating(String movie){
		return 2;
	}






	// Implement methods from ServerInterface;
  public BackEndServerInterface findBackEndServer(){
		Registry registry = LocateRegistry.getRegistry("127.0.0.1", 8043);
		String[] serverList = registry.list();
		for(String serverName : serverList){
			BackEndServerInterface stub = (BackEndServerInterface) registry.lookUp(serverName)
			if(stub.getStatus().equals("active")){
				break;
			}
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
