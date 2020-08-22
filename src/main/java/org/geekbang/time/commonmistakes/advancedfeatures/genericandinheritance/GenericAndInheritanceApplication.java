package org.geekbang.time.commonmistakes.advancedfeatures.genericandinheritance;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;


public class GenericAndInheritanceApplication {

    public static void main(String[] args) {
        wrong3();
    }

    public static void wrong1() {
        Child1 child1 = new Child1();
        Arrays.stream(child1.getClass().getMethods())
                .filter(method -> method.getName().equals("setValue"))
                .forEach(method -> {
                    try {
                        method.invoke(child1, "test");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        System.out.println(child1.toString());
    }

    public static void wrong2() {
        Child1 child1 = new Child1();
        Arrays.stream(child1.getClass().getDeclaredMethods())
                .filter(method -> method.getName().equals("setValue"))
                .forEach(method -> {
                    try {
                        method.invoke(child1, "test");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        System.out.println(child1.toString());
    }

    public static void wrong3() {
        Child2 child2 = new Child2();
        Arrays.stream(child2.getClass().getDeclaredMethods())
                .filter(method -> method.getName().equals("setValue"))
                .forEach(method -> {
                    try {
                        method.invoke(child2, "test");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        System.out.println(child2.toString());
    }

    /**
     * 问题：Child2 类其实有 2 个 setValue 方法，入参分别是 String 和 Object。其实，这就是泛型类型擦除导致的问题。
     * <p>
     * 解决：
     * 1、通过 getDeclaredMethods 方法获取到所有方法后，必须同时根据方法名 setValue 和 非 isBridge 两个条件过滤，才能实现唯一过滤;
     * 2、使用 Stream 时，如果希望只匹配 0 或 1 项的话，可以考虑配合 ifPresent 来使用 findFirst 方法。
     */
    public static void right() {
        Child2 child2 = new Child2();
        Arrays.stream(child2.getClass().getDeclaredMethods())
                .filter(method -> method.getName().equals("setValue") && !method.isBridge())
                .findFirst().ifPresent(method -> {
            try {
                method.invoke(child2, "test");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.out.println(child2.toString());
    }
}

class Parent<T> {

    AtomicInteger updateCount = new AtomicInteger();

    private T value;

    @Override
    public String toString() {
        return String.format("value: %s updateCount: %d", value, updateCount.get());
    }

    public void setValue(T value) {
        System.out.println("Parent.setValue called");
        this.value = value;
        updateCount.incrementAndGet();
    }
}

class Child1 extends Parent {

    public void setValue(String value) {
        System.out.println("Child1.setValue called");
        super.setValue(value);
    }
}


class Child2 extends Parent<String> {

    /**
     * todo：重写的方法是Object 类型的，还会有2个setValue的方法吗？
     *
     * @param value
     */
    @Override
    public void setValue(String value) {
        System.out.println("Child2.setValue called");
        super.setValue(value);
    }
}