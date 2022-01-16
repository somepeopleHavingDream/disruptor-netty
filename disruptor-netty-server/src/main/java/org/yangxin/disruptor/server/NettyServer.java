package org.yangxin.disruptor.server;

import io.netty.channel.*;
import org.yangxin.disruptor.codec.MarshallingCodecFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author yangxin
 * 2022/1/13 22:26
 */
@SuppressWarnings("deprecation")
public class NettyServer {

    public NettyServer() {
        // 创建两个工作线程组，一个用于接收网络请求请求的线程组，另一个用于实际处理业务的线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        // 辅助类
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // 标识缓存动态调配（自适应）
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    // 缓存区，池化操作
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(MarshallingCodecFactory.buildMarshallingDecoder())
                                    .addLast(new ServerHandler())
                                    .addLast(MarshallingCodecFactory.buildMarshallingEncoder());
                        }
                    });

            // 绑定端口，同步等待请求连接
            ChannelFuture future = bootstrap.bind(8765).sync();
            System.out.println("Server startup...");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅停机
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            System.out.println("Server shutdown...");
        }
    }
}
