package com.wanjun.canalsync.service;

import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-22
 */
public interface AggregationService {

    /**
     * 通过Emp插入聚合表数据
     *
     * @param map
     */
    public Integer saveByEmp(Map<String, Object> map);

    /**
     * 通过Emp更新聚合表数据
     *
     * @param map
     */
    public Long updateByEmp(Map<String, Object> map);

    /**
     * 通过Emp删除聚合表数据
     *
     * @param map
     * @return
     */
    public Long deleteByEmp(Map<String, Object> map);

    /**
     * 通过Dept更新聚合表数据
     *
     * @param map
     * @return
     */
    public Long updateByDept(Map<String, Object> map);

    /**
     * 通過Dept删除聚合表数据
     * @param map
     * @return
     */
    public Long deleteByDept(Map<String, Object> map);

}
