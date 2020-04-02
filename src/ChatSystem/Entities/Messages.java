package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Messages implements Serializable {
	public User user;
	public Contact contact;
	
	public Messages(User u, Contact c) {
		this.user = u;
		this.contact = c;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.user);
		out.writeObject(this.contact);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.user = (User) in.readObject();
		this.contact = (Contact) in.readObject();
	}
}
