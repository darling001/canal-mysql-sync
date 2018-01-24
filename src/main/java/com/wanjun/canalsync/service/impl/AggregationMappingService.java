package com.wanjun.canalsync.service.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.wanjun.canalsync.model.AggregationModel;
import com.wanjun.canalsync.model.DatabaseTableModel;
import com.wanjun.canalsync.model.IndexTypeModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import sun.jvm.hotspot.utilities.BitMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-24
 */
@Service
@PropertySource("classpath:aggregation-mapping.properties")
@ConfigurationProperties
public class AggregationMappingService implements InitializingBean {

    private Map<String, String> dbAggregationMapping;
    private Map<DatabaseTableModel, AggregationModel> dbAggregationModelMapping;
    private BiMap<DatabaseTableModel, IndexTypeModel> dbEsBiMapping;

    @Override
    public void afterPropertiesSet() throws Exception {
        dbAggregationModelMapping = Maps.newHashMap();
        dbEsBiMapping = HashBiMap.create();

        dbAggregationMapping.forEach((key, value) -> {
            String[] keyStrings = StringUtils.split(key, ".");
            String[] valueStrings = StringUtils.split(value, ".");
            String index = valueStrings[0];
            String type = valueStrings[1];
            String mainPkColumn = valueStrings[2];
            String slavePKColumnStr = valueStrings[3];
            String mainTableName = valueStrings[4];
            String slaveTableStr = valueStrings[5];
            String aggregationTable = valueStrings[6];
            String databaseName = valueStrings[7];
            List<String> slavePKColumnList = Arrays.asList(StringUtils.split(slavePKColumnStr, ","));
            List<String> slaveTableList = Arrays.asList(StringUtils.split(slaveTableStr, ","));
            dbEsBiMapping.put(new DatabaseTableModel(keyStrings[0], keyStrings[1]), new IndexTypeModel(valueStrings[0], valueStrings[1]));
            dbAggregationModelMapping.put(new DatabaseTableModel(keyStrings[0], keyStrings[1]), new AggregationModel(index, type, mainPkColumn, slavePKColumnList, mainTableName, slaveTableList, aggregationTable, databaseName));
        });
        System.out.println(dbAggregationModelMapping);

    }

    public Map<String, String> getDbAggregationMapping() {
        return dbAggregationMapping;
    }

    public void setDbAggregationMapping(Map<String, String> dbAggregationMapping) {
        this.dbAggregationMapping = dbAggregationMapping;
    }
}
