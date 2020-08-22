package org.geekbang.time.commonmistakes.cachedesign.cacheinvalid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Slf4j
@RequestMapping("cacheinvalid")
@RestController
public class CacheInvalidController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private AtomicInteger atomicInteger = new AtomicInteger();

    /**
     * 产生缓存雪崩：大量的key在同一时间过期，请求流量到db去了
     */
    //@PostConstruct
    public void wrongInit() {

        IntStream.rangeClosed(1, 1000).forEach(i -> stringRedisTemplate.opsForValue().set("city" + i, getCityFromDb(i), 30, TimeUnit.SECONDS));
        log.info("Cache init finished");

        //每秒一次，输出数据库访问的QPS
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            log.info("DB QPS : {}", atomicInteger.getAndSet(0));
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * 缓存雪崩解决方案1：ThreadLocalRandom.current().nextInt(10)，增加随机休眠时间
     */
    @PostConstruct
    public void rightInit1() {

        IntStream.rangeClosed(1, 1000).forEach(i -> stringRedisTemplate.opsForValue().set("city" + i, getCityFromDb(i), 30 + ThreadLocalRandom.current().nextInt(10), TimeUnit.SECONDS));

        log.info("Cache init finished");

        //每秒一次，输出数据库访问的QPS
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            log.info("DB QPS : {}", atomicInteger.getAndSet(0));
        }, 0, 1, TimeUnit.SECONDS);

    }

    /**
     * 让缓存不主动过期。初始化缓存数据的时候设置缓存永不过期，然后启动一个后台 线程 30 秒一次定时把所有数据更新到缓存，而且通过适当的休眠.
     * DB QPS : 37
     * DB QPS : 37
     * DB QPS : 35
     * DB QPS : 36
     * DB QPS : 38
     * DB QPS : 40
     * DB QPS : 40
     *
     * @throws InterruptedException
     */
    //@PostConstruct
    public void rightInit2() throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        //模拟每30s，更新数据库
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {

            IntStream.rangeClosed(1, 1000).forEach(i -> {

                String data = getCityFromDb(i);
                try {
                    TimeUnit.MILLISECONDS.sleep(20);
                } catch (InterruptedException e) {
                }
                if (!StringUtils.isEmpty(data)) {
                    stringRedisTemplate.opsForValue().set("city" + i, data);
                }
            });
            log.info("Cache update finished");
            //启动程序的时候需要等待首次更新缓存完成
            countDownLatch.countDown();
        }, 0, 30, TimeUnit.SECONDS);

        //每秒一次，输出数据库访问的QPS
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            log.info("DB QPS : {}", atomicInteger.getAndSet(0));
        }, 0, 1, TimeUnit.SECONDS);

        countDownLatch.await();
    }

    @GetMapping("city")
    public String city() {

        int id = ThreadLocalRandom.current().nextInt(1000) + 1;

        String key = "city" + id;
        String data = stringRedisTemplate.opsForValue().get(key);

        if (data == null) {

            data = getCityFromDb(id);

            if (!StringUtils.isEmpty(data))
                stringRedisTemplate.opsForValue().set(key, data, 30, TimeUnit.SECONDS);
        }

        return data;
    }


    private String getCityFromDb(int cityId) {
        atomicInteger.incrementAndGet();
        return "citydata" + System.currentTimeMillis();
    }
}
