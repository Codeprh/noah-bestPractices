package org.geekbang.time.commonmistakes.asyncprocess.deadletter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

/**
 * 死信MQ定义
 */
@Configuration
@Slf4j
public class RabbitConfiguration {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 快速声明一组对象，包含队列、交换器，以及队列到交换器的绑定
     *
     * @return
     */
    @Bean
    public Declarables declarables() {
        Queue queue = new Queue(Consts.QUEUE);
        DirectExchange directExchange = new DirectExchange(Consts.EXCHANGE);
        return new Declarables(queue, directExchange,
                BindingBuilder.bind(queue).to(directExchange).with(Consts.ROUTING_KEY));
    }

    /**
     * 定义死信交换器和队列，并且进行绑定
     *
     * @return
     */
    @Bean
    public Declarables declarablesForDead() {
        Queue queue = new Queue(Consts.DEAD_QUEUE);
        DirectExchange directExchange = new DirectExchange(Consts.DEAD_EXCHANGE);
        return new Declarables(queue, directExchange,
                BindingBuilder.bind(queue).to(directExchange).with(Consts.DEAD_ROUTING_KEY));
    }

    /**
     * 定义重试操作拦截器，对于同一条消息，能够先进行几次重试，解决因为网络问题导致 的偶发消息处理失败，如果还是不行的话，再把消息投递到专门的一个死信队列。
     *
     * @return
     */
    @Bean
    public RetryOperationsInterceptor interceptor() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(5)
                .backOffOptions(1000, 2.0, 10000)
                .recoverer(new RepublishMessageRecoverer(rabbitTemplate, Consts.DEAD_EXCHANGE, Consts.DEAD_ROUTING_KEY))
                .build();
    }

    /**
     * 通过定义SimpleRabbitListenerContainerFactory，设置其adviceChain属性为之前定义的RetryOperationsInterceptor
     * <p>
     * 默认情况下 SimpleMessageListenerContainer 只有一个消费线程。只有等msg1消费完成之后，msg2才开始消费
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAdviceChain(interceptor());
        factory.setConcurrentConsumers(10);
        return factory;
    }
}
