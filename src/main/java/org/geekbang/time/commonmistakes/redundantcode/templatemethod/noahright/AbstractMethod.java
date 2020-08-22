package org.geekbang.time.commonmistakes.redundantcode.templatemethod.noahright;

/**
 * 描述:
 * 模板方法
 *
 * @author Noah
 * @create 2020-06-29 15:08
 */
public abstract class AbstractMethod {

    /**
     * 模板方法
     */
    public void method() {
        concreteMethod();
        hookMethod();
        abstractMethod();
    }

    /**
     * 具体方法
     */
    public void concreteMethod() {
        System.out.println("模板里自带的实现方法，万年不变");
    }

    /**
     * 钩子方法，子类可以依据情况实现的方法
     */
    protected void hookMethod() {
        System.out.println("默认是模板的钩子方法");
    }

    /**
     * 抽象方法，必须让子类实现的方法
     */
    public abstract void abstractMethod();
}
