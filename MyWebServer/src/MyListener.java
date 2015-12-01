/*--------------------------------------------------------

1. Tim Abilzade / 04/19/2015
2. Java version: build 1.8.0

3. Precise command-line compilation examples / instructions:
 
> javac JokeServer.java


4. 
In separate shell windows:

> java JokeServer
> java JokeClient
> java JokeClientAdmin

All acceptable commands are displayed on the various consoles.

5. How to use

For this server you only need to run it, and it will be listening to any potention clients

6. Files needed for running the program:
 a. JokeServer.java
 b. JokeClient.java
 c. JokeClientAdmin.java


7. Notes:

N/A
*/
import java.io.*; // Get the Input Output libraries
import java.net.*; // Get the Java networking libraries
import java.util.*;

class Worker extends Thread { // Define Class
	Socket sock; // Class member, socket, local to Worker


	Worker(Socket s) {
		sock = s;
	}// Constructor, assign arg s to local socket

	public void run() {
		// Get I/O streams in/out from the socket:
		PrintStream out = null;
		BufferedReader in = null;

		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));// Getting an
																	// input
																	// from a
																	// connection
																	// with
																	// client
																	// (user)
			out = new PrintStream(sock.getOutputStream());// out put from the
															// connection
			
			
			
			
			try {
				String input = null;
				while (in.ready()) //while not empty - while there is a stream of information
				{
					input = in.readLine();
					System.out.println(input); //keep printing that input from FireFox (Client) until there is nothing more to read
				}
				
				out.println("Request received");
				
				
				
                out.flush();
				

			} catch (IOException x) {
				System.out.println("Server read error");// debugging if
														// something wrong with
														// input
				x.printStackTrace();
			}
			
			/*PrintStream output_to_Text = new PrintStream(new FileOutputStream("JokeOutput.txt"));
			System.setOut(output_to_Text);*/
			
			sock.close(); // close the connection, but not the server
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}

}

public class MyListener {


	public static void main(String a[]) throws IOException {
		int q_len = 6;
		int port = 80;
		Socket sock;


		ServerSocket servsock = new ServerSocket(port, q_len);// Create socket
																// with the
																// following
																// arguments
																// (port number,
																// queue length)
		// created new object servsock, pass 2 arg to constructor

		System.out.println("Clark Elliot's WebServer 1.8 starting up, listening at port:"
						+ port);
		while (true) { // I could have made a switch that would turn the server OFF here         
			sock = servsock.accept();// New client connection
			new Worker(sock).start(); // Spawn worker to handle the connection
		}
	}
}

