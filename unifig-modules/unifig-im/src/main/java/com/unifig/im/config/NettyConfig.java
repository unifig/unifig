package com.unifig.im.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Netty配置
 * <p>
 *
 *   contact by kaixin@254370777.com
 * @date 2018/6/28 - 上午10:17
 */
@Configuration
@Data
public class NettyConfig {

    /**
     * WebSocket-netty Server port
     */
    @Value("${netty.web.socket.port}")
    private int port;

    /**
     * WebSocket Url
     */
    @Value("${netty.web.socket.url}")
    private String url;
}
