package ChatSystem.Frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import ChatSystem.Client.Client;
import ChatSystem.Entities.Contact;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.User;
import ChatSystem.Frontend.Emoji.Emoji;
import ChatSystem.Frontend.Frames.ChatFrame;
import ChatSystem.Frontend.PopUps.EmojiPopUp;
import ChatSystem.Packets.WelcomePacket;

public class ChatManager {

	public User user;
	public Client client;

	public EmojiPopUp emojiPopUp;
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
	}

	public List<Message> getMessagesWith(Contact c) {
		if (chatData.containsKey(c)) {
			return chatData.get(c);
		}
		return new ArrayList<Message>();
	}

	public Message getLatestMessageWith(Contact c) {
		if (chatData.containsKey(c)) {
			return chatData.get(c).get(chatData.get(c).size() - 1);
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
		if (contact.name.equals(user.name)) {
			System.out.println("Mglw. was falsch gelaufen: " + m);
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

	public void addContact(Contact c, boolean addToGroup) {
		
	}

	public void emojiPicket(Emoji emoji) {
		chatFrame.insertEmoji(emoji);
	}
}
