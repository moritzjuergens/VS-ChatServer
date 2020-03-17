package Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;

public class Server {

    static final HashSet<String> loginHash = new HashSet<>();
    static HashSet<PrintWriter> printHash = new HashSet<>();

    public static void main(String[] args) throws Exception{

        ServerSocket serverSocket = new ServerSocket(7777);
        System.out.println("Server online");
        var pool = Executors.newFixedThreadPool(10);

        while(true){
            pool.execute(new HandlerThread(serverSocket.accept()));
        }

    }
}
