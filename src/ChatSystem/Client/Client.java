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

	/**
	 * Creates a new client and connects him to a random server
	 * 
	 * @param controllerUI controllerUI to update on changes
	 */
	public Client(Controller controllerUI) {
		this(controllerUI, -1);
	}

	/**
	 * Creates a new client and connects him to a given server
	 * 
	 * @param controllerUI controllerUI to update on changes
	 */
	public Client(Controller controllerUI, int port) {
		this.controllerUI = controllerUI;

		// add all available ports to try
		portsToTrie = Arrays.stream(Server.portRange).map(x -> "" + x).collect(Collectors.toList());
		this.port = port == -1 ? getUntriedPort() : port;

		Client.registeredClients.add(this);

		// Connect to a server and open the Loginframe
		connect();
		this.loginFrame = new LoginFrame(this);
	}

	/**
	 * connect Client to a random Server
	 */
	public void connect() {
		this.controllerUI.updateClients();
		new Thread(() -> {
			try {

				// Open conection to Server
				s = new Socket("localhost", port);
				out = new ObjectOutputStream(s.getOutputStream());
				in = new ObjectInputStream(s.getInputStream());
				CSLogger.log(Client.class, "Client using server %s", port);
				reconnecting = false;

				// New Thread for incoming messages
				new Thread(() -> {
					while (!reconnecting) {
						try {
							// Message received and forwarded for further actions
							ServerMessage m = (ServerMessage) in.readObject();
							this.messageReceived(m);
						} catch (ClassNotFoundException | IOException e) {
						}
					}
				}).start();

				// Refill portlist for later uses
				portsToTrie = Arrays.stream(Server.portRange).map(x -> "" + x).collect(Collectors.toList());

				// Resend all messages, waiting to be send while connection hadn't been
				// established
				messageQueue.stream().forEach(this::sendMessage);
				messageQueue.clear();

				// Resend SignInUpPacket if user had already been signed in, but lost connection
				// to a server
				if (chat != null && chat.user != null) {
					sendMessage(new ServerMessage("signin",
							new SignInUpPacket(chat.user.name, chat.user.password, true, lastTimeStamp)));
				}

			} catch (IOException e) {
				CSLogger.log(Client.class, "Client cant use Server %s", port);

				// Remove currently tried port, try new port if one is available
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

	/**
	 * reconnect to an other server
	 */
	public void reconnect() {
		if(reconnecting) return;
		this.reconnecting = true;
		close();

		// Server is no longer available, remove current port and retry with a different
		// port
		this.lastTimeStamp = System.currentTimeMillis();
		portsToTrie.remove("" + port);
		connect();
	}

	/**
	 * close open connections
	 */
	public void close() {
		try {
			in.close();
			out.close();
			s.close();
		} catch (IOException e) {
			// e.printStackTrace();
		}

	}

	/**
	 * close all open connections
	 */
	public static void closeAll() {
		CSLogger.log(Client.class, "Shutting down all Clients...");
		for (Client c : registeredClients) {
			c.close();
		}
	}

	/**
	 * Get a random port from the untried-port-pool
	 * 
	 * @return random port used to connect to a server
	 */
	public int getUntriedPort() {
		return Integer.parseInt(portsToTrie.get(new Random().nextInt(portsToTrie.size())));
	}

	/**
	 * Send a message to the currently connected Server
	 * 
	 * @param message ServerMessage to be sent
	 */
	public void sendMessage(ServerMessage message) {

		// if client is currently reconnection store message in vault and send after new
		// connection has been established
		if (reconnecting) {
			messageQueue.add(message);
			return;
		}

		try {
			CSLogger.log(Client.class, "Sending %s", message);
			out.writeObject(new ServerMessage("", ""));
			out.writeObject(new ServerMessage("", ""));
			out.writeObject(message);
		} catch (IOException e) {
			CSLogger.log(Client.class, "Der Server ist nicht mehr erreichbar");

			messageQueue.add(message);
			reconnect();
		}
	}

	/**
	 * Gets called whenever the client receives a message from the server
	 * 
	 * @param message ServerMessage received
	 */
	public void messageReceived(ServerMessage message) {

		CSLogger.log(Client.class, "Message received: %s", message);

		if (message == null)
			return;

		// Message received and is not null
		switch (message.prefix.toLowerCase()) {

		// Server hasn't verfied Login, Reason forwarded to loginFrame
		case "signresponse":
			loginFrame.signResponseReceived((String) message.object);
			break;

		// Server has verfied Login
		case "welcome":
			loginFrame.setVisible(false);
			chat = new ChatManager((WelcomePacket) message.object, this);
			this.controllerUI.updateClients();
			break;

		// Every available contact has been received (for new chat or group invitation)
		case "allcontacts":
			chat.contactListReceived(((AllContactsPacket) message.object).clients);
			break;

		// Client has received a new chatmessage
		case "message":
			chat.messageReceived((Message) message.object);
			break;

		// New group has successfully been created, client automatically opens groupchat
		case "openchat":
			chat.openChatWith((Contact) message.object);
			break;

		// If server was able to send shutdown message to client
		case "reconnect":
			reconnect();
			break;

		}
	}
}
