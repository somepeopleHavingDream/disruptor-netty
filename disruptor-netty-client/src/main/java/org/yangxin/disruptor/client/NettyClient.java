package org.yangxin.disruptor.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.yangxin.disruptor.codec.MarshallingCodecFactory;
import org.yangxin.disruptor.entity.TranslatorData;

/**
 * @author yangxin
 * 2022/1/16 16:18
 */
@SuppressWarnings({"AlibabaUndefineMagicConstant", "SameParameterValue", "deprecation"})
public class NettyClient {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8765;

    private Channel channel;

    /**
     * 创建工作线程组，另一个用于实际处理业务的线程组
     */
    private final EventLoopGroup workGroup = new NioEventLoopGroup();

    private ChannelFuture channelFuture;

    public NettyClient() {
        connect(HOST, PORT);
    }

    private void connect(String host, int port) {
        // 辅助类（注意Client和Server不一样）
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    // 标识缓存动态调配（自适应）
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    // 缓存区，池化操作
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(MarshallingCodecFactory.buildMarshallingDecoder())
                                    .addLast(new ClientHandler())
                                    .addLast(MarshallingCodecFactory.buildMarshallingEncoder());
                        }
                    });

            // 绑定端口，同步等待请求连接
            this.channelFuture = bootstrap.connect(host, port).sync();
            System.out.println("Client connected...");

            // 接下来就进行数据的发送，但是首先我们要获取通道
            this.channel = channelFuture.channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendData() {
        for (int i = 0; i < 10; i++) {
            TranslatorData request = TranslatorData.builder()
                    .id("" + i)
                    .name("请求消息名称" + i)
                    .message("请求消息内容" + i)
                    .build();
            channel.writeAndFlush(request);
        }
    }

    public void close() throws InterruptedException {
        channelFuture.channel().closeFuture().sync();

        // 优雅停机
        workGroup.shutdownGracefully();
        System.out.println("Client shutdown...");
    }
}
