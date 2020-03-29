package Server;

import java.io.IOException;

public class Run {

    public static void main(String[] args) throws IOException {

        Register obj = new Register("Jan","pass");
        obj.writeFile();    // Registrierung

//        Server.Login login = new Server.Login();
//        login.run();        // Logindaten prüfen

    }
}
