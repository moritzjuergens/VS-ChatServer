package ChatSystem.Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import ChatSystem.DWH.Warehouse;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Entities.User;

public class Server {

	public static List<Server> registeredServer = new ArrayList<Server>();
	private List<User> users = new ArrayList<User>();
	private ServerSocket ss;

	public Server(int port) {

		new Thread(() -> {
			try {
				ss = new ServerSocket(port);
				var pool = Executors.newFixedThreadPool(10);
				new Thread(() -> {
					while (true) {
						try {
							pool.execute(new ServerThread(this, ss.accept()));
						} catch (IOException e) {
							break;
						}
					}
				}).start();

			} catch (IOException e) {
			} finally {
				System.out.println("Server listening on port " + port);
				Server.registeredServer.add(this);
			}
		}).start();
	}

	public static void closeAll() {
		for (Server s : registeredServer) {
			s.close();
		}
	}

	private void close() {
		try {
			System.out.println("Shutting down Server " + ss.getLocalPort());
			ss.close();
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void registerClient(User u) {

	}

	public void unregisterClient(User u) {
		this.users.remove(u);
	}

	public void sendMessage(User u, ServerMessage m) {
		System.out.println("[Server] Nachricht an " + u);
		if (users.contains(u)) {
			System.out.println("[Server] Sending " + m + " to " + u);
			try {
				u.out.writeObject(m);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void messageReceived(ServerMessage message, ObjectOutputStream out) {
		System.out.println("[Server] Message received: " + message);
		switch(message.prefix.toLowerCase()) {
		case "login":
			synchronized (users) {				
				User u = Warehouse.getUser(message.object.toString());
				u.out = out;
				users.add(u);
				sendMessage(u, new ServerMessage("Hi, ich kenn dich jz", u));
			}
			break;
		}
	}
}
