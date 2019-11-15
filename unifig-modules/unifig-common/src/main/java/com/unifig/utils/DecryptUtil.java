package com.unifig.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;

/**
* AES CBC 解密微信密文
* @author robinyang
*
*/
public class DecryptUtil {

    /**
     * 算法名称
     */
    final static String KEY_ALGORITHM = "AES";

    /**
     * 加解密算法/模式/填充方式
     */
    final static String algorithmStr = "AES/CBC/PKCS7Padding";
    private static Key key;
    private static Cipher cipher;

    public static void init(byte[] keyBytes) {

        // 如果密钥不足16位，那么就补足. 这个if 中的内容很重要
        int base = 16;
        if (keyBytes.length % base != 0) {
            int groups = keyBytes.length / base + (keyBytes.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
            keyBytes = temp;
        }
        // 初始化
        Security.addProvider(new BouncyCastleProvider());
        // 转化成JAVA的密钥格式
        key = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        try {
            // 初始化cipher
            cipher = Cipher.getInstance(algorithmStr);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 解密方法
     *
     * @param encryptedDataStr
     *            要解密的字符串
     * @param keyBytesStr
     *            解密密钥
     * @return
     */
    public static byte[] decrypt(String encryptedDataStr, String keyBytesStr, String ivStr) {
        byte[] encryptedText = null;
        byte[] encryptedData = null;
        byte[] sessionkey = null;
        byte[] iv = null;

        try {
            sessionkey = Base64.decodeBase64(keyBytesStr);
            encryptedData = Base64.decodeBase64(encryptedDataStr);
            iv = Base64.decodeBase64(ivStr);

            init(sessionkey);

            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            encryptedText = cipher.doFinal(encryptedData);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return encryptedText;
    }

    public static void main(String[] args) throws UnsupportedEncodingException{
        String content = "dacfvAwzuZ0N6mYOtA1i2qRL2VZYKwE/TzgNDbNtUsC5jgpru7zzDc34yQKnpe/xhThNS9lgZ4KKUhlQ/ffQ2nCsbqCDqYNrEsxXzLvYDBqpPY8IoQsHakOStI2ZN86kqV1XHO5v+tz+2JxpDoRE5KVSj/XbbbYDTFZa7ca+OQ49deXA1S4q0/Bl8ScHwmS3IkV/JX6wf6aphdnoosQpSw==";
        String key = "UUFmp+DX0+dWioQ\\/J2nCMQ==";
        String iv = "OJcXcXOPzivaQYVQ0a4SvQ==";
        byte[] result = decrypt(content, key, iv);
        System.out.println(new String(result,"UTF-8"));
        JSONObject jsonObject1 = JSONObject.parseObject(new String(result, "UTF-8"));
        String phoneNumber = (String)jsonObject1.get("phoneNumber");
        System.out.println(phoneNumber);
    }

}