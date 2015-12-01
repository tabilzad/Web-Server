/*--------------------------------------------------------

1. Tim Abilzade / 04/19/2015
2. Java version: build 1.8.0

3. Precise command-line compilation examples / instructions:
 
> javac MyWebServer.java


4. 
In separate shell windows:

> java MyWebServer.java


All acceptable commands are displayed on the various consoles.

5. How to use

For this server you only need to run it, and it will be listening to a client (Mozilla FireFox)

6. Files needed for running the program:
 a. MyWebServer.java
 b. Mozilla FireFox Browser

7. Notes:

Small parts of the code were borrowed (although modified) from examples mentioned in the instructions to this assignment: ReadFiles.java and "MyWebServer Tips"

 */
import java.io.*; // Get the Input Output libraries
import java.net.*; // Get the Java networking libraries
import java.util.StringTokenizer;
import com.thoughtworks.xstream.XStream;

class MyWebServerWorker extends Thread { // Define Class
	Socket sock; // Class member, socket, local to Worker
	int BUF_SIZE = 4096;
	final static String CRLF = "\r\n";

	MyWebServerWorker(Socket s) {
		sock = s;
	}// Constructor, assign arg s to local socket

	public void run() {
		// Get I/O streams in/out from the socket:
		PrintStream out = null;
		BufferedReader in = null;

		try {
			in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			out = new PrintStream(sock.getOutputStream());

			try {
				String contentType = null;
				String filename = null;
				String dirName = null;
				String input = null;
				/*
				 * while (in.ready()) //while not empty - while there is a
				 * stream of information { input = in.readLine();
				 * System.out.println(input); //keep printing that input from
				 * FireFox (Client) until there is nothing more to read }
				 */

				// input = in.readLine(); // one line
				System.out.println();
				// look for request

				while (true) {// get clients request (first line) but output all
								// of the lines
					String other = in.readLine();
					input = (input == null) ? other : input;
					if (other == null || other.length() == 0) {
						break;
					}
					System.out.println(other);
				}
				// checking correctness of input from client
				if (!input.startsWith("GET")
						|| input.length() < 14
						|| !(input.endsWith("HTTP/1.0") || input
								.endsWith("HTTP/1.1"))) {
					// bad request
					errorReport(out, sock, "400", "Bad Request",
							"Your browser sent a request that "
									+ "this server could not understand.");
				} else {

					input = input.replace("GET /", "");// gets rid of GET
					input = input.replace(" HTTP/1.1", "");// only compatible
															// gets rid of
															// HTTP/1.1
															// with HTML 1.1 :)

					System.out.println("Requested URL:" + input);

					out.flush();
					// This is from the tips of the assignment
					if (input.indexOf("..") != -1
							|| input.indexOf("/.ht") != -1
							|| input.endsWith("~")) { // evil hacker trying to
														// read non-wwwhome or
														// secret file
						errorReport(out, sock, "403", "Forbidden",
								"You don't have permission to access the requested URL.");
						// throw new FileNotFoundException();
					}

					else {

						if (input.contains(".fake-cgi")
								&& !(input.contains("?"))) {// if requested
															// .fake-cgi

							getFile(out, input, contentType);
						} else if (input.contains(".fake-cgi?")) {

							// System.out.println("THE NAME IS: "+ input);
							// cgi/addnums.fake-cgi?person=YourName&num1=4&num2=5

							StringTokenizer test = new StringTokenizer(input,
									"=");// Breaks the input into tokens
											// separated by '='
							if (test.countTokens() != 4) {// security? if there
															// are more equal
															// signs
								errorReport(out, sock, "400", "Bad Request",
										"Unsecure request");
							}
							int num1, num2, sum;

							String name;
							/*
							 * System.out.println("Token 1 "+ test.nextToken());
							 * Token 1 cgi/addnums.fake-cgi?person
							 * System.out.println("Token 1 "+ test.nextToken());
							 * Token 1 YourName&num1
							 * System.out.println("Token 1 "+ test.nextToken());
							 * Token 1 4&num2 System.out.println("Token 1 "+
							 * test.nextToken()); Token 1 5
							 */

							test.nextToken();
							name = test.nextToken().replace("&num1", "");// name
																			// is
																			// whatever
																			// is
																			// after
																			// &num1
							num1 = Integer.parseInt(test.nextToken().replace(
									"&num2", "")); // num1 is whatever is after
													// &num1
							num2 = Integer.parseInt(test.nextToken());// last
																		// token
																		// is
																		// the
																		// second
																		// number
							// also converts to Integers
							/*
							 * System.out.println("Persons name"+ name);
							 * System.out.println("NUM1 "+ num1);
							 * System.out.println("Num2 "+ num2);
							 */

							sum = num1 + num2;// computes the sum
							out.print("HTTP/1.1 "
									+ "WebServer"
									+ CRLF
									+ CRLF
									+ "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\r\n"
									+ "<TITLE>" + " WebServer "
									+ "</TITLE>\r\n" + "</HEAD><BODY>\r\n");
							out.println("<html><body><h1> ANSWER </h1> <br/>Hello "
									+ name
									+ " the sum of "
									+ num1
									+ " and "
									+ num2
									+ " is equals to "
									+ sum
									+ "<h1> <p>THANK YOU</p></h1> </body></html>");

						} else

						if (input.endsWith(".txt") || input.endsWith(".java")) {// handles
																				// .txt
																				// files
																				// or
																				// of
																				// .java
																				// then
																				// also
																				// text
																				// type
							filename = input;// for organization
							contentType = "text/plain";// assigns the type
							getFile(out, filename, contentType);// gets the file
							System.out
									.println("The client has requested a TEXT file called: "
											+ filename);
						} else if (input.endsWith(".xyz")) {
							contentType = "application/xyz";
							getFile(out, input, contentType);
							System.out
									.println("The client has requested an custom XYZ file called: "
											+ input);

						} else if (input.endsWith(".html")) {
							filename = input;
							contentType = "text/html";
							getFile(out, filename, contentType);
							System.out
									.println("The client has requested an HTML file called: "
											+ filename);
						} else if (input.endsWith(".wml")) {
							contentType = "text/vnd.wap.wml";

							getFile(out, filename, contentType);
							System.out
									.println("The client has requested a wml file "
											+ input);
						} else if (input.endsWith(".jpg")
								|| input.endsWith(".jpeg")) {
							contentType = "image/jpeg";

							getFile(out, input, contentType);
							System.out
									.println("The client has requested an image called: "
											+ input);
						}

						else if (input.endsWith(".class")) {
							contentType = "application/octet-stream";
							System.out
									.println("The client has requested .class file "
											+ input);

						} else if (input.endsWith("/") || (input.endsWith(""))) {// handles
																					// directories
							dirName = input;// organization
							if (input.endsWith("")) {
								dirName = dirName + "/";// treats "" as
														// directory but still
														// adds a slash so that
														// it would say Not
														// found
							}
							getDir(out, dirName);// gets directory
							System.out
									.println("The client has requested a directory at index of "
											+ dirName);
						}

					}
				}
			} catch (IOException x) {
				System.out.println("Server read error");
				x.printStackTrace();
			}
			sock.close(); // close the connection, but not the server
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}

	private static void errorReport(PrintStream pout, Socket connection,// Error
																		// report
																		// (borrowed
																		// from
																		// the
																		// examples
																		// of
																		// the
																		// assignment)
			String code, String title, String msg) {
		pout.print("HTTP/1.1 " + code + " " + title + CRLF + CRLF
				+ "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\r\n"
				+ "<TITLE>" + code + " " + title + "</TITLE>\r\n"
				+ "</HEAD><BODY>\r\n" + "<H1>" + title + "</H1>\r\n" + msg
				+ "<P>\r\n" + "<HR><ADDRESS>FileServer 1.0 at "
				+ connection.getLocalAddress().getHostName() + " Port "
				+ connection.getLocalPort() + "</ADDRESS>\r\n"
				+ "</BODY></HTML>\r\n");
	}

	void getDir(PrintStream out, String dirName) {
		File f1 = new File("./" + dirName);
		File[] strFilesDirs = f1.listFiles();
		String name = null;

		try {
			out.print("HTTP/1.1 "// basic header
					+ "WebServer"
					+ CRLF
					+ CRLF
					+ "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\r\n"
					+ "<TITLE>" + " WebServer "
					+ "</TITLE>\r\n"
					+ "</HEAD><BODY>\r\n");
			out.println("<h1> Directory is: " + dirName + "</h1>" + "\r\n\r\n"
					+ "</BODY></HTML>\r\n");

			out.println("<a href=/> Parent/root  </a><br/>");// goes back to
																// parent
			out.println("<br/>");

			for (int i = 0; i < strFilesDirs.length; i++) {// sees what files
															// are in this
															// directory or
															// other
															// sub-directories

				name = strFilesDirs[i].getName();
				if (strFilesDirs[i].isDirectory()) {// if its a folder then
													// create a link to its
													// address
					System.out.println("Directory: " + strFilesDirs[i]);

					out.println("<a href=\"" + name // create a link, name of
													// dir folder
							+ "/\">/" + name + "</a><br>");

				} else if (strFilesDirs[i].isFile()) {// if its a file then
														// create a link to its
														// address
					System.out.println("File: " + strFilesDirs[i] + " ("
							+ strFilesDirs[i].length() + ")");

					out.println("<a href=\""
							+ name// create a link and also get its size
							+ "\">" + name + " (" + strFilesDirs[i].length()
							+ " bytes) </a><br>");

				}

			}

		} catch (Exception e) {
			errorReport(out, sock, "404", "Not Found",
					"The requested directory was not found on this server ");
			// f1.mkdirs(); For debugging purposes (a.k.a sometimes I misspell
			// stuff)
			// out.println("Made a new directory called: "+dirName+" \nPlease try to acces it again :)");

		}

	}

	void getFile(PrintStream out, String filename, String fType) {

		File f = new File(filename);

		try (InputStream fstream = new FileInputStream(f);) {// see if file
																// exists

			out.print("HTTP/1.1 200 OK" + CRLF + "Content-Length: "
					+ f.length() + CRLF + "Content-Type: " + fType + CRLF
					+ CRLF);

			sendBytes(fstream, out);

		} catch (IOException e) {// TODO Auto-generated catch block

		} // if file does not exist
		errorReport(out, sock, "404", "Not Found",
				"Your browser sent a URL that " + "this server does not have.");

	}

	private void sendBytes(InputStream f, PrintStream out) {// send contents of
															// the file to
															// client

		try {
			byte[] buffer = new byte[BUF_SIZE];
			while (f.available() > 0)
				out.write(buffer, 0, f.read(buffer));
		} catch (IOException e) {
			System.err.println(e);
		}

	}

}

public class MyWebServer {

	public static void main(String a[]) throws IOException {
		int q_len = 6;
		int port = 2540;
		Socket sock;

		BCLooper AL = new BCLooper();
		Thread t = new Thread(AL);
		t.start();

		ServerSocket servsock = new ServerSocket(port, q_len);// Create socket
																// with the
																// following
																// arguments
																// (port number,
																// queue length)
		// created new object servsock, pass 2 arg to constructor

		System.out
				.println("Clark Elliot's WebServer 1.8 starting up, listening at port:"
						+ port);
		while (true) { // I could have made a switch that would turn the server
						// OFF here
			sock = servsock.accept();// New client connection
			new MyWebServerWorker(sock).start(); // Spawn worker to handle the
													// connection
		}
	}
}

class myDataArray {
	int num_lines = 0;
	String[] lines = new String[10];
}

class BCWorker extends Thread {
	private Socket sock;
	private int i;

	BCWorker(Socket s) {
		sock = s;
	}

	PrintStream out = null;
	BufferedReader in = null;

	String[] xmlLines = new String[15];
	String[] testLines = new String[10];
	String xml;
	String temp;
	XStream xstream = new XStream();
	final String newLine = System.getProperty("line.separator");
	myDataArray da = new myDataArray();

	public void run() {
		System.out.println("Called BC worker.");
		try {
			in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			out = new PrintStream(sock.getOutputStream()); // to send ack back
															// to client
			i = 0;
			xml = "";
			while (true) {
				temp = in.readLine();
				if (temp.indexOf("end_of_xml") > -1)
					break;
				else
					xml = xml + temp + newLine; // Should use StringBuilder in
												// 1.5
			}
			System.out.println("The XML marshaled data:");
			System.out.println(xml);
			out.println("Acknowledging Back Channel Data Receipt"); // send the
																	// ack
			out.flush();
			sock.close();

			da = (myDataArray) xstream.fromXML(xml); // deserialize / unmarshal
														// data
			System.out.println("Here is the restored data: ");
			for (i = 0; i < da.num_lines; i++) {
				System.out.println(da.lines[i]);
			}
		} catch (IOException ioe) {
		} // end run
	}
}

class BCLooper implements Runnable {
	public static boolean adminControlSwitch = true;

	public void run() { // RUNning the Admin listen loop
		System.out.println("In BC Looper thread, waiting for 2570 connections");

		int q_len = 6; // Number of requests for OpSys to queue
		int port = 2570; // Listen here for Back Channel Connections
		Socket sock;

		try {
			ServerSocket servsock = new ServerSocket(port, q_len);
			while (adminControlSwitch) {
				// wait for the next ADMIN client connection:
				sock = servsock.accept();
				new BCWorker(sock).start();
			}
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}
}
