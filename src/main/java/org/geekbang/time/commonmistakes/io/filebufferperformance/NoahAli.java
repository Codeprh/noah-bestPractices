package org.geekbang.time.commonmistakes.io.filebufferperformance;

import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import org.springframework.util.StopWatch;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * 描述:
 * 测试题目跑通程序【处理单个文件】
 *
 * @author Noah
 * @create 2020-05-14 18:20
 */
public class NoahAli {

    public static void main(String[] args) throws IOException, InterruptedException {
        handleV1(20);
    }

    /**
     * 第一版本：实现功能，查看数据结构和数据条数，根据tranceId写入到各个文件中
     * todo：
     * 1、不是读取一行，减少I/O
     * 2、多线程处理处理
     * 3、freqs.computeIfAbsent(key, k -> new LongAdder()).increment();
     * 4、map初始化容量
     *
     * @throws IOException
     */
    public static void handleV1(int threadCount) throws IOException, InterruptedException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("noahTest");

        String str = null;
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadCount);
        Map<String, LongAdder> map = new ConcurrentHashMap<>(10000 * 100);

        try (InputStream is = new FileInputStream("trace1.data");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            while ((str = reader.readLine()) != null) {

                String line = str;
                forkJoinPool.execute(() -> {

                    String[] split = line.split("\\|");

                    //当Key不存在的时候提供一个Function来代表根据Key获取Value的过程
                    map.computeIfAbsent(split[0], i -> new LongAdder()).increment();

                });
            }
        }

        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(10, TimeUnit.MINUTES);
        stopWatch.stop();

        map.entrySet().stream().limit(100).forEach(item -> {
            System.out.println("key=" + item.getKey() + ",value=" + item.getValue().longValue());
        });

        Long reduce = map.entrySet().stream().map(item -> item.getValue().longValue()).reduce(0L, Long::sum);

        System.out.println("统计行数=" + reduce);
        System.out.println(stopWatch.prettyPrint());
        System.out.println("对象占用大小=" + ObjectSizeCalculator.getObjectSize(map));

    }
}
