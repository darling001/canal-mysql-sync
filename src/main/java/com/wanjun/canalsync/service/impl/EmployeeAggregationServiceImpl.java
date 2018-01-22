package com.wanjun.canalsync.service.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.wanjun.canalsync.annotation.Schema;
import com.wanjun.canalsync.annotation.Table;
import com.wanjun.canalsync.dao.BaseDao;
import com.wanjun.canalsync.service.EmployeeAggregationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-22
 */
@Component
@Schema("wanjun")
public class EmployeeAggregationServiceImpl implements EmployeeAggregationService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeAggregationServiceImpl.class);
    @Autowired
    private BaseDao baseDao;

    @Override
    @Table(value = "tbl_emp", event = {CanalEntry.EventType.INSERT})
    public void save(Map<String, Object> map) {
        System.out.println("d_id = " + map.get("d_id"));

        Map<String, Object> result = baseDao.selectByPK("dept_id", map.get("d_id"), "wanjun", "tbl_dept");

        System.out.println("result = " + result);
    }

    @Override
    @Table(value = "tbl_emp", event = {CanalEntry.EventType.UPDATE})
    public void update(Map<String, Object> map) {
        System.out.println("map = " + map);
    }
}
