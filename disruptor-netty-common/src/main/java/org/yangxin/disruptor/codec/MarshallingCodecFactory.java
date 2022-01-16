package org.yangxin.disruptor.codec;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 * Marshalling工厂
 *
 * @author yangxin
 * 2022/1/16 15:50
 */
public class MarshallingCodecFactory {

    /**
     * 创建Jboss Marshalling解码器MarshallingDecoder
     *
     * @return MarshallingDecoder
     */
    public static MarshallingDecoder buildMarshallingDecoder() {
        // 首先通过Marshalling工具类的精通方法获取Marshalling实例对象，参数serial标识创建的是java序列化工厂对象
        final MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");

        // 创建MarshallingConfiguration对象，配置了版本号为5
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);

        // 根据marshallerFactory和configuration创建provider
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(factory, configuration);

        // 构建Netty的MarshallingDecoder对象，两个参数分别为provider和单个消息序列化后的最大长度
        return new MarshallingDecoder(provider, 1024 * 1024);
    }

    /**
     * 创建Jboss Marshalling编码器MarshallingEncoder
     *
     * @return MarshallingEncoder
     */
    public static MarshallingEncoder buildMarshallingEncoder() {
        final MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");

        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);

        MarshallerProvider provider = new DefaultMarshallerProvider(factory, configuration);

        // 构建Netty的Marshalling的Encoder对象，MarshallingEncoder用于实现序列化接口的pojo对象序列化为二进制数组
        return new MarshallingEncoder(provider);
    }
}
