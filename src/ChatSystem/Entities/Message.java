package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ChatSystem.AES;

@SuppressWarnings("serial")
public class Message implements Comparable<Message>, Serializable {

	public long timestamp;
	public Contact sender;
	public Contact receiver;
	public String message;

	/**
	 * Create a new message
	 * 
	 * @param sender   Sender of message
	 * @param receiver Receiver of message
	 * @param message  Text of message
	 */
	public Message(Contact sender, Contact receiver, String message) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.timestamp = System.currentTimeMillis();
	}

	public boolean equals(Message m) {
		if (m == null)
			return false;
		return this.sender.equals(m.sender) && this.receiver.equals(m.receiver) && this.message.equals(m.message)
				&& this.timestamp == m.timestamp;
	}

	/**
	 * Encrypt sender, receiver and message. Sender and receiver will be encrypted
	 * in Entities.Contact.java
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeLong(this.timestamp);
		out.writeObject(this.sender);
		out.writeObject(this.receiver);
		out.writeObject(AES.encrypt(this.message));
	}

	/**
	 * Decrypt sender, receiver and message. Sender and receiver will be decrypted
	 * in Entities.Contact.java
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.timestamp = in.readLong();
		this.sender = (Contact) in.readObject();
		this.receiver = (Contact) in.readObject();
		this.message = AES.decrypt((String) in.readObject());
	}

	public String toString() {
		return this.timestamp + "\t" + this.sender + "\t" + this.receiver + "\t" + this.message;
	}

	@Override
	public int compareTo(Message m) {
		return this.timestamp < m.timestamp ? -1 : 1;
	}

}
