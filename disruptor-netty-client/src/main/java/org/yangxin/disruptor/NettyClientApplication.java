package org.yangxin.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yangxin.disruptor.client.MessageConsumerImpl4Client;
import org.yangxin.disruptor.client.NettyClient;
import org.yangxin.disruptor.disruptor.AbstractMessageConsumer;
import org.yangxin.disruptor.disruptor.RingBufferWorkerPoolFactory;

/**
 * @author yangxin
 * 2022/1/16 16:39
 */
@SpringBootApplication
public class NettyClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyClientApplication.class, args);

        AbstractMessageConsumer[] consumers = new AbstractMessageConsumer[4];
        for (int i = 0; i < consumers.length; i++) {
            AbstractMessageConsumer consumer = new MessageConsumerImpl4Client("code:clientId:" + i);
            consumers[i] = consumer;
        }

        RingBufferWorkerPoolFactory.getInstance().initAndStart(ProducerType.MULTI,
                1024*1024,
//                new YieldingWaitStrategy(),
                new BlockingWaitStrategy(),
                consumers);

        // 建立连接，并发送消息
        new NettyClient().sendData();
    }
}
