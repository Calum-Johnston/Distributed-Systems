import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class FrontEndServer implements FrontEndServerInterface{

    // Default Constructor
    public FrontEndServer(){}
    
    // Main method: 
    // Instatntiates and registers an instance of the server with the rmi registry
    public static void main(String args[]){
		try {
            // Defines the serve name
            String name = "frontEnd";

            // Create the front end server
            FrontEndServer obj = new FrontEndServer();

            // Create the remote object stub from server object
            FrontEndServerInterface stub = (FrontEndServerInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Get registry
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1010);

            // Bind the remote object's stub in the registry
            registry.rebind(name, stub);

            // Display Server Ready
            System.out.println("Front End Server ==== READY");

		} catch (Exception e) {
			System.err.println("Front End Server exception: " + e.toString());
			e.printStackTrace();
		}
    }
}