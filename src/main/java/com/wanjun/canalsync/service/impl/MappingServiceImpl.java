package com.wanjun.canalsync.service.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.wanjun.canalsync.model.DatabaseTableModel;
import com.wanjun.canalsync.model.IndexTypeModel;
import com.wanjun.canalsync.service.MappingService;
import com.wanjun.canalsync.util.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2017-12-10
 */
@Service
@PropertySource("classpath:mapping.properties")
@ConfigurationProperties
public class MappingServiceImpl implements MappingService, InitializingBean {

    private Map<String, String> dbEsMapping;
    private BiMap<DatabaseTableModel, IndexTypeModel> dbEsBiMapping;
    private Map<String, String> tablePrimaryKeyMap;
    private Map<String, Converter> mysqlTypeElasticsearchTypeMapping;

    @Override
    public Map<String, String> getTablePrimaryKeyMap() {
        return tablePrimaryKeyMap;
    }

    @Override
    public void setTablePrimaryKeyMap(Map<String, String> tablePrimaryKeyMap) {
        this.tablePrimaryKeyMap = tablePrimaryKeyMap;
    }

    @Override
    public IndexTypeModel getIndexType(DatabaseTableModel databaseTableModel) {
        return dbEsBiMapping.get(databaseTableModel);
    }


    @Override
    public DatabaseTableModel getDatabaseTableModel(IndexTypeModel indexTypeModel) {
        return dbEsBiMapping.inverse().get(indexTypeModel);
    }

    @Override
    public Object getElasticsearchTypeObject(String mysqlType, String data) {
        Optional<Entry<String, Converter>> result = mysqlTypeElasticsearchTypeMapping.entrySet().
                parallelStream().filter(entry -> mysqlType.toLowerCase().contains(entry.getKey())).findFirst();
        return (result.isPresent() ? result.get().getValue() : (Converter) data1 -> data1).convert(data);

       /* if (mysqlType.toLowerCase().contains("varchar")) {
            return StringUtils.trimToNull(data);
        } else if (mysqlType.toLowerCase().contains("decimal")) {
            return Double.valueOf(data);
        } else if (mysqlType.toLowerCase().equals("date")) {
            return DateUtils.formatDate(DateUtils.parse(data, DateUtils.DATE_SMALL_STR), DateUtils.DATE_SMALL_STR);
        } else if (mysqlType.toLowerCase().equals("datetime")) {
            return DateUtils.formatDate(DateUtils.parse(data, DateUtils.DATE_DEFAULT_STR), DateUtils.DATE_DEFAULT_STR);
        } else if (mysqlType.toLowerCase().contains("json")) {
            return StringUtils.trimToNull(data);
        } else if (mysqlType.toLowerCase().contains("char")) {
            return StringUtils.trimToNull(data);
        } else if (mysqlType.toLowerCase().contains("text")) {
            return StringUtils.trimToNull(data);
        } else if (mysqlType.toLowerCase().contains("int") || mysqlType.toLowerCase().contains("bigint")) {
            return Long.parseLong(data);
        } else if (mysqlType.toLowerCase().contains("float")) {
            return Double.valueOf(data);
        } else if (mysqlType.toLowerCase().contains("double")) {
            return Double.valueOf(data);
        } else {
            return StringUtils.trimToNull(data);
        }*/


    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dbEsBiMapping = HashBiMap.create();
        dbEsMapping.forEach((key, value) -> {
            String[] keyStrings = StringUtils.split(key, ".");
            String[] valueStrings = StringUtils.split(value, "|");
            if (valueStrings.length == 2) {
                dbEsBiMapping.put(new DatabaseTableModel(keyStrings[0], keyStrings[1]), new IndexTypeModel(valueStrings[0], valueStrings[1]));
            } else if (valueStrings.length == 3) {
                dbEsBiMapping.put(new DatabaseTableModel(keyStrings[0], keyStrings[1]), new IndexTypeModel(valueStrings[0], valueStrings[1], valueStrings[2]));
            } else {
                Map<String, String> map = Maps.newHashMap();
                for (int i = 3; i < valueStrings.length; i++) {
                    String[] splitStrings = StringUtils.split(valueStrings[i], ":");
                    map.put(splitStrings[0], splitStrings[1]);
                    dbEsBiMapping.put(new DatabaseTableModel(keyStrings[0], keyStrings[1]), new IndexTypeModel(valueStrings[0], valueStrings[1], valueStrings[2], map));
                }
            }
        });
        mysqlTypeElasticsearchTypeMapping = Maps.newHashMap();
        mysqlTypeElasticsearchTypeMapping.put("json", data -> StringUtils.trimToNull(data));
        mysqlTypeElasticsearchTypeMapping.put("char", data -> StringUtils.trimToNull(data));
        mysqlTypeElasticsearchTypeMapping.put("text", data -> StringUtils.trimToNull(data));
        mysqlTypeElasticsearchTypeMapping.put("blob", data -> data);
        mysqlTypeElasticsearchTypeMapping.put("int", Long::valueOf);
        mysqlTypeElasticsearchTypeMapping.put("datetime", data -> DateUtils.parse(data));
        mysqlTypeElasticsearchTypeMapping.put("date", data -> DateUtils.parse(data, DateUtils.DATE_SMALL_STR));
        mysqlTypeElasticsearchTypeMapping.put("float", Double::valueOf);
        mysqlTypeElasticsearchTypeMapping.put("double", Double::valueOf);
        mysqlTypeElasticsearchTypeMapping.put("decimal", Double::valueOf);
    }


    public Map<String, String> getDbEsMapping() {
        return dbEsMapping;
    }

    public void setDbEsMapping(Map<String, String> dbEsMapping) {
        this.dbEsMapping = dbEsMapping;
    }

    private interface Converter {
        Object convert(String data);
    }
}
