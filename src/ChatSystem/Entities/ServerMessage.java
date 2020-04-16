package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ChatSystem.AES;

@SuppressWarnings("serial")
public class ServerMessage implements Serializable {

	public String prefix;
	public Object object;

	public ServerMessage(String prefix, Object object) {
		this.prefix = prefix;
		this.object = object;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(AES.encrypt(this.prefix));
		out.writeObject(this.object);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.prefix = AES.decrypt((String) in.readObject());
		this.object = in.readObject();
	}

	public String toString() {
		if(this.object == null) return this.prefix + "\t";
		return this.prefix + "\t" + this.object.toString() + "\t";
	}
}
