package ChatSystem.Packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ChatSystem.AES;
import ChatSystem.Entities.Contact;

/**
 * used to send a new message to users server and sharing between server
 * 
 * @author timos
 *
 */
@SuppressWarnings("serial")
public class SendMessagePacket implements Serializable {

	public Contact sender;
	public Contact receiver;
	public String message;
	public boolean forward;

	/**
	 * Generate a new SendMessagePacket
	 * 
	 * @param sender   author
	 * @param receiver receiver
	 * @param message  message content
	 * @param forward  forward message to other servers if first recipient
	 */
	public SendMessagePacket(Contact sender, Contact receiver, String message, boolean forward) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.forward = forward;
	}

	/**
	 * update forward
	 * 
	 * @param forward boolean
	 * @return SendMessagePacket
	 */
	public SendMessagePacket forward(boolean forward) {
		this.forward = forward;
		return this;
	}

	/**
	 * Sender, receiver will be encrypted in Entities.Contact.java
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.sender);
		out.writeObject(this.receiver);
		out.writeObject(AES.encrypt(this.message));
		out.writeBoolean(this.forward);
	}

	/**
	 * Sender, receiver will be decrypted in Entities.Contact.java
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.sender = (Contact) in.readObject();
		this.receiver = (Contact) in.readObject();
		this.message = AES.decrypt((String) in.readObject());
		this.forward = in.readBoolean();
	}

	public String toString() {
		return this.sender + "\t" + this.receiver + "\t" + this.message;
	}
}
