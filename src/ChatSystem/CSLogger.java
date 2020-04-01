package ChatSystem;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CSLogger {
	public static void log(Class<?> c, String message, Object... values) {
		Logger log = Logger.getLogger(c.getSimpleName());
		log.log(Level.INFO, "[" + c.getSimpleName() + "\t] " + String.format(message, values));		
	}
}
