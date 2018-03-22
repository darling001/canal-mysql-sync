package com.wanjun.canalsync.client;

import com.wanjun.canalsync.util.DataSourceContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-03-20
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSource.class);
    /*

     * 代码中的determineCurrentLookupKey方法取得一个字符串，

     * 该字符串将与配置文件中的相应字符串进行匹配以定位数据源，配置文件，即applicationContext.xml文件中需要要如下代码：(non-Javadoc)

     * @see org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource#determineCurrentLookupKey()

     */

    @Override

    protected Object determineCurrentLookupKey() {

        /*

         * DynamicDataSourceContextHolder代码中使用setDataSourceType

         * 设置当前的数据源，在路由类中使用getDataSourceType进行获取，

         *  交给AbstractRoutingDataSource进行注入使用。

         */

        return DataSourceContextHolder.getDataSourceType();

    }


}
