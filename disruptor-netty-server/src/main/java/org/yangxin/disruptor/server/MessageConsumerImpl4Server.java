package org.yangxin.disruptor.server;

import io.netty.channel.ChannelHandlerContext;
import org.yangxin.disruptor.disruptor.AbstractMessageConsumer;
import org.yangxin.disruptor.entity.TranslatorData;
import org.yangxin.disruptor.entity.TranslatorDataWrapper;

/**
 * @author yangxin
 * 2022/1/17 21:20
 */
public class MessageConsumerImpl4Server extends AbstractMessageConsumer {

    public MessageConsumerImpl4Server(String consumerId) {
        super(consumerId);
    }

    @Override
    public void onEvent(TranslatorDataWrapper event) {
        TranslatorData request = event.getData();
        ChannelHandlerContext channelHandlerContext = event.getChannelHandlerContext();

        // 业务处理逻辑
        System.out.println("Server端：" + request);

        // 回送响应信息
        TranslatorData response = TranslatorData.builder()
                .id(request.getId())
                .name(request.getName())
                .message(request.getMessage())
                .build();

        // 写出response响应信息
        channelHandlerContext.writeAndFlush(response);
    }
}
