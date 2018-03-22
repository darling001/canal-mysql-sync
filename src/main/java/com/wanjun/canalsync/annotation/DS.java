package com.wanjun.canalsync.annotation;

import java.lang.annotation.*;

/**
 * Created by wangchengli on 2018/3/20
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.METHOD
})
@Documented
public @interface DS {
    String value() default "gms1";
}
