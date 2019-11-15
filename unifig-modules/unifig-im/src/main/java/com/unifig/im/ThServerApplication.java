package com.unifig.im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 基于Netty开发的WebSocketServer端启动类
 *
 *   contact by kaixin@254370777.com
 * @date 2018/6/28 - 上午10:17
 */
@SpringBootApplication
public class ThServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThServerApplication.class, args);
    }

}
