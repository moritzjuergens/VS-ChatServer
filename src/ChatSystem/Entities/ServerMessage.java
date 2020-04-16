package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ChatSystem.AES;

/**
 * Object to be send between server <-> server or server <-> client. Every
 * ServerMessage has a prefix (Header) and its content
 * 
 * @author timos
 *
 */
@SuppressWarnings("serial")
public class ServerMessage implements Serializable {

	public String prefix;
	public Object object;

	/**
	 * Create a new ServermEssage
	 * 
	 * @param prefix Prefix
	 * @param object Object to be sent
	 */
	public ServerMessage(String prefix, Object object) {
		this.prefix = prefix;
		this.object = object;
	}

	/**
	 * Encrypt prefix and object. Object will encrypt itself
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(AES.encrypt(this.prefix));
		out.writeObject(this.object);
	}

	/**
	 * Decrypt prefix and object. Object will decrypt itself
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.prefix = AES.decrypt((String) in.readObject());
		this.object = in.readObject();
	}

	public String toString() {
		if (this.object == null)
			return this.prefix + "\t";
		return this.prefix + "\t" + this.object.toString() + "\t";
	}
}
