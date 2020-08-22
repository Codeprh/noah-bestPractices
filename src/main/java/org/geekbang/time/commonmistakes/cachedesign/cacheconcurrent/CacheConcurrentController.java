package org.geekbang.time.commonmistakes.cachedesign.cacheconcurrent;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequestMapping("cacheconcurrent")
@RestController
public class CacheConcurrentController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private AtomicInteger atomicInteger = new AtomicInteger();
    @Autowired
    private RedissonClient redissonClient;

    @PostConstruct
    public void init() {

        //初始化一个热点数据到Redis中，过期时间设置为5秒
        stringRedisTemplate.opsForValue().set("hotsopt", getExpensiveData(), 5, TimeUnit.SECONDS);

        //每隔1秒输出一下回源的QPS
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            log.info("DB QPS : {}", atomicInteger.getAndSet(0));
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * qps能达到10，产生了回源并发问题
     *
     * @return
     */
    @GetMapping("wrong")
    public String wrong() {

        String data = stringRedisTemplate.opsForValue().get("hotsopt");

        if (StringUtils.isEmpty(data)) {

            data = getExpensiveData();
            stringRedisTemplate.opsForValue().set("hotsopt", data, 5, TimeUnit.SECONDS);
        }
        return data;
    }

    /**
     * 缓存击穿（缓存并发问题，）
     * 分布式锁：解决回源并发问题，如果回源的成本很高，这是不能忽略的问题。
     * 方案1：分布式锁+双重检查限制了全局的并发只有一个。
     * <p>
     * 在真实的业务场景下，不一定要这么严格地使用双重检查分布式锁进行全局的并发限制，因 为这样虽然可以把数据库回源并发降到最低，但也限制了缓存失效时的并发。可以考虑的方 式是:
     * <p>
     * 1、使用进程内的锁进行限制，这样每一个节点都可以以一个并发回源数据库;
     * 2、todo:不使用锁进行限制，而是使用类似 Semaphore 的工具限制并发数，比如限制 为 10，这样既限制了回源并发数不至于太大，又能使得一定量的线程可以同时回源
     *
     * @return
     */
    @GetMapping("right")
    public String right() {

        String data = stringRedisTemplate.opsForValue().get("hotsopt");

        if (StringUtils.isEmpty(data)) {

            RLock locker = redissonClient.getLock("locker");

            if (locker.tryLock()) {

                try {
                    //双重检查，因为可能已经有一个B线程过了第一次判断，在等锁，然后A线程已经把结果返回了
                    data = stringRedisTemplate.opsForValue().get("hotsopt");
                    if (StringUtils.isEmpty(data)) {
                        data = getExpensiveData();
                        stringRedisTemplate.opsForValue().set("hotsopt", data, 5, TimeUnit.SECONDS);
                    }
                } finally {
                    locker.unlock();
                }
            }
        }
        return data;
    }

    private String getExpensiveData() {
        atomicInteger.incrementAndGet();
        return "important data";
    }
}
