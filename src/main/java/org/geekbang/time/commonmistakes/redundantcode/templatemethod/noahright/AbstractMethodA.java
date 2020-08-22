package org.geekbang.time.commonmistakes.redundantcode.templatemethod.noahright;

/**
 * 描述:
 * 模板方法：A
 *
 * @author Noah
 * @create 2020-06-29 15:12
 */
public class AbstractMethodA extends AbstractMethod {
    @Override
    protected void hookMethod() {
        System.out.println("模板A：钩子方法被我替换实现了！！");
    }

    @Override
    public void abstractMethod() {
        System.out.println("模板A：我实现了抽象方法");
    }
}
