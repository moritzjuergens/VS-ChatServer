package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ChatSystem.DWH.Warehouse;

@SuppressWarnings("serial")
public class Message implements Comparable<Message>, Serializable {

	public long timestamp;
	public Contact sender;
	public Contact receiver;
	public String message;

	public Message(Contact sender, Contact receiver, String message) {
		this(sender, receiver, message, System.currentTimeMillis());
	}

	public Message(Contact sender, Contact receiver, String message, long timestamp) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.timestamp = timestamp;
		Warehouse.addMessage(this);
	}

	public boolean equals(Message m) {
		if (m == null)
			return false;
		return this.sender.equals(m.sender) && this.receiver.equals(m.receiver) && this.message.equals(m.message)
				&& this.timestamp == m.timestamp;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeLong(this.timestamp);
		out.writeObject(this.sender);
		out.writeObject(this.receiver);
		out.writeObject(this.message);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.timestamp = in.readLong();
		this.sender = (Contact) in.readObject();
		this.receiver = (Contact) in.readObject();
		this.message = (String) in.readObject();
	}

	public String toString() {
		return this.timestamp + "\t" + this.sender + "\t" + this.receiver + "\t" + this.message;
	}

	@Override
	public int compareTo(Message m) {
		return this.timestamp < m.timestamp ? -1 : 1;
	}

}