package ChatSystem.Frontend;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import ChatSystem.Client.Client;
import ChatSystem.Entities.Contact;
import ChatSystem.Entities.Contact.ContactType;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Entities.User;
import ChatSystem.Frontend.Emoji.Emoji;
import ChatSystem.Frontend.Frames.ChatFrame;
import ChatSystem.Frontend.PopUps.ContactPopUp;
import ChatSystem.Frontend.PopUps.EmojiPopUp;
import ChatSystem.Packets.AddToPacket;
import ChatSystem.Packets.CreateGroupPacket;
import ChatSystem.Packets.WelcomePacket;

public class ChatManager {

	public User user;
	public Client client;

	public EmojiPopUp emojiPopUp;
	public ContactPopUp contactPopUp;
	private ChatFrame chatFrame;
	private Contact currentContact = null;
	private HashMap<Contact, List<Message>> chatData = new HashMap<>();

	/**
	 * Client has received login confirmation. Store variable references, open
	 * Chatframe, init emojipop & contactpop
	 * 
	 * @param packet WelcomePacket
	 * @param c      Client
	 */
	public ChatManager(WelcomePacket packet, Client c) {
		this.user = packet.user;
		this.chatData = packet.userData;
		this.client = c;
		this.chatFrame = new ChatFrame(this);
		this.chatFrame.updateContacts(this.chatData);
		this.emojiPopUp = new EmojiPopUp(this);
		this.contactPopUp = new ContactPopUp(this);
	}

	/**
	 * Returns a List of messages with a specific chat partner
	 * 
	 * @param c Contact chat partner
	 * @return List<Message>
	 */
	public List<Message> getMessagesWith(Contact c) {
		List<Contact> searchResults = chatData.keySet().stream().filter(x -> x.equals(c)).collect(Collectors.toList());
		if (searchResults.isEmpty()) {
			return new ArrayList<Message>();
		}
		return chatData.get(searchResults.get(0));
	}

	/**
	 * Returns the last message received/sent from/to a specific contact
	 * 
	 * @param c Contact chat partner
	 * @return Message
	 */
	public Message getLatestMessageWith(Contact c) {
		if (chatData.containsKey(c)) {
			try {
				return chatData.get(c).get(chatData.get(c).size() - 1);
			} catch (Exception e) {
				System.out.println("Fehler bei ChatManager Zeile 68");
				return null;
			}
		}
		return null;
	}

	/**
	 * Returns every Contact the User hast
	 * 
	 * @return List<Contact>
	 */
	public List<Contact> getContacts() {
		return chatData.keySet().stream().collect(Collectors.toList());
	}

	/**
	 * Open a specific chat
	 * 
	 * @param c Contact chat partner
	 */
	public void openChatWith(Contact c) {
		if (c.equals(currentContact))
			return;
		currentContact = c;
		chatFrame.setChat(c);
		chatFrame.setMessages(getMessagesWith(c));
		chatFrame.updateContacts(chatData);
	}

	/**
	 * Client has received a new chat message
	 * 
	 * @param m Message chat message
	 */
	public void messageReceived(Message m) {

		// if chatframe isnt focused, send OS Notification
		if (!chatFrame.isFocused()) {
			sendNotification(m.sender.name + " send you a message", m.message, (e) -> {
				chatFrame.requestFocus();
				openChatWith(m.sender);
			});
		}

		// get chat partner
		Contact contact = m.receiver;
		if (contact.name.equals(user.name)) {
			contact = m.sender;
		}

		final Contact partner = contact;

		// add received message to locally stored data
		chatData.forEach((c, msgs) -> {
			if (c.name.equals(partner.name)) {
				msgs.add(m);
			}
		});

		if (chatData.keySet().stream().filter(x -> x.name.equals(partner.name)).count() == 0) {
			List<Message> messages = new ArrayList<Message>();
			messages.add(m);
			chatData.put(partner, messages);
		}

		// if users is already chatting with message's partner, display message in chat
		// room
		if (partner.equals(currentContact)) {
			chatFrame.addMessage(m);
		}

		// remove duplicated (can sometimes occur)
		Iterator<Contact> i = chatData.keySet().iterator();
		while (i.hasNext()) {
			Contact c = i.next();
			if (c.name.equals(user.name))
				i.remove();
		}

		// update contact list
		chatFrame.updateContacts(chatData);
	}

	/**
	 * List of every contact has been received, forward List to contactPopUp
	 * 
	 * @param contacts Contacts received
	 */
	public void contactListReceived(List<Contact> contacts) {
		contactPopUp.addContacts(contacts.stream().filter(x -> !x.name.equals(user.name)).collect(Collectors.toList()));
	}

	/**
	 * Adds a Contact to the currently opened chat
	 * 
	 * @param contact    Contact to be added
	 * @param addToGroup boolean
	 */
	public void addContact(Contact contact, boolean addToGroup) {

		// Start a new privat chat
		if (!addToGroup) {

			// check if user is already chatting with specified contact
			List<Contact> contacts = chatData.keySet().stream().filter(x -> x.equals(contact))
					.collect(Collectors.toList());

			// if user is already chatting, open chat
			if (contacts.size() > 0) {
				openChatWith(contacts.get(0));
				return;
			}

			// send invitation
			client.sendMessage(new ServerMessage("addto", new AddToPacket(user.getContact(), contact, true)));
			return;
		}

		// Add user to a group or create a new one
		if (!contact.equals(currentContact)) {

			// Contact is a group, added contact to specific group
			if (currentContact.type.equals(ContactType.GROUP)) {
				client.sendMessage(new ServerMessage("addto", new AddToPacket(currentContact, contact, true)));
				return;
			}

			// Contact is not a group, create a new group. Initial member (User himself,
			// chat partner he's chatting with, contact user has selected in contact pop up)
			client.sendMessage(new ServerMessage("creategroup",
					new CreateGroupPacket(user.getContact(), currentContact, contact)));
		}

	}

	/**
	 * Emoji has been selected in popup, forward emoji to chatFrame
	 * 
	 * @param emoji Emoji
	 */
	public void emojiPicked(Emoji emoji) {
		chatFrame.insertEmoji(emoji);
	}

	/**
	 * Send system notification if chatFrame isn't focused
	 * 
	 * @param title   Notification Title
	 * @param message Message
	 * @param l       Action to be performed if notification has been clicked
	 */
	public void sendNotification(String title, String message, ActionListener l) {
		SystemTray tray = SystemTray.getSystemTray();
		try {
			Image image = ImageIO.read(new File("./emojis/" + Emoji.BICEPS.name));
			TrayIcon trayIcon = new TrayIcon(image, "");
			trayIcon.setImageAutoSize(true);
			tray.add(trayIcon);
			trayIcon.addActionListener(l);
			trayIcon.displayMessage(title, message, MessageType.INFO);
		} catch (IOException | AWTException e) {
			e.printStackTrace();
		}
	}
}
