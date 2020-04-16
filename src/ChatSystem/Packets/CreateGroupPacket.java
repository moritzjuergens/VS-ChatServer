package ChatSystem.Packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ChatSystem.Entities.Contact;

/**
 * used to create a new group with 3 initial member
 * 
 * @author timos
 *
 */
@SuppressWarnings("serial")
public class CreateGroupPacket implements Serializable {

	public Contact user, chat, selected;

	/**
	 * Generate a new CreateGroupPacket
	 * 
	 * @param contact        owner
	 * @param currentContact currently opened chat
	 * @param contact2       contact selected in contactpopup
	 */
	public CreateGroupPacket(Contact contact, Contact currentContact, Contact contact2) {
		this.user = contact;
		this.chat = currentContact;
		this.selected = contact2;
	}

	/**
	 * User, chat, selected will be encrypted in Entities.Contact.java
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.user);
		out.writeObject(this.chat);
		out.writeObject(this.selected);
	}

	/**
	 * User, chat, selected will be decrypted in Entities.Contact.java
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.user = (Contact) in.readObject();
		this.chat = (Contact) in.readObject();
		this.selected = (Contact) in.readObject();
	}

	public String toString() {
		return user + "\t" + chat + "\t" + selected;
	}

}
