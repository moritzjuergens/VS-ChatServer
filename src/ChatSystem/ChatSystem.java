package ChatSystem;

import java.util.Arrays;

import ChatSystem.Client.Client;
import ChatSystem.DWH.Warehouse;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.User;
import ChatSystem.Server.Server;

public class ChatSystem {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tT] [%4$-7s] %5$s %n");

		Warehouse.loadFiles();

		if (!true) {
			new User("Timo", "Pass");
			new User("Eger", "Jan");
			new User("Nicolas", "Passwort1234");
			new User("Moritz", "Mobama");
			new Message(Warehouse.getUser("Timo").getContact(), Warehouse.getUser("Nicolas").getContact(),
					"Hallo was geht");
			new Message(Warehouse.getUser("Nicolas").getContact(), Warehouse.getUser("Timo").getContact(),
					"Mir gehts supi :)");
		}

		if (!(Arrays.stream(args).filter(x -> x.equals("onlyClient")).count() > 0)) {
			new Server(7777);
		}

		Warehouse.getMessages().stream().forEach(System.out::println);

		new Client();
//		new Client();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Server.closeAll();
			Client.closeAll();
			Warehouse.saveFiles();
		}));

		/*
		 * new User("Timo", "Pass"); new User("Nicolas", "Passwort1234"); new
		 * User("Moritz", "Mobama");
		 * 
		 * System.err.println("Users");
		 * Warehouse.getUsers().stream().forEach(System.out::println);
		 * System.err.println("Messages");
		 * Warehouse.getMessages().stream().forEach(System.out::println);
		 * System.err.println("Groups");
		 * Warehouse.getGroups().stream().forEach(System.out::println);
		 */

	}

}
