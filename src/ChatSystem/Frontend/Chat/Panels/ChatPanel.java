package ChatSystem.Frontend.Chat.Panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledEditorKit;

import ChatSystem.Entities.Message;
import ChatSystem.Entities.User;

@SuppressWarnings("serial")
public class ChatPanel extends JPanel {

	User user;
	String chatPartner;
	JLabel title = new JLabel("Chat", SwingConstants.CENTER);
	JTextPane chatArea = new JTextPane();
	JTextField textField = new JTextField();
	JButton sendMessage = new JButton("send");

	public ChatPanel(User u) {

		this.user = u;
		setLayout(new BorderLayout(10, 10));

		add(title, BorderLayout.PAGE_START);

		chatArea.setAutoscrolls(true);
		chatArea.setEditorKit(new StyledEditorKit());
		add(new JScrollPane(chatArea), BorderLayout.CENTER);

		JPanel footer = new JPanel(new BorderLayout(10, 10));
		footer.add(new JLabel("Message"), BorderLayout.LINE_START);
		footer.add(textField, BorderLayout.CENTER);
		footer.add(sendMessage, BorderLayout.LINE_END);

		add(footer, BorderLayout.PAGE_END);

	}

	public void updateMessages(List<Message> messages) {
		chatArea.setText("");
		for (Message m : messages) {
			String msg = (m.from.equals(user) ? "You: " : "") +  m.message + "\n";
			Color c = m.from.equals(user) ? new Color(37, 202, 73) : new Color(0, 136, 255);
			StyleContext sc = StyleContext.getDefaultStyleContext();
			AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

			aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
			aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");

			int len = chatArea.getDocument().getLength();
			chatArea.setCaretPosition(len);
			chatArea.setCharacterAttributes(aset, false);
			chatArea.replaceSelection(msg);
		}

	}

	public void updatePartner(String name) {
		this.title.setText("Chat - " + name);
	}

}
