package ChatSystem.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ChatSystem.CSLogger;
import ChatSystem.Entities.Contact;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Frontend.ChatManager;
import ChatSystem.Frontend.Frames.LoginFrame;
import ChatSystem.Packets.AllContactsPacket;
import ChatSystem.Packets.WelcomePacket;

public class Client extends Thread {

	public static List<Client> registeredClients = new ArrayList<Client>();
	public ChatManager chat;

	Socket s;
	ObjectInputStream in;
	ObjectOutputStream out;
	int[] portRange= {
		7777,7778,7779,7780
	};
	LoginFrame loginFrame;

	public Client() {

		this.loginFrame = new LoginFrame(this);

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
				CSLogger.log(Client.class, "Client using server %s", port);
			} catch (IOException e) {
				CSLogger.log(Client.class, "Client cant use Server %s", port);
			} finally {
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
		return getRandomElement(portRange);
	}

	public int getRandomElement(int[] arr)
	{
		Random r=new Random();
		int randomNumber=r.nextInt(arr.length);
		return arr[randomNumber];
	}

	public void sendMessage(ServerMessage message) {
		try {
			CSLogger.log(Client.class, "Sending %s", message);
			out.writeObject(message);
		} catch (IOException e) {
			CSLogger.log(Client.class, "Der Server ist nicht erreichbar");
		}
	}

	public void messageReceived(ServerMessage message) {
		CSLogger.log(Client.class, "Message received: %s", message);
		switch (message.prefix.toLowerCase()) {
		case "signresponse":
			loginFrame.signResponseReceived((String) message.object);
			break;
		case "welcome":
			loginFrame.setVisible(false);
			chat = new ChatManager((WelcomePacket) message.object, this);
			break;
		case "allcontacts":
			chat.contactListReceived(((AllContactsPacket) message.object).clients);
			break;
		case "message":
			chat.messageReceived((Message) message.object);
			break;
		case "openchat":
			chat.openChatWith((Contact) message.object);
			break;
		}
	}
}
