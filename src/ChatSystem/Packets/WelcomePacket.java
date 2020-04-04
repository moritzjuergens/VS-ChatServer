package ChatSystem.Packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import ChatSystem.Entities.Contact;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.User;

@SuppressWarnings("serial")
public class WelcomePacket implements Serializable {

	public User user;
	public HashMap<Contact, List<Message>> userData = new HashMap<>();

	public WelcomePacket(User user, HashMap<Contact, List<Message>> userData) {
		this.user = user;
		this.userData = userData;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.user);
		out.writeObject(this.userData);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.user = (User) in.readObject();
		this.userData = (HashMap<Contact, List<Message>>) in.readObject();
	}

	public String toString() {
		return user.name + "\t" + userData.toString();
	}
}
