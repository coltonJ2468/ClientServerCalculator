# Client Server Calculator
The server creates and starts the monitor then begins looking for incoming client
requests. The monitor creates the thread pool and begins comparing the thread pool to the size
of the request queue to periodically test to see if the thread pool size needs to be increased or
decreased. The thread pool fills itself with running threads that check the request queue for
clients and then wait for the client to enter arithmetic operations to solve.

# How To Compile and Run
To Compile Server: 

	cd into sever

	javac CapitalizeServer.java
		   
To Run Server: 

	java CapitalizeServer

To Compile Client: 

	cd into client

	javac CapitalizeClient.java
		   
To Run Client: 

	java CapitalizeClient
