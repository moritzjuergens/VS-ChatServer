package ChatSystem.Packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@SuppressWarnings("serial")
public class SignInUpPacket implements Serializable {

	public String name;
	public String password;
	public boolean alreadySignedIn;
	public long timestamp;

	public SignInUpPacket(String name, String password, boolean alreadySignedIn, long timestamp) {
		this.name = name;
		this.password = password;
		this.alreadySignedIn = alreadySignedIn;
		this.timestamp = timestamp;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.name);
		out.writeObject(this.password);
		out.writeBoolean(this.alreadySignedIn);
		out.writeLong(this.timestamp);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.name = (String) in.readObject();
		this.password = (String) in.readObject();
		this.alreadySignedIn = in.readBoolean();
		this.timestamp = in.readLong();
	}
	
	public String toString() {
		return "\t" + this.name + "\t" + this.password + "\t" + this.timestamp;
	}
}