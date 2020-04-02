package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ChatSystem.DWH.Warehouse;
import ChatSystem.Entities.Contact.ContactType;

@SuppressWarnings("serial")
public class User implements Serializable {

	public long id;
	public String name;
	public String password;
	public ObjectOutputStream out;

	public User(String name, String password) {
		this(name, password, System.currentTimeMillis());
	}
	
	public User(String name, String password, long id) {
		this.name = name;
		this.password = password;
		this.id = id;
		Warehouse.addUser(this);
	}
	
	public Contact getContact() {
		return new Contact(this.name, ContactType.USER);
	}
	
	public boolean equals(User u) {
		if(u == null) return false;
		return this.name.equals(u.name);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeLong(this.id);
		out.writeObject(this.name);
		out.writeObject(this.password);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.id = in.readLong();
		this.name = (String) in.readObject();
		this.password = (String) in.readObject();
	}

	public String toString() {
		return this.id + "\t" + this.name + "\t" + this.password + "\t";
	}

}
