package org.geekbang.time.commonmistakes.io.badencodingissue;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Hex;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 文件读写需要确保字符编码一致：badencodingissue
 */
@Slf4j
public class FileBadEncodingIssueApplication {

    public static void main(String[] args) throws IOException {
        init();
        wrong();
//        right1();
//        right2();
    }

    /**
     * 使用 GBK 编码把“你好 hi”写入一个名为 hello.txt 的文本文件，
     * 然后直接以字节数组形式读取文件内容，转换为十六进制字符串输出到日志
     *
     * @throws IOException
     */
    private static void init() throws IOException {
        Files.deleteIfExists(Paths.get("hello.txt"));
        Files.write(Paths.get("hello.txt"), "你好hi".getBytes(Charset.forName("GBK")));
        log.info("bytes:{}", Hex.encodeHexString(Files.readAllBytes(Paths.get("hello.txt"))).toUpperCase());
    }

    private static void wrong() throws IOException {

        //读取机器的默认编码格式
        log.info("charset: {}", Charset.defaultCharset());

        char[] chars = new char[10];
        String content = "";

        //FileReader 是以当前机器的默认字符集来读取文件的
        try (FileReader fileReader = new FileReader("hello.txt")) {
            int count;
            while ((count = fileReader.read(chars)) != -1) {
                content += new String(chars, 0, count);
            }
        }

        //文件格式是GBK，使用UTF-8采用字符集操作，导致乱码
        log.info("result:{}", content);

        //UTF-8 编码 的“你好”的十六进制是 E4BDA0E5A5BD，每一个汉字需要三个字节;而 GBK 编码的汉 字，每一个汉字两个字节。
        Files.write(Paths.get("hello2.txt"), "你好hi".getBytes(Charsets.UTF_8));

        log.info("utf-8,bytes:{}", Hex.encodeHexString(Files.readAllBytes(Paths.get("hello2.txt"))).toUpperCase());
        log.info("gbk,bytes:{}", Hex.encodeHexString(Files.readAllBytes(Paths.get("hello.txt"))).toUpperCase());

    }

    /**
     * 最佳实践：FileReader 是以当前机器的默认字符集来读取文件的。
     * 按照文档所说，直接使用 FileInputStream 拿文件流， 然后使用 InputStreamReader 读取字符流，并指定字符集为 GBK。
     *
     * @throws IOException
     */
    private static void right1() throws IOException {

        char[] chars = new char[10];
        String content = "";

        try (FileInputStream fileInputStream = new FileInputStream("hello.txt");
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName("GBK"))) {
            int count;
            while ((count = inputStreamReader.read(chars)) != -1) {
                content += new String(chars, 0, count);
            }
        }

        log.info("result: {}", content);
    }

    /**
     * 正确姿势读取GBK字符02
     *
     * @throws IOException
     */
    private static void right2() throws IOException {
        log.info("result: {}", Files.readAllLines(Paths.get("hello.txt"), Charset.forName("GBK")).stream().findFirst().orElse(""));
    }

}


