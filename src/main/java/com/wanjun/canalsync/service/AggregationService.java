package com.wanjun.canalsync.service;

import com.wanjun.canalsync.model.AggregationModel;

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
    public void saveByEmp(Map<String, Object> map, AggregationModel aggregationModel);

    /**
     * 通过Emp更新聚合表数据
     *
     * @param map
     */
    public void updateByEmp(Map<String, Object> map,AggregationModel aggregationModel);

    /**
     * 通过Emp删除聚合表数据
     *
     * @param map
     * @return
     */
    public void deleteByEmp(Map<String, Object> map,AggregationModel aggregationModel);

    /**
     * 通过Dept更新聚合表数据
     *
     * @param map
     * @return
     */
    public void updateByDept(Map<String, Object> map,AggregationModel aggregationModel);

    /**
     * 通過Dept删除聚合表数据
     * @param map
     * @return
     */
    public void deleteByDept(Map<String, Object> map,AggregationModel aggregationModel);

}
