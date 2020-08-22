package org.geekbang.time.commonmistakes.advancedfeatures.annotationinheritance;

import java.lang.annotation.*;

/**
 * 定义一个注解作用在方法和类上
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MyAnnotation {
    String value();
}
