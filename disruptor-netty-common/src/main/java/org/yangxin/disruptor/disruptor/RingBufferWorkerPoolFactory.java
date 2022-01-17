package org.yangxin.disruptor.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import org.yangxin.disruptor.entity.TranslatorDataWrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * @author yangxin
 * 2022/1/17 20:03
 */
@SuppressWarnings({"AlibabaThreadPoolCreation", "MismatchedQueryAndUpdateOfCollection"})
public class RingBufferWorkerPoolFactory {

    private static class SingletonHolder {

        private static final RingBufferWorkerPoolFactory INSTANCE = new RingBufferWorkerPoolFactory();
    }

    private RingBufferWorkerPoolFactory() {
    }

    public static RingBufferWorkerPoolFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final Map<String, MessageProducer> PRODUCERS = new ConcurrentHashMap<>();
    private static final Map<String, AbstractMessageConsumer> CONSUMERS = new ConcurrentHashMap<>();

    private RingBuffer<TranslatorDataWrapper> ringBuffer;

    public void initAndStart(ProducerType type,
                             int bufferSize,
                             WaitStrategy waitStrategy,
                             AbstractMessageConsumer[] messageConsumers) {
        // 构建ringBuffer对象
        this.ringBuffer = RingBuffer.create(type, TranslatorDataWrapper::new, bufferSize, waitStrategy);

        // 设置序号栅栏
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        // 设置工作池
        WorkerPool<TranslatorDataWrapper> workerPool = new WorkerPool<>(ringBuffer,
                sequenceBarrier,
                new EventExceptionHandler(),
                messageConsumers);

        // 把所构建的消费者置入池中
        for (AbstractMessageConsumer consumer : messageConsumers) {
            CONSUMERS.put(consumer.getConsumerId(), consumer);
        }

        // 添加我们的sequences
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());

        // 启动我们的工作池
        workerPool.start(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2));
    }

    public MessageProducer getMessageProducer(String producerId) {
        MessageProducer producer = PRODUCERS.get(producerId);
        if (producer == null) {
            producer = new MessageProducer(producerId, ringBuffer);
            PRODUCERS.put(producerId, producer);
        }

        return producer;
    }

    /**
     * 异常静态类
     *
     * @author yangxin
     * 2022/01/17 20:49
     */
    private static class EventExceptionHandler implements ExceptionHandler<TranslatorDataWrapper> {

        @Override
        public void handleEventException(Throwable ex, long sequence, TranslatorDataWrapper event) {
        }

        @Override
        public void handleOnStartException(Throwable ex) {
        }

        @Override
        public void handleOnShutdownException(Throwable ex) {
        }
    }
}
