import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    public Client() {}

    public static void main(String[] args) {

			// Gets the name of the server to connect to (Front End Server)
			String host = (args.length < 1) ? "frontEnd" : args[0];

			try {
				// Get registry
				Registry registry = LocateRegistry.getRegistry("127.0.0.1", 8043);

				// Lookup the remote object "frontEnd" from registry (represents the Server)
				// and create a stub for it
				FrontEndServerInterface stub = (FrontEndServerInterface) registry.lookup(host);

				//Invoke a remote method
				stub.findBackEndServer();

			} catch (Exception e) {
				System.err.println("Client exception: " + e.toString());
				e.printStackTrace();
			}
    }
}
