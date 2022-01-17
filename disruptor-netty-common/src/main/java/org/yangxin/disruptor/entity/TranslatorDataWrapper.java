package org.yangxin.disruptor.entity;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * @author yangxin
 * 2022/1/17 20:39
 */
@Data
public class TranslatorDataWrapper {

    private TranslatorData data;

    private ChannelHandlerContext channelHandlerContext;
}
