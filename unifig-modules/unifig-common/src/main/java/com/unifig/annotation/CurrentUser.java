package com.unifig.annotation;

import java.lang.annotation.*;

/**
 * 后台管理员Service
 *    on 2018/4/26.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {

}
