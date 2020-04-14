package ChatSystem;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ChatSystem.Client.Client;
import ChatSystem.Server.Server;

@SuppressWarnings("serial")
public class Controller extends JFrame {

	public JPanel clientContainer = new JPanel(new GridLayout(0, 3, 10, 10));
	public JPanel serverContainer = new JPanel(new GridLayout(0, 4, 10, 10));
	public JTextField portField = new JTextField("7777", 6);
	public JLabel infoLabel = new JLabel("", SwingConstants.CENTER);
	
	public Controller() {
		super("VS Chatsystem - Controller");
		setSize(500, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setLayout(new GridLayout(0, 1, 20, 20));
		
		JPanel head = new JPanel();
		head.add(new JLabel("   VS ChatSystem Controller", SwingConstants.CENTER));
		head.add(infoLabel);
		head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
		add(head);

		JPanel clientPanel = new JPanel(new BorderLayout());
		JPanel clientHeader = new JPanel(new BorderLayout());
		clientHeader.add(new JLabel("Active Clients", SwingConstants.CENTER), BorderLayout.CENTER);
		JButton newClient = new JButton("Start Client");
		newClient.addActionListener((l) -> {
			infoLabel.setText("Client started!");
			new Client(this);
			updateClients();
		});
		clientHeader.add(newClient, BorderLayout.LINE_END);
		clientPanel.add(clientHeader, BorderLayout.PAGE_START);
		clientPanel.add(clientContainer, BorderLayout.CENTER);
		add(clientPanel);

		JPanel serverPanel = new JPanel(new BorderLayout());
		JPanel serverHeader = new JPanel(new BorderLayout());
		serverHeader.add(new JLabel("Active Server", SwingConstants.CENTER), BorderLayout.LINE_START);
		JPanel serverPortPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		serverPortPanel.add(new JLabel("Ports: " + Server.portRange[0] + "-" + Server.portRange[Server.portRange.length-1]));
		serverPortPanel.add(portField);
		serverHeader.add(serverPortPanel, BorderLayout.CENTER);
		JButton newServer = new JButton("Start Server");
		newServer.addActionListener((l) -> {
			createServer();
			updateServer();
		});
		serverHeader.add(newServer, BorderLayout.LINE_END);
		serverPanel.add(serverHeader,BorderLayout.PAGE_START);
		serverPanel.add(serverContainer, BorderLayout.CENTER);
		add(serverPanel);
		
		pack();
		
		setVisible(true);
	}
	
	public void createServer() {
		int port;
		try {
			port = Integer.parseInt(portField.getText());
		} catch (Exception e) {
			infoLabel.setText("Port muss eine Zahl sein!");
			return;
		}
		if(Arrays.stream(Server.portRange).filter(x -> x == port).count() != 1) {
			infoLabel.setText("Port ungültig!");
			return;
		}
		if(Server.registeredServer.stream().filter(x -> x.port == port).count() != 0) {
			infoLabel.setText("Port wird bereits verwendet!");
			return;
		}
		new Server(this, port);
		infoLabel.setText("Server started!");
	}

	public void updateClients() {
		clientContainer.removeAll();
		int clientCount = 0;
		for(Client c : Client.registeredClients) {
			System.out.println("adding client");
			clientContainer.add(new JLabel("    Client #" + ++clientCount));
			if(c.chat == null || c.chat.user == null ) {
				clientContainer.add(new JLabel("not signed in"));
			}
			else {				
				clientContainer.add(new JLabel("User: " + c.chat.user.name));
			}
			clientContainer.add(new JLabel("Connected to S:" + c.port + "    "));
		}
		clientContainer.repaint();
		pack();
		repaint();
	}
	public void updateServer() {
		serverContainer.removeAll();
		int serverCount = 0;
		for(Server s : Server.registeredServer) {
			serverContainer.add(new JLabel("    Server #" + ++serverCount));
			serverContainer.add(new JLabel("Client count: " + s.clientCount()));
			serverContainer.add(new JLabel("Using port:" + s.port + "    "));
			
			JButton button = new JButton("Shutdown");
			button.addActionListener((l) -> {
				s.shutdown();
			});
			serverContainer.add(button);
		}
		serverContainer.repaint();
		pack();
		repaint();
	}
}
