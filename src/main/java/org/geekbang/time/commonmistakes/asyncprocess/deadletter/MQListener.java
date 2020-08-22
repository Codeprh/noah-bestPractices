package org.geekbang.time.commonmistakes.asyncprocess.deadletter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * MQ消息消费者
 */
@Component
@Slf4j
public class MQListener {

    /**
     * 模拟MQ消费信息出现异常的情况：
     * <p>
     * 解决方案1：解决死信无限重复进入队列最简单的方式是，在程序处理出错的时候，直接抛出 {@link AmqpRejectAndDontRequeueException}异常
     * 解决方案2：对于同一条消息，能够先进行几次重试，解决因为网络问题导致 的偶发消息处理失败，如果还是不行的话，再把消息投递到专门的一个死信队列。
     *
     * @param data
     */
    @RabbitListener(queues = Consts.QUEUE)
    public void handler(String data) {
        //http://localhost:15672/#/
        log.info("got message {}", data);
        throw new NullPointerException("error");
        //throw new AmqpRejectAndDontRequeueException("error");
    }

    /**
     * 死信队列处理程序
     *
     * @param data
     */
    @RabbitListener(queues = Consts.DEAD_QUEUE)
    public void deadHandler(String data) {
        log.error("got dead message {}", data);
    }
}
