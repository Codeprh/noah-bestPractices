package org.geekbang.time.commonmistakes.springpart1.beansingletonandorder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 模拟单例的bean注入有状态的bean
 * Bean 默认是单例的，所以单例的 Controller 注入的 Service 也是一次性创建的，即使 Service 本身标识了 prototype 的范围也没用。
 */
@Slf4j
@RestController
@RequestMapping("beansingletonandorder")
public class BeanSingletonAndOrderController {

    /**
     * 在为类标记上 @Service 注解把类型交由容器管理前，首先评估一下类是 否有状态，然后为 Bean 设置合适的 Scope。
     */
    @Autowired
    List<SayService> sayServiceList;

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("test")
    public void test() {
        log.info("====================");
        sayServiceList.forEach(SayService::say);
    }

    /**
     * 当然，如果不希望走代理的话还有一种方式是，每次直接从 ApplicationContext 中获取 Bean.
     */
    @GetMapping("test2")
    public void test2() {
        log.info("====================");
        applicationContext.getBeansOfType(SayService.class).values().forEach(SayService::say);
    }
}
