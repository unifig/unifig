package com.unifig.organ.config;

import com.unifig.organ.meta.OAuthWebChatMeta;
import com.unifig.organ.utils.ResourceUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 授权信息配置类，应用id,secret  etc.
 * </p>
 *
 *
 * @date 2018.11.29
 */
@Configuration
public class OAuthConfig {


    @Bean
    public OAuthWebChatMeta oAuthWebChatMeta() {
        OAuthWebChatMeta oAuthWebChatMeta = new OAuthWebChatMeta(ResourceUtil.getConfigByName("wx.appId"), ResourceUtil.getConfigByName("wx.secret"), ResourceUtil.getConfigByName("wx.grantType"), ResourceUtil.getConfigByName("web_chat.url")
        );
        return oAuthWebChatMeta;
    }

}
