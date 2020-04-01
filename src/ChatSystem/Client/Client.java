package ChatSystem.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ChatSystem.CSLogger;
import ChatSystem.ChatSystem;
import ChatSystem.Entities.ServerMessage;

public class Client extends Thread {

	public static List<Client> registeredClients = new ArrayList<Client>();

	String name = "Client";
	Socket s;
	ObjectInputStream in;
	ObjectOutputStream out;

	public Client(String name) {
		this();
		this.name = name;
	}

	public Client() {

		new Thread(() -> {

			int port = getPort();
			try {
				s = new Socket("localhost", port);
				out = new ObjectOutputStream(s.getOutputStream());
				in = new ObjectInputStream(s.getInputStream());

				new Thread(() -> {
					while (true) {
						if (s.isClosed())
							break;
						try {
							ServerMessage m = (ServerMessage) in.readObject();
							this.messageReceived(m);
						} catch (ClassNotFoundException | IOException e) {
						}
					}
				}).start();

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				CSLogger.log(Client.class, "Client using server %s", port);
				Client.registeredClients.add(this);
			}
		}).start();
	}

	public static void closeAll() {
		CSLogger.log(Client.class, "Shutting down all Clients...");
		for (Client c : registeredClients) {
			try {
				c.in.close();
				c.out.close();
				c.s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// TODO: Lastverteilung
	public int getPort() {
		return 7777;
	}

	public void sendMessage(ServerMessage message) {
		try {
			CSLogger.log(Client.class, "Sending %s", message);
			out.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void messageReceived(ServerMessage message) {
		CSLogger.log(Client.class, "Message received: %s", message);
		switch (message.prefix.toLowerCase()) {
		case "wrongcredentials":
			ChatSystem.frameSignIn.wrongCredentials();
			break;
		case "usernametaken":
			ChatSystem.frameSignIn.usernameTaken();
			break;
		case "signedin":
			System.out.println("Welcome " + message.object);
			break;
		}
	}

}
