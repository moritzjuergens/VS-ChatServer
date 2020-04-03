package ChatSystem.Frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ChatSystem.Client.Client;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Entities.SignInUp;

@SuppressWarnings("serial")
public class SignIn extends JFrame {

	private JLabel labelInfo;
	private JTextField fieldUsername;
	private JTextField fieldPassword;
	private Client client;

	public SignIn(Client c) {
		super("VS Chatsystem - Login");

		this.client = c;
		setSize(300, 200);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		labelInfo = new JLabel("", SwingConstants.CENTER);
		fieldUsername = new JTextField("Timo");
		fieldPassword = new JTextField("Pass");

		JButton buttonSignUp = new JButton("Sign up");
		JButton buttonSignIn = new JButton("Sign in");
		JLabel labelUsername = new JLabel("Enter Username");
		JLabel labelPassword = new JLabel("Enter Password");

		setLayout(new BorderLayout(20, 20));

		JPanel panelUsername = new JPanel(new BorderLayout(20, 10));
		panelUsername.add(labelUsername, BorderLayout.PAGE_START);
		panelUsername.add(fieldUsername, BorderLayout.CENTER);

		JPanel panelPassword = new JPanel(new BorderLayout(20, 10));
		panelPassword.add(labelPassword, BorderLayout.PAGE_START);
		panelPassword.add(fieldPassword, BorderLayout.CENTER);

		JPanel panelButtons = new JPanel(new FlowLayout());
		panelButtons.add(buttonSignIn);
		panelButtons.add(buttonSignUp);

		JPanel panelInputs = new JPanel(new FlowLayout());
		panelInputs.add(panelUsername);
		panelInputs.add(panelPassword);

		add(panelInputs, BorderLayout.PAGE_START);
		add(labelInfo, BorderLayout.CENTER);
		add(panelButtons, BorderLayout.PAGE_END);

		buttonSignIn.addActionListener((e) -> {
			signInUp(true);
		});

		buttonSignUp.addActionListener((e) -> {
			signInUp(false);
		});
		
		styleLabel(labelPassword);
		styleLabel(labelUsername);
		styleField(fieldPassword);
		styleField(fieldUsername);
		styleButton(buttonSignIn, true);
		styleButton(buttonSignUp, false);
		
		setVisible(true);
	}

	private void signInUp(boolean signIn) {
		String name = fieldUsername.getText();
		String password = fieldPassword.getText();
		if (name.length() == 0 || password.length() == 0)
			return;
		client.sendMessage(new ServerMessage("sign" + (signIn ? "in" : "up"), new SignInUp(name, password)));
	}

	public void wrongCredentials() {
		labelInfo.setText("Username und Passwort stimmen nicht überein");
		labelInfo.setForeground(Color.RED);
	}

	public void usernameTaken() {
		labelInfo.setText("Username ist bereits vergeben");
		labelInfo.setForeground(new Color(39, 174, 96));
	}
	
	public JLabel styleLabel(JLabel label) {
		label.setBackground(null);
		label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		label.setFont(new Font("Arial", Font.BOLD, 13));
		return label;
	}
	public JTextField styleField(JTextField field) {
		field.setBackground(Color.WHITE);
		field.setMargin(new Insets(5, 5, 5, 5));
		field.setBorder(BorderFactory.createLineBorder(Color.WHITE, 6, true));
		field.setFont(new Font("Arial", Font.PLAIN, 12));
		return field;
	}
	public JButton styleButton(JButton button, boolean isPrimary) {
		button.setBackground(isPrimary ? new Color(0, 136, 255) : new Color(240, 240, 240));
		button.setForeground(isPrimary ? new Color(255, 255, 255) : Color.BLACK);
		return button;
	}

}
