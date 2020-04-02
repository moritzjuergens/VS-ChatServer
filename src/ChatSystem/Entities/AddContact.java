package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@SuppressWarnings("serial")
public class AddContact implements Serializable {
	
	public Contact who;
	public Contact whom;
	
	public AddContact(Contact who, Contact whom) {
		this.who = who;
		this.whom = whom;
	}
	
	public String toString() {
		return this.who + "\t" + this.whom; 
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.who);
		out.writeObject(this.whom);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.who = (Contact) in.readObject();
		this.whom = (Contact) in.readObject();
	}
}
