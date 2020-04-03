package ChatSystem.Frontend.Chat.Panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

import ChatSystem.Entities.Contact;
import ChatSystem.Entities.Contact.ContactType;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.SendMessage;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Entities.User;
import ChatSystem.Frontend.Chat.Chat;
import ChatSystem.Frontend.Chat.Frames.UserListFrame;

@SuppressWarnings("serial")
public class ChatPanel extends JPanel {

	User user;
	Contact currentContact;

	JLabel title = new JLabel("Chat", SwingConstants.CENTER);
	JTextPane chatArea = new JTextPane();
	JTextField textField = new JTextField();
	JButton sendMessage = new JButton("send");
	JButton addUser = new JButton("+");
	JPanel header = new JPanel(new BorderLayout(10, 10));
	JPanel footer = new JPanel(new BorderLayout(10, 10));
	List<Message> messages = new ArrayList<>();
	UserListFrame userListframe;
	Chat chat;

	public ChatPanel(Chat chat, UserListFrame userListFrame) {

		this.chat = chat;
		this.user = chat.user;
		this.userListframe = userListFrame;

		setLayout(new BorderLayout(10, 10));

		JPanel header = new JPanel(new BorderLayout(10, 10));
		addUser.setVisible(false);
		header.add(title, BorderLayout.CENTER);
		header.add(addUser, BorderLayout.LINE_END);
		add(header, BorderLayout.PAGE_START);

		chatArea.setAutoscrolls(true);
		chatArea.setEditorKit(new StyledEditorKit());
		chatArea.setEditable(false);
		add(new JScrollPane(chatArea), BorderLayout.CENTER);

		footer.add(new JLabel("Message"), BorderLayout.LINE_START);
		footer.add(textField, BorderLayout.CENTER);
		footer.add(sendMessage, BorderLayout.LINE_END);
		footer.setVisible(false);

		sendMessage.addActionListener((e) -> {
			String msg = textField.getText();
			if (msg.length() != 0) {
				chat.client.sendMessage(new ServerMessage("message",
						new SendMessage(chat.user.getContact(), chat.getCurrentContact(), msg)));
				textField.setText("");
			}
		});

		addUser.addActionListener((e) -> {
			chat.client.sendMessage(new ServerMessage("getalluser", chat.user.getContact()));
			userListFrame.open("chat");
		});

		add(footer, BorderLayout.PAGE_END);

	}

	public void setMessages(List<Message> newMessages) {
		this.messages = new ArrayList<Message>();
		chatArea.setText("");
		for (Message m : newMessages) {
			addMessage(m);
		}
	}

	public void addMessage(Message m) {
		this.messages.add(m);

		String msg = (user.name.equals(m.sender.name) ? "You: "
				: (currentContact.type.equals(ContactType.GROUP) ? m.sender.name + ": " : "")) + m.message + "\n";
		Color c = user.name.equals(m.sender.name) ? new Color(37, 202, 73) : new Color(0, 136, 255);
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");

		chatArea.setEditable(true);
		int len = chatArea.getDocument().getLength();

		chatArea.setCaretPosition(len);
		chatArea.setCharacterAttributes(aset, false);
		chatArea.replaceSelection("\n" + msg);

		StyledDocument document = chatArea.getStyledDocument();
		Style labelStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
		Icon icon = new ImageIcon("./emojis/question-triangle – 1.png");
		JLabel label = new JLabel(icon);
		StyleConstants.setComponent(labelStyle, label);
		try {
			document.insertString(document.getLength(), "d", aset);
			document.insertString(document.getLength(), "Ignored", labelStyle);
		} catch (BadLocationException badLocationException) {
			System.err.println("Oops");
		}

		chatArea.setEditable(false);
	}

	public void updatePartner(Contact contact) {
		this.currentContact = contact;
		addUser.setVisible(true);
		footer.setVisible(true);
		this.title.setText("Chat - " + contact.name);
	}

}
