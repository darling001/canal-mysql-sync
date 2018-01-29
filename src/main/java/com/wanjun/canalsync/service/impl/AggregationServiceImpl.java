package com.wanjun.canalsync.service.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.common.base.VerifyException;
import com.wanjun.canalsync.annotation.Schema;
import com.wanjun.canalsync.annotation.Table;
import com.wanjun.canalsync.dao.BaseDao;
import com.wanjun.canalsync.model.AggregationModel;
import com.wanjun.canalsync.service.AggregationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-22
 */
@SuppressWarnings("all")
@Service
@Schema("wanjun")
public class AggregationServiceImpl implements AggregationService {

    private static final Logger logger = LoggerFactory.getLogger(AggregationServiceImpl.class);
    @Autowired
    private BaseDao baseDao;


    @Override
    @Table(value = "tbl_emp", event = {CanalEntry.EventType.INSERT})
    public void saveByEmp(Map<String, Object> map, AggregationModel aggregationModel) {
        List<String> slavePKColumnList = aggregationModel.getSlavePKColumnList();
        List<String> slaveTableNameList = aggregationModel.getSlaveTableNameList();
        List<String> fkColumnList = aggregationModel.getFkColumnList();
        for (int i = 0; i < fkColumnList.size(); i++) {
            Map<String, Object> result = baseDao.selectByPK(slavePKColumnList.get(i),
                    map.get(fkColumnList.get(i)), aggregationModel.getDatabaseName(), slaveTableNameList.get(i));

            result.remove(slavePKColumnList.get(i));
            map.putAll(result);

        }
        //防止主表主键和聚合表主键名不一致的情况，直接进行替换
        map.put(aggregationModel.getMainPKColumn(), map.remove(aggregationModel.getMainPKColumn()));

        baseDao.insertByMap(aggregationModel.getDatabaseName(), aggregationModel.getAggregationTableName(), map);
    }

    @Override
    @Table(value = "tbl_emp", event = {CanalEntry.EventType.UPDATE})
    public void updateByEmp(Map<String, Object> map, AggregationModel aggregationModel) {
        List<String> slavePKColumnList = aggregationModel.getSlavePKColumnList();
        List<String> slaveTableNameList = aggregationModel.getSlaveTableNameList();
        List<String> fkColumnList = aggregationModel.getFkColumnList();
        for (int i = 0; i < fkColumnList.size(); i++) {
            Map<String, Object> result = baseDao.selectByPK(slavePKColumnList.get(i),
                    map.get(fkColumnList.get(i)), aggregationModel.getDatabaseName(), slaveTableNameList.get(i));
            result.remove(slavePKColumnList.get(i));
            map.putAll(result);
        }
        //防止主表主键和聚合表主键名不一致的情况，直接进行替换
        map.put(aggregationModel.getMainPKColumn(), map.remove(aggregationModel.getMainPKColumn()));

        baseDao.updateByMap(aggregationModel.getDatabaseName(), aggregationModel.getAggregationTableName(),
                map, aggregationModel.getAggregationPKColumn(), map.get(aggregationModel.getAggregationPKColumn()));
    }

    @Override
    @Table(value = "tbl_emp", event = {CanalEntry.EventType.DELETE})
    public void deleteByEmp(Map<String, Object> map, AggregationModel aggregationModel) {
        baseDao.deleteByPK(aggregationModel.getDatabaseName(), aggregationModel.getAggregationTableName(),
                aggregationModel.getAggregationPKColumn(), map.get(aggregationModel.getMainPKColumn()));
    }


    @Override
    @Table(value = "tbl_dept", event = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.INSERT})
    public void updateByDept(Map<String, Object> map, AggregationModel aggregationModel) {
        String aggregationSFK = aggregationModel.getAggregationSFK();
        String slaveSPK = aggregationModel.getSlaveSPK();
        Object aggregationSFKValue  = map.get(slaveSPK);
        map.remove(slaveSPK);
        baseDao.updateByMap(aggregationModel.getDatabaseName(), aggregationModel.getAggregationTableName(),
                map, aggregationSFK, aggregationSFKValue);


    }


    @Override
    @Table(value = "tbl_dept", event = {CanalEntry.EventType.DELETE})
    public void deleteByDept(Map<String, Object> map, AggregationModel aggregationModel) {
        String aggregationSFK = aggregationModel.getAggregationSFK();
        String slaveSPK = aggregationModel.getSlaveSPK();
        map.put(aggregationSFK, map.remove(slaveSPK));
        baseDao.updateNull(aggregationModel.getDatabaseName(), aggregationModel.getAggregationTableName(),
                map, aggregationSFK, map.get(slaveSPK));

    }
}
