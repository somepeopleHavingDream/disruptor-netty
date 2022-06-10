# 高性能Java并发框架disruptor源码解析与实战
## 第1章 课程介绍
- Disruptor VS BlockingQueue

NUM|Disruptor|BlockingQueue|性能占比
-|-|-|-
1千万|984ms|2218ms|2.254
5千万|4498ms|10589ms|2.354
1亿|8229ms|21146ms|2.569
## 第2章 并发编程框架核心讲解
- Disruptor Quick Start
    - 它能够在一个线程里每秒处理6百万订单
    - 业务逻辑处理器完全是运行在内存中，使用事件源驱动方式
    - 业务逻辑处理器的核心是Disruptor
    - 建立一个工厂Event类，用于创建Event类实例对象
    - 需要有一个监听事件类，用于处理数据（Event类）
    - 实例化Disruptor实例，配置一系列参数，编写Disruptor核心组件
    - 编写生产者组件，向Disruptor容器中去投递数据
-  Disruptor 核心容器
    - 初看Disruptor，给人的印象就是RingBuffer是其核心，生产者向RingBuffer中写入元素，消费者从RingBuffer中消费元素
    - RingBuffer到底是啥？
    - 正如名字所说的一样，它是一个环（首尾相接的环）
    - 它用做在不同的上下文（线程）间传递数据的buffer！
    - RingBuffer拥有一个序号，这个序号指向数组中下一个可用元素
- 扔芝麻与捡芝麻的小故事
    - Disruptor说的是生产者和消费者的故事
    - 有一个数组：生产者往里面扔芝麻，消费者从里面捡芝麻
    - 但是捡芝麻和扔芝麻也要考虑速度的问题
    - 1 消费者捡的比扔的快，那么消费者要停下来，生产者扔了新的芝麻，然后消费者继续
    - 2 数组的长度是有限的，生产者到末尾的时候会再从数组的开始位置继续，这个时候可能会追上消费者，消费者还没从那个地方捡走芝麻，这个时候生产者要等待消费者捡走芝麻，然后继续
- RingBuffer数据结构深入探究
    - 随着你不停地填充这个buffer（可能也会有相应的读取），这个序号会一直增长，直到绕过这个环
    - 要找到数组中当前序号指向的元素，可以通过mod操作：sequence mod array length = array index（取模操作）
    - 如果槽的个数是2的N次方更有利于基于二进制的计算机进行计算
- Disruptor核心-RingBuffer
    - RingBuffer：基于数组的缓存实现，也是创建sequencer与定义WaitStrategy的入口
    - Disruptor：持有RingBuffer、消费者线程池Executor、消费者ConsumerRepository的引用
- Disruptor核心-Sequence
    - 通过顺序递增的序号来编号，管理进行交换的数据（事件）
    - 对数据（事件）的处理过程总是沿着序号逐个递增处理
    - 一个Sequence用于跟踪标识某个特定的事件处理者（RingBuffer/Producer/Consumer）的处理进度
    - Sequence可以看成是一个AtomicLong用于标识进度
    - 还有另外一个目的就是防止不同Sequence之间CPU缓存伪共享（Flase Sharing）的问题
- Disrutpr核心-Sequencer
    - Sequencer是Disruptor的真正核心
    - 此接口有两个实现类
        - SingleProducerSequencer
        - MultiProducerSequencer
    - 主要实现生产者和消费者之间快速、正确地传递数据的并发算法
- Disruptor核心-Sequence Barrier
    - 用于保持对RingBuffer的Main Published Sequence（Producer）和Consumer之间的平衡关系；Sequence Barrier还定义了决定Consumer是否还有可处理的事件的逻辑
- Disruptor-WaitStrategy
    - 决定一个消费者将如何等待生产者将Event置入Disruptor
    - 主要策略
        - BlockingWaitStrategy
            - 最低效的策略，但其对CPU的消耗最小，并且在各种不同部署环境中能提供更加一致的性能表现
        - SleepingWaitStrategy
            - 性能表现跟BlockingWaitStrategy差不多，对CPU的消耗也类似 ，但其对生产者线程的影响最小，适合用于异步日志类似的场景
        - YieldingWaitStrategy
            - 性能是最好的，适合用于低延迟的系统。在要求极高性能且事件处理线程数小于CPU逻辑核心数的场景中，推荐使用此策略；例如，CPU开启超线程的特性
- Disruptor核心-Event
    - Event：从生产者到消费者过程中所处理的数据单元
    - Disruptor中没有代码表示Event，因为它完全是由用户定义的
- Disruptor核心-EvnetProcessor
    - EventProcessor：主要事件循环，处理Disruptor中的Event，消费者的Sequence
    - 它有一个实现类是BatchEventProcessor，包含了event loop有效实现，并且将回调到一个EventHandler接口的实现对象
- Disruptor核心-EventHandler
    - EventHandler：由用户实现并且代表了Disruptor中的一个消费者的接口，也就是我们的消费者逻辑都需要写在这里
- Disruptor核心-WorkProcessor
    - WorkProcessor：确保每个sequence只被一个processor消费，在同一个WorkPool中处理多个WorkProcessor不会消费同样的sequence！
## 第3章 并发编程框架高级特性讲解
- Disruptor核心链路应用场景讲解
    - 啥是核心链路？
    - 核心链路的代码实现，往往业务逻辑非常复杂
    - 核心链路特点：至关重要且业务复杂，那么代码应该如何实现
        - 实现方式一：传统的完全解耦模式
        - 实现方式二：模板模式
            - 1 有限状态机框架，例如Spring-StateMachine！
            - 2 使用Disruptor
## 第4章 并发编程深入学习与面试精讲
## 第5章 并发编程框架底层源码深度分析
- Disruptor为何底层性能如此牛掰？
    - 数据结构层面：使用环形结构、数组、内存预加载
    - 使用单线程写方式、内存屏障
    - 消除伪共享（填充缓存行）
    - 序号栅栏和序号配合使用来消除锁和CAS
- 高性能之道-数据结构-内存预加载机制
    -  RingBuffer使用数组Object[] entries作为存储元素
- 高性能之道-内核-使用单线程写
    - Disruptor的RingBuffer，之所以可以做到完全无锁，也是因为“单线程写”，这是所有“前提的前提”
    - 离了这个前提条件，没有任何技术可以做到完全无锁
    - Redis、Netty等等高性能技术框架的设计都是这个核心思想
- 高性能之道-系统内存优化-内存屏障
    - 要正确的实现无锁，还需要另外一个关键技术：内存屏障
    - 对应到Java语言，就是valotile变量与happens before
    - 内存屏障-Linux的smp_wmb()/smp_rmb()
    - 系统内核：拿Linux的kfifo来举例：smp_wmb()，无论是底层的读写都是使用了Linux的smp_wmb
- 高性能之道-系统缓存优化-消除伪共享
    - 缓存系统中是以缓存行（cache line）为单位存储的
    - 缓存行是2的整数幂个连续字节，一般为32-256个字节
    - 最常见的缓存行大小是64个字节
    - 当多线程修改互相独立的变量时，如果这些变量共享同一个缓存行，就会无意中影响彼此的性能，这就是伪共享
- 高性能之道-算法优化-序号栅栏机制
    - 我们在生产者进行投递Event的时候，总是会使用：long sequence = ringBuffer.next()
    - Disruptor3.0中，序号栅栏SequenceBarrier和序号Sequence搭配使用，协调和管理消费者与生产者的工作节奏，避免了锁和CAS的使用
    - 在Disruptor3.0中，各个消费者和生产者持有自己的序号，这些序号的变化必须满足如下基本条件：
        - 消费者序号数值必须小于生产者序号数值
        - 消费者序号数值必须小于其前置（依赖关系）消费者的序号数值
        - 生产者序号数值不能大于消费者中最小的序号数值，以避免生产者速度过快，将还未来得及消费的消息覆盖
- WaitStrategy等待策略深度分析
    - Disruptor之所以说是高性能，其实也有一部分原因取决于它等待策略的实现：WaitStrategy接口
## 第6章 Netty整合并发编程框架Disruptor实战百万长链接服务构建
- 与Netty网络通信框架整合提升性能
    - 在使用Netty进行接收处理数据的时候，我们尽量都不要在工作线程上全编写自己的代码逻辑！
    - 我们需要利用异步的机制，比如使用线程池异步处理，如果使用线程池就意味着使用阻塞队列，这里可以替换为Disruptor提高性能
## 第7章 分布式统一ID生成服务架构设计
- 分布式统一ID生成策略抗压
    - 简单ID生成方式
        - 最简单的就是利用java.util.UUID工具类进行生成，ID没有排序策略，这种方式的问题就是比如我要查询一批数据，进行入库时间做数据排序的时候，只能够自己在表里设置一个create_time，给这个字段加索引然后进行排序
        - （雪花算法/数据库sequence序列，自增ID等）
    - 顺序ID生成方式
        - 我们通过代码，KeyUtil里生成的ID是有时间先后顺序的，我们可以使用ID天然进行排序，这做法比较好的就是没必要浪费一个索引字段了。从数据库角度来讲，一般能尽量减少索引，就减少索引。因为索引虽然可以提升查询性能，但也是需要 占用空间的，并且一张表的索引最好不要超过3个，所以在做索引优化的时候，往往也是要根据业务进行考量
    - 业务ID生成方式
        - 最好使用带有业务含义的ID生成策略，这种方式也在传统应用系统、特定的场景下非常的好用
    - 高并发下的统一ID生成策略服务
        - 如何解决ID生成在并发下的重复生成问题
        - 如何承载高并发ID生成的性能瓶颈问题（zookeeper和redis在高并发下不行，因为zookeeper有写性能瓶颈，redis在高并发下会失败重试影响性能）
    - 业界主流的分布式ID生成器的策略
        - 实现一：提前加载，也就是预加载的机制
            - 1 提前加载，也就是预加载的机制
            - 2 并发的获取，采用Disruptor框架去提升性能
        - 实现二：单点生成方式
            - 1 固定的一个机器结点来生成一个唯一的ID，好处是能做到全局唯一
            - 2 需要相应的业务规则拼接：机器码+时间戳+自增序列（避免NTP问题）
## 第8章 课程总结