/**
 * FileName: KartorHttpUtils
 * Author:
 * Date:     2019/4/3 17:17
 * Description: 驾图http工具类
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.utils.kartor;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.unifig.utils.HttpUtils;
import org.apache.commons.lang.RandomStringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <h3>概要:</h3><p>KartorHttpUtils</p>
 * <h3>功能:</h3><p>驾图http工具类</p>
 *
 * @create 2019/4/3
 * @since 1.0.0
 */
public class KartorUtils {

//    public static final String APPID = "ce5d49f68a9e4a6eacdf35156c7d7bee";
//    public static final String KEY = "9261bb9ffd584beda2b7bef97c516767";
    public static final String APPID = "c784a0e124064e7f8e873845f057947a";
    public static final String KEY = "53e85d61036b4118bcd718f301afe0b0";
    private static final String TAGID = "rbshop.vip.001";


    /**
     * 驾图接口GET请求
     * @param url  接口url
     * @param parameters   参数map结合
     * @return
     */
    public static String sendGet(String url, Map<String, String> parameters) {
        Map<String, String> signature = KartorSignatureUtils.createSignature(parameters);
        return HttpUtils.sendGet(url, signature);
    }

    /**
     * 驾图接口Post请求
     * @param url  接口url
     * @param parameters   参数map结合
     * @return
     */
    public static String sendPost(String url, Map<String, String> parameters) {
        Map<String, String> signature = KartorSignatureUtils.createSignature(parameters);
        return HttpUtils.sendPost(url, signature);
    }

    /**
     * 驾图接口GET请求 无参数
     * @param url 接口url
     * @param appId  应用id
     * @param key  key
     * @return
     */
    public static String sendGet(String url, String appId,String key) {
        Map<String, String> signature = KartorSignatureUtils.createSignature(appId,key);
        return HttpUtils.sendGet(url, signature);
    }

    /**
     * 驾图接口Post请求 无参数
     * @param url 接口url
     * @param appId  应用id
     * @param key  key
     * @return
     */
    public static String sendPost(String url, String appId,String key) {
        Map<String, String> signature = KartorSignatureUtils.createSignature(appId,key);
        return HttpUtils.sendPost(url, signature);
    }
    /**
     * 创建用户
     * @param phoneNumber  手机号
     * @return  openId  用户标识
     */
    public static String creatUser(String phoneNumber){
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("phoneNumber", phoneNumber);
        parameters.put("appId", APPID);
        parameters.put("key",KEY);
        String result =sendGet("https://open.kartor.cn/res/user/add", parameters);
        JSONObject objects = new JSONObject(result);
        String code = objects.get("code").toString();
        if("0".equals(code)){
            return new JSONObject(objects.get("data").toString()).get("openId").toString();
        }
        return null;
    }

    /**
     * 给用户添加车辆
     * @param openId  驾图用户标识
     * @return openCarId  车辆id
     */
    public static String addPersonal(String openId){
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("openId", openId);
        parameters.put("plateNumber", "核销"+ RandomStringUtils.randomAlphanumeric(5));
        parameters.put("modelId", "3014");
        parameters.put("appId", APPID);
        parameters.put("key", KEY);
        String result =sendPost("https://open.kartor.cn/res/car/add/personal", parameters);
        JSONObject objects = new JSONObject(result);
        String code = objects.get("code").toString();
        if("0".equals(code)){
            return new JSONObject(objects.get("data").toString()).get("openCarId").toString();
        }
        return null;
    }

    /**
     * 给用户添加标签
     * @param openCarId
     * @return
     */
    public static boolean addTag(String openCarId){
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("openCarId", openCarId);
        parameters.put("tagId", TAGID);
        parameters.put("appId",APPID);
        parameters.put("key", KEY);
        String result =sendPost("https://open.kartor.cn/res/app/ubi/tag", parameters);
        JSONObject objects = new JSONObject(result);
        String code = objects.get("code").toString();
        if("0".equals(code)){
            return true;
        }
        return false;
    }

    /**
     * 删除用户标签
     * @param openCarId
     * @return
     */
    public static boolean delTag(String openCarId){
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("openCarId", openCarId);
        parameters.put("tagId", TAGID);
        parameters.put("appId",APPID);
        parameters.put("key", KEY);
        String result =sendPost("https://open.kartor.cn/res/app/ubi/delete/tag", parameters);
        JSONObject objects = new JSONObject(result);
        String code = objects.get("code").toString();
        if("0".equals(code)){
            return true;
        }
        return false;
    }

    /**
     * 查询手机号是否注册
     * @param mobile
     * @return
     */
    public static Map<String, String> selectKartorInfo(String mobile){
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("phoneNumber", mobile);
        parameters.put("appId",APPID);
        parameters.put("key", KEY);
        String result =sendPost(" https://open.kartor.cn/res/user/kartor/info", parameters);
        System.out.println(result);
        JSONObject objects = new JSONObject(result);
        Object code = objects.get("code");
        if("0".equals(code.toString())){
            Map<String, String> map = new HashMap<>();
            map.put("openId",new JSONObject(objects.get("data").toString()).get("openId").toString());
            JSONObject data = new JSONObject(objects.get("data"));
            JSONArray data1 = JSONUtil.parseArray(data.get("cars").toString());
            if (data1.size()>0) {
                map.put("openCarId",new JSONObject(data1.get(0).toString()).get("openCarId").toString());
            }
            return map;
        }
        return null;
    }

    /**
     * 根据用户openid查询用户基本信息
     * @param openId
     * @return
     */
    public static String query(String openId){
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("openId", openId);
        parameters.put("appId",APPID);
        parameters.put("key", KEY);
        String result =sendPost(" https://open.kartor.cn/res/user/profile/query", parameters);
        JSONObject objects = new JSONObject(result);
        Object code = objects.get("code");
        if("0".equals(code.toString())){
            String data = objects.get("data").toString();
            return data;
        }
        return null;

    }


    /**
     * 根据用户openid查询用户手机号
     * @param openId
     * @return
     */
    public static String queryPhone(String openId){
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("openId", openId);
        parameters.put("appId",APPID);
        parameters.put("key", KEY);
        String result =sendPost(" https://open.kartor.cn/res/user/profile/private", parameters);
        JSONObject objects = new JSONObject(result);
        Object code = objects.get("code");
        if("0".equals(code.toString())){
            String data = objects.get("data").toString();
            return data;
        }
        return null;

    }

    /**
     * 创建驾图用户并开启核销权限
     * @param mobile
     * @return
     */
    public static String createKartorUser(String mobile){
        //驾图用户id
        String openId = null;
        //车辆id
        String openCarId = null;
        Map<String, String> stringStringMap = selectKartorInfo(mobile);
        if(stringStringMap != null) {
            openId = stringStringMap.get("openId");
            openCarId = stringStringMap.get("openCarId");

        }else{
            openId = creatUser(mobile);
            openCarId = addPersonal(openId);
        }
        if(openCarId == null){
            openCarId = addPersonal(openId);
        }
        if(openCarId != null){
            if (KartorUtils.addTag(openCarId)) {
                return openId;
            }
        }
        return null;
    }

    /**
     * 关闭驾图用户核销权限
     * @param mobile
     * @return
     */
    public static String delKartorUserTag(String mobile){
        //驾图用户id
        String openId = null;
        //车辆id
        String openCarId = null;
        Map<String, String> stringStringMap = selectKartorInfo(mobile);
        if(stringStringMap != null) {
            openId = stringStringMap.get("openId");
            openCarId = stringStringMap.get("openCarId");
        }
        if(openCarId != null){
            if (delTag(openCarId)) {
                return openId;
            }
        }
        return null;
    }
}
