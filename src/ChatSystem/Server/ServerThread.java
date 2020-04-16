package ChatSystem.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ChatSystem.Entities.ServerMessage;

public class ServerThread implements Runnable {

	Server server;
	Socket socket;
	ObjectOutputStream out;
	ObjectInputStream in;

	/**
	 * New socket opened to establish connection to client
	 * 
	 * @param server parent
	 * @param socket opened socket
	 */
	public ServerThread(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {

			// read all incoming messages as long as server is online
			ServerMessage m;
			while ((m = (ServerMessage) in.readObject()) != null && !server.shutdown) {
				// forward received message for further handling back to server
				server.messageReceived(m, out);
			}

			// close connection
			out.close();
			in.close();
			socket.close();
		} catch (ClassNotFoundException | IOException e) {
			// e.printStackTrace();
		}
	}

}