package com.wanjun.canalsync.service.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.wanjun.canalsync.annotation.Schema;
import com.wanjun.canalsync.annotation.Table;
import com.wanjun.canalsync.dao.BaseDao;
import com.wanjun.canalsync.service.AggregationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-22
 */
@Service
@Schema("wanjun")
public class AggregationServiceImpl implements AggregationService {

    private static final Logger logger = LoggerFactory.getLogger(AggregationServiceImpl.class);
    @Autowired
    private BaseDao baseDao;

    @Override
    @Table(value = "tbl_emp", event = {CanalEntry.EventType.INSERT})
    public Integer saveByEmp(Map<String, Object> map) {
        Map<String, Object> result = baseDao.selectByPK("dept_id", map.get("d_id"), "wanjun", "tbl_dept");
        result.putAll(map);
        return baseDao.insertByMap("wanjun","tbl_emp_dept",result);
    }

    @Override
    @Table(value = "tbl_emp", event = {CanalEntry.EventType.UPDATE})
    public Long updateByEmp(Map<String, Object> map) {
        return baseDao.updateByMap("wanjun","tbl_emp_dept",map,"emp_id",map.get("emp_id"));
    }

    @Override
    @Table(value = "tbl_dept",event = {CanalEntry.EventType.UPDATE})
    public Long updateByDept(Map<String, Object> map) {
        return baseDao.updateByMap("wanjun","tbl_emp_dept",map,"dept_id", map.get("dept_id"));
    }
}
