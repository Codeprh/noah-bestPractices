package org.geekbang.time.commonmistakes.springpart2.aopfeign;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 切面：表达式，切带有@FeignClietn注解的bean
 */
@Aspect
@Slf4j
@Component
public class Wrong2Aspect {

    /**
     * 运行日志
     * @within(org.springframework.cloud.openfeign.FeignClient) pjp execution(String org.geekbang.time.commonmistakes.springpart2.aopfeign.feign.ClientWithUrl.api()), args:[]
     * @param pjp
     */
    @Before("@within(org.springframework.cloud.openfeign.FeignClient)")
    public void before(JoinPoint pjp) {
        log.info("@within(org.springframework.cloud.openfeign.FeignClient) pjp {}, args:{}", pjp, pjp.getArgs());
    }
}
