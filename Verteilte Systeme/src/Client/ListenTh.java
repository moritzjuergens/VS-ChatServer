package Client;

import java.io.*;
import java.net.*;

public class ListenTh extends Thread{
    Socket s;
    PrintWriter out;
    BufferedReader in;
    BufferedReader sysIn;
    String name;

    ListenTh(Socket s, PrintWriter out, BufferedReader in, BufferedReader sysIn){
        this.s = s;
        this.out = out;
        this.in = in;
        this.sysIn = sysIn;
    }

    @Override
    public void run() {
        try{
            while(true) {
                var line = in.readLine();

                if (line.startsWith("LOGIN")) {
                    System.out.println("Enter Login Name: ");
                    name = sysIn.readLine();
                    out.println(name);
                } else if (line.startsWith("CONFIRMED")) {
                    System.out.println("Login successful");
                    SendTh sendThread = new SendTh(s,out,in,sysIn);
                    sendThread.start();
                } else if (line.startsWith("MESSAGE")) {
                    System.out.println(line.substring(7));
                }
            }

        }catch(IOException e){
                System.err.println(e);}

    }
}
class SendTh extends Thread{
    Socket s;
    PrintWriter out;
    BufferedReader in;
    BufferedReader sysIn;
    SendTh(Socket s, PrintWriter out, BufferedReader in, BufferedReader sysIn){
        this.s = s;
        this.out = out;
        this.in = in;
        this.sysIn = sysIn;
    }

    @Override
    public void run() {
        try {
            while(true) {
                String msg = sysIn.readLine();
                if (msg.equals("GOODBYE")) {
                    out.println(msg);
                    System.out.println("See you soon!");
                    System.err.println("Logged off...");
                    s.close();
                    break;
                }else{
                    out.println(msg);
                }
            }
        }catch (IOException e){
            System.err.println(e);
        }
    }
}