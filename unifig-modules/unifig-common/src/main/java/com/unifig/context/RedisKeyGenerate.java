package com.unifig.context;

/**
 *
 * @create 2019-01-27
 */
public class RedisKeyGenerate {

    /**
     * hash action(动态资源)
     * @return unifig:ums:member:integration:rule:setting:
     */
    public static String memeberRuleSetting() {
        return new StringBuffer().append(RedisConstants.RATEL_PATH_DEF).append(RedisConstants.RATEL_MEMBER_INTG_RULE_SETTING).toString();
    }


    /**
     *  String  Json 用户分享内容数据
     * @return unifig:ums:member:share:code:$code
     */
    public static String shareCacheKey(String code) {
        return new StringBuffer().append(RedisConstants.UNIFIG_PATH_DEF).append(RedisConstants.UNIFIG_MEMBER_SHARE_CODE).append(code).toString();
    }

    /**
     *  String    用户id 数据id 和 code 做绑定    String code 放入缓存
     * @return unifig:ums:member:share:userId:$userId:dataId:$dataId
     */
    public static String shareOrcodeCacheKey(String userId,String dataId) {
        return new StringBuffer().append(RedisConstants.UNIFIG_PATH_DEF).append(RedisConstants.UNIFIG_MEMBER_SHARE_ORCODE_DATA).append(dataId).append(RedisConstants.USER_ID).append(userId).toString();
    }

}
