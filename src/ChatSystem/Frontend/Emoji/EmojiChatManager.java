package ChatSystem.Frontend.Emoji;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class EmojiChatManager {

	public static void changed(JTextPane chatPane) {
		System.err.println("Changed");

		StyledDocument document = chatPane.getStyledDocument();

		try {
			String text = document.getText(0, document.getLength());
			int start = -1;
			for (int i = 0; i < text.length(); i++) {
				if (String.valueOf(text.charAt(i)).equals(":")) {
					if (start == -1) {
						start = i;
					} else {
						String testString = document.getText(start + 1, (i - start - 1));
						Emoji emoji = Emoji.forName(testString);
						if (emoji != null) {

							String replaceString = ":" + emoji + ":";
							chatPane.select(start, i + 1);
							chatPane.replaceSelection("");

							StyleContext sc = StyleContext.getDefaultStyleContext();
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
		System.out.println("Change END");

	}

}
