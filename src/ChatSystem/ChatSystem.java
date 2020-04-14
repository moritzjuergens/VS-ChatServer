package ChatSystem;

import java.io.UnsupportedEncodingException;

import ChatSystem.Client.Client;
import ChatSystem.Server.Server;

public class ChatSystem {

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tT] [%4$-7s] %5$s %n");

		new Controller();

//		if (!(Arrays.stream(args).filter(x -> x.equals("onlyClient")).count() > 0)) {
//			new Server(7777);
//			new Server(7778);
//			new Server(7779);
//		}
//		new Client(7777);
//		new Client(7778);
//		new Client(7779);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Server.closeAll();
			Client.closeAll();
		}));
	}

}
