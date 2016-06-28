import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {
	public static final String SERVER_HOSTNAME = "localhost";
	public static final int SERVER_PORT = 2055;

	public static void main(String[] args) {
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			// Connect to Server
			Socket socket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			System.out.println("Connected to server " + SERVER_HOSTNAME + ":" + SERVER_PORT);
		} catch (IOException ioe) {
			System.err.println("Can not establish connection to " + SERVER_HOSTNAME + ":" + SERVER_PORT);
			ioe.printStackTrace();
			System.exit(-1);
		}

		// Create and start Sender thread
		Sender sender = new Sender(out);
		sender.setDaemon(true);
		sender.start();

		try {
			// Read messages from the server and print them
			String message;
			while ((message = in.readLine()) != null) {
				System.out.println(message);
			}
		} catch (IOException ioe) {
			System.err.println("Connection to server broken.");
			ioe.printStackTrace();
		}

	}
}


// { request_type : "SERVER" }

// { request_type : "CLIENTS" }

class Sender extends Thread {
	private PrintWriter mOut;

	public Sender(PrintWriter aOut) {
		mOut = aOut;
	}

	/**
	 * Until interrupted reads messages from the standard input (keyboard) and
	 * sends them to the chat server through the socket.
	 */
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			
			mOut.println("{ \"request_type\" : \"START\" }");
			mOut.flush();
			
			while (!isInterrupted()) {
				String message = in.readLine();
				if(message.equals("1")){
					message = "{\"request_type\": \"JOIN_SERVER\", character_id:\"1\"}";
				}
				mOut.println(message);
				mOut.flush();
			}
		} catch (IOException ioe) {
			// Communication is broken
		}
    }
}