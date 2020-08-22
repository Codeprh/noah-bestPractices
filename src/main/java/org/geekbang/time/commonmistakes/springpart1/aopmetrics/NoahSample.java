package org.geekbang.time.commonmistakes.springpart1.aopmetrics;

import java.lang.reflect.Array;

/**
 * 描述:
 * 测试主类
 *
 * @author Noah
 * @create 2020-06-29 17:11
 */
public class NoahSample {
    public static void main(String[] args) {

        Object o1 = Array.get(Array.newInstance(int.class, 1), 0);
        Object o2 = Array.get(Array.newInstance(byte.class, 1), 0);

        System.out.println(o1 + "," + o2);

    }
}
