package com.unifig.organ.meta;

import com.unifig.utils.ResourceUtil;

/**
 * <p>
 * ${todo}
 * </p>
 *
 *
 * @date 2018.11.29
 */
public class OAuthWebChatMeta {

    public OAuthWebChatMeta() {
    }

    public OAuthWebChatMeta(String appid, String secret, String grantType, String url) {

        this.appid = ResourceUtil.getConfigByName("wx.appId");
        this.secret = ResourceUtil.getConfigByName("wx.secret");
        this.grantType = ResourceUtil.getConfigByName("wx.grantType");
        this.url = ResourceUtil.getConfigByName("wx.url");
    }

    private String appid;

    private String secret;

    private String grantType;

    private String url;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
