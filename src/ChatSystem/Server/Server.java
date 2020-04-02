package ChatSystem.Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import ChatSystem.CSLogger;
import ChatSystem.DWH.Warehouse;
import ChatSystem.Entities.Messages;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Entities.SignInUp;
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
				CSLogger.log(Server.class, "Server listening on port %s", port);
				Server.registeredServer.add(this);
			}
		}).start();
	}

	public static void closeAll() {
		CSLogger.log(Server.class, "Shutting down all Server...");
		for (Server s : registeredServer) {
			s.close();
		}
	}

	private void close() {
		try {
			CSLogger.log(Server.class, "Shutting down Server %s", ss.getLocalPort());
			ss.close();
		} catch (IOException | NullPointerException e) {
		}
	}

	public void registerClient(User u) {

	}

	public void unregisterClient(User u) {
		this.users.remove(u);
	}

	public void sendMessage(ObjectOutputStream out, ServerMessage m) {
		try {
			out.writeObject(m);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(User u, ServerMessage m) {
		if (users.contains(u)) {
			CSLogger.log(Server.class, "Sending %s to $s", m, u);
			try {
				u.out.writeObject(m);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void messageReceived(ServerMessage message, ObjectOutputStream out) {
		CSLogger.log(Server.class, "Message received: %s", message);

		switch (message.prefix.toLowerCase()) {
		case "login":
			synchronized (users) {
				User u = Warehouse.getUser(message.object.toString());
				u.out = out;
				users.add(u);
				sendMessage(u, new ServerMessage("Hi, ich kenn dich jz", u));
			}
			break;
		case "signin":
			synchronized (users) {
				SignInUp data = (SignInUp) message.object;
				if (!Warehouse.doesUserExist(data.name)) {
					sendMessage(out, new ServerMessage("wrongcredentials", null));
					break;
				}
				User u = Warehouse.getUser(data.name);
				if (!(u.name.equals(data.name) && u.password.equals(data.password))) {
					sendMessage(out, new ServerMessage("wrongcredentials", null));
					break;
				}
				synchronized (users) {
					u.out = out;
					this.users.add(u);
					sendMessage(out, new ServerMessage("welcome", u));
				}

			}
			break;
		case "signup":
			synchronized (users) {
				SignInUp data = (SignInUp) message.object;
				if (Warehouse.doesUserExist(data.name)) {
					sendMessage(out, new ServerMessage("usernametaken", null));
					break;
				}
				User u = new User(data.name, data.password);
				synchronized (users) {
					u.out = out;
					this.users.add(u);
					sendMessage(out, new ServerMessage("welcome", u));
				}
			}
			break;
		case "getcontacts":
			User target = (User) message.object;
			sendMessage(out, new ServerMessage("contacts", Warehouse.getContactsOf(target)));
			break;
		case "getmessages":
			Messages messages = (Messages) message.object;
			sendMessage(out, new ServerMessage("messages", Warehouse.getMessages(messages.user, messages.contact)));
			break;
		}
	}
}
