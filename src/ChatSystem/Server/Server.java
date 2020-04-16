package ChatSystem.Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import ChatSystem.CSLogger;
import ChatSystem.Controller;
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
import ChatSystem.Packets.FetchPacket;
import ChatSystem.Packets.SendMessagePacket;
import ChatSystem.Packets.SignInUpPacket;
import ChatSystem.Packets.WelcomePacket;

public class Server {

	public static List<Server> registeredServer = new ArrayList<Server>();
	public static Integer[] portRange = new Integer[] { 7777, 7778, 7779, 7780 };

	private List<User> users = new ArrayList<User>();
	public List<String> registeredPorts = new ArrayList<>();
	protected ServerSocket ss;

	public boolean shutdown = false;
	public Contact system = new Contact("System", ContactType.SYSTEM);
	public int port;
	public long lastHeartbeat = 0;
	private Warehouse warehouse;

	private boolean firstBroadcast = true;
	public Controller controllerUI;

	public Server(Controller controllerUI, int port) {
		this.controllerUI = controllerUI;
		this.port = port;
		this.warehouse = new Warehouse(this);
		this.warehouse.loadFiles();
		Server.registeredServer.add(this);
		controllerUI.updateServer();

		new Thread(() -> {
			try {
				ss = new ServerSocket(port);
				var pool = Executors.newCachedThreadPool();
				new Thread(() -> {
					while (!shutdown) {
						try {
							pool.execute(new ServerThread(this, ss.accept()));
						} catch (IOException e) {
							break;
						}
					}
				}).start();
				broadcast(new ServerMessage("heartbeat", getPort()), Arrays.asList(portRange));

			} catch (IOException e) {
			} finally {
				CSLogger.log(Server.class, "Server listening on port %s", port);
			}
		}).start();
	}

	protected int getPort() {
		return port;
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
			ss = null;
		} catch (IOException | NullPointerException e) {
		}
	}

	public void registerClient(User u, ObjectOutputStream out, SignInUpPacket data) {
		if(users.stream().filter(x -> x.equals(u)).count() > 0) {			
			this.sendMessage(out, new ServerMessage("signresponse", "You're already signed in"));
			return;
		}
		u.out = out;
		users.add(u);
		this.controllerUI.updateServer();
		if (!data.alreadySignedIn) {
			this.sendMessage(out, new ServerMessage("welcome", new WelcomePacket(u, warehouse.getUserData(u))));
		} else {
			for (Message m : warehouse.getMessagesForUserAfter(u.getContact(), data.timestamp)) {
				sendMessage(out, new ServerMessage("message", m));
			}
		}
	}

	public void unregisterClient(User u) {
		this.users.remove(u);
		this.controllerUI.updateServer();
	}

	public int clientCount() {
		return this.users.size();
	}

	public void sendMessage(ObjectOutputStream out, ServerMessage m) {
		try {
			out.writeObject(m);
			out.flush();
		} catch (IOException e) {
//			e.printStackTrace();
		}
	}

	public void sendMessage(Contact c, ServerMessage m) {
		CSLogger.log(Server.class, "[%s\t] Sending %s to %s", getPort(), m, c);
		users.stream().forEach(x -> {
			if(x.name.equals(c.name)) {
				sendMessage(x.out, m);
			}
		});
	}

	public void broadcast(ServerMessage sm, List<Integer> ports) {
		new Thread(() -> {
			for (int port : ports) {
				try {
					if (port != getPort()) {
						CSLogger.log(Server.class, "[%s\t] Broadcasting %s to %s", getPort(), sm, port);
						Socket serverClient = new Socket("localhost", port);
						ObjectOutputStream outServer = new ObjectOutputStream(serverClient.getOutputStream());
						outServer.writeObject(sm);

						if (firstBroadcast) {
							firstBroadcast = false;
							ServerMessage fm = new ServerMessage("fetch", lastHeartbeat + "_" + this.port);
							outServer.writeObject(fm);
						}

						outServer.flush();
						outServer.close();
						serverClient.close();
						if (!registeredPorts.contains("" + port)) {
							registeredPorts.add("" + port);
						}

					}
				} catch (IOException e) {
					CSLogger.log(Server.class, "[%s\t] Cant connect to Server: %s, removeing from list", getPort(),
							port);
					registeredPorts.remove("" + port);
				}
			}
		}).start();
	}

	public void broadcast(ServerMessage sm) {
		synchronized (registeredPorts) {
			broadcast(sm, registeredPorts.stream().map(x -> Integer.parseInt(x)).collect(Collectors.toList()));
		}
	}

	public void messageReceived(ServerMessage message, ObjectOutputStream out) {
		CSLogger.log(Server.class, "[%s\t] Message received: %s", getPort(), message);

		if (message.equals(null))
			return;

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
					this.registerClient(u, out, data);
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
				broadcast(new ServerMessage("user", u));
				warehouse.addUser(u);

				synchronized (users) {
					this.registerClient(u, out, data);
				}
			}
			break;
		case "user":
			warehouse.addUser((User) message.object);
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

			if (atp.forward) {
				broadcast(new ServerMessage("addto", atp.forward(false)));
			} else {
				warehouse.updateHeartBeat(System.currentTimeMillis());
			}

			Contact invitee = atp.invitee;
			if (atp.contact.type.equals(ContactType.GROUP)) {
				Group g = warehouse.getGroupById(atp.contact.name);
				if (warehouse.addUserToGroup(invitee, g)) {
					Message m = new Message(system, atp.contact, invitee.name + " has joined the group!");
					ServerMessage sm = new ServerMessage("message", m);
					for (Contact c : g.members) {
						sendMessage(c, sm);
					}
				}

			} else {
				Message m = new Message(atp.contact, invitee, "Hi, I'd like to chat with you :GRINNING_FACE:");
				ServerMessage sm = new ServerMessage("message", m);
				sendMessage(out, sm);
				sendMessage(out, new ServerMessage("openchat", invitee));
				sendMessage(invitee, sm);
			}
			break;
		case "group":
			warehouse.updateHeartBeat(System.currentTimeMillis());
			warehouse.addGroup((Group) message.object);
			break;
		case "creategroup":
			CreateGroupPacket cgp = (CreateGroupPacket) message.object;
			Group g = new Group(cgp.user, cgp.chat, cgp.selected);
			broadcast(new ServerMessage("group", g));

			warehouse.addGroup(g);

			Message mc = new Message(system, g.getContact(), cgp.user.name + " created this group");
			broadcast(new ServerMessage("message", new SendMessagePacket(mc.sender, mc.receiver, mc.message, false)));

			ServerMessage smc = new ServerMessage("message", mc);
			for (Contact c : g.members) {

				Message m = new Message(system, g.getContact(), c.name + " has joined the group!");
				broadcast(new ServerMessage("message", new SendMessagePacket(m.sender, m.receiver, m.message, false)));

				ServerMessage sm = new ServerMessage("message", m);
				sendMessage(c, smc);
				for (Contact c_ : g.members) {
					sendMessage(c_, sm);
				}
			}
			sendMessage(out, new ServerMessage("openchat", g.getContact()));
			break;
		case "message":
			SendMessagePacket sm = (SendMessagePacket) message.object;
			if (sm.forward) {
				this.broadcast(new ServerMessage("message", sm.forward(false)));
			} else {
				warehouse.updateHeartBeat(System.currentTimeMillis());
			}

			Message m = new Message(sm.sender, sm.receiver, sm.message);
			warehouse.addMessage(m);
			if (sm.receiver.type.equals(ContactType.GROUP)) {
				for (Contact c : warehouse.getGroupsById(sm.receiver.name).get(0).members) {
					sendMessage(c, new ServerMessage("message", m));
				}
			} else {
				sendMessage(sm.receiver, new ServerMessage("message", m));
				sendMessage(sm.sender, new ServerMessage("message", m));
			}
			break;
		case "heartbeat":
			synchronized (registeredPorts) {
				String port = "" + ((int) message.object);
				if (!registeredPorts.contains(port)) {
					registeredPorts.add(port);
				}
			}
			break;
		case "fetch":
			String[] split = ((String) message.object).split("_");
			long timestamp = Long.parseLong(split[0]);
			Integer[] port = new Integer[] { Integer.parseInt(split[1]) };

			FetchPacket fp = new FetchPacket(warehouse.getMessagesAfter(timestamp), warehouse.getGroupsAfter(timestamp),
					warehouse.getUsersAfter(timestamp));
			broadcast(new ServerMessage("fetchreceive", fp), Arrays.asList(port));

			break;
		case "fetchreceive":
			FetchPacket fpr = (FetchPacket) message.object;
			for (Message messages : fpr.messages) {
				warehouse.addMessage(messages);
			}
			for (Group groups : fpr.groups) {
				warehouse.addGroup(groups);
			}
			for (User users : fpr.users) {
				warehouse.addUser(users);
			}
			break;
		}
	}

	public void shutdown() {
		this.shutdown = true;
		close();
		Server.registeredServer.remove(this);
		this.controllerUI.updateServer();
	}
}