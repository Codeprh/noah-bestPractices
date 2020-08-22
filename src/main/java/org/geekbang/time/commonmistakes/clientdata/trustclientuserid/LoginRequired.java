package org.geekbang.time.commonmistakes.clientdata.trustclientuserid;

import java.lang.annotation.*;

/**
 * 标识接口，需要登录了才能访问
 *
 * @author codingprh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface LoginRequired {
    String sessionKey() default "currentUser";
}