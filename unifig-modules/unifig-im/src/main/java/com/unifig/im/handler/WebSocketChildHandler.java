package com.unifig.im.handler;

import com.unifig.im.constant.HttpObjectConstant;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * webSocketChildHandler
 * <p>
 *
 *   contact by kaixin254370777@163.com
 * @date 2018/6/28 - 下午5:14
 */
public class WebSocketChildHandler extends ChannelInitializer<SocketChannel> {

    /**
     * WebSocketUrl
     */
    private String webSocketUrl;

    /**
     * 构造方法，传递参数
     *
     * @param webSocketUrl webSocketUrl
     */
    public WebSocketChildHandler(String webSocketUrl) {
        this.webSocketUrl = webSocketUrl;
    }

    /**
     * 初始化Channel
     *
     * @param socketChannel socketChannel
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 将请求与应答消息编码或者解码为HTTP消息
        pipeline.addLast("http-codec", new HttpServerCodec());
        // 将http消息的多个部分组合成一条完整的HTTP消息
        pipeline.addLast("aggregator", new HttpObjectAggregator(HttpObjectConstant.MAX_CONTENT_LENGTH));
        // 向客户端发送HTML5文件。主要用于支持浏览器和服务端进行WebSocket通信
        pipeline.addLast("http-chunked", new ChunkedWriteHandler());
        // 服务端Handler
        pipeline.addLast("handler", new WebSocketServerHandler(webSocketUrl));

    }
}
