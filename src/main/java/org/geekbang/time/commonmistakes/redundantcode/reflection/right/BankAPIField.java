package org.geekbang.time.commonmistakes.redundantcode.reflection.right;

import java.lang.annotation.*;

/**
 * 用于描述接口的每一个字段规范，包 含参数的次序、类型和长度三个属性
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface BankAPIField {
    int order() default -1;

    int length() default -1;

    String type() default "";
}
