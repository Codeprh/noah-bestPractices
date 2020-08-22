package org.geekbang.time.commonmistakes.asyncprocess.fanoutvswork;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 为了代码简洁直观，我们把消息发布者、消费者、以及MQ的配置代码都放在了一起.
 * <p>
 * 错误栗子：同一个会员服务两个实例都收到了消息：出现这个问题的原因是，我们没有理清楚 RabbitMQ 直接交换器和队列的绑定关系。
 */
@Slf4j
//@Configuration
//@RestController
@RequestMapping("workqueuewrong")
public class WorkQueueWrong {

    private static final String EXCHANGE = "newuserExchange";
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping
    public void sendMessage() {
        rabbitTemplate.convertAndSend(EXCHANGE, "", UUID.randomUUID().toString());
    }

    /**
     * 使用匿名队列作为消息队列,导致2个会员服务实例监听，不同的两个队列
     *
     * @return
     */
    @Bean
    public Queue queue() {
        return new AnonymousQueue();
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
                BindingBuilder.bind(queue()).to(exchange).with(""));
    }

    /**
     * 监听队列，队列名称直接通过SpEL表达式引用Bean
     *
     * @param userName
     */
    @RabbitListener(queues = "#{queue.name}")
    public void memberService(String userName) {
        log.info("memberService: welcome message sent to new user {} from {}", userName, System.getProperty("server.port"));

    }
}
