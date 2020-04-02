package ChatSystem.Frontend.Chat;

import java.util.List;

import ChatSystem.Client.Client;
import ChatSystem.Entities.Contact;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.Messages;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Entities.User;
import ChatSystem.Frontend.Chat.Frames.ContainerFrame;

public class Chat {

	public User user;
	public Client client;
	private ContainerFrame cFrame;
	private Contact currentContact = null;

	public Chat(User u, Client c) {
		this.user = u;
		this.client = c;
		cFrame = new ContainerFrame(this);
		cFrame.setVisible(true);
	}
	
	public void updateContacts(List<Contact> contacts) {
		cFrame.panelContacts.updateContacts(contacts);
	}
	
	public void updateMessage(List<Message> messages) {
		cFrame.panelChat.updateMessages(messages);
	}
	
	public void openChat(Contact contact) {
		if(contact.equals(currentContact)) return;
		currentContact = contact;
		cFrame.panelChat.updatePartner(contact.name);
		client.sendMessage(new ServerMessage("getmessages", new Messages(user, contact)));
	}

}
