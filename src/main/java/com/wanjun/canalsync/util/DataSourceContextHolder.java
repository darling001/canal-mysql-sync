package com.wanjun.canalsync.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-03-20
 */
public class DataSourceContextHolder {
    public static final Logger log = LoggerFactory.getLogger(DataSourceContextHolder.class);
    /*

     * 当使用ThreadLocal维护变量时，ThreadLocal为每个使用该变量的线程提供独立的变量副本，

     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。

     */

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();

    /*

     * 管理所有的数据源id;

     * 主要是为了判断数据源是否存在;

     */

    public static List<String> dataSourceIds = new ArrayList<String>();



    /**

     * 使用setDataSourceType设置当前的

     * @param dataSourceType

     */

    public static void setDataSourceType(String dataSourceType) {

        contextHolder.set(dataSourceType);

    }



    public static String getDataSourceType() {

        return contextHolder.get();

    }



    public static void clearDataSourceType() {

        contextHolder.remove();

    }



    /**

     * 判断指定DataSrouce当前是否存在

     *

     * @param dataSourceId

     * @return

     * @author SHANHY

     * @create  2016年1月24日

     */

    public static boolean containsDataSource(String dataSourceId){

        return dataSourceIds.contains(dataSourceId);

    }
}
