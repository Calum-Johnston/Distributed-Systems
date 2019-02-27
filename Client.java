import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    private Client() {}

    public static void main(String[] args) {

	String host = (args.length < 1) ? null : args[0];
	try {

	    // Get registry
	    Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1010);

	    // Lookup the remote object "Hello" from registry
	    // and create a stub for it
	    ServerInterface stub = (ServerInterface) registry.lookup("Hello");

	    // Invoke a remote method
	    String response = stub.sayHello();
	    System.out.println("response: " + response);

	} catch (Exception e) {
		System.err.println("Client exception: " + e.toString());
		e.printStackTrace();
	}
    }
}
