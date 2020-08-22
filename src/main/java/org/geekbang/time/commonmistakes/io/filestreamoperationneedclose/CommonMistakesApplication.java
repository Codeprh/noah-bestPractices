package org.geekbang.time.commonmistakes.io.filestreamoperationneedclose;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * 使用Files类静态方法进行文件操作注意释放文件句柄：filestreamoperationneedclose
 */
@Slf4j
public class CommonMistakesApplication {

    public static void main(String[] args) throws IOException {
        //init();
        //readLargeFileRight();
        //readLargeFileWrong();
        //linesTest();
        //wrong();
        right();
    }

    /**
     * {@link Files#readAllLines(Path)}是读取整个文件
     *
     * @throws IOException
     */
    private static void readLargeFileWrong() throws IOException {
        log.info("lines {}", Files.readAllLines(Paths.get("large.txt")).size());
    }

    /**
     * {@link Files#lines(Path)} }是一行一行来读取
     * <p>
     * 使用Files工具类正确姿势读取大文件
     *
     * @throws IOException
     */
    private static void readLargeFileRight() throws IOException {
        AtomicLong atomicLong = new AtomicLong();
        Files.lines(Paths.get("large.txt")).forEach(line -> atomicLong.incrementAndGet());
        log.info("lines {}", atomicLong.get());
    }

    private static void linesTest() throws IOException {

        StopWatch stopWatch = new StopWatch();
        //1.4s
        stopWatch.start("read 200000 lines");
        //log.info("lines {}", Files.lines(Paths.get("large.txt")).limit(200000).collect(Collectors.toList()).size());
        stopWatch.stop();

        stopWatch.start("read 2000000 lines");
        log.info("lines {}", Files.lines(Paths.get("large.txt")).limit(2000000).collect(Collectors.toList()).size());
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
    }

    /**
     * 初始化数据
     *
     * @throws IOException
     */
    private static void init() throws IOException {

        //我们尝试读取一个 1 亿 1 万行的文件，文件占用磁盘 空间超过 4GB
        String payload = IntStream.rangeClosed(1, 1000)
                .mapToObj(i -> "a")
                .collect(Collectors.joining("")) + UUID.randomUUID().toString();

        Files.deleteIfExists(Paths.get("large.txt"));
        IntStream.rangeClosed(1, 10).forEach(index -> {
            try {
                Files.write(Paths.get("large.txt"),
                        IntStream.rangeClosed(1, 500000).mapToObj(i -> payload).collect(Collectors.toList())
                        , UTF_8, CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Files.write(Paths.get("demo.txt"),
                IntStream.rangeClosed(1, 10).mapToObj(i -> UUID.randomUUID().toString()).collect(Collectors.toList())
                , UTF_8, CREATE, TRUNCATE_EXISTING);
    }

    private static void wrong() {
        //ps aux | grep CommonMistakesApplication
        //lsof -p 63937
        LongAdder longAdder = new LongAdder();
        IntStream.rangeClosed(1, 1000000).forEach(i -> {

            try {
                Thread.sleep(5000);
                Files.lines(Paths.get("demo.txt")).forEach(line -> longAdder.increment());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        log.info("total : {}", longAdder.longValue());
    }

    private static void right() {
        //https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html
        LongAdder longAdder = new LongAdder();
        IntStream.rangeClosed(1, 1000000).forEach(i -> {
            try (Stream<String> lines = Files.lines(Paths.get("demo.txt"))) {
                lines.forEach(line -> longAdder.increment());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        log.info("total : {}", longAdder.longValue());
    }
}

