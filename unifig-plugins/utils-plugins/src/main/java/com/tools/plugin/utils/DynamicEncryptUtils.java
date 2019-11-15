package com.tools.plugin.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 动态文本加密/解密
 * (可以把正常文本加密为类似"3E24D795C1E8BAC38986E4D1B4DDB2B4C081E485334532334532"的密文)
 */
public class DynamicEncryptUtils {

	public static void main(String[] args) {
		String data1 = "这里是要加密的内容12345600000000";
		String pswd = "123456hijk111111111111";
		String Ret1 = Encrypt(data1, pswd);
		System.out.println("data1第一次加密结果:" + Ret1);
		System.out.println("data1第一次解密结果:" + Decrypt(Ret1, pswd));
		System.out.println("data1第二次解密结果:" + Decrypt(Ret1, pswd));

		String data = "这里是要加密的内容123456";
		String Ret = Encrypt(data, pswd);
		System.out.println("第二次加密结果:" + Ret);
		Ret = Decrypt(Ret, pswd);
		System.out.println("第二次解密结果:" + Ret);
		System.out.println("data1第三次解密结果:" + Decrypt(Ret1, pswd));
	}

	/**
	 * String 加密
	 * @param (String 数据,String 密码)
	 * @return String 加密后的String
	 */
	public static String Encrypt(String Data, String Password) {
		Random rand = new Random();
		int ra = rand.nextInt();
		int rb = rand.nextInt();
		String mod = Integer.toHexString(ra ^ rb).toUpperCase();

		mod = mod + "00000";
		mod = mod.substring(0, 4);

		int aLen = Data.getBytes().length;
		int bLen = Password.getBytes().length;
		int clen = mod.getBytes().length;
		String result = "";
		String temp = "";

		for (int i = 0, j = 0, k = 0; i < aLen; i++) {
			int a = Data.getBytes()[i];
			int b = Password.codePointAt(j);
			int c = mod.codePointAt(k);
			temp = Integer.toHexString(a ^ b ^ c).toUpperCase();
			temp = "00000" + temp;
			temp = temp.substring(temp.length() - 2, temp.length());
			result = result + temp;
			j += 1;
			k += 1;
			if (j + 1 == bLen)
				j = 0;
			if (k + 1 == clen)
				k = 0;
		}

		return mod + result;
	}

	/**
	 * String 解密
	 * @param (String 已加密的数据,String 密码)
	 * @return String 解密出来的String
	 */
	public static String Decrypt(String Data, String Password) {
		if (Data.length() < 4)
			return Data;
		String resultString = "";
		String mod = "";

		mod = Data.substring(0, 4);
		Data = Data.substring(4);
		int aLen = Data.length();
		int bLen = Password.length();
		int cLen = mod.length();
		int j = 0;
		int k = 0;
		byte[] data = new byte[aLen / 2];
		for (int i = 0; i < aLen; i += 2) {
			data[i / 2] = (byte) (HexToFirstInt(Data.substring(i, i + 2)) ^ Password.codePointAt(j) ^ mod.codePointAt(k));

			j = j + 1;
			k = k + 1;
			if (j == bLen - 1)
				j = 0;
			if (k == cLen - 1)
				k = 0;
		}
		resultString = new String(data);
		return resultString;
	}

	/**
	 * Int 十六进制文本返回第一个Byte
	 * @param (String  十六进制文本)
	 * @return Int 第一个Byte
	 */
	private static int HexToFirstInt(String HexStr) {
		if (HexStr.length() == 0) {
			HexStr = "0";
		}
		if (HexStr.length() % 2 == 1) {
			HexStr = "0" + HexStr;
		}
		int tempa = 0;
		int tempb = 0;
		byte[] bytes = HexStr.getBytes();
		int i = 0;
		if (bytes[i] < 58) {
			tempa = bytes[i] - 48;
		} else {
			tempa = bytes[i] - 55;
		}
		if (bytes[i + 1] < 58) {
			tempb = bytes[i + 1] - 48;
		} else {
			tempb = bytes[i + 1] - 55;
		}
		return ((tempa * 16) + tempb);
	}

	/**
	 * List<String> 把String解密成List<String>
	 * @param (String 已加密数据)
	 * @return List<String> 数组
	 */
	public static List<String> Trans(String Data) {
		String tempda = Decrypt(Data, GetDefaultPass());
		String[] tempS = tempda.split("\\|\\|");
		List<String> ret = new ArrayList<String>();
		for (int i = 0; i < tempS.length; i++) {
			ret.add(Decrypt(tempS[i], GetDefaultPass()));
		}
		return ret;
	}

	/**
	 * String 把List<String>加密成String
	 * @param (List<String> 数组)
	 * @return String 已加密数据
	 */
	public static String Trans(List<String> Data) {
		if (Data.size() <= 0) {
			return "";
		} else {
			String ret = Encrypt(Data.get(0), GetDefaultPass());
			for (int i = 1; i < Data.size(); i++) {
				ret = ret + "||" + Encrypt(Data.get(i), GetDefaultPass());
			}
			return Encrypt(ret, GetDefaultPass());
		}
	}

	public static String GetDefaultPass() {
		return "encrypt.Password";
	}
}
