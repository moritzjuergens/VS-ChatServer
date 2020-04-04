package ChatSystem.Frontend.Frames;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ChatSystem.Client.Client;
import ChatSystem.Entities.AddContact;
import ChatSystem.Entities.Contact;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Frontend.ChatManager;

@SuppressWarnings("serial")
public class UserListFrame extends JFrame {

	JPanel userList = new JPanel();
	Client client;
	ChatManager chat;
	String origin;

	public UserListFrame(Client client, ChatManager chat) {
		this.client = client;
		this.chat = chat;

		setSize(50, 300);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		add(new JScrollPane(userList));
	}

	public void open(String origin) {
		this.origin = origin;
		setVisible(true);
		setLocationRelativeTo(null);
	}

	public void updateList(List<Contact> contacts) {
		userList.removeAll();
		for (Contact c : contacts) {
			JButton button = new JButton(c.name);
			button.addActionListener((e) -> {
				userClicked(c);
			});
			userList.add(button);
		}
		userList.setLayout(new BoxLayout(userList, BoxLayout.Y_AXIS));
	}

	public void userClicked(Contact c) {
		if (origin.equals("contacts")) {
			client.sendMessage(new ServerMessage("newcontact", new AddContact(chat.user.getContact(), c)));
		} else {
//			if (chat.getCurrentContact().type.equals(ContactType.GROUP)) {
//				client.sendMessage(new ServerMessage("addtogroup", new AddContact(chat.getCurrentContact(), c)));
//			} else {
//				client.sendMessage(new ServerMessage("newgroup", new AddContact(chat.user.getContact(), c)));
//			}
		}
		this.setVisible(false);
	}
}
