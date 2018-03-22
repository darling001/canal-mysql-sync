package com.wanjun.canalsync.aspect;

import com.wanjun.canalsync.annotation.DS;
import com.wanjun.canalsync.util.DataSourceContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-03-20
 */
@Component
@Aspect
@Order(1)   //设置AOP执行顺序(需要在事务之前，否则事务只发生在默认库中)
public class DynamicDataSourceAspect {

    @Pointcut(value = "@annotation(com.wanjun.canalsync.annotation.DS)")
    public void methodPointcut(){

    }
    @Before(value= "@annotation(ds)")
    public void beforeSwitchDS(JoinPoint point, DS ds) {


        //获取当前的指定的数据源;

        String dsId = ds.value();

        //如果不在我们注入的所有的数据源范围之内，那么输出警告信息，系统自动使用默认的数据源。

        if (!DataSourceContextHolder.containsDataSource(dsId)) {

            System.err.println("数据源[{}]不存在，使用默认数据源 > {}" + ds.value() + point.getSignature());

        } else {

            System.out.println("Use DataSource : {} > {}" + ds.value() + point.getSignature());

            //找到的话，那么设置到动态数据源上下文中。

            DataSourceContextHolder.setDataSourceType(ds.value());

        }

    }


    @After("@annotation(ds)")

    public void restoreDataSource(JoinPoint point, DS ds) {

        System.out.println("Revert DataSource : {} > {}" + ds.value() + point.getSignature());

        //方法执行完毕之后，销毁当前数据源信息，进行垃圾回收。

        DataSourceContextHolder.clearDataSourceType();

    }


}
