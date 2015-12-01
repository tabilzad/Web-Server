/* --------------------
1. Tim Abilzade / 04/19/2015
2. Java version: build 1.8.0

How to use

Run MyWebServer.java
Using a Browser like Mozilla FireFox -0 acces .xyz file on the server

3. Files needed for running the program:
 a. MyWebServer.java
 b. Mozilla FireFox Browser


For use with webserver back channel. Written for Windows.

This program may contain bugs. Note: version 1.0.

Note that both classpath mechanisms are included. One should work for you.

Requires the Xstream libraries contained in .jar files to compile, AND to run.
See: http://xstream.codehaus.org/tutorial.html


This is a standalone program to connect with MyWebServer.java through a
back channel maintaining a server socket at port 2570.

This BCHandler is a combination of Handler and BCClient 

----------------------------------------------------------------------*/

import java.io.*; // Get the Input Output libraries
import java.net.*; // Get the Java networking libraries
import java.util.Properties;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class BCHandler {

	private static String XMLfileName = "C:\\temp\\mimer2.output";//write temp files to this location
	private static PrintWriter toXmlOutputFile;
	private static File xmlFile;
	private static BufferedReader fromMimeDataFile;

	// data holders-^ via back channel

	class MyDataArray {
		int num_lines = 0;
		String[] lines = new String[8];
	}

	public static void main(String args[]) {
		int i = 0;
		String serverName;
		
		if (args.length < 1) {
			serverName = "localhost";
		} else {
			serverName = args[0];
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		myDataArray da = new myDataArray();
		myDataArray daTest = new myDataArray();
		XStream xstream = new XStream();//marsheling the data

		try {
			System.out.println("Executing the java application.");
			System.out.flush();
			Properties p = new Properties(System.getProperties());

			String argOne = p.getProperty("firstarg");// The argument from shim
														// file
			System.out.println("First var is: " + argOne);

			fromMimeDataFile = new BufferedReader(new FileReader(argOne));// read
																			// the
																			// file
																			// from
																			// reader
			// Only allows for five lines of data in input file plus safety:
			while (((da.lines[i++] = fromMimeDataFile.readLine()) != null)
					&& i < 8) {
				System.out.println("Data is: " + da.lines[i - 1]);
			}
			// the total number of lines
			da.num_lines = i - 1;
			System.out.println("i is: " + i);
			String xml = xstream.toXML(da);// convert data to XML
			sendToBC(xml, serverName);

			System.out.println("\n\nHere is the XML version:");//to console
			System.out.print(xml);

			
			//unmarshel data
			daTest = (myDataArray) xstream.fromXML(xml); // deserialized data
			System.out.println("\n\nHere is the deserialized data: ");
			for (i = 0; i < daTest.num_lines; i++) {
				System.out.println(daTest.lines[i]);
			}
			System.out.println("\n");

			xmlFile = new File(XMLfileName);// create a temporary file. If
											// already exists then delete and
											// then create
			if (xmlFile.exists() == true && xmlFile.delete() == false) {
				throw (IOException) new IOException("XML file delete failed.");
			}// if old deleted or did not exist then create
			xmlFile = new File(XMLfileName);
			if (xmlFile.createNewFile() == false) {
				throw (IOException) new IOException("XML file creation failed.");
			} else {//print file given from shim.bat and wirite it to temp file.
				toXmlOutputFile = new PrintWriter(new BufferedWriter(
						new FileWriter(XMLfileName)));
				toXmlOutputFile.println("First arg to Handler is: " + argOne
						+ "\n");
				toXmlOutputFile.println(xml);//output that XML from shim file
				toXmlOutputFile.close();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
//This sends bytes to server in/as XML
	static void sendToBC(String sendData, String serverName) {
		Socket sock;
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;
		try {
			// Open our connection Back Channel on server:
			sock = new Socket(serverName, 2570);
			toServer = new PrintStream(sock.getOutputStream());
			// Will be blocking until we get ACK from server that data sent
			fromServer = new BufferedReader(new InputStreamReader(
					sock.getInputStream()));

			toServer.println(sendData);
			toServer.println("end_of_xml");
			toServer.flush();
			// Read two or three lines of response from the server,
			// and block while synchronously waiting:
			System.out.println("Blocking on acknowledgment from Server... ");
			textFromServer = fromServer.readLine();
			if (textFromServer != null) {
				System.out.println(textFromServer);
			}
			sock.close();
		} catch (IOException x) {
			System.out.println("Socket error.");
			x.printStackTrace();
		}
	}
}