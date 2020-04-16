package ChatSystem.Frontend.Frames;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ChatSystem.Client.Client;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Frontend.ChatFrameListener;
import ChatSystem.Frontend.ComponentFactory;
import ChatSystem.Packets.SignInUpPacket;

@SuppressWarnings("serial")
public class LoginFrame extends JFrame {

	private Client client = null;
	JLabel feedback = ComponentFactory.getLabel("", 0, 40, 400, 30);

	/**
	 * open a new loginframe
	 * 
	 * @param c Client parent
	 */
	@SuppressWarnings("deprecation")
	public LoginFrame(Client c) {

		this.client = c;

		addWindowListener(new ChatFrameListener(client));
		setSize(400, 260);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		JLabel background = new JLabel(new ImageIcon("./assets/login.png"));
		add(background);
		background.setLayout(null);

		feedback.setVerticalAlignment(SwingConstants.CENTER);
		feedback.setHorizontalAlignment(SwingConstants.CENTER);
		feedback.setForeground(Color.RED);
		background.add(feedback);

		JLabel title = ComponentFactory.getLabel("VS Chatsystem");
		title.setVerticalAlignment(SwingConstants.CENTER);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(16.5f));
		title.setBounds(0, 10, 400, 30);

		background.add(ComponentFactory.getLabel("Username", 20, 70, 160, 20));
		background.add(ComponentFactory.getLabel("Password", 210, 70, 160, 20));

		JTextField un = ComponentFactory.getTextField(20, 100, 160, 25);
		JPasswordField pw = ComponentFactory.getPasswordField(210, 100, 160, 25);

		un.setText("Timo");
		pw.setText("Pass");

		background.add(ComponentFactory.getButton("Login", true, 20, 150, 160, 30, (e) -> {
			this.loginRegister(un.getText(), pw.getText(), true);
		}));
		background.add(ComponentFactory.getButton("Register", false, 210, 150, 160, 30, (e) -> {
			this.loginRegister(un.getText(), pw.getText(), false);
		}));

		background.add(title);
		background.add(pw);
		background.add(un);
		setVisible(true);
	}

	/**
	 * send server login/register request
	 * 
	 * @param name     username
	 * @param password password
	 * @param isLogin  boolean
	 */
	private void loginRegister(String name, String password, boolean isLogin) {
		if (name.length() == 0 || password.length() == 0)
			return;
		client.sendMessage(
				new ServerMessage("sign" + (isLogin ? "in" : "up"), new SignInUpPacket(name, password, false, 0)));
	}

	/**
	 * Registration or Login hasn't been accepted
	 * 
	 * @param message Server response
	 */
	public void signResponseReceived(String message) {
		feedback.setText(message);
	}
}
