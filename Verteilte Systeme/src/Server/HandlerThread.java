package Server;

import java.net.*;
import java.io.*;
import java.util.Iterator;
import java.util.Map;


class HandlerThread implements Runnable{
    private String name;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public HandlerThread(Socket socket) {
        this.socket = socket;
    }

    public void run(){
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while(true){
                out.println("LOGIN");
                name = in.readLine();
                if(name.isEmpty()){
                    return;
                }
                synchronized (Server.connect) {
                    if(!Server.connect.containsKey(name) && !name.isBlank()) {
                        Server.connect.put(name,out);
                        out.println("CONFIRMED");
                        System.out.println("User: '" + name + "' has logged in");
                        break;
                    }
                }
            }

            Server.connect.get(name).println("MESSAGEFriends online:");
            connectIterate("");

            while (true) {
                String input = in.readLine();
                System.out.println(name + ": " + input);

                if (input.equals("GOODBYE")) {
                    System.out.println("'" + name + "' has logged off");
                    connectIterate("has logged off...");
                    socket.close();
                }else if(input.contains("/#")&&input.contains("#/")){
                    String s = (String)input.subSequence(input.indexOf("/#")+2,input.indexOf("#/"));
                    Iterator i = Server.connect.entrySet().iterator();
                    while (i.hasNext()) {
                        Map.Entry pair = (Map.Entry)i.next();
                        if(s.equals(pair.getKey())){
                            Server.connect.get(pair.getKey()).println("MESSAGE"+pair.getKey() +" has whispered: " + input);
                        }else{
                            PrintWriter pp = Server.connect.get(pair.getKey());
                            pp.write("MESSAGEUser has not been found...");
                        }
                    }
                }else {
                   connectIterate(input);
                }
            }
        }catch(IOException e){
            System.err.println(e);
        }
    }


    private void connectIterate(String msg){
        Iterator i = Server.connect.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry pair = (Map.Entry) i.next();
            Server.connect.get(pair.getKey()).println("MESSAGE" + pair.getKey() +": "+ msg);
        }
    }

}




