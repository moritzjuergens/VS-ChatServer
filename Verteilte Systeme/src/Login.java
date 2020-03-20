import java.util.Arrays;
import java.util.Scanner;
import java.io.*;

public class Login {
    public void run() throws IOException {

        Scanner scan = new Scanner(new File("user.txt"));
        Scanner keyboard = new Scanner(System.in);

        String record = null;
        BufferedReader br = new BufferedReader(new FileReader("user.txt"));

//        String[] user = scan.nextLine().split(";"); // Trennt String nach ";"

        System.out.println("User?");
        String inpName = keyboard.nextLine();   // Eingabe Name
        System.out.println("Passwort?");
        String inpPass = keyboard.nextLine();   // Eingabe Passwort

        while ((record = br.readLine()) != null) {
            String[] user = record.split(";");
            if (Arrays.asList(user[1]).contains(inpName) && Arrays.asList(user[2]).contains(inpPass)) {   // überprüft, ob Name & Passwort in user.txt enthalten ist
                System.out.print("Erfolgreich.");
                return;
            }
        }

        System.out.println("Falsche Zugangsdaten.");

    }
}