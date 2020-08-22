package org.geekbang.time.commonmistakes.springpart1.beansingletonandorder;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * SayService 抽象类，有状态的[prototype]。
 */
@Slf4j
public abstract class SayService {
    /**
     * 其中维护了一个类型是 ArrayList 的字 段 data，用于保存方法处理的中间数据。
     * <p>
     * 每次调用 say 方法都会往 data 加入新数据，可 以认为 SayService 是有状态。
     * <p>
     * 如果 SayService 是单例的话必然会 OOM
     */
    List<String> data = new ArrayList<>();

    public void say() {
        data.add(IntStream.rangeClosed(1, 1000000)
                .mapToObj(__ -> "a")
                .collect(Collectors.joining("")) + UUID.randomUUID().toString());
        log.info("I'm {} size:{}", this, data.size());
    }
}
