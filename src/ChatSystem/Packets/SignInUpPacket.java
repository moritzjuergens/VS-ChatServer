package ChatSystem.Packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@SuppressWarnings("serial")
public class SignInUpPacket implements Serializable {

	public String name;
	public String password;

	public SignInUpPacket(String name, String password) {
		this.name = name;
		this.password = password;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.name);
		out.writeObject(this.password);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.name = (String) in.readObject();
		this.password = (String) in.readObject();
	}
}