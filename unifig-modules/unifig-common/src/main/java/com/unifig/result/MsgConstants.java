package com.unifig.result;

/**
 *
 * @ClassName MsgCode
 * @Description 通用消息码
 * @date 2017年12月23日 下午3:33:02
 */
public class MsgConstants {


    /***
     * 使用数据为空  ifn判断 return  703
     */
    public static final int DATA_IS_NULL = 703;


    /***
     * 使用数据为空  ifn判断 return  701
     */
    public static final int PARAM_MISS = 701;

    /**
     * code错误
     */
    public static final int CODE_FAILURE=10081;


    /***
     * 用户token错误
     */
    public static final int USER_TOKEN_ERROR = 10102;

    /***
     * 用户密码错误
     */
    public static final int USER_PASSWORD_ERROR = 10103;

    /***
     * 用户登陆信息过期
     */
    public static final int USER_AUTH_TIMEOUT = 10104;


    /**
     * 积分配置错误
     */
    public static final int INTEGRATION_RULE_SETTING_ERRO = 10105;

    /**
     * 用户openId获取失败
     */
    public static final int USER_WX_OPENID_GET_ERROR = 10106;

    /**
     * 用户未找到
     */
    public static final int  USER_NOT_FOUND = 700;


    /**
     * 验证码错误
     */
    public static final int VERIFICATION_CODE_ERROR= 10107;

    /**
     * 用户未认证
     */
    public static final int USER_UNAUTH= 18007;

    /**
     * 超时
     */
    public static final int REGISTERED_IS_TIMEOUT= 18008;


    /**
     * 电话找不到
     */
    public static final int PHONE_UNKNOWN= 18009;

    /**
     * 用户找不到
     */
    public static final int USER_UNKNOWN= 18010;

    /**
     * 生成二维码错误
     */
    public static final int ORCODE_PAME_ERROR= 18011;


    /**
     * 二维码未找到
     */
    public static final int ORCODE_NOTFOUND_ERROR= 18012;





}
