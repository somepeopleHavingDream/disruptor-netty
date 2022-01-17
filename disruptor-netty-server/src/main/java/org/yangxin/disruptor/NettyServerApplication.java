package org.yangxin.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yangxin.disruptor.disruptor.AbstractMessageConsumer;
import org.yangxin.disruptor.disruptor.RingBufferWorkerPoolFactory;
import org.yangxin.disruptor.server.MessageConsumerImpl4Server;
import org.yangxin.disruptor.server.NettyServer;

/**
 * @author yangxin
 * 2022/1/16 16:43
 */
@SpringBootApplication
public class NettyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyServerApplication.class, args);

        AbstractMessageConsumer[] consumers = new AbstractMessageConsumer[4];
        for (int i = 0; i < consumers.length; i++) {
            AbstractMessageConsumer consumer = new MessageConsumerImpl4Server("code:serverId:" + i);
            consumers[i] = consumer;
        }

        RingBufferWorkerPoolFactory.getInstance().initAndStart(ProducerType.MULTI,
                1024*1024,
//                new YieldingWaitStrategy(),
                new BlockingWaitStrategy(),
                consumers);

        new NettyServer();
    }
}
