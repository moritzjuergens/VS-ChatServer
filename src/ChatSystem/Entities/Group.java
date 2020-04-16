package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ChatSystem.AES;
import ChatSystem.RandomGroupNameGenerator;
import ChatSystem.Entities.Contact.ContactType;

/**
 * 
 * @author timos
 *
 */
@SuppressWarnings("serial")
public class Group implements Serializable {

	public long id;
	public List<Contact> members;
	public Contact creator;
	public String name;

	/**
	 * Create a new group
	 * 
	 * @param member Members the group should automatically be filled with
	 */
	public Group(Contact... member) {
		this.members = new ArrayList<Contact>();
		this.creator = member[0];
		for (Contact c : member) {
			this.members.add(c);
		}
		this.id = System.currentTimeMillis();
		setName(RandomGroupNameGenerator.generate());
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * get contact of Group
	 * 
	 * @return Contact
	 */
	public Contact getContact() {
		return new Contact(this.id + "", ContactType.GROUP, this.name);
	}

	public boolean equals(Group g) {
		return this.id == g.id;
	}

	/**
	 * Encrypt members, creator and name. Members and creator will be encypted in
	 * Entities.Contact.java
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeLong(this.id);
		out.writeObject(this.members);
		out.writeObject(this.creator);
		out.writeObject(AES.encrypt(this.name));
	}

	/**
	 * Decrypt memvers, creator and name. Members and creator will be decrypted in
	 * Entities.Contact.Java
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.id = in.readLong();
		this.members = (ArrayList<Contact>) in.readObject();
		this.creator = (Contact) in.readObject();
		this.name = AES.decrypt((String) in.readObject());
	}

	public String toString() {
		return this.id + "\t" + this.name;
	}

}
