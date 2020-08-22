package org.geekbang.time.commonmistakes.logging.placeholder;

import lombok.extern.log4j.Log4j2;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Log4j2
@RequestMapping("logging")
@RestController
public class LoggingController {

    /**
     * SLF4J 的{}占位符语法，到真正记录日志时才会获取实际参数， 因此解决了日志数据获取的性能问题。
     */
    @GetMapping
    public void index() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("debug1");

        log.debug("debug1:" + slowString("debug1"));
        stopWatch.stop();

        stopWatch.start("debug2");
        log.debug("debug2:{}", slowString("debug2"));
        stopWatch.stop();

        stopWatch.start("debug3");
        if (log.isDebugEnabled())
            log.debug("debug3:{}", slowString("debug3"));
        stopWatch.stop();

        stopWatch.start("debug4");
        log.debug("debug4:{}", () -> slowString("debug4"));
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String slowString(String s) {
        System.out.println("slowString called via " + s);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
        }
        return "OK";
    }
}
