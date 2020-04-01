package ChatSystem.Frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ChatSystem.Client.Client;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Entities.SignInUp;

public class SignIn extends JFrame {

	private JLabel labelInfo;
	private JTextField fieldUsername;
	private JTextField fieldPassword;
	private Client client;

	public SignIn(Client c) {
		super("Client Login");

		this.client = c;
		this.setSize(300, 200);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);

		labelInfo = new JLabel("", SwingConstants.CENTER);
		fieldUsername = new JTextField();
		fieldPassword = new JTextField();

		JButton buttonSignUp = new JButton("Sign up");
		JButton buttonSignIn = new JButton("Sign in");
		JLabel labelUsername = new JLabel("Enter Username");
		JLabel labelPassword = new JLabel("Enter Password");

		this.setLayout(new BorderLayout());

		JPanel panelUsername = new JPanel(new BorderLayout());
		panelUsername.add(labelUsername, BorderLayout.PAGE_START);
		panelUsername.add(fieldUsername, BorderLayout.CENTER);

		JPanel panelPassword = new JPanel(new BorderLayout());
		panelPassword.add(labelPassword, BorderLayout.PAGE_START);
		panelPassword.add(fieldPassword, BorderLayout.CENTER);

		JPanel panelButtons = new JPanel(new FlowLayout());
		panelButtons.add(buttonSignIn);
		panelButtons.add(buttonSignUp);

		JPanel panelInputs = new JPanel(new FlowLayout());
		panelInputs.add(panelUsername);
		panelInputs.add(panelPassword);

		this.add(panelInputs, BorderLayout.PAGE_START);
		this.add(labelInfo, BorderLayout.CENTER);
		this.add(panelButtons, BorderLayout.PAGE_END);

		buttonSignIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				signInUp(true);
			}
		});

		buttonSignUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				signInUp(false);
			}
		});
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

}
