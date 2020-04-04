package ChatSystem;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import ChatSystem.Client.Client;
import ChatSystem.DWH.Warehouse;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.User;
import ChatSystem.Server.Server;

public class ChatSystem {

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tT] [%4$-7s] %5$s %n");

		//Warehouse.loadFiles();
		
		Warehouse.getMessages().forEach(System.out::println);
	
		User t = new User("Timo", "Pass");
		User e = new User("Eger", "Pass");
		
		new Message(t.getContact(), e.getContact(), "Hallo Welt");
		new Message(e.getContact(), t.getContact(), "Na süßer");
		
		//Warehouse.saveFiles();

		if (!(Arrays.stream(args).filter(x -> x.equals("onlyClient")).count() > 0)) {
			new Server(7777);
		}

		new Client();
		new Client();
		new Client();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Server.closeAll();
			Client.closeAll();
//			Warehouse.saveFiles();
		}));
	}

}
