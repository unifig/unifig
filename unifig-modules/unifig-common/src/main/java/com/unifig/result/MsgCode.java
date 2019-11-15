package com.unifig.result;

/**
 *
 * @ClassName MsgCode
 * @Description 通用消息码
 * @date 2017年12月23日 下午3:33:02
 */
public enum MsgCode {


    SUCCESS("成功", 200), SERVER_ERROR("服务器异常", 500),

    PARAM_MISS("parameter is not complete", MsgConstants.PARAM_MISS),

    CODE_FAILURE("code failure", MsgConstants.CODE_FAILURE),

    USER_PASSWORD_ERROR("用户密码错误", MsgConstants.USER_PASSWORD_ERROR), USER_AUTH_TIMEOUT("用户登陆信息过期", MsgConstants.USER_AUTH_TIMEOUT),

    USER_TOKEN_ERROR("用户TOEKN非法", MsgConstants.USER_TOKEN_ERROR),

    DATA_IS_NULL("数据为空", MsgConstants.DATA_IS_NULL),

    INTEGRATION_RULE_SETTING_ERRO("积分规则action错误", MsgConstants.INTEGRATION_RULE_SETTING_ERRO),

    USER_WX_OPENID_GET_ERROR("用户获取openId失败", MsgConstants.USER_WX_OPENID_GET_ERROR),

    USER_NOT_FOUND("用户未找到", MsgConstants.USER_NOT_FOUND),

    USER_UNAUTH("用户未授权", MsgConstants.USER_UNAUTH),

    REGISTERED_IS_TIMEOUT("超时", MsgConstants.REGISTERED_IS_TIMEOUT),

    PHONE_UNKNOWN("电话找不到", MsgConstants.PHONE_UNKNOWN),

    USER_UNKNOWN("用户找不到", MsgConstants.USER_UNKNOWN),

    VERIFICATION_CODE_ERROR("验证码错误", MsgConstants.VERIFICATION_CODE_ERROR),


    ORCODE_PAME_ERROR("生成二维码错误", MsgConstants.ORCODE_PAME_ERROR),
    ORCODE_NOTFOUND_ERROR("二维码未找到", MsgConstants.ORCODE_NOTFOUND_ERROR);

    // 成员变量
    private String msg;
    private Integer code;


    // 构造方法
    private MsgCode(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    // 普通方法
    public static String getMsg(int code) {
        for (MsgCode c : MsgCode.values()) {
            if (c.getCode() == code) {
                return c.msg;
            }
        }
        return null;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
