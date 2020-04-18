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

	/**
	 * Create and start a new server
	 * 
	 * @param controllerUI
	 * @param port         int
	 */
	public Server(Controller controllerUI, int port) {
		this.controllerUI = controllerUI;
		this.port = port;

		// Create warehouse and load files
		this.warehouse = new Warehouse(this);
		this.warehouse.loadFiles();

		Server.registeredServer.add(this);
		controllerUI.updateServer();

		// open ServerSocket
		new Thread(() -> {
			try {
				ss = new ServerSocket(port);
				var pool = Executors.newCachedThreadPool();

				// Start Thread to allow connections
				new Thread(() -> {
					while (!shutdown) {
						try {
							pool.execute(new ServerThread(this, ss.accept()));
						} catch (IOException e) {
							break;
						}
					}
					
					// Inform clients about shutdown, if possible
					for(User u : users) {
						sendMessage(u.out, new ServerMessage("reconnect", ""));
					}
				}).start();

				// Fetch missed messages
				broadcast(new ServerMessage("heartbeat", getPort()), Arrays.asList(portRange));

			} catch (IOException e) {
			} finally {
				CSLogger.log(Server.class, "[%s\t] Listening on port %s", getPort(), port);
			}
		}).start();
	}

	/**
	 * Returns server port
	 * 
	 * @return port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * close all Server
	 */
	public static void closeAll() {
		CSLogger.log(Server.class, "Shutting down all Server...");
		for (Server s : registeredServer) {
			s.close();
		}
	}

	/**
	 * close Server (shutdown)
	 */
	private void close() {
		try {
			CSLogger.log(Server.class, "[%s\t] Shutting down", getPort());
			ss.close();
			ss = null;
		} catch (IOException | NullPointerException e) {
		}
	}

	/**
	 * Register a new client
	 * 
	 * @param u    User
	 * @param out  User's outputstream for communication
	 * @param data SignInUpPacket's data
	 */
	public void registerClient(User u, ObjectOutputStream out, SignInUpPacket data) {

		// Check if users is already signed in on server
		if (users.stream().filter(x -> x.equals(u)).count() > 0) {
			this.sendMessage(out, new ServerMessage("signresponse", "You're already signed in"));
			return;
		}

		// update user to add outputstream and add to list
		u.out = out;
		users.add(u);
		this.controllerUI.updateServer();
		
		// check if user had already been signed in on a different server
		if (!data.alreadySignedIn) {
			
			// user hasnt been signed in, send welcome packet (all chats and contacts)
			this.sendMessage(out, new ServerMessage("welcome", new WelcomePacket(u, warehouse.getUserData(u))));
		} else {

			// user had been signed in, send all misses messages
			for (Message m : warehouse.getMessagesForUserAfter(u.getContact(), data.timestamp)) {
				sendMessage(out, new ServerMessage("message", m));
			}
		}
	}

	/**
	 * unregister User
	 * 
	 * @param u User to unregister
	 */
	public void unregisterClient(User u) {
		this.users.remove(u);
		this.controllerUI.updateServer();
	}

	/**
	 * used in controllerUI
	 * 
	 * @return current amount of connected clients
	 */
	public int clientCount() {
		return this.users.size();
	}

	// Send a message
	public void sendMessage(ObjectOutputStream out, ServerMessage m) {
		try {
			out.writeObject(m);
			out.flush();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	/**
	 * send a message to a specific Contact
	 * 
	 * @param c receiver
	 * @param m message
	 */
	public void sendMessage(Contact c, ServerMessage m) {
		CSLogger.log(Server.class, "[%s\t] Sending %s to %s", getPort(), m, c);

		// check if user is registered => send message
		users.stream().forEach(x -> {
			if (x.name.equals(c.name)) {
				sendMessage(x.out, m);
			}
		});
	}

	/**
	 * broadcast a message to multiple server
	 * 
	 * @param sm    message to be broadcasted
	 * @param ports server ports
	 */
	public void broadcast(ServerMessage sm, List<Integer> ports) {

		// start a new thread to run in the background
		new Thread(() -> {

			// iterate through every port and skip yourself
			for (int port : ports) {
				try {
					if (port != getPort()) {

						CSLogger.log(Server.class, "[%s\t] Broadcasting %s to %s", getPort(), sm, port);

						// open connection to server and send message
						Socket serverClient = new Socket("localhost", port);
						ObjectOutputStream outServer = new ObjectOutputStream(serverClient.getOutputStream());
						outServer.writeObject(sm);

						// if its the first successful broadcast, request every missed message, user,
						// group from that specific server
						if (firstBroadcast) {
							firstBroadcast = false;
							ServerMessage fm = new ServerMessage("fetch", lastHeartbeat + "_" + this.port);
							outServer.writeObject(fm);
						}

						// close connection
						outServer.flush();
						outServer.close();
						serverClient.close();

						// add port to available server if not already done
						if (!registeredPorts.contains("" + port)) {
							registeredPorts.add("" + port);
						}

					}
				} catch (IOException e) {
					CSLogger.log(Server.class, "[%s\t] Cant connect to Server: %s, removeing from list", getPort(),
							port);

					// Server cant be reached, so remove from available server
					registeredPorts.remove("" + port);
				}
			}
		}).start();
	}

	/**
	 * broadcast a given ServerMessage to all available server
	 * 
	 * @param sm
	 */
	public void broadcast(ServerMessage sm) {
		synchronized (registeredPorts) {
			broadcast(sm, registeredPorts.stream().map(x -> Integer.parseInt(x)).collect(Collectors.toList()));
		}
	}

	/**
	 * ServerThread has received a message and forwarded it.
	 * 
	 * @param message ServerMessage received
	 * @param out     Outputstream for anwsers
	 */
	public void messageReceived(ServerMessage message, ObjectOutputStream out) {
		CSLogger.log(Server.class, "[%s\t] Message received: %s", getPort(), message);

		// check if message exists and handle prefix.
		if (message.equals(null))
			return;

		switch (message.prefix.toLowerCase()) {

		// Client wants to sign in on server
		case "signin":
			synchronized (users) {
				SignInUpPacket data = (SignInUpPacket) message.object;

				// check if username exists
				if (!warehouse.doesUserExist(data.name)) {
					sendMessage(out, new ServerMessage("signresponse", "Username or Password incorrect"));
					break;
				}

				// retrieve user and compare name and password
				User u = warehouse.getUser(data.name);
				if (!(u.name.equals(data.name) && u.password.equals(data.password))) {
					sendMessage(out, new ServerMessage("signresponse", "Username or Password incorrect"));
					break;
				}

				// register client
				synchronized (users) {
					this.registerClient(u, out, data);
				}

			}
			break;

		// Client wants to sign up on server
		case "signup":
			synchronized (users) {
				SignInUpPacket data = (SignInUpPacket) message.object;

				// check if user name is already taken
				if (warehouse.doesUserExist(data.name)) {
					sendMessage(out, new ServerMessage("signresponse", "Username already taken"));
					break;
				}

				// create new server, broadcast object to other server, store user in warehouae
				User u = new User(data.name, data.password);
				broadcast(new ServerMessage("user", u));
				warehouse.addUser(u);

				// register client
				synchronized (users) {
					this.registerClient(u, out, data);
				}
			}
			break;

		// other server has created a new user and broadcasted it to me
		case "user":
			warehouse.addUser((User) message.object);
			break;

		// client close window
		case "logoff":
			if (message.object == null)
				break;

			// remove user from registerd clients
			synchronized (users) {
				users = users.stream().filter(x -> !x.equals((User) message.object)).collect(Collectors.toList());
			}
			break;

		// client requests all available contact
		case "allcontacts":
			sendMessage(out, new ServerMessage("allcontacts", new AllContactsPacket(warehouse.getAllUser())));
			break;

		// Some User wants to add a Client to a specific Chat
		case "addto":
			AddToPacket atp = (AddToPacket) message.object;

			// if still on forward, forward with no further forward
			// if not, update heartbeat to known when I've received my last message
			if (atp.forward) {
				broadcast(new ServerMessage("addto", atp.forward(false)));
			} else {
				warehouse.updateHeartBeat(System.currentTimeMillis());
			}

			Contact invitee = atp.invitee;

			// add Client to a existing group
			if (atp.contact.type.equals(ContactType.GROUP)) {

				// retrieve group and add client
				Group g = warehouse.getGroupById(atp.contact.name);
				if (warehouse.addUserToGroup(invitee, g)) {

					// inform every group member (if successful), that a new client has joined the
					// group
					Message m = new Message(system, atp.contact, invitee.name + " has joined the group!");
					ServerMessage sm = new ServerMessage("message", m);
					for (Contact c : g.members) {
						sendMessage(c, sm);
					}
				}

			}

			// start a new privat chat
			else {
				// send Chat invitation to both (client and invitee)
				// on client side: open chat with newly added chat partner
				Message m = new Message(atp.contact, invitee, "Hi, I'd like to chat with you :GRINNING_FACE:");
				ServerMessage sm = new ServerMessage("message", m);
				sendMessage(out, sm);
				sendMessage(out, new ServerMessage("openchat", invitee));
				sendMessage(invitee, sm);
			}
			break;

		// other server has sent an update to a group
		case "group":
			// store update in warehouse, update heartbeat
			warehouse.updateHeartBeat(System.currentTimeMillis());
			warehouse.addGroup((Group) message.object);
			break;

		// Client wants to create a new group
		case "creategroup":

			CreateGroupPacket cgp = (CreateGroupPacket) message.object;

			// Create a new group, broadcast to other servers and store in warehouse
			Group g = new Group(cgp.user, cgp.chat, cgp.selected);
			broadcast(new ServerMessage("group", g));
			warehouse.addGroup(g);

			// Create Creation Message, broadcast message and store in warehouse
			Message mc = new Message(system, g.getContact(), cgp.user.name + " created this group");
			broadcast(new ServerMessage("message", new SendMessagePacket(mc.sender, mc.receiver, mc.message, false)));
			warehouse.addMessage(mc);
			ServerMessage smc = new ServerMessage("message", mc);

			// Send Creation Message to every group member
			for (Contact c : g.members) {
				sendMessage(c, smc);

				// Create Join Message, broadcast message and store in warehouse
				Message m = new Message(system, g.getContact(), c.name + " has joined the group!");
				broadcast(new ServerMessage("message", new SendMessagePacket(m.sender, m.receiver, m.message, false)));
				warehouse.addMessage(m);
				ServerMessage sm = new ServerMessage("message", m);

				// Send Join Message to every group member
				for (Contact c_ : g.members) {
					sendMessage(c_, sm);
				}
			}

			// open group chat for group owner/creator
			sendMessage(out, new ServerMessage("openchat", g.getContact()));
			break;

		// Some User has wrote a message
		case "message":
			SendMessagePacket smp = (SendMessagePacket) message.object;

			// if still on forward, forward with no further forward
			// if not, update heartbeat to known when I've received my last message
			if (smp.forward) {
				this.broadcast(new ServerMessage("message", smp.forward(false)));
			} else {
				warehouse.updateHeartBeat(System.currentTimeMillis());
			}

			// Create Message, broadcast and store in warehouse
			Message m = new Message(smp.sender, smp.receiver, smp.message);
			ServerMessage sm = new ServerMessage("message", m);
			warehouse.addMessage(m);

			// If Receiver is a group, forward message to every member in the group
			if (smp.receiver.type.equals(ContactType.GROUP)) {
				for (Contact c : warehouse.getGroupsById(smp.receiver.name).get(0).members) {
					sendMessage(c, sm);
				}
			}
			// Receiver is not a group, send validated message to sender and receiver
			else {
				sendMessage(smp.receiver, sm);
				sendMessage(smp.sender, sm);
			}
			break;

		// some server has sent me a heartbeat, to let me know, that we both are still
		// online
		case "heartbeat":
			synchronized (registeredPorts) {
				String port = "" + ((int) message.object);

				// if I didn't know him before, add server to registered ports for broadcasting
				if (!registeredPorts.contains(port)) {
					registeredPorts.add(port);
				}
			}
			break;

		// some server need every data I received after a specific timestamp
		case "fetch":

			// retrieve timestamp and port, store port in array for broadcast.
			String[] split = ((String) message.object).split("_");
			long timestamp = Long.parseLong(split[0]);
			Integer[] port = new Integer[] { Integer.parseInt(split[1]) };

			// collect data and 'broadcast' message to requesting server
			FetchPacket fp = new FetchPacket(warehouse.getMessagesAfter(timestamp), warehouse.getGroupsAfter(timestamp),
					warehouse.getUsersAfter(timestamp));
			broadcast(new ServerMessage("fetchreceive", fp), Arrays.asList(port));

			break;

		// some server answered my fetch request
		case "fetchreceive":

			// unwrapping and storing data
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

	/**
	 * shutdown server, close all connections
	 */
	public void shutdown() {
		this.shutdown = true;
		close();
		Server.registeredServer.remove(this);
		this.controllerUI.updateServer();
	}
}