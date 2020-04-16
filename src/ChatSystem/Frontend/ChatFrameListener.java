package ChatSystem.Frontend;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import ChatSystem.Client.Client;
import ChatSystem.Entities.ServerMessage;

/**
 * Used to handle closeevent of ChatFrame
 * 
 * @author timos
 *
 */
public class ChatFrameListener implements WindowListener {

	ChatManager manager = null;
	Client client = null;

	public ChatFrameListener(ChatManager manager) {
		this.manager = manager;
	}

	public ChatFrameListener(Client client) {
		this.client = client;
	}

	/**
	 * close all open connections, close windows, send logoff to server
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		try {
			if (this.manager != null) {
				this.manager.client.sendMessage(new ServerMessage("logoff", this.manager.user));
				this.manager.contactPopUp.setVisible(false);
				this.manager.emojiPopUp.setVisible(false);
				this.manager.client.close();
				Client.registeredClients.remove(this.manager.client);
				this.manager.client.controllerUI.updateClients();
				this.client = this.manager.client;
				this.manager = null;
			}
			if (this.client != null) {
				Client.registeredClients.remove(client);
				client.controllerUI.updateClients();
				client.close();
				client = null;
			}
		} catch (Exception ex) {
			// Only called if window hasnt been initalized on slow mashines
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}
