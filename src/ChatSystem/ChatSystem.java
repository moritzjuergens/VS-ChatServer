package ChatSystem;

import java.io.UnsupportedEncodingException;

import ChatSystem.Client.Client;
import ChatSystem.Server.Server;

public class ChatSystem {

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tT] [%4$-7s] %5$s %n");

		new Controller();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Server.closeAll();
			Client.closeAll();
		}));
	}

}
