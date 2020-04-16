package ChatSystem.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ChatSystem.CSLogger;
import ChatSystem.Controller;
import ChatSystem.Entities.Contact;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Frontend.ChatManager;
import ChatSystem.Frontend.Frames.LoginFrame;
import ChatSystem.Packets.AllContactsPacket;
import ChatSystem.Packets.SignInUpPacket;
import ChatSystem.Packets.WelcomePacket;
import ChatSystem.Server.Server;

public class Client extends Thread {

	public static List<Client> registeredClients = new ArrayList<Client>();
	public ChatManager chat;
	public int port;
	public List<String> portsToTrie = new ArrayList<>();
	public Controller controllerUI;
	public long lastTimeStamp = 0;
	private List<ServerMessage> messageQueue = new ArrayList<>();
	private boolean reconnecting = false;

	Socket s;
	ObjectInputStream in;
	ObjectOutputStream out;
	LoginFrame loginFrame;

	public Client(Controller controllerUI) {
		this.controllerUI = controllerUI;
		portsToTrie = Arrays.stream(Server.portRange).map(x -> "" + x).collect(Collectors.toList());
		this.port = getUntriedPort();

		Client.registeredClients.add(this);
		connect();
		this.loginFrame = new LoginFrame(this);
	}

	public void connect() {
		this.controllerUI.updateClients();
		new Thread(() -> {
			try {
				s = new Socket("localhost", port);
				out = new ObjectOutputStream(s.getOutputStream());
				in = new ObjectInputStream(s.getInputStream());

				new Thread(() -> {
					while (true) {
						try {
							ServerMessage m = (ServerMessage) in.readObject();
							this.messageReceived(m);
						} catch (ClassNotFoundException | IOException e) {
						}
					}
				}).start();
				portsToTrie = Arrays.stream(Server.portRange).map(x -> "" + x).collect(Collectors.toList());
				CSLogger.log(Client.class, "Client using server %s", port);
				reconnecting = false;
				messageQueue.stream().forEach(this::sendMessage);
				messageQueue.clear();

				if (chat != null && chat.user != null) {
					sendMessage(new ServerMessage("signin",
							new SignInUpPacket(chat.user.name, chat.user.password, true, lastTimeStamp)));
				}

			} catch (IOException e) {
				CSLogger.log(Client.class, "Client cant use Server %s", port);

				portsToTrie.remove(port + "");
				if (portsToTrie.size() > 0) {
					this.port = getUntriedPort();
					connect();
				} else {
					CSLogger.log(Client.class, "Client cant use any Server. All offline!");
				}
			}
		}).start();

	}

	public void close() {
		try {
			in.close();
			out.close();
			s.close();
		} catch (IOException e) {
//			e.printStackTrace();
		}

	}

	public static void closeAll() {
		CSLogger.log(Client.class, "Shutting down all Clients...");
		for (Client c : registeredClients) {
			c.close();
		}
	}

	public int getUntriedPort() {
		return Integer.parseInt(portsToTrie.get(new Random().nextInt(portsToTrie.size())));
	}

	public void sendMessage(ServerMessage message) {
		if (reconnecting) {
			messageQueue.add(message);
			return;
		}
		try {
			CSLogger.log(Client.class, "Sending %s", message);
			out.writeObject(new ServerMessage("ping", "pong"));
			out.writeObject(new ServerMessage("clinton", "pong"));
			out.writeObject(message);
		} catch (IOException e) {
			close();
			CSLogger.log(Client.class, "Der Server ist nicht erreichbar");

			this.reconnecting = true;
			this.lastTimeStamp = System.currentTimeMillis();

			portsToTrie.remove("" + port);
			messageQueue.add(message);

			connect();
		}
	}

	public void messageReceived(ServerMessage message) {
		CSLogger.log(Client.class, "Message received: %s", message);
		if (message == null)
			return;
		switch (message.prefix.toLowerCase()) {
		case "signresponse":
			loginFrame.signResponseReceived((String) message.object);
			break;
		case "welcome":
			loginFrame.setVisible(false);
			chat = new ChatManager((WelcomePacket) message.object, this);
			this.controllerUI.updateClients();
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
