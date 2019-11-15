package com.unifig.context;

/**
 *
 * @create 2018-11-14
 */
public class RedisConstants {
    /**
     * redis ratel 统一路径
     */
    public static final String RATEL_PATH_DEF = "ratel:";

    /**
     * redis UNIFIG 统一路径
     */
    public static final String UNIFIG_PATH_DEF = "unifig:";

    /**
     * hash
     * ratel:jwt:admin:user:$userId
     */
    public static final String RATEL_JWT_ADMIN_USER_KAY = "jwt:admin:user:";
    /**
     * strings
     * ratel:jwt:admin:token:user:$userId
     */
    public static final String RATEL_JWT_ADMIN_TOKEN_USER_KAY = "jwt:admin:token:user:";
    /**
     * hash
     * ratel:jwt:plat:user:$userId
     */
    public static final String RATEL_JWT_PLAT_USER_KAY = "jwt:plat:user:";
    /**
     * strings
     * ratel:jwt:plat:token:user:$userId
     */
    public static final String RATEL_JWT_PLAT_TOKEN_USER_KAY = "jwt:plat:token:user:";

    /**
     * hash
     * ratel:ums:member:integration:rule:setting:
     */
    public static final String RATEL_MEMBER_INTG_RULE_SETTING = "ums:member:integration:rule:setting:";

    /**
     * hash
     * unifig:ums:member:share:code:
     */
    public static final String UNIFIG_MEMBER_SHARE_CODE = "ums:member:share:code:";

    /**
     * String
     * unifig:ums:member:share:orcode:data:
     */
    public static final String UNIFIG_MEMBER_SHARE_ORCODE_DATA = "ums:member:share:orcode:data:";


    /**
     * static
     * uuid
     */
    public static final String UUID = "uuid:";
    /**
     * userID
     */
    public static final String USER_ID = "userId:";


}
