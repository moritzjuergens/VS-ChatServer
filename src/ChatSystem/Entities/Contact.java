package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ChatSystem.AES;

@SuppressWarnings("serial")
public class Contact implements Serializable {

	public String name;
	public ContactType type;
	public String shortName = "";

	public Contact(String name, ContactType type, String shortName) {
		this(name, type);
		this.shortName = shortName;
	}
	
	public Contact(String name, ContactType type) {
		this.name = name;
		this.type = type;
	}

	public enum ContactType {
		USER(), GROUP(), SYSTEM();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (getClass() != o.getClass())
			return false;
		Contact c = (Contact) o;
		return this.name.equals(c.name) && this.type.equals(c.type);
	}

	public String toString() {
		return this.name + "\t" + this.type;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(AES.encrypt(this.name));
		out.writeObject(this.type);
		out.writeObject(AES.encrypt(this.shortName));
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.name = AES.decrypt((String) in.readObject());
		this.type = (ContactType) in.readObject();
		this.shortName = AES.decrypt((String) in.readObject());
	}

}
