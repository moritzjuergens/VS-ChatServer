package ChatSystem.Frontend;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import ChatSystem.Entities.ServerMessage;

public class ChatFrameListener implements WindowListener {

	ChatManager manager;

	public ChatFrameListener(ChatManager manager) {
		this.manager = manager;
	}

	@Override
	public void windowClosing(WindowEvent e) {
		this.manager.client.sendMessage(new ServerMessage("logoff", this.manager.user));
		try {			
			this.manager.contactPopUp.setVisible(false);
			this.manager.emojiPopUp.setVisible(false);
		}
		catch(Exception ex) {
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
