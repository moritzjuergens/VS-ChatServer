package ChatSystem.Packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ChatSystem.Entities.Contact;

@SuppressWarnings("serial")
public class CreateGroupPacket implements Serializable {

	public Contact user, chat, selected;

	public CreateGroupPacket(Contact contact, Contact currentContact, Contact contact2) {
		this.user = contact;
		this.chat = currentContact;
		this.selected = contact2;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.user);
		out.writeObject(this.chat);
		out.writeObject(this.selected);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.user = (Contact) in.readObject();
		this.chat = (Contact) in.readObject();
		this.selected = (Contact) in.readObject();
	}

	public String toString() {
		return user + "\t" + chat + "\t" + selected;
	}

}
