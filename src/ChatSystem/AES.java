package ChatSystem;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	// Verfahren festlegen
	private static final String algorithmus = "AES";
	private static SecretKeySpec secretKey;
	private static byte[] key;

	public static void setKey(String myKey) throws Exception {

		MessageDigest sha = null;
		try {
			key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, algorithmus);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Verschlüsseln

	public static String encrypt(String strToEncrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance(algorithmus);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			System.out.println("Fehler bei der Verschlüsselung: " + e.toString());
		}
		return null;
	}

	// Entschlüsseln

	public static String decrypt(String strToDecrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance(algorithmus);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			System.out.println("Fehler bei der Entschlüsselung: " + e.toString());
		}
		return null;
	}
}
