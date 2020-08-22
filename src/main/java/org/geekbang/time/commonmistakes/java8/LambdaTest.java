package org.geekbang.time.commonmistakes.java8;

import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LambdaTest {

    @Test
    public void lambdavsanonymousclass() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello1");
            }
        }).start();

        new Thread(() -> System.out.println("hello2")).start();
    }

    @Test
    public void noahTest() {

        System.out.println("r=" + (2 > 0));

        Predicate<Integer> positiveNumber = i -> i > 0;
        boolean test = positiveNumber.test(2);

    }

    @Test
    public void functionalInterfaces() {
        //可以看一下java.util.function包

        //用于提供数据的 Supplier 接口，就只有一个 get 抽象方法，没有任何入参、有一个返回值
        Supplier<String> supplier = String::new;
        Supplier<String> stringSupplier = () -> "OK";

        //Predicate的例子：Predicate接口是输入一个参数，返回布尔值
        Predicate<Integer> positiveNumber = i -> i > 0;
        Predicate<Integer> evenNumber = i -> i % 2 == 0;
        assertTrue(positiveNumber.and(evenNumber).test(2));

        //Consumer的例子，输出两行abcdefg。Consumer接口是消费一个数据
        Consumer<String> println = System.out::println;
        println.andThen(println).accept("abcdefg");

        //Function的例子，Function接口是输入一个数据，计算后输出一个数据。
        Function<String, String> upperCase = String::toUpperCase;
        Function<String, String> duplicate = s -> s.concat(s);
        assertThat(upperCase.andThen(duplicate).apply("test"), is("TESTTEST"));

        //Supplier的例子，Supplier是提供一个数据的接口
        Supplier<Integer> random = () -> ThreadLocalRandom.current().nextInt();
        System.out.println(random.get());

        //BinaryOperator，BinaryOperator是输入两个同类型参数，输出一个同类型参数的接口
        BinaryOperator<Integer> add = Integer::sum;
        BinaryOperator<Integer> subtraction = (a, b) -> a - b;
        assertThat(subtraction.apply(add.apply(1, 2), 3), is(0));

    }

    @Test
    public void testMethod() {
        BinaryOperator<Object> objectBinaryOperator = BinaryOperator.minBy((a, b) -> {
            return (int) a - (int) b;
        });
        System.out.println(objectBinaryOperator.apply(1, 2));
    }
}
