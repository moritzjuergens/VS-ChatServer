package ChatSystem;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ChatSystem.Client.Client;
import ChatSystem.Server.Server;

@SuppressWarnings("serial")
public class Controller extends JFrame {

	public JPanel clientContainer = new JPanel(new GridLayout(3, 0, 10, 10));
	public JPanel serverContainer = new JPanel();
	
	public Controller() {
		super("VS Chatsystem - Controller");
		setSize(300, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setLayout(new BorderLayout(20, 20));

		add(new JLabel("VS ChatSystem Controller"), BorderLayout.PAGE_START);

		JPanel clientPanel = new JPanel(new BorderLayout());
		JPanel clientHeader = new JPanel(new BorderLayout());
		clientHeader.add(new JLabel("Active Clients"), BorderLayout.CENTER);
		JButton newClient = new JButton("Start Client");
		newClient.addActionListener((l) -> {
			new Client(7777);
			updateClients();
		});
		clientHeader.add(newClient, BorderLayout.LINE_END);
		clientPanel.add(BorderLayout.PAGE_START, clientHeader);
		clientPanel.add(BorderLayout.CENTER, clientContainer);
		add(clientPanel, BorderLayout.CENTER);

		JPanel serverPanel = new JPanel(new BorderLayout());
		JPanel serverHeader = new JPanel(new BorderLayout());
		serverHeader.add(new JLabel("Active Server"), BorderLayout.CENTER);
		JButton newServer = new JButton("Start Server");
		newServer.addActionListener((l) -> {
			new Server(7777);
			updateServer();
		});
		serverHeader.add(newServer, BorderLayout.LINE_END);
		serverPanel.add(BorderLayout.PAGE_START, serverHeader);
		serverPanel.add(BorderLayout.CENTER, serverContainer);
		add(serverPanel, BorderLayout.PAGE_END);
		
		setVisible(true);
	}

	public void updateClients() {
		clientContainer.removeAll();
		int clientCount = 0;
		for(Client c : Client.registeredClients) {
			clientContainer.add(new JLabel("Client #" + ++clientCount));
			clientContainer.add(new JLabel("User: " + c.chat.user.name));
			clientContainer.add(new JLabel("Connected to S:" + c.port));
		}
	}
	public void updateServer() {
		
	}
}
