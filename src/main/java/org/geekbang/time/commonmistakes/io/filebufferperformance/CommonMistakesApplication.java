package org.geekbang.time.commonmistakes.io.filebufferperformance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.*;

@Slf4j
public class CommonMistakesApplication {

    public static void main(String[] args) throws IOException {

        StopWatch stopWatch = new StopWatch();
        init();
        stopWatch.start("perByteOperation");
        perByteOperation();
        stopWatch.stop();
        stopWatch.start("bufferOperationWith100Buffer");
        bufferOperationWith100Buffer();
        stopWatch.stop();
        stopWatch.start("bufferedStreamByteOperation");
        bufferedStreamByteOperation();
        stopWatch.stop();
        stopWatch.start("bufferedStreamBufferOperation");
        bufferedStreamBufferOperation();
        stopWatch.stop();
        stopWatch.start("largerBufferOperation");
        largerBufferOperation();
        stopWatch.stop();
        stopWatch.start("fileChannelOperation");
        fileChannelOperation();
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
    }

    /**
     * 创建一个文件随机写入 100 万行数据，文件大小在 35MB 左 右:
     */
    private static void init() throws IOException {

        Files.write(Paths.get("src.txt"),
                IntStream.rangeClosed(1, 1000000).mapToObj(i -> UUID.randomUUID().toString()).collect(Collectors.toList())
                , UTF_8, CREATE, TRUNCATE_EXISTING);
    }

    /**
     * 读一个字节，写入一个字节
     * <p>
     * 使用 FileInputStream 获得一个文件输入 流，然后调用其 read 方法每次读取一个字节，最后通过一个 FileOutputStream 文件输出 流把处理后的结果写入另一个文件
     * <p>
     * 显然，每读取一个字节、每写入一个字节都进行一次 IO 操作，代价太大了。
     * 复制一个 35MB 的文件居然耗时 190 秒
     *
     * @throws IOException
     */
    private static void perByteOperation() throws IOException {

        Files.deleteIfExists(Paths.get("dest.txt"));

        try (FileInputStream fileInputStream = new FileInputStream("src.txt");
             FileOutputStream fileOutputStream = new FileOutputStream("dest.txt")) {
            int i;
            while ((i = fileInputStream.read()) != -1) {
                fileOutputStream.write(i);
            }
        }
    }

    /**
     * 使用缓存区
     * 改良后，使用 100 字节作为缓冲区，使用 FileInputStream 的 byte[]的重载来一次性读取 一定字节的数据，
     * 同时使用 FileOutputStream 的 byte[]的重载实现一次性从缓冲区写入 一定字节的数据到文件:
     *
     * @throws IOException
     */
    private static void bufferOperationWith100Buffer() throws IOException {
        Files.deleteIfExists(Paths.get("dest.txt"));

        try (FileInputStream fileInputStream = new FileInputStream("src.txt");
             FileOutputStream fileOutputStream = new FileOutputStream("dest.txt")) {
            byte[] buffer = new byte[100];
            int len = 0;
            while ((len = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
        }
    }


    /**
     * 使用BufferedInputStream和BufferedOutputStream
     * 第一种方式虽然使用了缓冲流，但逐字节的操作因为方法调用次数实在太多还是 慢，耗时 1.4 秒
     *
     * @throws IOException
     */
    private static void bufferedStreamByteOperation() throws IOException {
        Files.deleteIfExists(Paths.get("dest.txt"));

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("src.txt"));
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("dest.txt"))) {
            int i;
            while ((i = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(i);
            }
        }
    }

    /**
     * 直接使用FileInputStream和FileOutputStream，再使用一个8KB的缓冲
     *
     * @throws IOException
     */
    private static void largerBufferOperation() throws IOException {
        Files.deleteIfExists(Paths.get("dest.txt"));

        try (FileInputStream fileInputStream = new FileInputStream("src.txt");
             FileOutputStream fileOutputStream = new FileOutputStream("dest.txt")) {
            byte[] buffer = new byte[8192];
            int len = 0;
            while ((len = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
        }
    }

    /**
     * 额外使用一个8KB缓冲，再使用BufferedInputStream和BufferedOutputStream
     *
     * @throws IOException
     */
    private static void bufferedStreamBufferOperation() throws IOException {
        Files.deleteIfExists(Paths.get("dest.txt"));

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("src.txt"));
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("dest.txt"))) {
            byte[] buffer = new byte[8192];
            int len = 0;
            while ((len = bufferedInputStream.read(buffer)) != -1) {
                bufferedOutputStream.write(buffer, 0, len);
            }
        }
    }


    /**
     * 最高效的文件复制：走总线发送给目标，不走内存和cpu
     *
     * @throws IOException
     */
    private static void fileChannelOperation() throws IOException {
        Files.deleteIfExists(Paths.get("dest.txt"));

        FileChannel in = FileChannel.open(Paths.get("src.txt"), StandardOpenOption.READ);
        FileChannel out = FileChannel.open(Paths.get("dest.txt"), CREATE, WRITE);
        in.transferTo(0, in.size(), out);
    }
}

