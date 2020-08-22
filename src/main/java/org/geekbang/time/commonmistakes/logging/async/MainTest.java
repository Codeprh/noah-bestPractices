package org.geekbang.time.commonmistakes.logging.async;

import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 描述:
 *
 * @author Noah
 * @create 2020-05-01 16:31
 */
public class MainTest {
    public static void main(String[] args) {
        String payload = IntStream.rangeClosed(1, 1000000)
                .mapToObj(__ -> "a")
                .collect(Collectors.joining("")) + UUID.randomUUID().toString();
        System.out.println(payload);
        System.out.println("===============================");
        System.out.println(ObjectSizeCalculator.getObjectSize(payload));
    }
}
