package org.geekbang.time.commonmistakes.asyncprocess.fanoutvswork;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

/**
 * rabbitmq实现工作队列模式，一个服务，多个实例，只有一个实例消费MQ信息。
 * <p>
 * 1、为了代码简洁直观，我们把消息发布者、消费者、以及MQ的配置代码都放在了一起.
 * 2、同一个会员服务两个实例都收到了消息：出现这个问题的原因是，我们没有理清楚 RabbitMQ 直接交换器和队列的绑定关系。
 * 3、轮训方式负载均衡
 */
@Slf4j
//@Configuration
//@RestController
@RequestMapping("workqueueright")
public class WorkQueueRight {

    private static final String EXCHANGE = "newuserExchange";
    private static final String QUEUE = "newuserQueue";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping
    public void sendMessage() {
        rabbitTemplate.convertAndSend(EXCHANGE, "test", UUID.randomUUID().toString());
    }

    /**
     * 定义相同队列名称的队列，两个实例绑定同一个队列
     *
     * @return
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE);
    }

    /**
     * 声明DirectExchange交换器，绑定队列到交换器
     *
     * @return
     */
    @Bean
    public Declarables declarables() {
        DirectExchange exchange = new DirectExchange(EXCHANGE);
        return new Declarables(queue(), exchange,
                BindingBuilder.bind(queue()).to(exchange).with("test"));

    }

    /**
     * mq消息，消费者
     *
     * @param userName
     */
    @RabbitListener(queues = "#{queue.name}")
    public void memberService(String userName) {
        log.info("memberService: welcome message sent to new user {}", userName);

    }
}
