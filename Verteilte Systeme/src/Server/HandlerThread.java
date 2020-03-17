package Server;

import java.net.*;
import java.io.*;


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
                synchronized (Server.loginHash) {
                    if(!Server.loginHash.contains(name) && !name.isBlank()) {
                        Server.loginHash.add(name);
                        out.println("CONFIRMED");
                        System.out.println("User: '" + name + "' has logged in");
                        break;
                    }
                }
            }

            Server.printHash.add(out);

            while (true) {
                String input = in.readLine();
                System.out.println(name + ": " + input);
                if (input.equals("GOODBYE")){
                    System.out.println("'"+name+"' has logged off");
                    for (PrintWriter printer : Server.printHash){
                        printer.println("MESSAGE'"+name+"' has logged off");
                    }
                    socket.close();
                }else {
                    for (PrintWriter printer : Server.printHash) {
                        printer.println("MESSAGE" + name+": "+input);
                    }
                }
        }

        }catch(IOException e){
            System.err.println(e);
        }
    }

}




