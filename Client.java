import java.io.Console;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {

	static Scanner in = new Scanner(System.in);

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

				
				boolean valid = false;
				do{
					// Get what the user wants to do
					int choice = getChoice();

					// Invokes a method in remote object based on choice
					switch(choice){
						case 1:
							retrieveRating(stub);
							break;
						case 2:
							updateRating(stub);
							break;
						default:
							valid = true;	
							break;
					}
				} while(!valid);

			} catch (Exception e) {
				System.err.println("Client exception: " + e.toString());
				e.printStackTrace();
			}
	}
	
	// Gets the method the user wishes to proceed with
	public static int getChoice(){
		boolean valid = false;
		int result = 4;
		while(valid == false){
			try{
				displayInterface();
				result = Integer.parseInt(in.nextLine());
				if(result < 4 && result > 0){
					valid = true;
				}else{
					clearScreen();
					System.out.println("Please enter a valid option");
				}
			} catch (Exception e){
				clearScreen();
				System.out.println("Invalid input - Try again");
			}
		}
		return result;
	}

	// Invokes the retrieveRating method on the FE object
	public static void retrieveRating(FrontEndServerInterface stub){
		clearScreen();
		String movie = getMovieName();
		int rating = 0;
		try{
			rating = stub.retrieveRating(movie);
			System.out.println("The movie: " + movie + " has a rating of: " + rating);
		} catch (Exception e){
			System.out.println("Error in retrieving rating: " + e.toString());
			e.printStackTrace();
		}
	}

	// Invokes the updateRating method on the FE object
	public static void updateRating(FrontEndServerInterface stub){
		clearScreen();
		String movie = getMovieName();
		int rating = getMovieRating();
		try{
			System.out.println("Updating record");
			stub.updateRating(movie, rating);
		} catch (Exception e){
			System.out.println("Error in updating rating: " + e.toString());
			e.printStackTrace();
		}
	}

	// Gets a movie name from the user
	public static String getMovieName(){
		boolean valid = false;
		String movie = "";
		do{
			System.out.println("Enter a movie");
			movie = in.nextLine();
			if(movie != null && !movie.isEmpty()){
				valid = true;
			}else{
				clearScreen();
				System.out.println("Please enter a valid movie");
			}
		} while(!valid);
		return movie;
	}

	// Gets a movie rating from the user
	public static int getMovieRating(){
		boolean valid = false;
		int rating = 0;
		do{
			try{
				System.out.println("Enter a rating (0 - 10)");
				rating = Integer.parseInt(in.nextLine());
				if(rating >= 0 && rating <= 10){
					valid = true;
				}else{
					clearScreen();
					System.out.println("Please enter a valid number (0-10)");
				}
			} catch (Exception e){
				System.out.println("Please enter a number");
			}
		} while(!valid);
		return rating;
	}

	// Displays the user interface
	public static void displayInterface(){
		System.out.println("===============");
		System.out.println("MOVIE RATINGS");
		System.out.println("===============");
		System.out.println("\nPlease select an option");
		System.out.println("1: Retrieve a movie rating");
		System.out.println("2: Update existing rating");
		System.out.println("3: Quit");
	}

	// Clears the console
	public static void clearScreen(){
		System.out.println("\033[H\033[2J");
		System.out.flush();
	}
}
