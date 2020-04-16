package ChatSystem.Packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ChatSystem.AES;

/**
 * used for login and registration verification
 * 
 * @author timos
 *
 */
@SuppressWarnings("serial")
public class SignInUpPacket implements Serializable {

	public String name;
	public String password;
	public boolean alreadySignedIn;
	public long timestamp;

	/**
	 * Generates a new SignInUpPacket
	 * 
	 * @param name            username
	 * @param password        password
	 * @param alreadySignedIn boolean (used for reconnecting on connection loss)
	 * @param timestamp       long
	 */
	public SignInUpPacket(String name, String password, boolean alreadySignedIn, long timestamp) {
		this.name = name;
		this.password = password;
		this.alreadySignedIn = alreadySignedIn;
		this.timestamp = timestamp;
	}

	/**
	 * encrypt
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(AES.encrypt(this.name));
		out.writeObject(AES.encrypt(this.password));
		out.writeBoolean(this.alreadySignedIn);
		out.writeLong(this.timestamp);
	}

	/**
	 * decrypt
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.name = AES.decrypt((String) in.readObject());
		this.password = AES.decrypt((String) in.readObject());
		this.alreadySignedIn = in.readBoolean();
		this.timestamp = in.readLong();
	}

	public String toString() {
		return "\t" + this.name + "\t" + this.password + "\t" + this.timestamp;
	}
}