package com.wanjun.canalsync.annotation;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.lang.annotation.*;

/**
 * Created by wangchengli on 2018/1/22
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    String value() default "";

    CanalEntry.EventType[] event() default {};
}
