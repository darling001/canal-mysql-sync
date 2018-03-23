package com.wanjun.canalsync.client;

import com.wanjun.canalsync.annotation.DataSourceTarget;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-03-23
 */
@Aspect
@Component
public class DynamicDataSourceAspect {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    private final String[] QUERY_PREFIX = {"select"};

    /**
     * Dao aspect.
     */
    @Pointcut("execution( * com.wanjun.canalsync.dao.*.*(..))")
    public void daoAspect() {
    }

    /**
     * Switch DataSource
     *
     * @param point the point
     */
 /*   @Before("daoAspect()")
    public void switchDataSource(JoinPoint point) {
        Boolean isQueryMethod = isQueryMethod(point.getSignature().getName());
        if (isQueryMethod) {
            DynamicDataSourceContextHolder.useSlaveDataSource();
            logger.debug("Switch DataSource to [{}] in Method [{}]",
                    DynamicDataSourceContextHolder.getDataSourceKey(), point.getSignature());
        }
    }*/
    @Before("daoAspect()")
    public void switchDataSource(JoinPoint point) {

        //获得当前访问的class
        Class<?> classes = point.getTarget().getClass();

        //获得访问的方法名称
        Signature signature = point.getSignature();
        try {
            //定义的接口方法
            Method abstractMethod = ((MethodSignature)signature).getMethod();
            if (abstractMethod.isAnnotationPresent(DataSourceTarget.class)) {
                DataSourceTarget data = abstractMethod.getAnnotation(DataSourceTarget.class);
                DynamicDataSourceContextHolder.setDataSourceKey(data.value().name());
                logger.debug("===============上下文赋值完成:{}", data.value().name());
            }
        } catch (Exception e) {
            logger.error("switchDataSource error ! target->{},method = {}", classes.getName(), signature.getName());
        }

    }


    /**
     * Restore DataSource
     *
     * @param point the point
     */
    @After("daoAspect())")
    public void restoreDataSource(JoinPoint point) {
        DynamicDataSourceContextHolder.clearDataSourceKey();
        logger.debug("Restore DataSource to [{}] in Method [{}]", DynamicDataSourceContextHolder.getDataSourceKey(), point.getSignature());
    }


    /**
     * @param methodName
     * @return
     */
    private Boolean isQueryMethod(String methodName) {
        for (String prefix : QUERY_PREFIX) {
            if (methodName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

}
