package ChatSystem.Frontend;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

/**
 * ComponentFactory used to build the Userinterfaces
 * 
 * @author timos
 *
 */
public class ComponentFactory {

	public static Color blue = new Color(0, 136, 255);

	/**
	 * Returns a uniform Font
	 * 
	 * @param size    Fontsize
	 * @param variant Fontvariant
	 * @return Font
	 */
	public static Font getFont(int size, int variant) {
		return new Font("Arial", variant, size);
	}

	/**
	 * Returns a prestyled JScrollPane
	 * 
	 * @param view   View to be scrollable
	 * @param x      Position
	 * @param y      Position
	 * @param width
	 * @param height
	 * @return JScrollPane
	 */
	public static JScrollPane getScrollPane(Component view, int x, int y, int width, int height) {
		JScrollPane pane = new JScrollPane(view);
		pane.setBounds(x, y, width, height);
		pane.setOpaque(false);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.getViewport().setOpaque(false);
		pane.setBorder(null);
		return pane;
	}

	/**
	 * Returns a prestyled Contact Button, Displayed in contact list
	 * 
	 * @param name          Contact's name
	 * @param latestMessage last message received/sent
	 * @param unread        boolean
	 * @param open          is the chat currently opened
	 * @param l             action to be performed on click
	 * @return JButton
	 */
	public static JButton getContact(String name, String latestMessage, boolean unread, boolean open,
			ActionListener l) {
		JButton button = getButton(
				"<html><body><b>" + name + "</b><br><font color=\"" + (open ? "white" : (unread ? "#08f" : "silver"))
						+ "\"><code>" + latestMessage + "</code></font></body></html>",
				open);
		if (unread) {
			button.setBorder(BorderFactory.createLineBorder(blue, 1, true));
		}
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.addActionListener(l);
		return button;
	}

	/**
	 * Spacer between Contacts in contact list
	 * 
	 * @return JLabel
	 */
	public static JLabel getContactSpacer() {
		return getLabel(" ");
	}

	/**
	 * Returns a prestyled TextPane
	 * 
	 * @return JTextPane
	 */
	public static JTextPane getTextPane() {
		JTextPane pane = new JTextPane();
		pane.setFont(getFont(14, Font.PLAIN));
		pane.setOpaque(false);
		pane.setForeground(Color.WHITE);
		return pane;
	}

	/**
	 * Returns a prestyled Button
	 * 
	 * @param icon    Icon to be displayed
	 * @param primary boolean (button variant)
	 * @param x       Position
	 * @param y       Position
	 * @param width
	 * @param height
	 * @param l       Action to be performed on click
	 * @return JButton
	 */
	public static JButton getButton(ImageIcon icon, boolean primary, int x, int y, int width, int height,
			ActionListener l) {
		JButton button = getButton("", primary, x, y, width, height, l);
		button.setIcon(icon);
		return button;
	}

	/**
	 * Returns a prestyled Button
	 * 
	 * @param title   Button's title
	 * @param primary boolean (button variant)
	 * @param x       Position
	 * @param y       Position
	 * @param width
	 * @param height
	 * @param l       Action to be performed on click
	 * @return JButton
	 */
	public static JButton getButton(String title, boolean primary, int x, int y, int width, int height,
			ActionListener l) {
		JButton button = getButton(title, primary);
		button.setBounds(x, y, width, height);
		button.addActionListener(l);
		return button;
	}

	/**
	 * Returns a prestyled Button
	 * 
	 * @param title   Button's title
	 * @param primary boolean (button variant)
	 * @return JButton
	 */
	public static JButton getButton(String title, boolean primary) {
		JButton button = new JButton(title);
		button.setBackground(primary ? blue : Color.DARK_GRAY);
		button.setOpaque(primary);
		button.setFocusable(false);
		button.setFont(getFont(14, Font.PLAIN));
		button.setVerticalAlignment(SwingConstants.CENTER);
		button.setHorizontalAlignment(SwingConstants.CENTER);
		button.setForeground(Color.WHITE);
		button.setBorder(BorderFactory.createLineBorder(primary ? blue : Color.GRAY, 1, true));
		return button;
	}

	/**
	 * Returns a prestyled Label
	 * 
	 * @param title  Text to be displayed
	 * @param x      Position
	 * @param y      Position
	 * @param width
	 * @param height
	 * @return JLabel
	 */
	public static JLabel getLabel(String title, int x, int y, int width, int height) {
		JLabel label = getLabel(title);
		label.setBounds(x, y, width, height);
		return label;
	}

	/**
	 * Returns a prestyled JLabel
	 * 
	 * @param title Text to be displayed
	 * @return JLabel
	 */
	public static JLabel getLabel(String title) {
		JLabel label = new JLabel(title);
		label.setOpaque(false);
		label.setForeground(Color.WHITE);
		label.setFont(getFont(14, Font.BOLD));
		return label;
	}

	/**
	 * Returns a prestyled TextField
	 * 
	 * @param x      Position
	 * @param y      Position
	 * @param width
	 * @param height
	 * @return JTextField
	 */
	public static JTextField getTextField(int x, int y, int width, int height) {
		JTextField field = getTextField();
		field.setBounds(x, y, width, height);
		return field;
	}

	/**
	 * Returns a prestyled JTextField
	 * 
	 * @return JTextField
	 */
	public static JTextField getTextField() {
		JPasswordField field = getPasswordField();
		field.setEchoChar((char) 0);
		return (JTextField) field;
	}

	/**
	 * Returns a prestyled PasswordField
	 * 
	 * @param x      Position
	 * @param y      Position
	 * @param width
	 * @param height
	 * @return JPasswordField
	 */
	public static JPasswordField getPasswordField(int x, int y, int width, int height) {
		JPasswordField field = getPasswordField();
		field.setBounds(x, y, width, height);
		return field;
	}

	/**
	 * Returns a prestyled PasswordField
	 * 
	 * @return JPasswordField
	 */
	public static JPasswordField getPasswordField() {
		JPasswordField field = new JPasswordField();
		field.setBackground(null);
		field.setOpaque(false);
		field.setSelectionColor(blue);
		field.setSelectedTextColor(Color.WHITE);
		field.setCaretColor(Color.WHITE);
		field.setForeground(Color.WHITE);
		field.setFont(getFont(14, Font.PLAIN));
		field.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.WHITE));
		return field;
	}

	/**
	 * Returns a prestyled JPanel
	 * 
	 * @param layout Layoutmanager to be used in Panel
	 * @return JPanel
	 */
	public static JPanel getPanel(LayoutManager layout) {
		JPanel panel = new JPanel(layout);
		panel.setOpaque(false);
		return panel;
	}

}
