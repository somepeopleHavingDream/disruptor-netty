package org.yangxin.disruptor.disruptor;

import com.lmax.disruptor.RingBuffer;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.yangxin.disruptor.entity.TranslatorData;
import org.yangxin.disruptor.entity.TranslatorDataWrapper;

/**
 * @author yangxin
 * 2022/1/17 20:31
 */
@NoArgsConstructor
@AllArgsConstructor
public class MessageProducer {

    private String producerId;

    private RingBuffer<TranslatorDataWrapper> ringBuffer;

    public void onData(TranslatorData data, ChannelHandlerContext channelHandlerContext) {
        long sequence = ringBuffer.next();
        try {
            TranslatorDataWrapper wrapper = ringBuffer.get(sequence);
            wrapper.setData(data);
            wrapper.setChannelHandlerContext(channelHandlerContext);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}
