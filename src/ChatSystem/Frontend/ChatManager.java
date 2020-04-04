package ChatSystem.Frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import ChatSystem.Client.Client;
import ChatSystem.Entities.Contact;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Entities.User;
import ChatSystem.Entities.Contact.ContactType;
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

	public ChatManager(WelcomePacket packet, Client c) {
		this.user = packet.user;
		this.chatData = packet.userData;
		this.client = c;
		this.chatFrame = new ChatFrame(this);
		this.chatFrame.updateContacts(this.chatData);
		this.emojiPopUp = new EmojiPopUp(this);
		this.contactPopUp = new ContactPopUp(this);
	}

	public List<Message> getMessagesWith(Contact c) {

		List<Contact> searchResults = chatData.keySet().stream().filter(x -> x.equals(c)).collect(Collectors.toList());
		if (searchResults.isEmpty()) {
			return new ArrayList<Message>();
		}
		return chatData.get(searchResults.get(0));
	}

	public Message getLatestMessageWith(Contact c) {
		if (chatData.containsKey(c)) {
			try {
				return chatData.get(c).get(chatData.get(c).size() - 1);
			} catch (Exception e) {
				System.out.println("Feheler bei ChatManager Zeile 56");
				return null;
			}
		}
		return null;
	}

	public List<Contact> getContacts() {
		return chatData.keySet().stream().collect(Collectors.toList());
	}

	public void openChatWith(Contact c) {
		if (c.equals(currentContact))
			return;
		currentContact = c;
		chatFrame.setChat(c);
		chatFrame.setMessages(getMessagesWith(c));
		chatFrame.updateContacts(chatData);
	}

	public void messageReceived(Message m) {
		Contact contact = m.receiver;
		if (contact.name.equals(user.name)) {
			contact = m.sender;
		}

		final Contact partner = contact;
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

		if (partner.equals(currentContact)) {
			chatFrame.addMessage(m);
		}

		Iterator<Contact> i = chatData.keySet().iterator();
		while (i.hasNext()) {
			Contact c = i.next();
			if (c.name.equals(user.name))
				i.remove();
		}

		chatFrame.updateContacts(chatData);
	}

	public void contactListReceived(List<Contact> contacts) {
		contactPopUp.addContacts(contacts.stream().filter(x -> !x.name.equals(user.name)).collect(Collectors.toList()));
	}

	public void addContact(Contact contact, boolean addToGroup) {
		if (!addToGroup) {
			List<Contact> contacts = chatData.keySet().stream().filter(x -> x.equals(contact))
					.collect(Collectors.toList());
			if (contacts.size() > 0) {
				openChatWith(contacts.get(0));
				return;
			}
			client.sendMessage(new ServerMessage("addto", new AddToPacket(user.getContact(), contact)));
			return;
		}
		if (!contact.equals(currentContact)) {
			if (currentContact.type.equals(ContactType.GROUP)) {
				client.sendMessage(new ServerMessage("addto", new AddToPacket(currentContact, contact)));
				return;
			}
			client.sendMessage(new ServerMessage("creategroup",
					new CreateGroupPacket(user.getContact(), currentContact, contact)));
		}

	}

	public void emojiPicked(Emoji emoji) {
		chatFrame.insertEmoji(emoji);
	}
}
