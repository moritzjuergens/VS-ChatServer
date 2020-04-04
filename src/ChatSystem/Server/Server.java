package ChatSystem.Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

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
	private List<User> users = new ArrayList<User>();
	private ServerSocket ss;
	public Contact system = new Contact("System", ContactType.SYSTEM);

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

	public void registerClient(User u, ObjectOutputStream out) {
		if (users.contains(u)) {
			this.sendMessage(out, new ServerMessage("alreadyconnected", ""));
			return;
		}
		u.out = out;
		users.add(u);
		this.sendMessage(out, new ServerMessage("welcome", new WelcomePacket(u, Warehouse.getUserData(u))));
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
			}
		} catch (IOException e) {
		}
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
					this.registerClient(u, out);
				}

			}
			break;
		case "signup":
			synchronized (users) {
				SignInUpPacket data = (SignInUpPacket) message.object;
				if (Warehouse.doesUserExist(data.name)) {
					sendMessage(out, new ServerMessage("usernametaken", null));
					break;
				}
				User u = new User(data.name, data.password);
				synchronized (users) {
					this.registerClient(u, out);
				}
			}
			break;
		case "allcontacts":
			sendMessage(out, new ServerMessage("allcontacts", new AllContactsPacket(Warehouse.getAllUser())));
			break;
		case "addto":
			AddToPacket atp = (AddToPacket) message.object;
			Contact invitee = atp.invitee;
			if (atp.contact.type.equals(ContactType.GROUP)) {
				Group g = Warehouse.getGroupById(atp.contact.name);
				if (Warehouse.addUserToGroup(invitee, g)) {
					Message m = new Message(system, atp.contact, invitee.name + " has joined the group!");
					ServerMessage sm = new ServerMessage("message", m);
					for (Contact c : g.members) {
						sendMessage(Warehouse.getUser(c), sm);
					}
				}

			} else {
				Message m = new Message(atp.contact, invitee, "Hi, I'd like to chat with you :GRINNING_FACE:");
				ServerMessage sm = new ServerMessage("message", m);
				sendMessage(out, sm);
				sendMessage(out, new ServerMessage("openchat", invitee));
				sendMessage(Warehouse.getUser(invitee), sm);
			}
			break;
		case "creategroup":
			CreateGroupPacket cgp = (CreateGroupPacket) message.object;
			Group g = new Group(cgp.user, cgp.chat, cgp.selected);

			Message mc = new Message(system, g.getContact(), cgp.user.name + " created this group");
			ServerMessage smc = new ServerMessage("message", mc);
			for (Contact c : g.members) {
				Message m = new Message(system, g.getContact(), c.name + " has joined the group!");
				ServerMessage sm = new ServerMessage("message", m);
				sendMessage(Warehouse.getUser(c), smc);
				for(Contact c_ : g.members) {
					sendMessage(Warehouse.getUser(c_), sm);					
				}
			}
			sendMessage(out, new ServerMessage("openchat", g.getContact()));			
			break;
		case "message":
			SendMessagePacket sm = (SendMessagePacket) message.object;
			Message m = new Message(sm.sender, sm.receiver, sm.message);
			if (sm.receiver.type.equals(ContactType.GROUP)) {
				for (Contact c : Warehouse.getGroupsById(sm.receiver.name).get(0).members) {
					sendMessage(Warehouse.getUser(c), new ServerMessage("message", m));
				}
			} else {
				sendMessage(Warehouse.getUser(sm.receiver), new ServerMessage("message", m));
				sendMessage(Warehouse.getUser(sm.sender), new ServerMessage("message", m));
			}
			break;
		}
	}
}
