package Server;

import java.util.Arrays;
import java.util.Scanner;
import java.io.*;

public class Login {
    public String run(PrintWriter out, BufferedReader in) throws IOException {

        FileWriter fileWrite = new FileWriter("./user.txt", true);  // Erstelle user.txt falls nicht vorhanden
        fileWrite.write("UserID" + ";" + "user_name" + ";" + "user_pass" + "\n");
        Scanner scan = new Scanner(new File("user.txt"));

        String record = null;
        BufferedReader br = new BufferedReader(new FileReader("user.txt"));

//        String[] user = scan.nextLine().split(";"); // Trennt String nach ";"

        out.println("LOGINUser?");
        String inpName = in.readLine();   // Eingabe Name
        out.println("LOGINPasswort?");
        String inpPass = in.readLine();   // Eingabe Passwort

        while ((record = br.readLine()) != null) {
            String[] user = record.split(";");
            if (Arrays.asList(user[1]).contains(inpName) && Arrays.asList(user[2]).contains(inpPass)) {   // überprüft, ob Name & Passwort in user.txt enthalten ist
                out.println("CONFIRMED");
                return inpName;
            }
        }

        out.println("LOGINFalsche Zugangsdaten.");
        return null;
    }
}