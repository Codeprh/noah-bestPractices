package org.geekbang.time.commonmistakes.logging.async;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequestMapping("logging")
@RestController
public class LoggingController {

    @GetMapping("log")
    public void log() {
        log.debug("debug");
        log.info("info");
        log.warn("warn");
        log.error("error");
        IntStream.rangeClosed(1, 100).forEach(i -> log.info("{} ", i));
    }

    @GetMapping("manylog")
    public void manylog(@RequestParam(name = "count", defaultValue = "1000") int count) {
        long begin = System.currentTimeMillis();
        IntStream.rangeClosed(1, count).forEach(i -> log.info("log-{}", i));

        System.out.println("took " + (System.currentTimeMillis() - begin) + " ms");
        System.out.println("noah say hello");
        System.out.println("noah say took");

        //log.info("took " + (System.currentTimeMillis() - begin) + " ms");
    }

    @GetMapping("performance")
    public void performance(@RequestParam(name = "count", defaultValue = "100") int count) {

        long begin = System.currentTimeMillis();

        //生成1.9MB大小的字符串
        String payload = IntStream.rangeClosed(1, 1000000)
                .mapToObj(i -> "a")
                .collect(Collectors.joining("")) + UUID.randomUUID().toString();

        //for循环次数写入文件
        IntStream.rangeClosed(1, count).forEach(i -> log.info("{} {}", i, payload));

        Marker timeMarker = MarkerFactory.getMarker("time");

        log.info(timeMarker, "took {} ms", System.currentTimeMillis() - begin);
    }
}
