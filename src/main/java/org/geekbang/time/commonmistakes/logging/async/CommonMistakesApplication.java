package org.geekbang.time.commonmistakes.logging.async;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommonMistakesApplication {

    public static void main(String[] args) {
        System.setProperty("logging.config", "classpath:org/geekbang/time/commonmistakes/logging/async/performance_async.xml");
        SpringApplication.run(CommonMistakesApplication.class, args);
    }
}

