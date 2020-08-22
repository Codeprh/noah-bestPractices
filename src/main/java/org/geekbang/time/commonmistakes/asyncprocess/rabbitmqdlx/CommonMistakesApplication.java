package org.geekbang.time.commonmistakes.asyncprocess.rabbitmqdlx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RabbitMQ 2.8.0,自定义的死信队列处理器
 */
@SpringBootApplication
public class CommonMistakesApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommonMistakesApplication.class, args);
    }
}

