package ChatSystem.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ChatSystem.DWH.Warehouse;

@SuppressWarnings("serial")
public class Group implements Serializable {

	public long id;
	public List<User> members;
	
	public Group() {
		this.members = new ArrayList<User>();
		this.id = System.currentTimeMillis();
		Warehouse.addGroup(this);
	}
	
	public boolean equals(Group g) {
		return this.id == g.id;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeLong(this.id);
		out.writeObject(this.members);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.id = in.readLong();
		this.members = (ArrayList<User>) in.readObject();
	}
	
	public String toString() {
		return this.id + "\t";
	}

	
}
