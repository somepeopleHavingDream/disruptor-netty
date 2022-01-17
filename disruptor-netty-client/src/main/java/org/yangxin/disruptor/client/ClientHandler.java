package org.yangxin.disruptor.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.yangxin.disruptor.disruptor.MessageProducer;
import org.yangxin.disruptor.disruptor.RingBufferWorkerPoolFactory;
import org.yangxin.disruptor.entity.TranslatorData;

/**
 * @author yangxin
 * 2022/1/16 16:28
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        TranslatorData response = (TranslatorData) msg;

        String producerId = "code:sessionId:002";
        MessageProducer producer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);
        producer.onData(response, ctx);
    }
}
