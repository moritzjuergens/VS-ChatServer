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
	public static final String key_string = "ADGE154ADR87DSW6";

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

	public static Object encrypt(Object ObjectToEncrypt) {
		return ObjectToEncrypt;
//		try {
//			setKey(key_string);
//			Cipher cipher = Cipher.getInstance(algorithmus);
//			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//
//			String strToEncrypt = ObjectToEncrypt.toString();
//			Object ergebnis_encrypt = Base64.getEncoder()
//					.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
//			return ergebnis_encrypt;
//		} catch (Exception e) {
//			System.out.println("Fehler bei der Verschlüsselung: " + e.toString());
//		}
//		return null;
	}

	// Entschlüsseln

	public static Object decrypt(Object ObjectToDecrypt) {
		return ObjectToDecrypt;
//		try {
//			setKey(key_string);
//			Cipher cipher = Cipher.getInstance(algorithmus);
//			cipher.init(Cipher.DECRYPT_MODE, secretKey);
//
//			String strToDecrypt = ObjectToDecrypt.toString();
//			Object ergebnis_decrypt = new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
//			return ergebnis_decrypt;
//
//		} catch (Exception e) {
//			System.out.println("Fehler bei der Entschlüsselung: " + e.toString());
//		}
//		return null;
	}
}
