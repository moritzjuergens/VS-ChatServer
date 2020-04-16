package ChatSystem;

import java.io.UnsupportedEncodingException;

import ChatSystem.Client.Client;
import ChatSystem.Server.Server;

public class ChatSystem {

	/**
	 * Main Method, opens Controller UI to create Server and Clients
	 * 
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tT] [%4$-7s] %5$s %n");

		AES.init();
		new Controller();

		// Close all open Connections, if the Controller gets closed
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Server.closeAll();
			Client.closeAll();
		}));
	}
}