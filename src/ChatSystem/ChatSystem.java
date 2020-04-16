package ChatSystem;

import java.io.UnsupportedEncodingException;

import ChatSystem.Client.Client;
import ChatSystem.Server.Server;

public class ChatSystem {

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tT] [%4$-7s] %5$s %n");

		AES.init();
		new Controller();
		
		System.out.println(AES.decrypt(AES.encrypt("Hallo was geht")));
		System.out.println(AES.decrypt(AES.encrypt("I love you")));
		System.out.println(AES.decrypt(AES.encrypt("Ja moin")));
		System.out.println(AES.decrypt(AES.encrypt("Nico stinkt!")));
		System.out.println(AES.decrypt(AES.encrypt("xD 1234 pi alpha")));
		

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Server.closeAll();
			Client.closeAll();
		}));
	}
}