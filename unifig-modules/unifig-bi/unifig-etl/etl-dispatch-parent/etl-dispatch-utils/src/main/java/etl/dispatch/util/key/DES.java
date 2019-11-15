package etl.dispatch.util.key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**des加密
 * @author DELL
 *
 */
public class DES {

	private static final String PASSWORD_CRYPT_KEY = "baidu16-";

	// 解密数据
	private static String decrypt(String message, String key) throws Exception {

		
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
		byte[] retByte = cipher.doFinal(new BASE64Decoder().decodeBuffer(message));
		return new String(retByte,"UTF-8");
	}
	
	
	/**数据解密
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String message) throws Exception {
		return decrypt( message, PASSWORD_CRYPT_KEY);
	}


	public static String  encrypt(String message, String key) throws Exception {
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
		return new BASE64Encoder().encode(cipher.doFinal(message.getBytes("UTF-8")));
	}
	
	
	public static String  encrypt(String message) throws Exception {
		return encrypt(message,PASSWORD_CRYPT_KEY);
	}

	public static void main(String[] args) throws Exception {
		String str=encrypt("MAITIAN1234qwer",PASSWORD_CRYPT_KEY);
		System.out.println(str);
		System.out.println(decrypt("hu0po/aLmb+lCgrvU/kqbA==",PASSWORD_CRYPT_KEY));
	}
}
