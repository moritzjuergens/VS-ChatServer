package ChatSystem.Packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import ChatSystem.Entities.Contact;

/**
 * used to retrieve all contacts registered to the server
 * 
 * @author timos
 *
 */
@SuppressWarnings("serial")
public class AllContactsPacket implements Serializable {

	public List<Contact> clients;

	/**
	 * Generate a new AllContactPacket
	 * 
	 * @param list Contact
	 */
	public AllContactsPacket(List<Contact> list) {
		this.clients = list;
	}

	/**
	 * Clients will be encrypted in Entities.Contact.java
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.clients);
	}

	/**
	 * Clients will be decrypted in Entities.Contact.java
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.clients = (List<Contact>) in.readObject();
	}

	public String toString() {
		return clients.toString();
	}

}
