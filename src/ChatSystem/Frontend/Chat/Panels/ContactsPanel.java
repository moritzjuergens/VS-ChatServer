package ChatSystem.Frontend.Chat.Panels;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ChatSystem.Entities.Contact;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Frontend.Chat.Chat;
import ChatSystem.Frontend.Chat.Frames.UserListFrame;

@SuppressWarnings("serial")
public class ContactsPanel extends JPanel {

	JPanel contactList = new JPanel();
	Chat chat;

	public ContactsPanel(Chat c, UserListFrame userListFrame) {
		this.chat = c;
		setLayout(new BorderLayout());

		contactList.setLayout(new BoxLayout(contactList, BoxLayout.Y_AXIS));

		JPanel header = new JPanel(new BorderLayout(10, 10));
		header.add(new JLabel("Contacts"), BorderLayout.LINE_START);

		JButton newChat = new JButton("new chat");
		header.add(newChat, BorderLayout.LINE_END);

		add(header, BorderLayout.PAGE_START);

		add(new JScrollPane(contactList), BorderLayout.CENTER);

		newChat.addActionListener((e) -> {
			chat.client.sendMessage(new ServerMessage("getalluser", chat.user.getContact()));
			userListFrame.open("contacts");
		});

	}

	public void updateContacts(List<Contact> contacts) {
		contactList.removeAll();
		for (Contact contact : contacts) {
			JButton button = new JButton(contact.name);
			contactList.add(button);

			button.addActionListener((e) -> {
				chat.openChat(contact);
			});
		}
		this.updateUI();
	}

}
