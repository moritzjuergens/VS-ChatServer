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
	public boolean forward;

	public SendMessagePacket(Contact sender, Contact receiver, String message, boolean forward) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.forward = forward;
	}
	
	public SendMessagePacket forward(boolean forward) {
		this.forward = forward;
		return this;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.sender);
		out.writeObject(this.receiver);
		out.writeObject(this.message);
		out.writeBoolean(this.forward);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.sender = (Contact) in.readObject();
		this.receiver = (Contact) in.readObject();
		this.message = (String) in.readObject();
		this.forward = in.readBoolean();
	}

	public String toString() {
		return this.sender + "\t" + this.receiver + "\t" + this.message;
	}
}
