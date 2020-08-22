package org.geekbang.time.commonmistakes.redundantcode.reflection.right;

import java.lang.annotation.*;

/**
 * 我们就能通过自定义注解为接口和所有参数增加一些元数据.
 * 我们定义一个接口 API 的注解 BankAPI，包含接口 URL 地址和接口说明
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface BankAPI {
    String desc() default "";

    String url() default "";
}
