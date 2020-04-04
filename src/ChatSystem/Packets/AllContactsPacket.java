package ChatSystem.Packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import ChatSystem.Entities.Contact;

@SuppressWarnings("serial")
public class AllContactsPacket implements Serializable {
	
	public List<Contact> clients;
	
	public AllContactsPacket(List<Contact> list) {
		this.clients = list;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.clients);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.clients = (List<Contact>) in.readObject();
	}
	
	public String toString() {
		return clients.toString();
	}
	
}
