NAME
----
Distributed Networks - Gossip Architecture



SYNOPOSIS
----
A simple Java application using RMI to implement a reliable distributed replication system based on the gossip replication architecture 



DESCRIPTION (200 words)
----
The program consists of 3 back end servers, 1 front end and a client. The client can query the rating of a variety of movies (stored in moviedata.txt) or update the ratings. The back end servers control consistency of data through communication of gossip messages, where they update when they require additional information from other servers to complete a request. The servers ensures high data integrity though storing updates in logs, where updates are removed from this log as soon as every server has seen the update. 

When running the system a client connects to the front end server (via java RMI) which in turn connects it to an appropriate back end server. A simple and effective UI for the client is provided to make requests easy. For example if the client wishes to update some record, they invoke a remote method in the front end server which in turn selects the most appropriate back end server to communicate with. In order to support high system availability the front end will select the next least congested server assuming it's default is overloaded/offline. The front end then passes the update data to the back end server where the back end processes the request and subsequently returns some data (if necessary).



HOW TO RUN:
----
=======================================================================
=== WINDOWS ===
=======================================================================
- Open CMD and navigate to the directionary where the files are stored
- Ensure all files are in the same folder (including movie data)
- Compile the classes with the following command

javac BackEndServerInterface.java logRecord.java updateRequest.java queryRequest.java queryReturn.java BackEndServer1.java BackEndServer2.java BackEndServer3.java FrontEndServerInterface.java FrontEndServer.java Client.java

- Run the registry (in the same folder) with the following command (ENSURE USING PORT 8043)

rmiregistry 8043    

- Next step is to run the servers, you will need several instances of the CMD open as each one will host an individual server. 
- To run a server, simply navigate to the folder where the java files have been compiled and run the command

java Server    (where Server is replaced with one of the servers provided)

- The recommended order to run the servers in is: FrontEndServer, BackEndServer1, BackEndServer2, BackEndServer3
- Finally, with another instance of the CMD, run the command

java Client

=======================================================================
=== LINUX (Specifically Linux Mint) ===
=======================================================================
- Shell scripts have been provided, simply navigate to the directory where the files are stored and load the scripts
- NOTE: You must load the RMI Registry script first, and the Client cannot be loaded until at least the Front End Server is running


=======================================================================
=== ADDITIONAL POINTS ===
=======================================================================
- The program represents a simulation of a distributed system, and so each server has a status representing it's state. It does not actually follow the state and so an offline server is not one that is not running, but simply one with a status of offline.
- In order to change a server's status (active, overloaded, offline) you must open the java file and navigate to line 46 (applies to all servers). Here you can change the status but please ensure you use only the three options given.
- If you physically make a server go offline, rather than changing the status then the system may fail.

- Feel free to add movies to the moviedata.txt, ensuring you keep it in the form: 'movie name','movie score' 
