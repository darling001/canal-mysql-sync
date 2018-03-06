package com.wanjun.canalsync.model;

import com.google.common.base.Objects;

import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-18
 */
public class IndexTypeModel {
    private String index;
    private String type;

    private String aggType; //聚合type

    private Map<String,String> pkMappingTableMap;

    public IndexTypeModel() {
    }

    public IndexTypeModel(String index, String type) {
        this.index = index;
        this.type = type;
    }

    public IndexTypeModel(String index, String type,String aggType) {
        this(index,type);
        this.aggType = aggType;
    }

    public IndexTypeModel(String index, String type,String aggType,Map<String,String>pkMappingTableMap) {
        this(index,type,aggType);
        this.pkMappingTableMap = pkMappingTableMap;
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

    public Map<String, String> getPkMappingTableMap() {
        return pkMappingTableMap;
    }

    public void setPkMappingTableMap(Map<String, String> pkMappingTableMap) {
        this.pkMappingTableMap = pkMappingTableMap;
    }


    public String getAggType() {
        return aggType;
    }

    public void setAggType(String aggType) {
        this.aggType = aggType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexTypeModel that = (IndexTypeModel) o;
        return Objects.equal(index, that.index) &&
                Objects.equal(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(index, type);
    }
}

