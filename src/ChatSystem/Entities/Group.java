package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ChatSystem.DWH.Warehouse;
import ChatSystem.Entities.Contact.ContactType;

@SuppressWarnings("serial")
public class Group implements Serializable {

	public long id;
	public List<Contact> members;

	public Group() {
		this(new Contact[] {});
	}

	public Group(Contact... member) {
		this.members = new ArrayList<Contact>();
		for (Contact c : member) {
			this.members.add(c);
		}
		this.id = System.currentTimeMillis();
		Warehouse.addGroup(this);
	}

	public Contact getContact() {
		return new Contact(this.id + "", ContactType.GROUP);
	}

	public boolean equals(Group g) {
		return this.id == g.id;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeLong(this.id);
		out.writeObject(this.members);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.id = in.readLong();
		this.members = (ArrayList<Contact>) in.readObject();
	}

	public String toString() {
		return this.id + "\t";
	}

}
