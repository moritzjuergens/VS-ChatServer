package Server;

import java.net.*;
import java.io.*;
import java.util.Iterator;
import java.util.Map;

/*Dieser Thread wird jedem neuen Client zugewiesen
* und übernimmt alle Funktionen des Routing*/
class HandlerThread implements Runnable {
    private String name;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // Konstruktor
    public HandlerThread(Socket socket) {
        this.socket = socket;
    }

    //Run übernimmt das Routing
    //Hier werden alle Nachrichten eingehen und
    //An die entsprechenden Empfänger geschickt
    public void run() {
        try {
            //Initialisierung der TCP Streams
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Login
            while (true) {
                out.println("LOGIN");
                name = in.readLine();
                if (name.isEmpty()) {
                    return;
                }
                synchronized (Server.connect) {
                    if (!Server.connect.containsKey(name) && !name.isBlank()) {
                        Server.connect.put(name, out);
                        out.println("CONFIRMED");
                        System.out.println("User: '" + name + "' has logged in");
                        connectIterate(" has logged in");
                        break;
                    }
                }
            }

//            Server.connect.get(name).println("MESSAGEFriends online:");
//            connectIterate("");

            //Beginn des Chatting-Vorgangs
            while (true) {
                //Eingang der Nachricht
                String input = in.readLine();
                System.out.println(name + ": " + input);
                //Log-Off Check
                if (input.equals("GOODBYE")) {
                    System.out.println("'" + name + "' has logged off");
                    connectIterate(" has logged off...");
                    socket.close();
                    Server.connect.remove(name);
                    return;
                    //Whisper-Check
                } else if (input.contains("/#") && input.contains("#/")) {
                    String s = (String) input.subSequence(input.indexOf("/#") + 2, input.indexOf("#/"));
                    Iterator i = Server.connect.entrySet().iterator();
                    while (i.hasNext()) {
                        Map.Entry pair = (Map.Entry) i.next();
                        if (s.equals(pair.getKey())) {
                            Server.connect.get(pair.getKey()).println("MESSAGE" + pair.getKey() + " has whispered: " + input);
                        } else {
                            PrintWriter pp = Server.connect.get(pair.getKey());
                            pp.write("MESSAGEUser has not been found...");
                        }
                    }
                    //Group-Chat
                } else {
                    connectIterate(": "+input);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    //Funktion des Routing bei Group-Chats
    private void connectIterate(String msg) {
        Iterator i = Server.connect.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry pair = (Map.Entry) i.next();
            if (!name.equals(pair.getKey())) {
                Server.connect.get(pair.getKey()).println("MESSAGE" + name + msg);
            }
        }
    }
}





