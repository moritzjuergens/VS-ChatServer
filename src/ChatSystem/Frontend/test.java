package ChatSystem.Frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class test extends JFrame {

	public JPanel wrapper = new JPanel();

	public test() {
		super("Test Chat Panel");
		setSize(300, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
		add(new JScrollPane(wrapper));
	}

	public void addMessage(String sender, String message, boolean received) throws UnsupportedEncodingException {
		JPanel container = new JPanel(new BorderLayout());
		JLabel label = new JLabel(sender);
		label.setBackground(null);
		JTextPane pane = new JTextPane();
		pane.setBackground(null);
		pane.setContentType("text/html");
		pane.setText(message);
		
		
		
		JLabel time = new JLabel(new Date(System.currentTimeMillis()).toString(), SwingConstants.RIGHT);
		container.add(label, BorderLayout.PAGE_START);
		container.add(pane, BorderLayout.CENTER);
		container.add(time, BorderLayout.PAGE_END);
		container.setBackground((received ? Color.BLUE : Color.GREEN));
		container.setBorder(BorderFactory.createLineBorder(container.getBackground(), 10, true));

		wrapper.add(container);
		wrapper.updateUI();
	}

}
