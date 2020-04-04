package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ChatSystem.RandomGroupNameGenerator;
import ChatSystem.DWH.Warehouse;
import ChatSystem.Entities.Contact.ContactType;

@SuppressWarnings("serial")
public class Group implements Serializable {

	public long id;
	public List<Contact> members;
	public Contact creator;
	public String name;

	public Group(Contact... member) {
		this.members = new ArrayList<Contact>();
		this.creator = member[0];
		for (Contact c : member) {
			this.members.add(c);
		}
		this.id = System.currentTimeMillis();
		setName(RandomGroupNameGenerator.generate());
		Warehouse.addGroup(this);
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Contact getContact() {
		return new Contact(this.id + "", ContactType.GROUP, this.name);
	}

	public boolean equals(Group g) {
		return this.id == g.id;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeLong(this.id);
		out.writeObject(this.members);
		out.writeObject(this.creator);
		out.writeObject(this.name);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.id = in.readLong();
		this.members = (ArrayList<Contact>) in.readObject();
		this.creator = (Contact) in.readObject();
		this.name = (String) in.readObject();
	}

	public String toString() {
		return this.id + "\t" + this.name;
	}

}
