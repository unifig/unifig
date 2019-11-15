package etl.dispatch.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES加密和解密。
 */
public class DESUtil {

	/** 安全密钥 */
	private String keyData = "ABCDEFGHIJKLMNOPQRSTWXYZabcdefghijklmnopqrstwxyz0123456789-_.!@#$^&*()_+";

	private boolean isOpen = true;
	/**
	 * 功能：构造
	 * 
	 */
	public DESUtil() {
	}

	/**
	 * 功能：构造
	 * 
	 * @param keyData
	 *            key
	 */
	public DESUtil(String key) {
		this.isOpen =false;
		if (!StringUtil.isNullOrEmpty(key)) {
			this.keyData = key;
		}
	}
	
	/**
	 * 功能：构造
	 * 
	 * @param keyData
	 *            key
	 */
	public DESUtil(String key, boolean isOpen) {
		this.isOpen = isOpen;
		if (!StringUtil.isNullOrEmpty(key)) {
			this.keyData = key;
		}
	}

	/**
	 * 功能：加密 (UTF-8),需要URL转码，防止特殊字符
	 * @param source 源字符串
	 * @param charSet 编码
	 * @return String
	 * @throws UnsupportedEncodingException 编码异常
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public String encrypt(String source) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		if (isOpen) {
			String encrypt = encrypt(source, "UTF-8");
			return URLEncoder.encode(Base64.encode(encrypt), "UTF-8");
		} else {
			return source;
		}
	}

	/**
	 * 
	 * 功能：解密 (UTF-8)需要URL转码，防止特殊字符
	 * @param encryptedData 被加密后的字符串
	 * @return String
	 * @throws UnsupportedEncodingException 编码异常
	 * @throws NoSuchAlgorithmException
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeySpecException 
	 * @throws InvalidKeyException 
	 */
	public String decrypt(String encryptedData) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		if (isOpen) {
			encryptedData = URLDecoder.decode(encryptedData, "UTF-8");
			String decrypt = decrypt(Base64.decode(encryptedData), "UTF-8");
			return decrypt;
		} else {
			return encryptedData;
		}
	}

	/**
	 * 功能：加密
	 * @param source源字符串
	 * @param charSet 编码
	 * @return String
	 * @throws UnsupportedEncodingException 编码异常
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public String encrypt(String source, String charSet) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String encrypt = null;
		byte[] ret = encrypt(source.getBytes(charSet));
		encrypt = new String(Base64.encode(ret));
		return encrypt;
	}

	/**
	 * 
	 * 功能：解密
	 * @param encryptedData被加密后的字符串
	 * @param charSet编码
	 * @return String
	 * @throws UnsupportedEncodingException编码异常
	 * @throws NoSuchAlgorithmException
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeySpecException 
	 * @throws InvalidKeyException 
	 */
	public String decrypt(String encryptedData, String charSet) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String descryptedData = null;
		byte[] ret = descrypt(Base64.decode(encryptedData.toCharArray()));
		descryptedData = new String(ret, charSet);
		return descryptedData;
	}

	/**
	 * 加密数据 用生成的密钥加密原始数据
	 * 
	 * @param primaryData
	 *            原始数据
	 * @return byte[]
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private byte[] encrypt(byte[] primaryData) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		/** 取得安全密钥 */
		byte rawKeyData[] = getKey();

		/** DES算法要求有一个可信任的随机数源 */
		SecureRandom sr = new SecureRandom();

		/** 使用原始密钥数据创建DESKeySpec对象 */
		DESKeySpec dks = null;
		try {
			dks = new DESKeySpec(keyData.getBytes());
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}

		/** 创建一个密钥工厂 */
		SecretKeyFactory keyFactory = null;
		try {
			keyFactory = SecretKeyFactory.getInstance("DES");
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException("Create a key factory(SecretKeyFactory) fail ,NoSuchAlgorithmException error : " + e.getMessage() + "; ");
		}

		/** 用密钥工厂把DESKeySpec转换成一个SecretKey对象 */
		SecretKey key = null;
		try {
			key = keyFactory.generateSecret(dks);
		} catch (InvalidKeySpecException e) {
			throw new InvalidKeySpecException("Convert DESKeySpec to a SecretKey object with a key factory fail ,InvalidKeySpecException  source:" + dks + " ; error : " + e.getMessage() + "; ");
		}

		/** Cipher对象实际完成加密操作 */
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("DES");
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException("Cipher object to complete the encryption operation fail ,NoSuchAlgorithmException  error : " + e.getMessage() + "; ");
		} catch (NoSuchPaddingException e) {
			throw new NoSuchPaddingException("Cipher object to complete the encryption operation fail ,NoSuchPaddingException  error : " + e.getMessage() + "; ");
		}

		/** 用密钥初始化Cipher对象 */
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key, sr);
		} catch (InvalidKeyException e) {
			throw new InvalidKeyException("Cipher object is initialized with a key fail ,InvalidKeyException  source:" + Cipher.DECRYPT_MODE + "," + key + "," + sr + " ; error : " + e.getMessage() + "; ");
		}

		/** 正式执行加密操作 */
		byte encryptedData[] = null;
		try {
			encryptedData = cipher.doFinal(primaryData);
		} catch (IllegalStateException e) {
			throw new IllegalStateException("Perform encryption operations fail ,IllegalStateException  source:" + primaryData + " ; error : " + e.getMessage() + "; ");
		} catch (IllegalBlockSizeException e) {
			throw new IllegalBlockSizeException("Perform encryption operations fail ,IllegalBlockSizeException  source:" + primaryData + " ; error : " + e.getMessage() + "; ");
		} catch (BadPaddingException e) {
			throw new BadPaddingException("Perform encryption operations fail ,BadPaddingException  source:" + primaryData + " ; error : " + e.getMessage() + "; ");
		}

		/** 返回加密数据 */
		return encryptedData;
	}

	/**
	 * 用密钥解密数据
	 * 
	 * @param encryptedData
	 *            加密后的数据
	 * @return byte[]
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private byte[] descrypt(byte[] encryptedData) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		/** DES算法要求有一个可信任的随机数源 */
		SecureRandom sr = new SecureRandom();

		/** 取得安全密钥 */
		byte rawKeyData[] = getKey();

		/** 使用原始密钥数据创建DESKeySpec对象 */
		DESKeySpec dks = null;
		try {
			dks = new DESKeySpec(keyData.getBytes());
		} catch (InvalidKeyException e) {
			throw new InvalidKeyException("Create a DESKeySpec object using the original key data fail ,InvalidKeyException  source:" + keyData + " ; error : " + e.getMessage() + "; ");
		}

		/** 创建一个密钥工厂 */
		SecretKeyFactory keyFactory = null;
		try {
			keyFactory = SecretKeyFactory.getInstance("DES");
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException("Create a key factory (SecretKeyFactory) fail ,NoSuchAlgorithmException  error : " + e.getMessage() + "; ");
		}

		/** 用密钥工厂把DESKeySpec转换成一个SecretKey对象 */
		SecretKey key = null;
		try {
			key = keyFactory.generateSecret(dks);
		} catch (InvalidKeySpecException e) {
			throw new InvalidKeySpecException("Convert DESKeySpec to a SecretKey object with a key factory fail ,InvalidKeySpecException  source:" + dks + " ; error : " + e.getMessage() + "; ");
		}

		/** Cipher对象实际完成加密操作 */
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("DES");
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException("Cipher object to complete the encryption operation(Cipher) fail ,NoSuchAlgorithmException   error : " + e.getMessage() + "; ");
		} catch (NoSuchPaddingException e) {
			throw new NoSuchPaddingException("Cipher object to complete the encryption operation(Cipher) fail ,NoSuchPaddingException  error : " + e.getMessage() + "; ");
		}

		/** 用密钥初始化Cipher对象 */
		try {
			cipher.init(Cipher.DECRYPT_MODE, key, sr);
		} catch (InvalidKeyException e) {
			throw new InvalidKeyException("Cipher object is initialized with a key fail ,InvalidKeyException  source:" + Cipher.DECRYPT_MODE + "," + key + "," + sr + " ; error : " + e.getMessage() + "; ");
		}

		/** 正式执行解密操作 */
		byte decryptedData[] = null;
		try {
			decryptedData = cipher.doFinal(encryptedData);
		} catch (IllegalStateException e) {
			throw new IllegalStateException("Perform decryption operation fail ,IllegalStateException  source:" + encryptedData + " ; error : " + e.getMessage() + "; ");
		} catch (IllegalBlockSizeException e) {
			throw new IllegalBlockSizeException("Perform decryption operation fail ,IllegalBlockSizeException  source:" + encryptedData + " ; error : " + e.getMessage() + "; ");
		} catch (BadPaddingException e) {
			throw new BadPaddingException("Perform decryption operation fail ,BadPaddingException  source:" + encryptedData + " ; error : " + e.getMessage() + "; ");
		}
		return decryptedData;
	}

	/**
	 * 取得安全密钥 此方法作废,因为每次key生成都不一样导致解密加密用的密钥都不一样， 从而导致Given final block not
	 * properly padded错误.
	 * 
	 * @return byte数组
	 * @throws NoSuchAlgorithmException
	 */
	private byte[] getKey() throws NoSuchAlgorithmException {

		/** DES算法要求有一个可信任的随机数源 */
		SecureRandom sr = new SecureRandom();

		/** 为我们选择的DES算法生成一个密钥生成器对象 */
		KeyGenerator kg = null;
		try {
			kg = KeyGenerator.getInstance("DES");
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException("DES algorithm generates a key generator object(KeyGenerator) fail ,NoSuchAlgorithmException error : " + e.getMessage() + "; ");
		}
		kg.init(sr);

		/** 生成密钥工具类 */
		SecretKey key = kg.generateKey();

		/** 生成密钥byte数组 */
		byte rawKeyData[] = key.getEncoded();

		return rawKeyData;
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		DESUtil DESUtil = new DESUtil("aaaaaaaaaaaaaaaaaaaaaaa");
		try {
			String aaa = "jYvIVbDsikXW/wR+fZnPCA==";
			aaa = URLEncoder.encode(aaa, "UTF-8");
			System.out.println(aaa);
			System.out.println(URLDecoder.decode(aaa));
			try {
				System.out.println(DESUtil.encrypt("{\"registerCode\":\"20991010101\",\"success\":true}"));
			} catch (InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			try {
				System.out.println(DESUtil.decrypt("VlBNbzNkVHdVUzVreTFGNnhZVWpxcmFDVytpdkNOTzZ5TzhVNFByVkRXdzEyZlFYbW5pTUVzcnhHcmFWbnF1dw%3D%3D"));
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}