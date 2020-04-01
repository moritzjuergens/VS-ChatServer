package ChatSystem;

import ChatSystem.Client.Client;
import ChatSystem.Entities.ServerMessage;
import ChatSystem.Entities.User;
import ChatSystem.Server.Server;

public class ChatSystem {

	public static void main(String[] args) {

		new User("Timo", "pass");
		new User("Nico", "pass");
		new User("Moritz", "pass");
		
		Server server = new Server(7777);
		Client timo = new Client("Timo");
		Client nico = new Client("Nico");
		
		new Thread( () -> {
			
			try {
				Thread.sleep(1000);
				timo.sendMessage(new ServerMessage("login", "Timo"));
				nico.sendMessage(new ServerMessage("login", "Nico"));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} ).start();
		

		new Thread(() -> {
			try {
				Thread.sleep(12000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				Server.closeAll();
				Client.closeAll();		
				System.exit(0);
			}
		}).start();


		  /**Warehouse.loadFiles();
		  
		  new User("Timo", "Pass");
		  new User("Nicolas", "Passwort1234");
		  new User("Moritz", "Mobama");
		  
		  System.err.println("Users");
		  Warehouse.getUsers().stream().forEach(System.out::println);
		  System.err.println("Messages");
		  Warehouse.getMessages().stream().forEach(System.out::println);
		  System.err.println("Groups");
		  Warehouse.getGroups().stream().forEach(System.out::println);
		  
		  Warehouse.saveFiles();**/
		 
	}

}
