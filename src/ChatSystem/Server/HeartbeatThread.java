package ChatSystem.Server;

import ChatSystem.Entities.ServerMessage;
import ChatSystem.Packets.SendMessagePacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


public class HeartbeatThread implements Runnable {
    Socket serverClient;
    ObjectOutputStream out;
    Server server;

    HeartbeatThread(Server server){
        this.server = server;
        run();
    }

    @Override
    public void run() {
        //Start HeartbeatHandler
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Server s : Server.registeredServer) {
                try {
                    if(s!=server) {
                        serverClient = new Socket("localhost", s.getPort());
                        out = new ObjectOutputStream(serverClient.getOutputStream());
                        out.writeObject(new ServerMessage("heartbeat", server.getPort() + "; " + System.currentTimeMillis()));
                        serverClient.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

