package ChatSystem.Packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import ChatSystem.Entities.Group;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.User;

@SuppressWarnings("serial")
public class FetchPacket implements Serializable {

	public List<Message> messages;
	public List<Group> groups;
	public List<User> users;

	public FetchPacket(List<Message> messages, List<Group> groups, List<User> users) {
		this.messages = messages;
		this.groups = groups;
		this.users = users;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.messages);
		out.writeObject(this.groups);
		out.writeObject(this.users);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.messages = (List<Message>) in.readObject();
		this.groups = (List<Group>) in.readObject();
		this.users = (List<User>) in.readObject();
	}

}
