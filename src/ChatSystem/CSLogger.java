package ChatSystem;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author timos
 *
 */
public class CSLogger {
	
	/**
	 * Prints a prestyled message to the console
	 * @param c Class the method got called
	 * @param message Message to print in Console
	 * @param values Variables included in 'message'
	 */
	public static void log(Class<?> c, String message, Object... values) {
		Logger log = Logger.getLogger(c.getSimpleName());
		log.log(Level.INFO, "[" + c.getSimpleName() + "\t] " + String.format(message, values));		
	}
}
