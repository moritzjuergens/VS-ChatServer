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

	public ServerThread(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		ServerMessage m;
		try {
			while ((m = (ServerMessage) in.readObject()) != null) {
				server.messageReceived(m, out);
			}
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

}