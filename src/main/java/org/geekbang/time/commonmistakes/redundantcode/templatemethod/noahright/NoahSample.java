package org.geekbang.time.commonmistakes.redundantcode.templatemethod.noahright;

/**
 * 描述:
 * 模板方法测试类
 *
 * @author Noah
 * @create 2020-06-29 15:15
 */
public class NoahSample {

    public static void main(String[] args) {
        AbstractMethod abstractMethodA = new AbstractMethodA();
        AbstractMethod abstractMethodB = new AbstractMethodB();

        abstractMethodA.method();
        System.out.println("=====");
        abstractMethodB.method();
    }
}
