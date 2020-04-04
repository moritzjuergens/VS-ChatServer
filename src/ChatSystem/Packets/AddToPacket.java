package ChatSystem.Packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ChatSystem.Entities.Contact;

@SuppressWarnings("serial")
public class AddToPacket implements Serializable {

	public Contact contact, invitee;

	public AddToPacket(Contact group, Contact user) {
		this.contact = group;
		this.invitee = user;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.contact);
		out.writeObject(this.invitee);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.contact = (Contact) in.readObject();
		this.invitee = (Contact) in.readObject();
	}

}
