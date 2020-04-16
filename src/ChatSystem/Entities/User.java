package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ChatSystem.AES;
import ChatSystem.Entities.Contact.ContactType;

@SuppressWarnings("serial")
public class User implements Serializable {

	public long id;
	public String name;
	public String password;
	public ObjectOutputStream out;

	public User(String name, String password) {
		this.name = name;
		this.password = password;
		this.id = System.currentTimeMillis();
	}
	
	public Contact getContact() {
		return new Contact(this.name, ContactType.USER);
	}
	
	public boolean equals(User u) {
		if(u == null) return false;
		return this.name.equals(u.name) && this.id == u.id;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeLong(this.id);
		out.writeObject(AES.encrypt(this.name));
		out.writeObject(AES.encrypt(this.password));
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.id = in.readLong();
		this.name = AES.decrypt((String) in.readObject());
		this.password = AES.decrypt((String) in.readObject());
	}

	public String toString() {
		return this.id + "\t" + this.name + "\t" + this.password + "\t";
	}

}
