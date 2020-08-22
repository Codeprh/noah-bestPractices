package org.geekbang.time.commonmistakes.asyncprocess.compensation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 会员服务：监听（消费）MQ信息
 */
@Component
@Slf4j
public class MemberService {

    /**
     * 幂等实现
     */
    private Map<Long, Boolean> welcomeStatus = new ConcurrentHashMap<>();

    /**
     * 监听MQ信息
     *
     * @param user
     */
    @RabbitListener(queues = RabbitConfiguration.QUEUE)
    public void listen(User user) {
        log.info("receive mq user {}", user.getId());
        welcome(user);
    }

    /**
     * 会员服务
     *
     * @param user
     */
    public void welcome(User user) {

        //不存在的用户，才执行方法里面的逻辑
        if (welcomeStatus.putIfAbsent(user.getId(), true) == null) {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
            }
            log.info("memberService: welcome new user {}", user.getId());
        }

    }

    /**
     * {@link ConcurrentHashMap}不允许key和value为Null
     * <p>
     * putIfAbsent，key存在的话，返回获取到的value值。
     * putIfAbsent，key不存在的话，返回null.
     */
    private void testPutIfAbsent() {
        Map<Long, Boolean> welcomeStatus = new ConcurrentHashMap<>();
        welcomeStatus.put(1L, true);

        if (welcomeStatus.putIfAbsent(2L, true) == null) {
            System.out.println("id=2被执行了");
        }

        if (welcomeStatus.putIfAbsent(1L, true) == null) {
            System.out.println("id=1被执行了");
        }
        System.out.println(welcomeStatus.toString());
    }
}
