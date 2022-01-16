package org.yangxin.disruptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yangxin.disruptor.client.NettyClient;

/**
 * @author yangxin
 * 2022/1/16 16:39
 */
@SpringBootApplication
public class NettyClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyClientApplication.class, args);

        // 建立连接，并发送消息
        new NettyClient().sendData();
    }
}
