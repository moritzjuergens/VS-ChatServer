package ChatSystem.Frontend.Chat.Panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ChatSystem.Entities.Contact;
import ChatSystem.Frontend.Chat.Chat;

@SuppressWarnings("serial")
public class ContactsPanel extends JPanel {

	JPanel contactList = new JPanel();
	Chat chat;
	
	public ContactsPanel(Chat c) {
		this.chat = c;
		setLayout(new BorderLayout());
		
		contactList.setLayout(new BoxLayout(contactList, BoxLayout.Y_AXIS));
		
		JPanel header = new JPanel(new BorderLayout(10, 10));
		header.add(new JLabel("Contacts"), BorderLayout.LINE_START);
		
		JButton newChat = new JButton("new chat");
		header.add(newChat, BorderLayout.LINE_END);
		
		add(header, BorderLayout.PAGE_START);
		
		add(new JScrollPane(contactList), BorderLayout.CENTER);
		
	}
	
	public void updateContacts(List<Contact> contacts) {
		contactList.removeAll();
		System.out.println("hallo");
		for(Contact contact : contacts) {
			System.out.println(contact);
			JButton button = new JButton(contact.name);
			
			contactList.add(button);
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					chat.openChat(new Contact(contact.name, contact.type));
				}
			});
		}
		this.updateUI();
	}
	
}
