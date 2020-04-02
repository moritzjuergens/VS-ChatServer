package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ChatSystem.DWH.Warehouse;

@SuppressWarnings("serial")
public class Message implements Comparable<Message>, Serializable {

	public long timestamp;
	public User from;
	public User toUser;
	public Group toGroup;
	public String message;

	public Message(User from, User to, String message) {
		this(from, to, null, message, System.currentTimeMillis());
	}

	public Message(User from, Group to, String message) {
		this(from, null, to, message, System.currentTimeMillis());
	}

	public Message(User from, User to, String message, long timestamp) {
		this(from, to, null, message, timestamp);
	}

	public Message(User from, Group to, String message, long timestamp) {
		this(from, null, to, message, timestamp);
	}

	public Message(User from, User uTo, Group gTo, String message, long timestamp) {
		this.from = from;
		this.toUser = uTo;
		this.toGroup = gTo;
		this.message = message;
		this.timestamp = timestamp;
		Warehouse.addMessage(this);
	}

	public boolean equals(Message m) {
		return this.from.equals(m.from) && this.toUser.equals(m.toUser) && this.toGroup.equals(m.toGroup)
				&& this.message.equals(m.message) && this.timestamp == m.timestamp;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeLong(this.timestamp);
		out.writeObject(this.from);
		out.writeObject(this.toUser);
		out.writeObject(this.toGroup);
		out.writeObject(this.message);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.timestamp = in.readLong();
		this.from = (User) in.readObject();
		this.toUser = (User) in.readObject();
		this.toGroup = (Group) in.readObject();
		this.message = (String) in.readObject();
	}

	public String toString() {
		return this.timestamp + "\t" + this.from.name + "\t" + (this.toUser != null ? this.toUser.name : this.toGroup.id) + "\t" + this.message;
	}

	@Override
	public int compareTo(Message m) {
		return this.timestamp < m.timestamp ? -1 : 1;
	}

}
