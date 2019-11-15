package com.tools.plugin.utils;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
/**
 * @Title:EncryptUtil
 * @Description: 加密解密工具类,提供十六进制的转换
 *
 *
 */
public class EncryptUtil{
    private static final String PASSWORD_CRYPT_KEY = "shixinxucong";
    private final static String DES = "DES";

    /**
     * @throws Exception
     */
    public static byte[] encrypt(byte[] src, byte[] key) throws Exception{
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        return cipher.doFinal(src);
    }

    /**
     * @throws Exception
     */
    public static byte[] decrypt(byte[] src, byte[] key) throws Exception{
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        return cipher.doFinal(src);
    }

    /**
     * DES解密，默认key为shishengfeng
     * @param data
     * @return
     */
    public final static String decrypt(String data){
        try{
            return new String(decrypt(hex2byte(data), PASSWORD_CRYPT_KEY.getBytes()));
        }catch (Exception e){
        }
        return null;
    }

    /**
     * DES解密
     * @param data
     * @param key
     * @return
     */
    public final static String decrypt(String data, String key){
        try{
            return new String(decrypt(hex2byte(data), key.getBytes()));
        }catch (Exception e){
        }
        return null;
    }

    /**
     * DES加密，默认key为shishengfeng
     * @param password
     * @return
     * @throws Exception
     */
    public final static String encrypt(String password){
        try{
            return byte2hex(encrypt(password.getBytes(), PASSWORD_CRYPT_KEY.getBytes()));
        }catch (Exception e){
        }
        return null;
    }

    /**
     * DES加密
     * @param password
     * @param key
     * @return
     */
    public final static String encrypt(String password, String key){
        try{
            return byte2hex(encrypt(password.getBytes(), key.getBytes()));
        }catch (Exception e){
        }
        return null;
    }

    /**
     * @Title: hex2byte
     * @Description: int类型的字符转换为十六进制的字节数组
     * @param strIn
     * @return
     * @throws Exception
     */
    public static byte[] hex2byte(String strIn) throws Exception{
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2){
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte)Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }
    
    /**
     * @Title: byte2hex
     * @Description: 字节数组转换为十六进制字符串
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b){
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++ ){
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1){
                hs = hs + "0" + stmp;
            }else{
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    /**
     * MD5加密
     * @param sourceString
     * @return
     */
    public static String MD5Encode(String sourceString){
        String resultString = null;
        try{
            resultString = new String(sourceString);
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byte2hexString(md.digest(resultString.getBytes()));
        }catch (Exception ex){
        }
        return resultString;
    }
    
    /**
     * @Title: byte2hexString
     * @Description: 将字节数组转换为十六进制字符串
     * @param bytes
     * @return
     */
    public static final String byte2hexString(byte[] bytes){
        StringBuffer buf = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++ ){
            if (((int)bytes[i] & 0xff) < 0x10){
                buf.append("0");
            }
            buf.append(Long.toString((int)bytes[i] & 0xff, 16));
        }
        return buf.toString();
    }
}