package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client1 {

    public static void main(String[] args) {

        try {

            Socket s = new Socket("localhost", 7777);
            BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream(),true);

            ListenTh th1 = new ListenTh(s,out,in,sysIn);
            th1.start();


        }catch(IOException e){
            System.out.println(e);
        }
    }

}
