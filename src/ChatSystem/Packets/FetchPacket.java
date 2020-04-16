package ChatSystem.Packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import ChatSystem.Entities.Group;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.User;

/**
 * used to send missed data between servers
 * 
 * @author timos
 *
 */
@SuppressWarnings("serial")
public class FetchPacket implements Serializable {

	public List<Message> messages;
	public List<Group> groups;
	public List<User> users;

	/**
	 * Generate a new FetchPacket
	 * 
	 * @param messages Messages missed
	 * @param groups   Groups missed
	 * @param users    Users missed
	 */
	public FetchPacket(List<Message> messages, List<Group> groups, List<User> users) {
		this.messages = messages;
		this.groups = groups;
		this.users = users;
	}

	/**
	 * Messages will be encrypted in Entities.Message.java.
	 * 
	 * Groups will be encrypted in Entities.Group.java
	 * 
	 * User will be encrypted in Entities.User.java
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.messages);
		out.writeObject(this.groups);
		out.writeObject(this.users);
	}

	/**
	 * Messages will be decrypted in Entities.Message.java.
	 * 
	 * Groups will be decrypted in Entities.Group.java
	 * 
	 * User will be decrypted in Entities.User.java
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.messages = (List<Message>) in.readObject();
		this.groups = (List<Group>) in.readObject();
		this.users = (List<User>) in.readObject();
	}

}
