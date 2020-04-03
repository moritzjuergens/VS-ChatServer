package ChatSystem;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import ChatSystem.Client.Client;
import ChatSystem.DWH.Warehouse;
import ChatSystem.Entities.Message;
import ChatSystem.Entities.User;
import ChatSystem.Frontend.EmojiPopUp;
import ChatSystem.Server.Server;

public class ChatSystem {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws UnsupportedEncodingException {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tT] [%4$-7s] %5$s %n");
		
		new EmojiPopUp();
		
		//AEEvent e = new AEEvent();
		///e.initUI();
		
//		char c = '\uDC66';
//		
//		System.out.println(c);
//		
//		test t = new test();
//		t.setVisible(true);
//
//		t.addMessage("s", "A baby\r\n" + 
//				"👦 string with \uD83C\uDDEF\uD83C\uDDF2 Jamaica a 0x1F334 " + ((char) '\uDC66')  +  " \uD83C \uDFFFfew emojis!", false);
//
//		t.addMessage("Timo", "Was geht ab bei euch?", false);
//		t.addMessage("Nico", "Nix, muss mich auf morgen vorbereiten", true);
//		t.addMessage("Eger", "Ich rauch eine.", true);
//		t.addMessage("Timo", "ok", false);

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

//		 Warehouse.getMessages().stream().forEach(System.out::println);

//		new Client();
		new Client();

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
