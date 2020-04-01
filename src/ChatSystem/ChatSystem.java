package ChatSystem;

import java.util.Arrays;

import ChatSystem.Client.Client;
import ChatSystem.DWH.Warehouse;
import ChatSystem.Frontend.SignIn;
import ChatSystem.Server.Server;

public class ChatSystem {

	public static SignIn frameSignIn;
	private static Client client;

	public static void main(String[] args) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tT] [%4$-7s] %5$s %n");
		Warehouse.loadFiles();
		
		if(!(Arrays.stream(args).filter(x -> x.equals("onlyClient")).count() > 0)) {
			new Server(7777);
		}
		
		client = new Client();

		frameSignIn = new SignIn(client);
		frameSignIn.setVisible(true);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Server.closeAll();
			Client.closeAll();
			Warehouse.saveFiles();
		}));

		/* new User("Timo", "Pass"); new User("Nicolas", "Passwort1234"); new
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
