package com.unifig.mall.util.print;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;

/**
 * 所有接口调用的地方 ,这里比较简陋
 */

public class LAVApi {

    /**
     * 获取token 开放应用服务模式所需参数
     *
     * @param client_id  易联云颁发给开发者的应用ID 非空值
     * @param grant_type 授与方式（固定为 “authorization_code”）
     * @param sign       签名 详见API文档列表-接口签名
     * @param code       详见商户授权-获取code
     * @param scope      授权权限，传all
     * @param timestamp  当前服务器时间戳(10位)
     * @param id         UUID4 详见API文档列表-uuid4
     * @return
     */
    public static String getToken(String client_id, String grant_type, String sign, String code, String scope, String timestamp, String id) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("grant_type", grant_type);
        hashMap.put("sign", sign);
        hashMap.put("code", code);
        hashMap.put("scope", scope);
        hashMap.put("timestamp", timestamp);
        hashMap.put("id", id);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.GET_TOKEN, hashMap, false);
    }

    /**
     * 获取token  自有应用服务模式所需参数
     *
     * @param client_id  平台id 非空值
     * @param grant_type 授与方式（固定为’client_credentials’）
     * @param sign       签名 详见API文档列表-接口签名
     * @param scope      授权权限，传all
     * @param timestamp  当前服务器时间戳(10位)
     * @param id         UUID4 详见API文档列表-uuid4
     * @return
     */
    public static String getToken(String client_id, String grant_type, String sign, String scope, String timestamp, String id) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("grant_type", grant_type);
        hashMap.put("sign", sign);
        hashMap.put("scope", scope);
        hashMap.put("timestamp", timestamp);
        hashMap.put("id", id);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.GET_TOKEN, hashMap, false);
    }

    /**
     * 刷新access_token
     *
     * @param client_id     易联云颁发给开发者的应用ID 非空值
     * @param grant_type    授与方式（固定为 “refresh_token”）
     * @param scope         授权权限，传all
     * @param sign          签名 详见API文档列表-接口签名
     * @param refresh_token 更新access_token所需
     * @param id            UUID4 详见API文档列表-uuid4
     * @param timestamp     当前服务器时间戳(10位)
     * @return
     */
    public static String refreshToken(String client_id, String grant_type, String scope, String sign, String refresh_token, String id, String timestamp) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("grant_type", grant_type);
        hashMap.put("scope", scope);
        hashMap.put("sign", sign);
        hashMap.put("refresh_token", refresh_token);
        hashMap.put("id", id);
        hashMap.put("timestamp", timestamp);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.GET_TOKEN, hashMap, false);
    }

    /**
     * 极速授权
     *
     * @param client_id    易联云颁发给开发者的应用ID 非空值
     * @param machine_code 易联云打印机终端号
     * @param qr_key       特殊密钥(有效期为300秒)
     * @param scope        授权权限，传all
     * @param sign         签名 详见API文档列表
     * @param id           UUID4 详见API文档列表-uuid4
     * @param timestamp    当前服务器时间戳(10位)
     * @return
     */
    public static String speedAu(String client_id, String machine_code, String qr_key, String scope, String sign, String id, String timestamp) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("machine_code", machine_code);
        hashMap.put("qr_key", qr_key);
        hashMap.put("scope", scope);
        hashMap.put("sign", sign);
        hashMap.put("id", id);
        hashMap.put("timestamp", timestamp);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.SPEED_AUTHORIZE, hashMap, false);
    }

    /**
     * 打印
     *
     * @param client_id    易联云颁发给开发者的应用ID 非空值
     * @param access_token 授权的token 必要参数
     * @param machine_code 易联云打印机终端号
     * @param content      打印内容(需要urlencode)
     * @param origin_id    商户系统内部订单号，要求32个字符内，只能是数字、大小写字母 ，且在同一个client_id下唯一。详见商户订单号
     * @param sign         签名 详见API文档列表
     * @param id           UUID4 详见API文档列表-uuid4
     * @param timestamp    当前服务器时间戳(10位)
     * @return
     */
    public static String print(String client_id, String access_token, String machine_code, String content, String origin_id, String sign, String id, String timestamp) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("access_token", access_token);
        hashMap.put("machine_code", machine_code);
        hashMap.put("content", content);
        hashMap.put("origin_id", origin_id);
        hashMap.put("sign", sign);
        hashMap.put("id", id);
        hashMap.put("timestamp", timestamp);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.API_PRINT, hashMap, false);
    }

    /**
     * 添加终端授权 开放应用服务模式不需要此接口 ,自有应用服务模式所需参数
     *
     * @param client_id    易联云颁发给开发者的应用ID 非空值
     * @param machine_code 易联云打印机终端号
     * @param msign        易联云终端密钥(如何快速获取终端号和终端秘钥)
     * @param access_token 授权的token 必要参数
     * @param sign         签名 详见API文档列表-接口签名
     * @param id           UUID4 详见API文档列表-uuid4
     * @param timestamp    当前服务器时间戳(10位)
     * @return
     */
    public static String addPrinter(String client_id, String machine_code, String msign, String access_token, String sign, String id, String timestamp) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("machine_code", machine_code);
        hashMap.put("msign", msign);
        hashMap.put("access_token", access_token);
        hashMap.put("sign", sign);
        hashMap.put("id", id);
        hashMap.put("timestamp", timestamp);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.API_ADD_PRINTER, hashMap, false);
    }

    /**
     * 删除终端授权 开放应用服务模式、自有应用服务模式所需参数
     * ps 一旦删除，意味着开发者将失去此台打印机的接口权限，请谨慎操作
     *
     * @param client_id    易联云颁发给开发者的应用ID 非空值
     * @param access_token 授权的token 必要参数
     * @param machine_code 易联云打印机终端号
     * @param sign         签名 详见API文档列表-接口签名
     * @param id           UUID4 详见API文档列表-uuid4
     * @param timestamp    当前服务器时间戳(10位)
     * @return
     */
    public static String deletePrinter(String client_id, String access_token, String machine_code, String sign, String id, String timestamp) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("access_token", access_token);
        hashMap.put("machine_code", machine_code);
        hashMap.put("sign", sign);
        hashMap.put("id", id);
        hashMap.put("timestamp", timestamp);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.API_DELET_PRINTER, hashMap, false);
    }

    /**
     * 添加应用菜单
     *
     * @param client_id    易联云颁发给开发者的应用ID 非空值
     * @param access_token 授权的token 必要参数
     * @param machine_code 易联云打印机终端号
     * @param content      json格式的应用菜单（其中url和菜单名称需要urlencode)
     * @param sign         签名 详见API文档列表-接口签名
     * @param id           UUID4 详见API文档列表-uuid4
     * @param timestamp    当前服务器时间戳(10位)
     * @return
     */
    public static String addPrintMenu(String client_id, String access_token, String machine_code, String content, String sign, String id, String timestamp) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("access_token", access_token);
        hashMap.put("machine_code", machine_code);
        hashMap.put("content", content);
        hashMap.put("sign", sign);
        hashMap.put("id", id);
        hashMap.put("timestamp", timestamp);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.API_ADD_PRINT_MENU, hashMap, false);
    }

    /**
     * 关机重启接口
     *
     * @param client_id     易联云颁发给开发者的应用ID 非空值
     * @param access_token  授权的token 必要参数
     * @param machine_code  易联云打印机终端号
     * @param response_type 重启:restart,关闭:shutdown
     * @param sign          签名 详见API文档列表-接口签名
     * @param id            UUID4 详见API文档列表-uuid4
     * @param timestamp     当前服务器时间戳(10位)
     * @return
     */
    public static String shutDownRestart(String client_id, String access_token, String machine_code, String response_type, String sign, String id, String timestamp) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("access_token", access_token);
        hashMap.put("machine_code", machine_code);
        hashMap.put("response_type", response_type);
        hashMap.put("sign", sign);
        hashMap.put("id", id);
        hashMap.put("timestamp", timestamp);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.API_SHUTDOWN_RESTART, hashMap, false);
    }

    /**
     * 声音调节接口
     *
     * @param client_id     易联云颁发给开发者的应用ID 非空值
     * @param access_token  授权的token 必要参数
     * @param machine_code  易联云打印机终端号
     * @param response_type 蜂鸣器:buzzer,喇叭:horn
     * @param voice         [1,2,3] 3种音量设置
     * @param sign          签名 详见API文档列表-接口签名
     * @param id            UUID4 详见API文档列表-uuid4
     * @param timestamp     当前服务器时间戳(10位)
     * @return
     */
    public static String setSound(String client_id, String access_token, String machine_code, String response_type, String voice, String sign, String id, String timestamp) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("access_token", access_token);
        hashMap.put("machine_code", machine_code);
        hashMap.put("response_type", response_type);
        hashMap.put("voice", voice);
        hashMap.put("sign", sign);
        hashMap.put("id", id);
        hashMap.put("timestamp", timestamp);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.API_SET_SOUND, hashMap, false);
    }

    /**
     * 获取机型打印宽度接口
     *
     * @param client_id    易联云颁发给开发者的应用ID 非空值
     * @param access_token 授权的token 必要参数
     * @param machine_code 易联云打印机终端号
     * @param sign         签名 详见API文档列表-接口签名
     * @param id           UUID4 详见API文档列表-uuid4
     * @param timestamp    当前服务器时间戳(10位)
     * @return
     */
    public static String getPrintInfo(String client_id, String access_token, String machine_code, String sign, String id, String timestamp) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("access_token", access_token);
        hashMap.put("machine_code", machine_code);
        hashMap.put("sign", sign);
        hashMap.put("id", id);
        hashMap.put("timestamp", timestamp);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.API_PRINT_INFO, hashMap, false);
    }

    /**
     * 获取机型软硬件版本接口
     *
     * @param client_id    易联云颁发给开发者的应用ID 非空值
     * @param access_token 授权的token 必要参数
     * @param machine_code 易联云打印机终端号
     * @param sign         签名 详见API文档列表-接口签名
     * @param id           UUID4 详见API文档列表-uuid4
     * @param timestamp    当前服务器时间戳(10位)
     * @return
     */
    public static String getVersion(String client_id, String access_token, String machine_code, String sign, String id, String timestamp) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("access_token", access_token);
        hashMap.put("machine_code", machine_code);
        hashMap.put("sign", sign);
        hashMap.put("id", id);
        hashMap.put("timestamp", timestamp);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.API_GET_VIERSION, hashMap, false);
    }

    /**
     * 取消所有未打印订单
     *
     * @param client_id    易联云颁发给开发者的应用ID 非空值
     * @param access_token 授权的token 必要参数
     * @param machine_code 易联云打印机终端号
     * @param sign         签名 详见API文档列表-接口签名
     * @param id           UUID4 详见API文档列表-uuid4
     * @param timestamp    当前服务器时间戳(10位)
     * @return
     */
    public static String cancelAll(String client_id, String access_token, String machine_code, String sign, String id, String timestamp) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("access_token", access_token);
        hashMap.put("machine_code", machine_code);
        hashMap.put("sign", sign);
        hashMap.put("id", id);
        hashMap.put("timestamp", timestamp);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.API_CANCEL_ALL, hashMap, false);
    }

    /**
     * 取消单条未打印订单
     *
     * @param client_id    易联云颁发给开发者的应用ID 非空值
     * @param access_token 授权的token 必要参数
     * @param machine_code 易联云打印机终端号
     * @param order_id     通过打印接口返回的订单号 详见API文档列表-打印接口
     * @param sign         签名 详见API文档列表-接口签名
     * @param id           UUID4 详见API文档列表-uuid4
     * @param timestamp    当前服务器时间戳(10位)
     * @return
     */
    public static String cancelOne(String client_id, String access_token, String machine_code, String order_id, String sign, String id, String timestamp) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("access_token", access_token);
        hashMap.put("machine_code", machine_code);
        hashMap.put("order_id", order_id);
        hashMap.put("sign", sign);
        hashMap.put("id", id);
        hashMap.put("timestamp", timestamp);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.API_CANCEL_ONE, hashMap, false);
    }

    /**
     * 设置logo接口
     *
     * @param client_id    易联云颁发给开发者的应用ID 非空值
     * @param access_token 授权的token 必要参数
     * @param machine_code 易联云打印机终端号
     * @param img_url      图片地址,图片宽度最大为350px,文件大小不能超过40Kb
     * @param sign         签名 详见API文档列表-接口签名
     * @param id           UUID4 详见API文档列表-uuid4
     * @param timestamp    当前服务器时间戳(10位)
     * @return
     */
    public static String setIcon(String client_id, String access_token, String machine_code, String img_url, String sign, String id, String timestamp) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("access_token", access_token);
        hashMap.put("machine_code", machine_code);
        hashMap.put("img_url", img_url);
        hashMap.put("sign", sign);
        hashMap.put("id", id);
        hashMap.put("timestamp", timestamp);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.API_SET_ICON, hashMap, false);
    }

    /**
     * 取消logo接口
     *
     * @param client_id    易联云颁发给开发者的应用ID 非空值
     * @param access_token 授权的token 必要参数
     * @param machine_code 易联云打印机终端号
     * @param sign         签名 详见API文档列表-接口签名
     * @param id           UUID4 详见API文档列表-uuid4
     * @param timestamp    当前服务器时间戳(10位)
     * @return
     */
    public static String deleteIcon(String client_id, String access_token, String machine_code, String sign, String id, String timestamp) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("access_token", access_token);
        hashMap.put("machine_code", machine_code);
        hashMap.put("sign", sign);
        hashMap.put("id", id);
        hashMap.put("timestamp", timestamp);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.API_DELET_ICON, hashMap, false);
    }

    /**
     * 接单拒单设置接口
     *
     * @param client_id     易联云颁发给开发者的应用ID 非空值
     * @param access_token  授权的token 必要参数
     * @param machine_code  易联云打印机终端号
     * @param response_type 开启:open,关闭:close
     * @param sign          签名 详见API文档列表-接口签名
     * @param id            UUID4 详见API文档列表-uuid4
     * @param timestamp     当前服务器时间戳(10位)
     * @return
     */
    public static String getOrder(String client_id, String access_token, String machine_code, String response_type, String sign, String id, String timestamp) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("access_token", access_token);
        hashMap.put("machine_code", machine_code);
        hashMap.put("response_type", response_type);
        hashMap.put("sign", sign);
        hashMap.put("id", id);
        hashMap.put("timestamp", timestamp);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.API_GET_ORDER, hashMap, false);
    }

    /**
     * 打印方式接口
     *
     * @param client_id     易联云颁发给开发者的应用ID 非空值
     * @param access_token  授权的token 必要参数
     * @param machine_code  易联云打印机终端号
     * @param response_type 开启:btnopen,关闭:btnclose; 按键打印
     * @param sign          签名 详见API文档列表-接口签名
     * @param id            UUID4 详见API文档列表-uuid4
     * @param timestamp     当前服务器时间戳(10位)
     * @return
     */
    public static String btnPrint(String client_id, String access_token, String machine_code, String response_type, String sign, String id, String timestamp) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", client_id);
        hashMap.put("access_token", access_token);
        hashMap.put("machine_code", machine_code);
        hashMap.put("response_type", response_type);
        hashMap.put("sign", sign);
        hashMap.put("id", id);
        hashMap.put("timestamp", timestamp);
        return HttpUtil.sendPost(ApiConst.MAIN_HOST_URL + ApiConst.API_BTN_PRINT, hashMap, false);
    }

    public static String getSin(String timestamp) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Methods.CLIENT_ID);
            stringBuilder.append(timestamp);
            stringBuilder.append(Methods.CLIENT_SECRET);
            return getMd5(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getuuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * @param str
     * @return
     * @Description: 32位小写MD5
     */
    public static String getMd5(String str) {
        String reStr = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes());
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bytes) {
                int bt = b & 0xff;
                if (bt < 16) {
                    stringBuffer.append(0);
                }
                stringBuffer.append(Integer.toHexString(bt));
            }
            reStr = stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return reStr;
    }
}
