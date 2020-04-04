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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
		System.out.println("Trying to create User " + u);
		if (doesUserExist(u)) {
			System.err.println("User already exists");
			return;
		}
		System.out.println("User created and stored in DB");
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
		return getUsers().stream().map(x -> new Contact(x.name, ContactType.USER)).collect(Collectors.toList());
	}

	public static List<Contact> getAllUserWithout(String name) {
		return getAllUser().stream().filter(x -> !x.name.equals(name)).collect(Collectors.toList());
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
		if (g == null)
			return false;
		synchronized (groups) {
			if (g.members.contains(c))
				return false;
			g.members.add(c);
			return true;
		}
	}

	public static List<Message> getMessages(Contact a, Contact b) {
		return getMessages().stream().filter(
				x -> (x.sender.equals(a) && x.receiver.equals(b)) || (x.sender.equals(b) && x.receiver.equals(a)))
				.collect(Collectors.toList());
	}

	public static List<Group> getGroupsById(String id) {
		return getGroups().stream().filter(x -> id.equals(x.id + "")).collect(Collectors.toList());
	}

	public static List<Contact> getContactsOf(Contact c) {
		if (c == null)
			return new ArrayList<Contact>();

		Set<String> names = new HashSet<String>();
		List<Contact> contacts = getGroupsOfContact(c).stream().map(x -> new Contact(x.id + "", ContactType.GROUP))
				.collect(Collectors.toList());
		getMessages().stream().filter(x -> x.receiver.type.equals(ContactType.USER)).forEach(x -> {
			if (x.sender.equals(c)) {
				names.add(x.receiver.name);
			}
			if (x.receiver.equals(c)) {
				names.add(x.sender.name);
			}
		});
		names.forEach(x -> contacts.add(new Contact(x, ContactType.USER)));

		return contacts;
	}

	public static List<Group> getGroupsOfContact(Contact c) {
		if (c == null)
			return new ArrayList<Group>();
		return getGroups().stream().filter(x -> x.members.contains(c)).collect(Collectors.toList());
	}

	public static List<Message> getChatMessagesSorted(Contact a, Contact b) {
		return getMessages().stream().filter(
				x -> ((x.sender.equals(a) && x.receiver.equals(b)) || (x.sender.equals(b) && x.receiver.equals(a))))
				.sorted().collect(Collectors.toList());
	}

	public static boolean doesMessageExist(Message m) {
		return getMessages().stream().filter(x -> x.equals(m)).count() > 0;
	}

	public static boolean doesUserExist(String name) {
		return getUsers().stream().filter(x -> x.name.equalsIgnoreCase(name)).count() > 0;
	}

	public static boolean doesUserExist(User u) {
		return getUsers().stream().filter(x -> x.equals(u)).count() > 0;
	}

	public static boolean doesGroupExsits(Group g) {
		return getGroups().stream().filter(x -> x.equals(g)).count() > 0;
	}

	public static List<Message> getGroupMessages(Group g) {
		return getMessages().stream()
				.filter(x -> x.receiver.type.equals(ContactType.GROUP) && x.receiver.name.equals(g.id))
				.collect(Collectors.toList());
	}
}