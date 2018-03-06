package com.wanjun.canalsync.model;

import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-02-01
 */
public class CanalRowData {

    private String database;

    private String table;

    private String index;

    private String type;

    private Map<String,Object> dataMap;

    private String idValue;

    private IndexTypeModel indexTypeModel;

    private int eventType;


    public CanalRowData() {
    }

    public CanalRowData(String database, String table, String index, String type, Map<String, Object> dataMap, String idValue, IndexTypeModel indexTypeModel,int eventType) {
        this.database = database;
        this.table = table;
        this.index = index;
        this.type = type;
        this.dataMap = dataMap;
        this.idValue = idValue;
        this.indexTypeModel = indexTypeModel;
        this.eventType = eventType;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }
    public IndexTypeModel getIndexTypeModel() {
        return indexTypeModel;
    }

    public void setIndexTypeModel(IndexTypeModel indexTypeModel) {
        this.indexTypeModel = indexTypeModel;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
}
