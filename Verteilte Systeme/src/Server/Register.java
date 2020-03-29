package Server;

import java.io.*;
import java.util.*;

public class Register {

    public String user_name;
    public String user_pass;
    public String record = null;
//    Scanner scan = new Scanner(new File("user.txt"));
//    String[] user = scan.nextLine().split(";"); // Trennt String nach ";"
    FileReader fr2 = new FileReader("user.txt");
    BufferedReader br2 = new BufferedReader(fr2);

    public Register (String user_name, String user_pass) throws IOException {
        while ((record = br2.readLine()) != null) {
            String [] user = record.split(";");
            if (Arrays.asList(user[1]).contains(user_name)) {   // überprüft, ob Name in user.txt enthalten ist
                System.out.print("Der User exisitiert bereits.");
                System.exit(0);
            }
        }

        this.user_name = user_name;
        this.user_pass = user_pass;
    }

    public int UserID() {
        int user_id = 1;
        try {
            FileReader fr = new FileReader("user.txt");
            BufferedReader br = new BufferedReader(fr);
            String zeile = null;
            String merke = null;
            while ((zeile = br.readLine()) != null) {
                merke = zeile;      // Letzte Zeile merken
            }
            if (merke == null) {    // Prüfe, ob letzte Zeile/Datei leer ist
                user_id = 1;        // Falls ja: starte bei ID 1
            } else{
                user_id = Integer.valueOf(merke.substring(0,1)) + 1;    // Falls nein: erhöhe ID um 1
            }
        } catch (IOException e1){
            e1.printStackTrace();
        }

        return user_id;
    }


    public void writeFile() {
        try{
            FileWriter fileWrite = new FileWriter("./user.txt", true);  // Erstelle user.txt falls nicht vorhanden
            fileWrite.write(UserID() + ";" + user_name + ";" + user_pass + "\n");   // Neue Zeile ID;Name;Pass einfügen
            fileWrite.flush();
            fileWrite.close();
        }catch (IOException e2) {
            e2.printStackTrace();
        }
    }
    //System.out.println("User " + user_name + " hat einen Account erstellt!");

}