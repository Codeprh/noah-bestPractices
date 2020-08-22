package org.geekbang.time.commonmistakes.advancedfeatures.annotationinheritance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;

@Slf4j
public class AnnotationInheritanceApplication {

    public static void main(String[] args) throws NoSuchMethodException {
        //wrong();
        right();
    }

    private static String getAnnotationValue(MyAnnotation annotation) {
        if (annotation == null) return "";
        return annotation.value();
    }

    /**
     * @throws NoSuchMethodException
     * 在注解上面增加@Inherited
     * @Inherited 只能实现类上的注解继承。
     * <p>
     * 要想实现方法上注解的继承，你可以通过反射在继承链上找到方法上的注解。但，这样实现起来很繁琐，而且需要考虑桥接方法。
     */
    public static void wrong() throws NoSuchMethodException {
        Parent parent = new Parent();
        log.info("ParentClass:{}", getAnnotationValue(parent.getClass().getAnnotation(MyAnnotation.class)));
        log.info("ParentMethod:{}", getAnnotationValue(parent.getClass().getMethod("foo").getAnnotation(MyAnnotation.class)));


        Child child = new Child();
        log.info("ChildClass:{}", getAnnotationValue(child.getClass().getAnnotation(MyAnnotation.class)));
        log.info("ChildMethod:{}", getAnnotationValue(child.getClass().getMethod("foo").getAnnotation(MyAnnotation.class)));

    }

    /**
     * Spring 提供了 AnnotatedElementUtils类，
     * 这个类的findMergedAnnotation工具方法，可以帮助我们找出父类和接口、父类方法和接口方法上的注解，并可以处理桥接方法，实现一键找到继承链的注解
     *
     * @throws NoSuchMethodException
     */
    public static void right() throws NoSuchMethodException {
        Parent parent = new Parent();
        log.info("ParentClass:{}", getAnnotationValue(parent.getClass().getAnnotation(MyAnnotation.class)));
        log.info("ParentMethod:{}", getAnnotationValue(parent.getClass().getMethod("foo").getAnnotation(MyAnnotation.class)));

        Child child = new Child();
        log.info("ChildClass:{}", getAnnotationValue(AnnotatedElementUtils.findMergedAnnotation(child.getClass(), MyAnnotation.class)));
        log.info("ChildMethod:{}", getAnnotationValue(AnnotatedElementUtils.findMergedAnnotation(child.getClass().getMethod("foo"), MyAnnotation.class)));

    }

    /**
     * 定义带注解有父类
     */
    @MyAnnotation(value = "Class")
    @Slf4j
    static class Parent {

        @MyAnnotation(value = "Method")
        public void foo() {
        }
    }

    /**
     * 子类（无注解）继承父类
     */
    @Slf4j
    static class Child extends Parent {
        @Override
        public void foo() {
        }
    }
}

