import java.io.FileNotFoundException;
import java.io.IOException;

public class Run {

    public static void main(String[] args) throws IOException {

        Register obj = new Register("Jan","pass");
        obj.WriteFile();    // Registrierung

//        Login login = new Login();
//        login.run();        // Logindaten prüfen

    }
}
