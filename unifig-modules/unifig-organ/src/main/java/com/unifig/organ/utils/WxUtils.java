package com.unifig.organ.utils;

import com.alibaba.fastjson.JSONObject;
import com.unifig.organ.meta.OAuthWebChatMeta;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Component
public class WxUtils {

    @Autowired
    private OAuthWebChatMeta oAuthWebChatMeta;


    private String CATEGORY_TEST = "/Users/maxiaoliang/work/maxl/code/java/gitee/unifig";


    private String environment = "";

    //从微信后台拿到APPID和APPSECRET 并封装为常量
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    /**
     * 编写Get请求的方法。但没有参数传递的时候，可以使用Get请求
     *
     * @param url 需要请求的URL
     * @return 将请求URL后返回的数据，转为JSON格式，并return
     */
    public static JSONObject doGetStr(String url) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();//获取DefaultHttpClient请求
        HttpGet httpGet = new HttpGet(url);//HttpGet将使用Get方式发送请求URL
        JSONObject jsonObject = null;
        HttpResponse response = client.execute(httpGet);//使用HttpResponse接收client执行httpGet的结果
        HttpEntity entity = response.getEntity();//从response中获取结果，类型为HttpEntity
        if (entity != null) {
            String result = EntityUtils.toString(entity, "UTF-8");//HttpEntity转为字符串类型
            jsonObject = JSONObject.parseObject(result);//字符串类型转为JSON类型
        }
        return jsonObject;
    }

    /**
     * 编写Post请求的方法。当我们需要参数传递的时候，可以使用Post请求
     *
     * @param url    需要请求的URL
     * @param outStr 需要传递的参数
     * @return 将请求URL后返回的数据，转为JSON格式，并return
     */
    public static JSONObject doPostStr(String url, String outStr) throws ClientProtocolException, IOException {
        DefaultHttpClient client = new DefaultHttpClient();//获取DefaultHttpClient请求
        HttpPost httpost = new HttpPost(url);//HttpPost将使用Get方式发送请求URL
        JSONObject jsonObject = null;
        httpost.setEntity(new StringEntity(outStr, "UTF-8"));//使用setEntity方法，将我们传进来的参数放入请求中
        HttpResponse response = client.execute(httpost);//使用HttpResponse接收client执行httpost的结果
        String result = EntityUtils.toString(response.getEntity(), "UTF-8");//HttpEntity转为字符串类型
        jsonObject = JSONObject.parseObject(result);//字符串类型转为JSON类型
        return jsonObject;
    }

    /**
     * 获取AccessToken
     *
     * @return 返回拿到的access_token及有效期
     */
    public AccessToken getAccessToken() throws ClientProtocolException, IOException {
        AccessToken token = new AccessToken();
        String url = ACCESS_TOKEN_URL.replace("APPID", oAuthWebChatMeta.getAppid()).replace("APPSECRET", oAuthWebChatMeta.getSecret());//将URL中的两个参数替换掉
        JSONObject jsonObject = doGetStr(url);//使用刚刚写的doGet方法接收结果
        if (jsonObject != null) { //如果返回不为空，将返回结果封装进AccessToken实体类
            token.setToken(jsonObject.getString("access_token"));//取出access_token
            int expires_in = jsonObject.getIntValue("expires_in");
            token.setExpiresIn(expires_in);//取出access_token的有效期
        }
        return token;
    }

    /**
     * @param uuid
     * @param page
     * @param accessToken
     * @return base64 后编码的图片文件
     */
    public byte[] getminiqrQr(String uuid, String page, String accessToken) {
        RestTemplate rest = new RestTemplate();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken;
//            Map<String, Object> param = new HashMap<>();
//            param.put("scene", "");
//            param.put("page", "pages/index/index");
//            param.put("width", 430);
//            param.put("auto_color", false);
//            Map<String, Object> line_color = new HashMap<>();
//            line_color.put("r", 0);
//            line_color.put("g", 0);
//            line_color.put("b", 0);
//            param.put("line_color", line_color);
            //String json = JSONObject.toJSONString(param);
            // JSONObject json = new JSONObject();
            //json.put("scence","id=id");
            //json.put("width",400);
            String json = "{\"scene\":\"" + uuid + "\",\"width\":400,\"page\":" + "\"" + page + "\"" + "}";
            // System.out.println(json);
            //   String json2 = "{\"scene\":\"id=maxl\",\"width\":400,\"page\":"+"\""+page+"\""+"}";
            // System.out.println(json2);
            // getminiqrQr(accessToken,id,page);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            //    String s = HttpClientUtil.doPostJson("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken, json);
            org.springframework.http.HttpEntity requestEntity = new org.springframework.http.HttpEntity(json, headers);
            ResponseEntity<byte[]> entity = rest.exchange(url, HttpMethod.POST, requestEntity, byte[].class, new Object[0]);
            byte[] result = entity.getBody();
            //   BASE64Encoder encoder = new BASE64Encoder();
            //   String data = encoder.encode(result);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


//    public String getminiqrQr(String token, String id, String page) throws IOException {
//        String openid = oAuthWebChatMeta.getAppid();
//        String qrName = "session" + openid;
//        String path = null;
//        if (environment.equals("local")) {
//            path = CATEGORY_TEST;
//        } else {
//            path = System.getProperty("user.dir").concat("/files");
//        }
//        File file = new File(path + "/12.png");
//        if (!file.exists()) {
//            file.createNewFile();
//        }
//        //你的json数据 ,格式不要错
//        String json = "{\"scene\":\"id=maxl\",\"width\":400,\"page\":" + "\"" + page + "\"" + "}";
//        String s = HttpClientUtil.doPostJson("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + token, json);
//
//        //new一个文件对象用来保存图片
//        File imageFile = new File(path);
//        //创建输出流
//        FileOutputStream outStream = null;
//        try {
//            outStream = new FileOutputStream(imageFile);
//            //写入数据
//            // outStream.write(data);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                //关闭输出流
//                outStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//        return imageFile.getPath();
//    }
}