package org.yangxin.disruptor.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.yangxin.disruptor.disruptor.MessageProducer;
import org.yangxin.disruptor.disruptor.RingBufferWorkerPoolFactory;
import org.yangxin.disruptor.entity.TranslatorData;

/**
 * @author yangxin
 * 2022/01/16 15:40
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        TranslatorData request = (TranslatorData) msg;

        // 自己的应用服务应该有一个Id生成规则
        String producerId = "code:sessionId:001";
        MessageProducer producer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);
        producer.onData(request, ctx);
    }
}
