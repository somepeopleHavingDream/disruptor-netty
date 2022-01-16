package org.yangxin.disruptor.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import org.yangxin.disruptor.entity.TranslatorData;

/**
 * @author yangxin
 * 2022/1/16 16:28
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            TranslatorData response = (TranslatorData) msg;
            System.out.println("Client端：" + response);
        } finally {
            // 一定要注意，用完了缓存要进行释放
            ReferenceCountUtil.release(msg);
        }
    }
}
