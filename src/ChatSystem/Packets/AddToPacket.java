package ChatSystem.Packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ChatSystem.Entities.Contact;

@SuppressWarnings("serial")
public class AddToPacket implements Serializable {

	public Contact contact, invitee;
	public boolean forward;

	public AddToPacket(Contact group, Contact user, boolean forward) {
		this.contact = group;
		this.invitee = user;
		this.forward = forward;
	}
	
	public AddToPacket forward(boolean forward) {
		this.forward = forward;
		return this;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.contact);
		out.writeObject(this.invitee);
		out.writeBoolean(this.forward);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.contact = (Contact) in.readObject();
		this.invitee = (Contact) in.readObject();
		this.forward = in.readBoolean();
	}

}
