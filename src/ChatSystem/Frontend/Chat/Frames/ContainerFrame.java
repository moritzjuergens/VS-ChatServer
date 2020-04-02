package ChatSystem.Frontend.Chat.Frames;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import ChatSystem.Frontend.Chat.Chat;
import ChatSystem.Frontend.Chat.Panels.ChatPanel;
import ChatSystem.Frontend.Chat.Panels.ContactsPanel;

@SuppressWarnings("serial")
public class ContainerFrame extends JFrame {

	public ChatPanel panelChat;
	public ContactsPanel panelContacts;
	public Chat chat;
	
	public ContainerFrame(Chat c) {
		super("VS Chatsystem - Signed in as " + c.user.name);

		this.chat = c;
		
		setSize(600, 300);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		setLayout(new BorderLayout(10, 10));
		
		panelContacts = new ContactsPanel(c);
		panelChat = new ChatPanel(chat.user);
		
		add(panelContacts, BorderLayout.LINE_START);
		add(panelChat, BorderLayout.CENTER);
		
	}
}
