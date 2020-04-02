package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Messages implements Serializable {
	public Contact user;
	public Contact contact;
	
	public Messages(Contact u, Contact c) {
		this.user = u;
		this.contact = c;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.user);
		out.writeObject(this.contact);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.user = (Contact) in.readObject();
		this.contact = (Contact) in.readObject();
	}
	
	public String toString() {
		return this.user + "\t" + this.contact;
	}
}
