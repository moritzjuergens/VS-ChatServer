package ChatSystem.Frontend.PopUps;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ChatSystem.Entities.Contact;
import ChatSystem.Entities.Contact.ContactType;
import ChatSystem.Frontend.ChatManager;
import ChatSystem.Frontend.ComponentFactory;

@SuppressWarnings("serial")
public class ContactPopUp extends JFrame {

	JPanel contactWrapper = ComponentFactory.getPanel(null);
	private boolean addToGroup;
	private ChatManager manager;

	public ContactPopUp(ChatManager manager) {
		setSize(200, 300);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		JLabel background = new JLabel(new ImageIcon("./assets/contacts.png"));
		add(background);

		JLabel title = ComponentFactory.getLabel("Contacts", 0, 0, 200, 30);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(16f));
		background.add(title);

		contactWrapper.setLayout(new BoxLayout(this.contactWrapper, BoxLayout.Y_AXIS));
		background.add(ComponentFactory.getScrollPane(contactWrapper, 0, 30, 190, 270));

		background.setLayout(null);
	}

	public void open(boolean addToGroup) {
		removeAll();
		setLocationRelativeTo(null);
		setVisible(true);
		this.addToGroup = addToGroup;
	}

	public void removeAll() {
		contactWrapper.removeAll();
	}

	public void addContacts(List<Contact> contacts) {
		contacts.forEach(this::addContact);
	}

	private void addContact(Contact c) {
		JButton button = ComponentFactory.getContact(" " + c.name, "", false, false, (e) -> {
			manager.addContact(c, addToGroup);
		});
		String fileName = c.type.equals(ContactType.GROUP) ? "group" : "user";
		button.setIcon(new ImageIcon("./assets/" + fileName + ".png"));
		contactWrapper.add(button);
		contactWrapper.add(ComponentFactory.getContactSpacer());
	}

}
