package ChatSystem.Frontend;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ChatSystem.Emoji;

@SuppressWarnings("serial")
public class EmojiPopUp extends JFrame {

	JPanel wrapper = new JPanel();

	public EmojiPopUp() {
		super("Select your Emoji");
		setSize(300, 200);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		loadEmojis();
		wrapper.setLayout(new GridLayout(0, 5, 5, 5));

		add(new JScrollPane(wrapper));
		setVisible(true);
	}

	private void loadEmojis() {

		for (Emoji e : Emoji.values()) {
			JButton b = new JButton(new ImageIcon("./emojis/" + e.name));
			b.setBackground(new Color(255, 255, 255));
			wrapper.add(b);
		}
	}

}
