package org.yangxin.disruptor.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.yangxin.disruptor.disruptor.AbstractMessageConsumer;
import org.yangxin.disruptor.entity.TranslatorData;
import org.yangxin.disruptor.entity.TranslatorDataWrapper;

/**
 * @author yangxin
 * 2022/1/17 21:28
 */
@SuppressWarnings("unused")
public class MessageConsumerImpl4Client extends AbstractMessageConsumer {

    public MessageConsumerImpl4Client(String consumerId) {
        super(consumerId);
    }

    @Override
    public void onEvent(TranslatorDataWrapper event) {
        TranslatorData response = event.getData();
        ChannelHandlerContext channelHandlerContext = event.getChannelHandlerContext();

        // 业务逻辑处理
        try {
            System.out.println("Client端：" + response);
        } finally {
            ReferenceCountUtil.release(response);
        }
    }
}
