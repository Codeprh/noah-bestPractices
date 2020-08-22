package org.geekbang.time.commonmistakes.springpart2.aopfeign;

import org.geekbang.time.commonmistakes.common.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableMethodCache(basePackages = "org.geekbang.time.commonmistakes.springpart2.aopfeign")
//@EnableCreateCacheAnnotation
public class CommonMistakesApplication {

    public static void main(String[] args) {
        Utils.loadPropertySource(CommonMistakesApplication.class, "feign.properties");
        SpringApplication.run(CommonMistakesApplication.class, args);
    }
}

