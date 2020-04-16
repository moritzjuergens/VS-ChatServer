package ChatSystem.Frontend.Emoji;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 * Manager to display emojis in chat
 * 
 * @author timos
 *
 */
public class EmojiChatManager {

	/**
	 * gets called whenever a JTextPane has changed
	 * 
	 * @param chatPane target pane
	 */
	public static void changed(JTextPane chatPane) {

		StyledDocument document = chatPane.getStyledDocument();
		try {

			String text = document.getText(0, document.getLength());
			int start = -1;

			/*
			 * Iterate throught the whole text. How it works: Example String: Hello :love:
			 * World!
			 * 
			 * 1. Search for the first colon in a text 2. Store position of colon 3. Find
			 * next colon 4. Read text between colons. 5. Check if Emoji with read text
			 * exists 6. If true -> replace text false -> go back to #2
			 * 
			 */
			for (int i = 0; i < text.length(); i++) {
				if (String.valueOf(text.charAt(i)).equals(":")) {

					// check if first colon
					if (start == -1) {
						start = i;
					} else {

						// read text between colons
						String testString = document.getText(start + 1, (i - start - 1));

						// check if emoji exsits
						Emoji emoji = Emoji.forName(testString);

						if (emoji != null) {

							// emoji exits replace text with icon.
							String replaceString = ":" + emoji + ":";
							chatPane.select(start, i + 1);
							chatPane.replaceSelection("");

							StyleContext sc = StyleContext.getDefaultStyleContext();

							// create style, load icon, create label & insert
							Style labelStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
							Icon icon = new ImageIcon("./emojis/" + emoji.name);
							JLabel label = new JLabel(icon);
							StyleConstants.setComponent(labelStyle, label);
							document.insertString(start, replaceString, labelStyle);
						} else {
							start = i;
						}
					}
				}
			}

		} catch (Exception e) {
			System.out.println("Something went wrong");
			e.printStackTrace();
		}
	}

}
