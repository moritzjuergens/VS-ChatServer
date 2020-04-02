package ChatSystem.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ChatSystem.CSLogger;
import ChatSystem.Entities.Contact;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Entities.User;
import ChatSystem.Frontend.SignIn;
import ChatSystem.Frontend.Chat.Chat;

public class Client extends Thread {

	public static List<Client> registeredClients = new ArrayList<Client>();
	public Chat chat;

	Socket s;
	ObjectInputStream in;
	ObjectOutputStream out;
	SignIn frameSignIn;

	public Client() {

		this.frameSignIn = new SignIn(this);

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
		return 7777;
	}

	public void sendMessage(ServerMessage message) {
		try {
			CSLogger.log(Client.class, "Sending %s", message);
			out.writeObject(message);
		} catch (IOException e) {
			CSLogger.log(Client.class, "Der Server ist nicht erreichbar");
		}
	}

	@SuppressWarnings("unchecked")
	public void messageReceived(ServerMessage message) {
		CSLogger.log(Client.class, "Message received: %s", message);
		switch (message.prefix.toLowerCase()) {
		case "wrongcredentials":
			frameSignIn.wrongCredentials();
			break;
		case "usernametaken":
			frameSignIn.usernameTaken();
			break;
		case "welcome":
			User me = (User) message.object;
			frameSignIn.setVisible(false);
			chat = new Chat(me, this);
			sendMessage(new ServerMessage("getcontacts", me.getContact()));
			break;
		case "contacts":
			List<Contact> contacts = (List<Contact>) message.object;
			chat.updateContacts(contacts);
			break;
		case "messages":
			List<Message> messages = (List<Message>) message.object;
			chat.setMessages(messages);
			break;
		case "alluser":
			List<Contact> users = (List<Contact>) message.object;
			chat.updateUserList(users);
			break;
		case "message":
			Message m = (Message) message.object;
			chat.messageReceived(m);
			break;
		}
	}
}
