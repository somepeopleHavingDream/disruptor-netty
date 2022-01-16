package org.yangxin.disruptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yangxin.disruptor.server.NettyServer;

/**
 * @author yangxin
 * 2022/1/16 16:43
 */
@SpringBootApplication
public class NettyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyServerApplication.class, args);

        new NettyServer();
    }
}
