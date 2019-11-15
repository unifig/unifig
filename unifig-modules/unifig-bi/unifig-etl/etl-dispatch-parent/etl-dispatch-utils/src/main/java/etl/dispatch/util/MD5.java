package etl.dispatch.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	public static byte[] encrypt(byte[] input) {
		byte[] bytesEncrypted = null;
		if (input == null) {
			return null;
		}
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
			bytesEncrypted = md5.digest(input);
		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
		} catch (Exception localException) {
		}
		return bytesEncrypted;
	}

	public static byte[] encrypt(String input) {
		byte[] bytesEncrypted = null;
		if ((input == null) || (input.equals(""))) {
			return null;
		}
		try {
			bytesEncrypted = encrypt(input.getBytes("gbk"));
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
		} catch (Exception localException) {
		}
		return bytesEncrypted;
	}

	public static String encryptToHex(String input) {
		return UncString.toHex(encrypt(input));
	}

	public static void main(String[] args) {
		System.out.println(encryptToHex("1111"));
	}
}
