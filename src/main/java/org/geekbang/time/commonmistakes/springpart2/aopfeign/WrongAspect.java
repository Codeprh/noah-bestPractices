package org.geekbang.time.commonmistakes.springpart2.aopfeign;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 正确切面定义：表达式声明的是切入 feign.Client 的实现类。
 */
@Aspect
@Slf4j
@Component
public class WrongAspect {
    /**
     * 运行日志：within(feign.Client+) pjp execution(Response org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient.execute(Request,Options)), args:[GET http://client/feignaop/server HTTP/1.1
     * 切入点是：org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient.execute(Request,Options))
     *
     * @param pjp
     */
    @Before("within(feign.Client+)")
    public void before(JoinPoint pjp) {
        log.info("within(feign.Client+) pjp {}, args:{}", pjp, pjp.getArgs());
    }

}
