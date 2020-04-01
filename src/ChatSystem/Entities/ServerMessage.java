package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@SuppressWarnings("serial")
public class ServerMessage implements Serializable {

	public String prefix;
	public Object object;

	public ServerMessage(String prefix, Object object) {
		this.prefix = prefix;
		this.object = object;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.prefix);
		out.writeObject(this.object);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.prefix = (String) in.readObject();
		this.object = in.readObject();
	}

	public String toString() {
		return this.prefix + "\t" + this.object.toString() + "\t";
	}
}
