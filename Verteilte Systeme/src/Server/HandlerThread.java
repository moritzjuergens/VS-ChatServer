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
                        globalChat(" has logged in");
                        break;
                    }
                }
            }

            //Beginn des Chatting-Vorgangs
            while (true) {
                //Eingang der Nachricht
                String input = in.readLine();
                System.out.println(name + ": " + input);
                //Log-Off Check
                if (input.equals("GOODBYE")) {
                    logOff();
                    return;
                    //Whisper-Check
                } else if (input.startsWith("/whisper")) {
                    String recipient = input.substring(9, input.indexOf(" ", 9));
                    String message = input.substring(input.indexOf(" ", 9) + 1);
                    Iterator i = Server.connect.entrySet().iterator();
                    int count = 0;
                    while (i.hasNext()) {
                        Map.Entry pair = (Map.Entry) i.next();
                        if (recipient.equals(pair.getKey())) {
                            Server.connect.get(pair.getKey()).println("MESSAGE" + name + " has whispered: " + message);
                            count++;
                        }
                    }
                    if (count == 0) {
                        PrintWriter pp = Server.connect.get(name);
                        pp.println("MESSAGEUser has not been found...");
                    }

                } else if (input.contains("/chat")) {
                    String recipient = input.substring(6);
                    chatInvite(input, recipient);
                    chatFunction(input,recipient);
                } else if (input.startsWith("CHAT")) {
                    String recipient = input.substring(4);
                    chatFunction(input, recipient);
                } else {
                    globalChat(": " + input);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    //Funktion des Routing bei Group-Chats
    private void globalChat(String msg) {
        Iterator i = Server.connect.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry pair = (Map.Entry) i.next();
            if (!name.equals(pair.getKey())) {
                Server.connect.get(pair.getKey()).println("MESSAGE" + name + msg);
            }
        }
    }

    private boolean checkRecipients(String recipients) {
        Iterator i = Server.connect.entrySet().iterator();
        int count = 0;
        while (i.hasNext()) {
            Map.Entry pair = (Map.Entry) i.next();
            if (recipients.equals(pair.getKey())) {
                count++;
            }
        }
        if (count == 0) {
            Server.connect.get(name).println("MESSAGEUser has not been found...");
            return false;
        } else {
            return true;
        }
    }

    private void logOff() {
        try {
            System.out.println("'" + name + "' has logged off");
            globalChat(" has logged off...");
            socket.close();
            Server.connect.remove(name);
        } catch (IOException ignore) {
        }
    }

    private void chatInvite(String input, String recipient) {
        if (checkRecipients(recipient)) {
            Iterator i = Server.connect.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry pair = (Map.Entry) i.next();
                if (recipient.equals(pair.getKey())) {
                    Server.connect.get(pair.getKey()).println("CHAT" + name);
                }
            }
        } else {
            Server.connect.get(name).println("MESSAGEGlobal chat activated");
        }
    }

    private void chatFunction(String input, String recipient) {
        try {
            boolean chatActive = true;
            Server.connect.get(name).println("MESSAGEYou are now chatting with " + recipient);
            while (chatActive) {
                input = in.readLine();
                Iterator i = Server.connect.entrySet().iterator();
                if(input.startsWith("/exit")){
                    chatActive  = false;
                    Server.connect.get(name).println("MESSAGEYou have left the chat");
                    while (i.hasNext()) {
                        Map.Entry pair = (Map.Entry) i.next();
                        if (recipient.equals(pair.getKey())) {
                            Server.connect.get(pair.getKey()).println("MESSAGE" + name + " has left the chat");
                        }
                    }
                    break;
                }else {
                    while (i.hasNext()) {
                        Map.Entry pair = (Map.Entry) i.next();
                        if (recipient.equals(pair.getKey())) {
                            Server.connect.get(pair.getKey()).println("MESSAGE" + name + ": " + input);
                        }
                    }
                }
            }
        }catch (IOException ignore){}
    }
}





