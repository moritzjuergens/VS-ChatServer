package ChatSystem.Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import ChatSystem.CSLogger;
import ChatSystem.DWH.Warehouse;
import ChatSystem.Entities.Contact;
import ChatSystem.Entities.Contact.ContactType;
import ChatSystem.Entities.Group;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Entities.User;
import ChatSystem.Packets.AddToPacket;
import ChatSystem.Packets.AllContactsPacket;
import ChatSystem.Packets.CreateGroupPacket;
import ChatSystem.Packets.SendMessagePacket;
import ChatSystem.Packets.SignInUpPacket;
import ChatSystem.Packets.WelcomePacket;

public class Server {

	public static List<Server> registeredServer = new ArrayList<Server>();
	public static List<Integer> registeredPorts = new ArrayList<Integer>();
	private List<User> users = new ArrayList<User>();
	private ServerSocket ss;
	public Contact system = new Contact("System", ContactType.SYSTEM);
	public int port;
	private long lastHeartbeat = 0;
	private long currentHeartbeat = System.currentTimeMillis();
	private Warehouse warehouse;

	public Server(int port) {

		this.port = port;
		this.warehouse = new Warehouse(this);
		this.warehouse.loadFiles();

//		Nur Testzwecke, am Ende entfernen
		User ti = new User("Timo", "Pass");
		User eg = new User("Eger", "Pass");
		warehouse.addUser(ti);
		warehouse.addUser(eg);
		warehouse.addMessage(new Message(ti.getContact(), eg.getContact(), "Hallo Welt"));
		warehouse.addMessage(new Message(eg.getContact(), ti.getContact(), "Na s��er"));

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
				new HeartbeatThread(this);
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

	public void registerClient(User u, ObjectOutputStream out) {
		if (users.contains(u)) {
			this.sendMessage(out, new ServerMessage("signresponse", "You're already signed in"));
			return;
		}
		u.out = out;
		users.add(u);
		this.sendMessage(out, new ServerMessage("welcome", new WelcomePacket(u, warehouse.getUserData(u))));
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
		CSLogger.log(Server.class, "Sending %s to %s", m, u.getContact());
		try {
			if (users.contains(u)) {
				u.out.writeObject(m);
			} else {
				// TODO: Anderen Servern nachricht schicken
				for (Server s : Server.registeredServer) {
					try {
						if (s != this) {
							Socket serverClient = new Socket("localhost", s.getPort());
							var outServer = new ObjectOutputStream(serverClient.getOutputStream());
							outServer = new ObjectOutputStream(serverClient.getOutputStream());
							outServer.writeObject(new ServerMessage("message", m));
							serverClient.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
		}
	}

	int getPort() {
		return port;
	}

	public void sendMessage(User sender, User receiver, String message) {
		CSLogger.log(Server.class, "Sending %s from %s to %s", message, sender.getContact(), receiver.getContact());
		Message m = new Message(sender.getContact(), receiver.getContact(), message);
		ServerMessage sm = new ServerMessage("message", m);
		sendMessage(receiver, sm);
	}

	public void messageReceived(ServerMessage message, ObjectOutputStream out) {
		CSLogger.log(Server.class, "Message received: %s", message);

		switch (message.prefix.toLowerCase()) {
		case "signin":
			synchronized (users) {
				SignInUpPacket data = (SignInUpPacket) message.object;
				if (!warehouse.doesUserExist(data.name)) {
					sendMessage(out, new ServerMessage("signresponse", "Username or Password incorrect"));
					break;
				}
				User u = warehouse.getUser(data.name);
				if (!(u.name.equals(data.name) && u.password.equals(data.password))) {
					sendMessage(out, new ServerMessage("signresponse", "Username or Password incorrect"));
					break;
				}
				synchronized (users) {
					this.registerClient(u, out);
				}

			}
			break;
		case "signup":
			synchronized (users) {
				SignInUpPacket data = (SignInUpPacket) message.object;
				if (warehouse.doesUserExist(data.name)) {
					sendMessage(out, new ServerMessage("signresponse", "Username already taken"));
					break;
				}
				User u = new User(data.name, data.password);
				warehouse.addUser(u);
				synchronized (users) {
					this.registerClient(u, out);
				}
			}
			break;
		case "logoff":
			if (message.object == null)
				break;
			synchronized (users) {
				users = users.stream().filter(x -> !x.equals((User) message.object)).collect(Collectors.toList());
			}
			break;
		case "allcontacts":
			sendMessage(out, new ServerMessage("allcontacts", new AllContactsPacket(warehouse.getAllUser())));
			break;
		case "addto":
			AddToPacket atp = (AddToPacket) message.object;
			Contact invitee = atp.invitee;
			if (atp.contact.type.equals(ContactType.GROUP)) {
				Group g = warehouse.getGroupById(atp.contact.name);
				if (warehouse.addUserToGroup(invitee, g)) {
					Message m = new Message(system, atp.contact, invitee.name + " has joined the group!");
					ServerMessage sm = new ServerMessage("message", m);
					for (Contact c : g.members) {
						sendMessage(warehouse.getUser(c), sm);
					}
				}

			} else {
				Message m = new Message(atp.contact, invitee, "Hi, I'd like to chat with you :GRINNING_FACE:");
				ServerMessage sm = new ServerMessage("message", m);
				sendMessage(out, sm);
				sendMessage(out, new ServerMessage("openchat", invitee));
				sendMessage(warehouse.getUser(invitee), sm);
			}
			break;
		case "creategroup":
			CreateGroupPacket cgp = (CreateGroupPacket) message.object;
			Group g = new Group(cgp.user, cgp.chat, cgp.selected);
			warehouse.addGroup(g);

			Message mc = new Message(system, g.getContact(), cgp.user.name + " created this group");
			ServerMessage smc = new ServerMessage("message", mc);
			for (Contact c : g.members) {
				Message m = new Message(system, g.getContact(), c.name + " has joined the group!");
				ServerMessage sm = new ServerMessage("message", m);
				sendMessage(warehouse.getUser(c), smc);
				for (Contact c_ : g.members) {
					sendMessage(warehouse.getUser(c_), sm);
				}
			}
			sendMessage(out, new ServerMessage("openchat", g.getContact()));
			break;
		case "message":
			SendMessagePacket sm = (SendMessagePacket) message.object;
			Message m = new Message(sm.sender, sm.receiver, sm.message);
			warehouse.addMessage(m);
			if (sm.receiver.type.equals(ContactType.GROUP)) {
				for (Contact c : warehouse.getGroupsById(sm.receiver.name).get(0).members) {
					sendMessage(warehouse.getUser(c), new ServerMessage("message", m));
				}
			} else {
				sendMessage(warehouse.getUser(sm.receiver), new ServerMessage("message", m));
				sendMessage(warehouse.getUser(sm.sender), new ServerMessage("message", m));
			}
			break;
		case "heartbeat":
			if (currentHeartbeat - lastHeartbeat > 5000) {
				CSLogger.log(Server.class, "Oh he dead ", message);
			}
			lastHeartbeat = currentHeartbeat;
			break;
		case "serverlogin":
			var newServer = (int)message.object;
			registeredPorts.add(newServer);
			System.out.println("Okay" + port);
		}
	}
}
