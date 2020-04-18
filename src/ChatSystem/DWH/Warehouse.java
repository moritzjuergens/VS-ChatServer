package ChatSystem.DWH;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import ChatSystem.CSLogger;
import ChatSystem.Entities.Contact;
import ChatSystem.Entities.Contact.ContactType;
import ChatSystem.Entities.Group;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.User;
import ChatSystem.Server.Server;

/**
 * Data Warehouse, stores and retrieves all information a server needs
 * 
 * @author timos
 *
 */
public class Warehouse {

	private List<Message> messages = new ArrayList<Message>();
	private List<User> users = new ArrayList<User>();
	private List<Group> groups = new ArrayList<Group>();

	private String[] files = new String[] { "messages", "users", "groups", "heartbeat" };
	private Server server;

	/**
	 * Create a new Warehouse, bind to a specific Server
	 * 
	 * @param s Server the warehouse belongs to
	 */
	public Warehouse(Server s) {
		this.server = s;
	}

	/**
	 * Saves a file for persistent storage
	 * 
	 * @param fileName File to be saved
	 */
	public void saveFile(String fileName) {
		String name = "./" + fileName + "_" + server.getPort() + ".dat";

		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(name));
			CSLogger.log(Warehouse.class, "[%s\t] Saving %s in %s", server.getPort(), fileName, name);

			if (fileName.equals("messages")) {
				out.writeObject(getMessages());
			} else if (fileName.equals("groups")) {
				out.writeObject(getGroups());
			} else if (fileName.equals("users")) {
				out.writeObject(getUsers());
			} else if (fileName.equals("heartbeat")) {
				out.writeLong(server.lastHeartbeat);
			}

			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * loads every file and stores its data in local memory
	 */
	@SuppressWarnings({ "unchecked", "resource" })
	public void loadFiles() {
		synchronized (messages) {
			synchronized (groups) {
				synchronized (users) {
					for (String fileName : files) {

						String name = "./" + fileName + "_" + server.getPort() + ".dat";

						File f = new File(name);

						// If the file doesnt exists, theres no saved data => next file
						if (!f.exists())
							continue;

						CSLogger.log(Warehouse.class, "[%s\t] Loading file: %s", server.getPort(), f.getAbsolutePath());

						try {
							ObjectInputStream in = new ObjectInputStream(new FileInputStream(name));
							if (fileName.equals("messages")) {
								messages = (List<Message>) in.readObject();
							} else if (fileName.equals("groups")) {
								groups = (List<Group>) in.readObject();
							} else if (fileName.equals("users")) {
								users = (List<User>) in.readObject();
							} else if (fileName.equals("heartbeat")) {
								server.lastHeartbeat = in.readLong();
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * Stores last heartbeat. Is used to refetch messages send/received by other
	 * server, during own downtime
	 * 
	 * @param time last heartbeat
	 */
	public void updateHeartBeat(long time) {
		this.server.lastHeartbeat = time;
		saveFile("heartbeat");
	}

	/**
	 * Adds a new message to the warehouse and automatically updates its file
	 * 
	 * @param m Message to be stored
	 */
	public void addMessage(Message m) {
		if (!doesMessageExist(m)) {
			CSLogger.log(Warehouse.class, "[%s\t] Adding Message to Warehouse %s", server.getPort(), m);
			synchronized (messages) {
				messages.add(m);
				saveFile("messages");
			}
		}
	}

	/**
	 * Adds a new user to the warehouse and automatically updates its file
	 * 
	 * @param u User to be stored
	 */
	public void addUser(User u) {
		CSLogger.log(Warehouse.class, "[%s\t] Trying to create User %s", server.getPort(), u);
		if (doesUserExist(u.name)) {
			CSLogger.log(Warehouse.class, "[%s\t] User already exists", server.getPort());
			return;
		}
		CSLogger.log(Warehouse.class, "[%s\t] User created and stored in DB", server.getPort());
		synchronized (users) {
			users.add(u);
			saveFile("users");
		}
	}

	/**
	 * Adds a new group to the warehouse and automatically updates its file
	 * 
	 * @param g Group to be stored
	 */
	public void addGroup(Group g) {

		synchronized (groups) {

			// If the group already exists, check if their had been changes to its member
			// list
			if (doesGroupExsits(g)) {
				CSLogger.log(Warehouse.class, "[%s\t] Updating members of in group %s", server.getPort(), g);
				Group local = getGroupById("" + g.id);
				for (Contact member : g.members) {
					if (!local.members.contains(member)) {
						local.members.add(member);
					}
					saveFile("groups");
				}
			} else {
				CSLogger.log(Warehouse.class, "[%s\t] Adding Group to Warehouse %s", server.getPort(), g);
				groups.add(g);
				saveFile("groups");
			}
		}
	}

	/**
	 * Get all messages stored in the warehouse
	 * 
	 * @return List of Messages
	 */
	public List<Message> getMessages() {
		return messages.stream().sorted((m1, m2) -> m1.timestamp > m2.timestamp ? 1 : -1).collect(Collectors.toList());
	}

	/**
	 * Get all users stored in the warehouse
	 * 
	 * @return List of Users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * Get all groups stored in the warehouse
	 * 
	 * @return List of Groups
	 */
	public List<Group> getGroups() {
		return groups;
	}

	/**
	 * Returns a list of messages send after 'timestamp'
	 * 
	 * @param timestamp Timestamp
	 * @return List of messages
	 */
	public List<Message> getMessagesAfter(long timestamp) {
		return getMessages().stream().filter(x -> x.timestamp >= timestamp).collect(Collectors.toList());
	}

	/**
	 * Returns a list of users created after 'timestamp'
	 * 
	 * @param timestamp Timestamp
	 * @return List of users
	 */
	public List<User> getUsersAfter(long timestamp) {
		return getUsers().stream().filter(x -> x.id >= timestamp).collect(Collectors.toList());
	}

	/**
	 * Returns a list of groups created after 'timestamp'
	 * 
	 * @param timestamp Timestamp
	 * @return List of groups
	 */
	public List<Group> getGroupsAfter(long timestamp) {
		return getGroups().stream().filter(x -> x.id >= timestamp).collect(Collectors.toList());
	}

	/**
	 * Get all contacts stored in the warehouse
	 * 
	 * @return List of contacts
	 */
	public List<Contact> getAllUser() {
		return getUsers().stream().map(x -> x.getContact()).collect(Collectors.toList());
	}

	/**
	 * Returns every Message of a User grouped by their chat partner
	 * 
	 * @param u User to retrieve data
	 * @return HashMap<Contact, List<Message>>
	 */
	public HashMap<Contact, List<Message>> getUserData(User u) {
		HashMap<Contact, List<Message>> data = new HashMap<>();

		List<Contact> contacts = getContactsOf(u.getContact());
		contacts.stream().forEach(c -> data.put(c, getMessages(u.getContact(), c)));
		return data;
	}

	/**
	 * Finds a user by its name
	 * 
	 * @param name User's name
	 * @return User
	 */
	public User getUser(String name) {
		List<User> usersFound = getUsers().stream().filter(x -> x.name.equals(name)).collect(Collectors.toList());
		if (usersFound.size() == 0)
			return null;
		return usersFound.get(0);
	}

	/**
	 * Finds a user by its Contact
	 * 
	 * @param c User's contact
	 * @return User
	 */
	public User getUser(Contact c) {
		return getUser(c.name);
	}

	/**
	 * Add a User to a existing group
	 * 
	 * @param c Contact to be added
	 * @param g Group, Contact to be added to
	 * @return true if successful
	 */
	public boolean addUserToGroup(Contact c, Group g) {

		synchronized (groups) {
			if (g == null || g.members.stream().filter(x -> x.equals(c)).count() > 0) {
				return false;
			}
			g.members.add(c);
			saveFile("groups");
			return true;
		}
	}

	/**
	 * Returns a List of messages send between two chat partners
	 * 
	 * @param user User A
	 * @param chat User B (type can be Group)
	 * @return List<Message>
	 */
	public List<Message> getMessages(Contact user, Contact chat) {
		return getMessages().stream().filter(x -> {
			if (x.receiver.type.equals(ContactType.GROUP) && x.receiver.equals(chat)) {
				return true;
			}
			return (x.receiver.equals(user) && x.sender.equals(chat))
					|| (x.receiver.equals(chat) && x.sender.equals(user));
		}).collect(Collectors.toList());
	}

	/**
	 * Returns a list of messages a users has received after a given timestamp
	 * 
	 * @param user      User
	 * @param timestamp Timestamp
	 * @return List<Message>
	 */
	public List<Message> getMessagesForUserAfter(Contact user, long timestamp) {
		return getMessages().stream().filter(x -> {
			if (x.sender.equals(user) || x.receiver.equals(user))
				return true;
			if (x.receiver.type.equals(ContactType.GROUP)) {
				Group g = getGroupById(x.receiver.name);
				return g.members.contains(user);
			}
			return false;
		}).filter(x -> x.timestamp >= timestamp).collect(Collectors.toList());
	}

	/**
	 * Find groups by its ID
	 * 
	 * @param id GroupID
	 * @return List<Group>
	 */
	public List<Group> getGroupsById(String id) {
		return getGroups().stream().filter(x -> id.equals(x.id + "")).collect(Collectors.toList());
	}

	/**
	 * Find group by its ID
	 * 
	 * @param id GroupID
	 * @return Group
	 */
	public Group getGroupById(String id) {
		return getGroupsById(id).get(0);
	}

	/**
	 * Returns a List with every Contact of any type a User has chatted with
	 * 
	 * @param c User's contact
	 * @return List<Contact>
	 */
	public List<Contact> getContactsOf(Contact c) {
		ArrayList<Contact> contacts = new ArrayList<>();
		if (c == null) {
			return contacts;
		}

		getPrivatChatsOfContact(c).forEach(contacts::add);
		getGroupsOfContact(c).stream().map(x -> x.getContact()).forEach(contacts::add);
		return contacts;
	}

	/**
	 * Returns a List with every Contact(type=User) a User has chatted with
	 * 
	 * @param c User's contact
	 * @return List<Contact>
	 */
	public List<Contact> getPrivatChatsOfContact(Contact c) {
		if (c == null) {
			return new ArrayList<Contact>();
		}
		return getMessages().stream().filter(x -> x.receiver.type.equals(ContactType.USER))
				.filter(x -> x.sender.equals(c) || x.receiver.equals(c))
				.map(x -> x.sender.equals(c) ? x.receiver.name : x.sender.name).distinct()
				.map(x -> new Contact(x, ContactType.USER)).collect(Collectors.toList());
	}

	/**
	 * Returns a List with every Contact(type=Group) a User has chatted with
	 * 
	 * @param c User's contact
	 * @return List<Contact>
	 */
	public List<Group> getGroupsOfContact(Contact c) {
		if (c == null) {
			return new ArrayList<Group>();
		}
		return getGroups().stream().filter(x -> x.members.stream().filter(y -> y.equals(c)).count() > 0)
				.collect(Collectors.toList());
	}

	/**
	 * Check if a message exists
	 * 
	 * @param m Message
	 * @return boolean
	 */
	public boolean doesMessageExist(Message m) {
		return getMessages().stream().filter(x -> x.equals(m)).count() > 0;
	}

	/**
	 * Check if a user exists
	 * 
	 * @param name Username
	 * @return boolean
	 */
	public boolean doesUserExist(String name) {
		return getUsers().stream().filter(x -> x.name.equalsIgnoreCase(name)).count() > 0;
	}

	/**
	 * Check if a group exists
	 * 
	 * @param g Group
	 * @return boolean
	 */
	public boolean doesGroupExsits(Group g) {
		return getGroups().stream().filter(x -> x.equals(g)).count() > 0;
	}
}