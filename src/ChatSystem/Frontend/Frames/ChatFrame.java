package ChatSystem.Frontend.Frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import ChatSystem.Entities.Contact;
import ChatSystem.Entities.Contact.ContactType;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.SendMessage;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Frontend.ChatManager;
import ChatSystem.Frontend.ComponentFactory;
import ChatSystem.Frontend.Emoji.Emoji;
import ChatSystem.Frontend.Emoji.EmojiChatManager;

@SuppressWarnings("serial")
public class ChatFrame extends JFrame {

	private Contact currentContact = null;
	private JLabel chatTitle = ComponentFactory.getLabel("Open a Chat", 203, 6, 300, 25);
	private JPanel contactWrapper = ComponentFactory.getPanel(null);
	private JTextPane chatPane = ComponentFactory.getTextPane();
	private JTextField textField = ComponentFactory.getTextField(278, 334, 211, 25);
	private ChatManager manager;
	private JLabel background = new JLabel(new ImageIcon("./assets/chat.png"));

	public ChatFrame(ChatManager manager) {

		this.manager = manager;

		setTitle("VS Chatsystem - Signed in as " + manager.user.name);
		setSize(600, 400);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		add(background);
		background.setLayout(null);

		background.add(ComponentFactory.getButton(new ImageIcon("./assets/contact.png"), false, 6, 6, 25, 25, (e) -> {

		}));

		background.add(chatTitle);

		JLabel contactsTitle = ComponentFactory.getLabel("Contacts", 3, 6, 186, 25);
		contactsTitle.setHorizontalAlignment(SwingConstants.CENTER);
		background.add(contactsTitle);

		background.add(ComponentFactory.getScrollPane(this.chatPane, 200, 40, 385, 285));

		this.contactWrapper.setLayout(new BoxLayout(this.contactWrapper, BoxLayout.Y_AXIS));
		background.add(ComponentFactory.getScrollPane(this.contactWrapper, 0, 35, 192, 327));

		setVisible(true);
	}

	public void sendMessage() {
		String message = textField.getText();
		if (message.length() < 0)
			return;
		manager.client.sendMessage(
				new ServerMessage("message", new SendMessage(manager.user.getContact(), currentContact, message)));
		textField.setText("");
	}

	public void insertEmoji(Emoji emoji) {
		textField.setText(textField.getText() + ":" + emoji + ":");
	}

	public void removeAllMessages() {
		this.chatPane.setText("");
	}

	public void setMessages(List<Message> list) {
		list.stream().forEach(this::addMessage);
	}

	public void addMessage(Message m) {
		
		String msg = (manager.user.name.equals(m.sender.name) ? "You: "
				: (currentContact.type.equals(ContactType.GROUP) ? m.sender.name + ": " : "")) + m.message + "\n";
		Color c = manager.user.name.equals(m.sender.name) ? new Color(37, 202, 73) : new Color(0, 136, 255);
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");

		chatPane.setEditable(true);
		int len = chatPane.getDocument().getLength();

		chatPane.setCaretPosition(len);
		chatPane.setCharacterAttributes(aset, false);
		chatPane.replaceSelection("\n" + msg);
		
		EmojiChatManager.changed(chatPane);
		chatPane.setEditable(false);
		
	}

	public void updateContacts(HashMap<Contact, List<Message>> chatData) {
		contactWrapper.removeAll();
		for (Contact c : chatData.keySet()) {

			Message m = manager.getLatestMessageWith(c);
			String name = (m.sender.name.equals(manager.user.name) ? "You: " : "");

			contactWrapper
					.add(ComponentFactory.getContact(c.name, name + m.message, false, c.equals(currentContact), (e) -> {
						manager.openChatWith(c);
					}));
			contactWrapper.add(ComponentFactory.getContactSpacer());
		}
	}

	public void setChat(Contact c) {

		if (this.currentContact == null) {
			background.add(
					ComponentFactory.getButton(new ImageIcon("./assets/addToGroup.png"), true, 555, 6, 25, 25, (e) -> {

					}));
			background.add(ComponentFactory.getLabel("Message", 203, 334, 70, 25));

			background.add(textField);

			background.add(ComponentFactory.getButton("Send", true, 495, 334, 50, 25, (e) -> {
				sendMessage();
			}));

			background.add(ComponentFactory.getButton(new ImageIcon("./emojis/" + Emoji.GRINNING_FACE.name), false, 550,
					334, 25, 25, (e) -> {
						manager.emojiPopUp.open(this);
					}));
			background.updateUI();
		}

		this.currentContact = c;
		chatTitle.setText("Chatting with: " + c.name);
		this.removeAllMessages();
	}

}
