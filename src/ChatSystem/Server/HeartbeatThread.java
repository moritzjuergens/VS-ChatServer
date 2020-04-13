package ChatSystem.Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ChatSystem.Entities.ServerMessage;

public class HeartbeatThread implements Runnable {
	Socket serverClient;
	ObjectOutputStream out;
	Server server;
    List<Integer> registeredPorts;


	HeartbeatThread(Server server, List registeredPorts) {
		this.server = server;
		this.registeredPorts = registeredPorts;
		run();
	}

	@Override
	public void run() {
		// Start HeartbeatHandler

		while (true) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (int s : registeredPorts) {
				try {
					if (s != server.getPort()) {
						serverClient = new Socket("localhost", s);
						out = new ObjectOutputStream(serverClient.getOutputStream());
						out.writeObject(
								new ServerMessage("heartbeat", server.getPort()));
//						+"-->"+ s + "; " + System.currentTimeMillis())
						serverClient.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
