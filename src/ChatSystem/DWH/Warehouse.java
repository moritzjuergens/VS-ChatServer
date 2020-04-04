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

public class Warehouse {

	private static List<Message> messages = new ArrayList<Message>();
	private static List<User> users = new ArrayList<User>();
	private static List<Group> groups = new ArrayList<Group>();
	private static String[] files = new String[] { "messages", "users", "groups" };

	public static void saveFiles() {
		for (String fileName : files) {
			String name = "./" + fileName + ".dat";
			try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(name));
				CSLogger.log(Warehouse.class, "Saving %s in %s", fileName, name);
				if (fileName.equals("messages")) {
					out.writeObject(getMessages());
				} else if (fileName.equals("groups")) {
					out.writeObject(getGroups());

				} else if (fileName.equals("users")) {
					out.writeObject(getUsers());
				}
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings({ "unchecked", "resource" })
	public static void loadFiles() {
		synchronized (messages) {
			synchronized (groups) {
				synchronized (users) {

					for (String fileName : files) {
						String name = "./" + fileName + ".dat";
						File f = new File(name);
						if (!f.exists())
							continue;
						CSLogger.log(Warehouse.class, "Loading file: %s", f.getAbsolutePath());
						try {
							ObjectInputStream in = new ObjectInputStream(new FileInputStream(name));
							if (fileName.equals("messages")) {
								messages = (List<Message>) in.readObject();
							} else if (fileName.equals("groups")) {
								groups = (List<Group>) in.readObject();
							} else if (fileName.equals("users")) {
								users = (List<User>) in.readObject();
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

	public static void addMessage(Message m) {
		CSLogger.log(Warehouse.class, "Adding Message to Warehouse %s", m);
		synchronized (messages) {
			messages.add(m);
		}
	}

	public static void addUser(User u) {
		CSLogger.log(Warehouse.class, "Trying to create User %s", u);
		if (doesUserExist(u.name)) {
			CSLogger.log(Warehouse.class, "User already exists", "");
			return;
		}
		CSLogger.log(Warehouse.class, "User created and stored in DB", "");
		synchronized (users) {
			users.add(u);
		}
	}

	public static void addGroup(Group g) {

		CSLogger.log(Warehouse.class, "Adding Group to Warehouse %s", g);
		synchronized (groups) {
			groups.add(g);
		}
	}

	public static List<Message> getMessages() {
		return messages;
	}

	public static List<User> getUsers() {
		return users;
	}

	public static List<Group> getGroups() {
		return groups;
	}

	public static List<Contact> getAllUser() {
		return getUsers().stream().map(x -> x.getContact()).collect(Collectors.toList());
	}

	public static HashMap<Contact, List<Message>> getUserData(User u) {
		HashMap<Contact, List<Message>> data = new HashMap<>();

		List<Contact> contacts = getContactsOf(u.getContact());
		contacts.stream().forEach(c -> data.put(c, getMessages(u.getContact(), c)));
		return data;
	}

	public static User getUser(String name) {
		List<User> usersFound = getUsers().stream().filter(x -> x.name.equals(name)).collect(Collectors.toList());
		if (usersFound.size() == 0)
			return null;
		return usersFound.get(0);
	}

	public static User getUser(Contact c) {
		return getUser(c.name);
	}

	public static boolean addUserToGroup(Contact c, Group g) {

		synchronized (groups) {
			if (g == null || g.members.stream().filter(x -> x.equals(c)).count() > 0) {
				return false;
			}
			g.members.add(c);
			return true;
		}
	}

	public static List<Message> getMessages(Contact user, Contact chat) {
		return getMessages().stream().filter(x -> {
			if (x.receiver.type.equals(ContactType.GROUP) && x.receiver.equals(chat)) {
				return true;
			}
			return (x.receiver.equals(user) && x.sender.equals(chat))
					|| (x.receiver.equals(chat) && x.sender.equals(user));
		}).collect(Collectors.toList());
	}

	public static List<Group> getGroupsById(String id) {
		return getGroups().stream().filter(x -> id.equals(x.id + "")).collect(Collectors.toList());
	}

	public static Group getGroupById(String id) {
		return getGroupsById(id).get(0);
	}

	public static List<Contact> getContactsOf(Contact c) {
		ArrayList<Contact> contacts = new ArrayList<>();
		if (c == null) {
			return contacts;
		}

		getPrivatChatsOfContact(c).forEach(contacts::add);
		getGroupsOfContact(c).stream().map(x -> x.getContact()).forEach(contacts::add);
		return contacts;
	}

	public static List<Contact> getPrivatChatsOfContact(Contact c) {
		if (c == null) {
			return new ArrayList<Contact>();
		}
		return getMessages().stream().filter(x -> x.receiver.type.equals(ContactType.USER))
				.filter(x -> x.sender.equals(c) || x.receiver.equals(c))
				.map(x -> x.sender.equals(c) ? x.receiver.name : x.sender.name).distinct().map(x -> new Contact(x, ContactType.USER)).collect(Collectors.toList());
	}

	public static List<Group> getGroupsOfContact(Contact c) {
		if (c == null) {
			return new ArrayList<Group>();
		}
		return getGroups().stream().filter(x -> x.members.stream().filter(y -> y.equals(c)).count() > 0)
				.collect(Collectors.toList());
	}

	public static boolean doesUserExist(String name) {
		return getUsers().stream().filter(x -> x.name.equalsIgnoreCase(name)).count() > 0;
	}

	public static boolean doesGroupExsits(Group g) {
		return getGroups().stream().filter(x -> x.equals(g)).count() > 0;
	}
}