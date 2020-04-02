package ChatSystem.Frontend.Chat;

import java.util.ArrayList;
import java.util.List;

import ChatSystem.Client.Client;
import ChatSystem.Entities.Contact;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.Messages;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Entities.User;
import ChatSystem.Entities.Contact.ContactType;
import ChatSystem.Frontend.Chat.Frames.ContainerFrame;
import ChatSystem.Frontend.Chat.Frames.UserListFrame;

public class Chat {

	public User user;
	public Client client;
	private ContainerFrame cFrame;
	private Contact currentContact = null;
	private UserListFrame userListFrame = null;
	private List<Contact> contacts = new ArrayList<Contact>();

	public Chat(User u, Client c) {
		this.user = u;
		this.client = c;
		this.userListFrame = new UserListFrame(c, this);
		cFrame = new ContainerFrame(this, userListFrame);
		cFrame.setVisible(true);
	}

	public Contact getCurrentContact() {
		return this.currentContact;
	}

	public void messageReceived(Message m) {
		
		System.out.println("_________________________________");
		System.out.println("Me: " + user.getContact());
		System.out.println("_________________________________");
		System.out.println("Contact: " + contacts);
		System.out.println("Sender: " + m.sender);
		System.out.println("Receiver: " + m.receiver);
		System.out.println("_________________________________");
		
		if(m.receiver.type.equals(ContactType.GROUP)) {
			if(!contacts.contains(m.receiver)) {
				contacts.add(m.receiver);
				updateContacts(contacts);
			}
		}
		else {
			if (!contacts.contains(m.sender) && !m.sender.equals(user.getContact())) {
				contacts.add(m.sender);
				updateContacts(contacts);
			}
		}
		if (currentContact != null && (currentContact.equals(m.sender) || currentContact.equals(m.receiver))) {
			cFrame.panelChat.addMessage(m);
		}
	}

	public void updateUserList(List<Contact> users) {
		userListFrame.updateList(users);
	}

	public void updateContacts(List<Contact> contacts) {
		this.contacts = contacts;
		cFrame.panelContacts.updateContacts(contacts);
	}

	public void setMessages(List<Message> messages) {
		cFrame.panelChat.setMessages(messages);
	}

	public void openChat(Contact contact) {
		if (contact.equals(currentContact))
			return;
		currentContact = contact;
		cFrame.panelChat.updatePartner(contact);
		client.sendMessage(new ServerMessage("getmessages", new Messages(user.getContact(), contact)));
	}

}
