package ChatSystem.Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import ChatSystem.CSLogger;
import ChatSystem.DWH.Warehouse;
import ChatSystem.Entities.AddContact;
import ChatSystem.Entities.Contact;
import ChatSystem.Entities.Contact.ContactType;
import ChatSystem.Entities.Group;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.Messages;
import ChatSystem.Entities.SendMessage;
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
		System.out.println("Nachricht an " + u.name);
		try {
			if (users.contains(u)) {
				System.out.println("existiert");
				u.out.writeObject(m);
			} else {
				// TODO: Anderen Servern nachricht schicken
				System.out.println("existiert nicht");
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
			Contact target = (Contact) message.object;
			sendMessage(out, new ServerMessage("contacts", Warehouse.getContactsOf(target)));
			break;
		case "getmessages":
			Messages messages = (Messages) message.object;
			sendMessage(out, new ServerMessage("messages", Warehouse.getMessages(messages.user, messages.contact)));
			break;
		case "getalluser":
			Contact sender = (Contact) message.object;
			sendMessage(out, new ServerMessage("alluser", Warehouse.getAllUserWithout(sender.name)));
			break;
		case "newcontact":
			AddContact newC = (AddContact) message.object;
			if (!Warehouse.getContactsOf(newC.who).contains(newC.whom)) {
				Message m = new Message(newC.who, newC.whom, "Hi, I just added you as a new friend :)");
				ServerMessage sm = new ServerMessage("message", m);
				sendMessage(Warehouse.getUser(newC.who), sm);
				sendMessage(Warehouse.getUser(newC.whom), sm);
			}
			break;
		case "newgroup":
			AddContact newG = (AddContact) message.object;
			Group g = new Group(newG.who, newG.whom);
			// TODO: Anderen Server diese Gruppe schicken zum speichern
			for (Contact c : g.members) {
				Message m = new Message(newG.who, g.getContact(), "invited " + c.name);
				for (Contact c_ : g.members) {
					sendMessage(Warehouse.getUser(c_), new ServerMessage("message", m));
				}
			}
			break;
		case "addtogroup":
			AddContact addG = (AddContact) message.object;
			Group group = Warehouse.getGroupsById(addG.who.name).get(0);
			if (Warehouse.addUserToGroup(addG.whom, group)) {
				ServerMessage sm = new ServerMessage("message",
						new Message(addG.whom, group.getContact(), "joined the group"));
				for (Contact c : group.members) {
					sendMessage(Warehouse.getUser(c), sm);
				}
			}
			break;
		case "message":
			SendMessage sm = (SendMessage) message.object;
			Message m = new Message(sm.sender, sm.receiver, sm.message);
			System.out.println("new messagess");
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
