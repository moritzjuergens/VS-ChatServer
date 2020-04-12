package ChatSystem.Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import ChatSystem.Entities.ServerMessage;

public class HeartbeatThread implements Runnable {
	Socket serverClient;
	ObjectOutputStream out;
	Server server;
	int[] portRange = {
		7777,7778,7779,7780
	};

	HeartbeatThread(Server server) {
		this.server = server;
		run();
	}

	@Override
	public void run() {
		// Start HeartbeatHandler
		registerServer(portRange);
		while (true) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (int s : Server.registeredPorts) {
				try {
					if (s != server.getPort()) {
						serverClient = new Socket("localhost", s);
						out = new ObjectOutputStream(serverClient.getOutputStream());
						out.writeObject(
								new ServerMessage("heartbeat", server.getPort() + "; " + System.currentTimeMillis()));
						serverClient.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	void registerServer(int[] portRange){
		for(int p : portRange){
			if(p!=server.getPort()){
				try {
					serverClient = new Socket("localhost", p);
					out = new ObjectOutputStream(serverClient.getOutputStream());
					out.writeObject(
							new ServerMessage("serverlogin", server.getPort()));
					serverClient.close();
				}catch (IOException e){
					System.err.println(e);
				}
			}
		}
	}

}
