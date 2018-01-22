package com.wanjun.canalsync.service;

import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-22
 */
public interface EmployeeAggregationService {

    /**
     * 添加数据
     * @param map
     */
    public void save(Map<String,Object> map);

    /**
     * 更新数据
     * @param map
     */
    public void update(Map<String,Object> map);

}
