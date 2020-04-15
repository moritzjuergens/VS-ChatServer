package ChatSystem.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ChatSystem.AES;
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
//			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			ServerMessage m;
			while ((m = (ServerMessage) AES.decrypt(in.readObject())) != null && !server.shutdown) {
				server.messageReceived(m, out);
			}
			System.out.println("allo");
			out.close();
			in.close();
			socket.close();
		} catch (ClassNotFoundException | IOException e) {
			// e.printStackTrace();
		}
	}

}