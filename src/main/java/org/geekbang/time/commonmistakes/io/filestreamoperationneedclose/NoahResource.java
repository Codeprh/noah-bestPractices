package org.geekbang.time.commonmistakes.io.filestreamoperationneedclose;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 描述:
 * 测试读取文件，流自动关闭的流程
 *
 * @author Noah
 * @create 2020-05-04 09:54
 */
public class NoahResource implements Closeable {

    private static Runnable asUncheckedRunnable(Closeable c) {
        return () -> {
            try {
                c.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    private void overMethod() {

        NoahResource nr = new NoahResource();

        try (Stream<String> lines = Files.lines(Paths.get("demo.txt")).onClose(asUncheckedRunnable(nr))) {
            System.out.println(lines.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {


        //是否会自动关闭
        //nr.read(null);

        //try-with-resouces
        try (NoahResource nr = new NoahResource()) {
            nr.read(null);
        }

    }


    public void read(String path) {

        IntStream.rangeClosed(1, 10).forEach(i -> {

            try (Stream<String> lines = Files.lines(Paths.get("demo.txt"))) {
                System.out.println(lines.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public void close() {
        System.out.println("我关闭了资源");
    }
}
