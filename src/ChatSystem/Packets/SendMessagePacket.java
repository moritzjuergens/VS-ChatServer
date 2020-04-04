package ChatSystem.Packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ChatSystem.Entities.Contact;

@SuppressWarnings("serial")
public class SendMessagePacket implements Serializable {

	public Contact sender;
	public Contact receiver;
	public String message;

	public SendMessagePacket(Contact sender, Contact receiver, String message) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.sender);
		out.writeObject(this.receiver);
		out.writeObject(this.message);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.sender = (Contact) in.readObject();
		this.receiver = (Contact) in.readObject();
		this.message = (String) in.readObject();
	}

	public String toString() {
		return this.sender + "\t" + this.receiver + "\t" + this.message;
	}
}
