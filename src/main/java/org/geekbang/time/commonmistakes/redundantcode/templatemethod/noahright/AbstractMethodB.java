package org.geekbang.time.commonmistakes.redundantcode.templatemethod.noahright;

/**
 * 描述:
 * 模板方法：B
 *
 * @author Noah
 * @create 2020-06-29 15:13
 */
public class AbstractMethodB extends AbstractMethod {

    @Override
    public void abstractMethod() {
        System.out.println("模板B：我实现了抽象方法");
    }
}
