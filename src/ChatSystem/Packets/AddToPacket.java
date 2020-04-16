package ChatSystem.Packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ChatSystem.Entities.Contact;

/**
 * Used to add Users to an existing Chat (Privat or Group)
 * 
 * @author timos
 *
 */
@SuppressWarnings("serial")
public class AddToPacket implements Serializable {

	public Contact contact, invitee;
	public boolean forward;

	/**
	 * Generate a new AddToPacket
	 * 
	 * @param group   Group the user should be added to
	 * @param user    User
	 * @param forward forward message to other servers if first recipient
	 */
	public AddToPacket(Contact group, Contact user, boolean forward) {
		this.contact = group;
		this.invitee = user;
		this.forward = forward;
	}

	/**
	 * update forward
	 * 
	 * @param forward boolean
	 * @return AddToPacket
	 */
	public AddToPacket forward(boolean forward) {
		this.forward = forward;
		return this;
	}

	/**
	 * Contact, invitee will be encrypted in Entities.Contact.java
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.contact);
		out.writeObject(this.invitee);
		out.writeBoolean(this.forward);
	}

	/**
	 * Contact, invitee will be decrypted in Entities.Contact.java
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.contact = (Contact) in.readObject();
		this.invitee = (Contact) in.readObject();
		this.forward = in.readBoolean();
	}

}
