package org.yangxin.disruptor.server;

import org.yangxin.disruptor.entity.TranslatorData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author yangxin
 * 2022/01/16 15:40
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        TranslatorData request = (TranslatorData) msg;
        System.out.println("Server端：" + request);

        final String responsePrefix = "response: ";
        TranslatorData response = TranslatorData.builder()
                .id(responsePrefix + request.getId())
                .name(responsePrefix + request.getName())
                .message(responsePrefix + request.getMessage())
                .build();

        ctx.writeAndFlush(response);
    }
}
