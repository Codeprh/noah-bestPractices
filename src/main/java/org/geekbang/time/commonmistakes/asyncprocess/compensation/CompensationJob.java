package org.geekbang.time.commonmistakes.asyncprocess.compensation;

import jodd.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 全量补偿补偿Job
 * <p>
 * 生产级别的todo：
 * 1、考虑配置补偿的频次、每次处理数量，以及补偿线程池大小等参数为合适的值，以满足 补偿的吞吐量。
 * 2、考虑备线补偿数据进行适当延迟。比如，对注册时间在 30 秒之前的用户再进行补偿，以 方便和主线 MQ 实时流程错开，避免冲突。
 * 3、诸如当前补偿到哪个用户的 offset 数据，需要落地数据库。
 * 4、补偿 Job 本身需要高可用，可以使用类似 XXLJob 或 ElasticJob 等任务系统。
 */
@Component
@Slf4j
public class CompensationJob {

    /**
     * 补偿job异步线程池
     */
    private static ThreadPoolExecutor compensationThreadPool = new ThreadPoolExecutor(
            10, 10,
            1, TimeUnit.HOURS,
            new ArrayBlockingQueue<>(1000),
            new ThreadFactoryBuilder().setNameFormat("compensation-threadpool-%d").get());

    @Autowired
    private UserService userService;

    @Autowired
    private MemberService memberService;

    /**
     * 目前补偿到哪个用户id
     */
    private long offset = 0;

    /**
     * 系统启动后10s补偿，每隔5s补偿一次，每次补偿5个用户
     */
    @Scheduled(initialDelay = 10_000, fixedRate = 5_000)
    public void compensationJob() {

        log.info("开始从用户ID {} 补偿", offset);

        userService.getUsersAfterIdWithLimit(offset, 5).forEach(user -> {
            compensationThreadPool.execute(() -> memberService.welcome(user));
            offset = user.getId();
        });

    }
}
