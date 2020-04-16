package ChatSystem.Frontend.PopUps;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import ChatSystem.Frontend.ChatManager;
import ChatSystem.Frontend.ComponentFactory;
import ChatSystem.Frontend.Emoji.Emoji;

@SuppressWarnings("serial")
public class EmojiPopUp extends JFrame {

	/**
	 * creates a new emoji popup
	 * @param manager parent
	 */
	public EmojiPopUp(ChatManager manager) {
		setSize(220, 280);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		JLabel background = new JLabel(new ImageIcon("./assets/emoji.png"));
		add(background);
		background.setLayout(new FlowLayout());

		// Iterate through every emoji available and display it
		Arrays.stream(Emoji.values()).forEach(emoji -> {
			try {
				Image image = ImageIO.read(new File("./emojis/" + emoji.name));
				background.add(ComponentFactory.getButton(new ImageIcon(image.getScaledInstance(32, 32, 0)), false, 0,
						0, 10, 10, (e) -> {
							manager.emojiPicked(emoji);
							setVisible(false);
						}));
			} catch (IOException e1) {
				System.out.println(emoji.name + ": File does not exist!");
			}
		});
	}

	/**
	 * open popup
	 * @param comp Component the popup should be located at
	 */
	public void open(Component comp) {
		setVisible(true);
		setLocationRelativeTo(comp);
	}

}
