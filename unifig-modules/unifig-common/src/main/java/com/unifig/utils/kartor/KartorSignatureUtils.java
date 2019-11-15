/**
 * FileName: kartorSignatureUtils
 * Author:
 * Date:     2019/4/3 15:18
 * Description: 驾图签名生成工具类
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.utils.kartor;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;

/**
 * <h3>概要:</h3><p>kartorSignatureUtils</p>
 * <h3>功能:</h3><p>驾图签名生成工具类</p>
 *
 * @create 2019/4/3
 * @since 1.0.0
 */
@Slf4j
public class KartorSignatureUtils {

    static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 接口生成签名示例 。测试地址（车品牌查询）：https://open.kartor.cn/res/car/brand/all
     * ?appId=8f11044eca4048aab1c8935a04d60e8b&timestamp=20161011183737&nonce=
     * SsFXTYc0 &signature=2BslWzlzEnWUq3mE%2BmN2PAdUlR0%3D
     */
    public static void main(String[] str) throws UnsupportedEncodingException {
        // map里面的key和value对应http请求里面的key和value
//        Map<String,String> map = new HashMap<>();
//        map.put("timestamp", "20161011183737");
//        map.put("appId", "8f11044eca4048aab1c8935a04d60e8b");
//        map.put("nonce", "SsFXTYc0");
//        // 生成签名的源串
//        String urlParam = toUrlParam(sort(map));
//        System.out.println("源串：" + urlParam);
//        // 计算签名
//        String sig = generateSignature(urlParam, "f137f3dd5f3c483c9f8378366b08963f");
//        System.out.println("签名：" + sig); // 结果2BslWzlzEnWUq3mE+mN2PAdUlR0=
//        // ecode签名, 结果为2BslWzlzEnWUq3mE%2BmN2PAdUlR0%3D
//        System.out.println("签名编码：" + URLEncoder.encode(sig, "UTF-8"));
        Map<String, String> map = createSignature("8f11044eca4048aab1c8935a04d60e8b", "f137f3dd5f3c483c9f8378366b08963f");
//        System.out.println(map);
    }


    /**
     * 生成签名  无参数
     * @return
     */
    public static Map<String,String> createSignature(String appId,String key){
        //生成随机时间
        String timestamp = df.format(new Date());
        //生成随机字符串
        String nonce = RandomStringUtils.randomAlphanumeric(8);
        Map<String,String> map = new HashMap<>();
        map.put("timestamp", timestamp);
        map.put("appId", appId);
        map.put("nonce", nonce);
        try{
            String urlParam = toUrlParam(sort(map));
            System.out.println(urlParam);
            // 计算签名
            String sig = generateSignature(urlParam, key);
            String signature = URLEncoder.encode(sig, "UTF-8");
            map.put("signature",sig);
        }catch (Exception e){
            log.error(e.getLocalizedMessage());
        }
        return map;
    }

    /**
     * 生成签名
     * @return
     */
    public static Map<String,String> createSignature(Map<String,String> map){
        //生成随机时间
        String timestamp = df.format(new Date());
        //生成随机字符串
        String nonce = RandomStringUtils.randomAlphanumeric(8);
        map.put("timestamp", timestamp);
        map.put("nonce", nonce);
        String key = map.get("key");
        map.remove("key");
        try{
            String urlParam = toUrlParam(sort(map));
            // 计算签名
            String sig = generateSignature(urlParam, key);
            System.out.println(urlParam);
            String signature = URLEncoder.encode(sig, "UTF-8");
            map.put("signature",sig);
            System.out.println(sig);
        }catch (Exception e){
            log.error(e.getLocalizedMessage());
        }
        return map;
    }

    /**
     * 将map里面的key和value拼接成签名的源串
     * @param sortedMap 经过排序的map
     * @return 源串
     */
    private static String toUrlParam(TreeMap<String,String> sortedMap)
            throws UnsupportedEncodingException {
        String urlParam = "";
        for (Map.Entry entry : sortedMap.entrySet()) {
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            // signature 参数不作为签名计算的源串； key或者value为空，也不作为计算签名的源串
            if (key == null || value == null || "signature".equals(key)) {
                continue;
            }
            // 对value需要encode
            String encodedValue = URLEncoder.encode(value, "UTF-8");
            urlParam += (key + "=" + encodedValue + "&");
        }
        // 删除最后一个&符号
        urlParam = urlParam.substring(0, urlParam.length() - 1);
        return urlParam;
    }

    /**
     * 对参数map排序
     * @param map 包含http参数的map
     * @return 排序后的map
     */
    private static TreeMap<String,String> sort(Map<String,String> map) {
        if (map == null || map.isEmpty()) {
            throw new NullPointerException("The parameter map must not be null.");
        }
        TreeMap<String,String> sortedMap = new TreeMap<>();
        // tree map会自动排序
        sortedMap.putAll(map);
        return sortedMap;
    }

    /**
     * 生成签名数据
     * @param data 待加密的数据
     * @param key 加密使用的key
     */
    public static String generateSignature(String data, String key) {
        try {
            byte[] keyBytes = key.getBytes("UTF-8");
            byte[] dataBytes = data.getBytes("UTF-8");
            return generateSignature(dataBytes, keyBytes);
        } catch (UnsupportedEncodingException e) {
            return null;
        } catch (InvalidKeyException e) {
            return null;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * 对 data 以key 进行HmacSHA1加密，然后生成base64
     * @param data 签名源串的字节数组
     * @param key 签名key的字节数组
     * @return 签名串
     */
    private static String generateSignature(byte[] data, byte[] key)
            throws InvalidKeyException, NoSuchAlgorithmException {
        SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(data);
        // 用base64进行编码
        return getBase64Signature(rawHmac);
    }

    /**
     * 将原始二进制数据数据，进行base64编码
     * @return 签名串
     */
    private static String getBase64Signature(byte[] bytes) {
        byte[] base64Text = Base64.encodeBase64(bytes);
        return new String(base64Text);
    }
}
