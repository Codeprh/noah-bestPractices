package org.geekbang.time.commonmistakes.exception.threadpoolandexception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * 描述:
 * noah测试控制器
 *
 * @author Noah
 * @create 2020-04-29 09:32
 */
@RestController
@Slf4j
@RequestMapping(value = "/noahTest")
public class NoahTestController {

    @Autowired
    NoahTestController other;

    @GetMapping(value = "/common")
    public void common() {

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        IntStream.rangeClosed(1, 10).forEach(i -> executorService.execute(() -> {
            if (i == 5) {
                throw new RuntimeException("error");
            }
            log.info("i am done:{}", i);
        }));
    }


}
