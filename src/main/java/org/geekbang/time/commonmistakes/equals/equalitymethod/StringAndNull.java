package org.geekbang.time.commonmistakes.equals.equalitymethod;

/**
 * 描述:
 * 字符串和null比较的equals方法
 *
 * @author Noah
 * @create 2020-04-21 06:48
 */
public class StringAndNull {

    public static void main(String[] args) {
        String aa = "123";
        String bb = null;
        System.out.println("比较结果=" + bb.equals(aa));

    }
}
