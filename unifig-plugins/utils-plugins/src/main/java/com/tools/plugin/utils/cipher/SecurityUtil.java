package com.tools.plugin.utils.cipher;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class SecurityUtil {
	public static String DES = "AES"; // optional value AES/DES/DESede/Blowfish

	public static String CIPHER_ALGORITHM = "AES"; // optional value AES/DES/DESede/Blowfish

	public static Key getSecretKey(String key) throws Exception {
		SecretKey securekey = null;
		if (key == null) {
			key = "";
		}
		KeyGenerator keyGenerator = KeyGenerator.getInstance(DES);
		keyGenerator.init(new SecureRandom(key.getBytes()));
		securekey = keyGenerator.generateKey();
		return securekey;
	}
    
	/**
	 * 加密
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String data, String key) throws Exception {
		SecureRandom sr = new SecureRandom();
		Key securekey = getSecretKey(key);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		byte[] bt = cipher.doFinal(data.getBytes());
		String strs = new BASE64Encoder().encode(bt);
		return strs;
	}

	/**
	 * 解密
	 * @param message
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String detrypt(String message, String key) throws Exception {
		SecureRandom sr = new SecureRandom();
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		Key securekey = getSecretKey(key);
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		byte[] res = new BASE64Decoder().decodeBuffer(message);
		res = cipher.doFinal(res);
		return new String(res);
	}

	public static void main(String[] args) throws Exception {
		String message = "password";
		String key = "aaaaaaa";
		String entryptedMsg = encrypt(message, key);
		System.out.println("encrypted message is below :");
		System.out.println(entryptedMsg);

		String decryptedMsg = detrypt(entryptedMsg, key);
		System.out.println("decrypted message is below :");
		System.out.println(decryptedMsg);
	}
}