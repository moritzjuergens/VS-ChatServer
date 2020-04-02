package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Contact implements Serializable {

	public String name;
	public ContactType type;

	public Contact(String name, ContactType type) {
		this.name = name;
		this.type = type;
	}

	public enum ContactType {
		USER(), GROUP();
	}
	
	public boolean equals(Contact c) {
		if(c == null) return false;
		return this.name.equals(c.name) && this.type.equals(c.type);
	}
	
	public String toString() {
		return this.name + "\t" + this.type;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.name);
		out.writeObject(this.type);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.name = (String) in.readObject();
		this.type = (ContactType) in.readObject();
	}

}
