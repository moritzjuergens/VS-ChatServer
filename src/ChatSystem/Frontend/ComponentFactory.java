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

public class ComponentFactory {

	public static Color blue = new Color(0, 136, 255);

	public static Font getFont(int size, int variant) {
		return new Font("Arial", variant, size);
	}

	public static JScrollPane getScrollPane(Component view, int x, int y, int width, int height) {
		JScrollPane pane = new JScrollPane(view);
		pane.setBounds(x, y, width, height);
		pane.setOpaque(false);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.getViewport().setOpaque(false);
		pane.setBorder(null);
		return pane;
	}

	public static JButton getContact(String name, String latestMessage, boolean unread, boolean open, ActionListener l) {
		JButton button = getButton("<html><body><b>" + name + "</b><br><font color=\""
				+ (open ? "white" : (unread ? "#08f" : "silver")) + "\">" + latestMessage + "</font></body></html>",
				open);
		if (unread) {
			button.setBorder(BorderFactory.createLineBorder(blue, 1, true));
		}
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.addActionListener(l);
		return button;
	}
	
	public static JLabel getContactSpacer() {
		return getLabel(" ");
	}

	public static JTextPane getTextPane() {
		JTextPane pane = new JTextPane();
		pane.setFont(getFont(14, Font.PLAIN));
		pane.setOpaque(false);
		pane.setForeground(Color.WHITE);
		return pane;
	}

	public static JButton getButton(ImageIcon icon, boolean primary, int x, int y, int width, int height,
			ActionListener l) {
		JButton button = getButton("", primary, x, y, width, height, l);
		button.setIcon(icon);
		return button;
	}

	public static JButton getButton(String title, boolean primary, int x, int y, int width, int height,
			ActionListener l) {
		JButton button = getButton(title, primary);
		button.setBounds(x, y, width, height);
		button.addActionListener(l);
		return button;
	}

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

	public static JLabel getLabel(String title, int x, int y, int width, int height) {
		JLabel label = getLabel(title);
		label.setBounds(x, y, width, height);
		return label;
	}

	public static JLabel getLabel(String title) {
		JLabel label = new JLabel(title);
		label.setOpaque(false);
		label.setForeground(Color.WHITE);
		label.setFont(getFont(14, Font.BOLD));
		return label;
	}

	public static JTextField getTextField(int x, int y, int width, int height) {
		JTextField field = getTextField();
		field.setBounds(x, y, width, height);
		return field;
	}

	public static JTextField getTextField() {
		JPasswordField field = getPasswordField();
		field.setEchoChar((char) 0);
		return (JTextField) field;
	}

	public static JPasswordField getPasswordField(int x, int y, int width, int height) {
		JPasswordField field = getPasswordField();
		field.setBounds(x, y, width, height);
		return field;
	}

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

	public static JPanel getPanel(LayoutManager layout) {
		JPanel panel = new JPanel(layout);
		panel.setOpaque(false);
		return panel;
	}

}
